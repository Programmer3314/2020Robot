/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.DriveController.MoveParameters;

/**
 * Add your docs here.
 */
public class Shooter {
    public enum ShooterStates {
        IDLE, GET_HALF_BALL, GET_BALL, GOT_BALL, GAP_BALL, GROUND_GET_HALF_BALL, GROUND_GOT_BALL, GROUND_GAP_BALL, INTAKE_DONE, INTAKE_DONE2, PREPARE, FIRE_BALL_AUTO, DONE
    }

    double targetShooterRPM, shooterRPMTolerance;
    double queuingBeltSpeed;
    ShooterStates shooterStates;
    public CANSparkMax shooterLeft, shooterRight;
    public TalonSRX ballQueuing, hood, indexer, intake;
    public CANEncoder shooterEncoder;
    public double hoodSetpoint;
    int hoodEncoder, beltQueuingEncoder;
    double lastEncoderVal;
    boolean shooterBusy;
    boolean homedHood;
    boolean useGyro;
    double desiredGyroAngle;
    double gyroTolerance;
    int counter;
    CANPIDController shooterPidController;
    PDController hoodPDController;

    public Shooter(int CANMcshooterLeft, int CANMcshooterRight, int CANMcBallQueuing, 
        int CANMcHood, int CANMcIndexer, int CANMcIntake) {


        shooterStates = ShooterStates.IDLE;

        shooterLeft = new CANSparkMax(CANMcshooterLeft, CANSparkMaxLowLevel.MotorType.kBrushless);
        shooterRight = new CANSparkMax(CANMcshooterRight, CANSparkMaxLowLevel.MotorType.kBrushless);
        ballQueuing = new TalonSRX(CANMcBallQueuing);
        hood = new TalonSRX(CANMcHood);
        indexer = new TalonSRX(CANMcIndexer);
        intake = new TalonSRX(CANMcIntake);

        shooterLeft.restoreFactoryDefaults();
        shooterRight.restoreFactoryDefaults();
        ballQueuing.configFactoryDefault();
        hood.configFactoryDefault();
        indexer.configFactoryDefault();
        intake.configFactoryDefault();
        hood.setSelectedSensorPosition(0);

        indexer.setInverted(true);

        shooterPidController = shooterLeft.getPIDController();
        shooterPidController.setP(Constants.shooterkP);
        shooterPidController.setI(Constants.shooterkI);
        shooterPidController.setIAccum(Constants.shooterkIMax);
        shooterPidController.setIZone(Constants.shooterkIz);
        shooterPidController.setD(Constants.shooterkD);
        shooterPidController.setFF(Constants.shooterkFF);
        shooterPidController.setOutputRange(Constants.shooterkMinOutput, Constants.shooterkMaxOutput);

        hoodPDController = new PDController(Constants.hoodkP, Constants.hoodkD);
        hoodPDController.setMinCorrectionValue(Constants.hoodkMinCorrection);
        hoodPDController.setMaxCorrectionValue(Constants.hoodkMaxCorrection);
        hoodPDController.setToleranceValue(Constants.hoodkTolerance);

        shooterRight.follow(shooterLeft, true);

        shooterLeft.setSmartCurrentLimit(Constants.sparkShooterStallLimit, Constants.sparkShooterFreeLimit);

        shooterLeft.setIdleMode(IdleMode.kCoast);
        hood.setNeutralMode(NeutralMode.Brake);
        ballQueuing.setNeutralMode(NeutralMode.Coast);
        indexer.setNeutralMode(NeutralMode.Coast);
        intake.setNeutralMode(NeutralMode.Brake);

        shooterEncoder = shooterLeft.getEncoder();
        hoodEncoder = hood.getSelectedSensorPosition();
        beltQueuingEncoder = ballQueuing.getSelectedSensorPosition();

        shooterEncoder.setVelocityConversionFactor(Constants.sparkShooterVelocityConversionFactor);

        counter = 0;
    }

    public void update(MoveParameters mP) {
        beltQueuingEncoder = ballQueuing.getSelectedSensorPosition();

        //mP.currentState = DriveController.DriveState.SHOOTERPOWERPORTALIGNMENT;

        // Hood Control:
        // if the hood has not been homed, do so
        // if the hood is currently homed reset the encoder position
        // otherwise move to setpoint

        double hoodCalculated = 0;
        if(SensorInput.queuedHood){
            hood.setSelectedSensorPosition(0);
            if(!homedHood){
                hood.set(ControlMode.PercentOutput, 0.0);
            }
            homedHood = true;
        } 
        hoodEncoder = hood.getSelectedSensorPosition();
        
        if(!homedHood) {
            resetHood();
        } else {
            hoodCalculated = hoodPDController.calculate(hoodSetpoint, hoodEncoder);
            hood.set(ControlMode.PercentOutput, hoodCalculated);
        }
        SmartDashboard.putNumber("Hood Calculated", hoodCalculated);
        SmartDashboard.putNumber("Hood Set Point", hoodSetpoint);
        SmartDashboard.putNumber("Hood Encoder", hoodEncoder);


        if (HumanInput.hoodUp) {
            hood.set(ControlMode.PercentOutput, -0.1);
        } else if (HumanInput.hoodDown) {
            hood.set(ControlMode.PercentOutput, 0.1);
        } else if (HumanInput.hoodUpReleased || HumanInput.hoodDownReleased){
            hood.set(ControlMode.PercentOutput, 0);
            hoodSetpoint = hoodEncoder;
        } 

        // Manual Ball Queue Control:
        if (HumanInput.spinBallQueue) {
            ballQueuing.set(ControlMode.PercentOutput, 0.75);
        } else if (HumanInput.reverseBallQueue) {
            ballQueuing.set(ControlMode.PercentOutput, -0.75);
        } else {
            ballQueuing.set(ControlMode.PercentOutput, 0);
        }

        // Ball Management: (Intake, Queue, and Shoot)
        switch (shooterStates) {
            case IDLE:
            shooterLeft.set(0);
            intake.set(ControlMode.PercentOutput, 0);
            indexer.set(ControlMode.PercentOutput, 0);
            //ballQueuing.set(ControlMode.PercentOutput, 0);
            break;

            case GET_HALF_BALL:
            //Solenoids.ejectIntake(true); //Comment 1 of 2 when test loading station intake
            intake.set(ControlMode.PercentOutput, 0);
            indexer.set(ControlMode.PercentOutput, 0.5);
            ballQueuing.set(ControlMode.PercentOutput, 0.0);
            Solenoids.ejectIntake(false);
            break;

            case GET_BALL:
            intake.set(ControlMode.PercentOutput, 0.25);
            indexer.set(ControlMode.PercentOutput, 0.5);
            // Solenoids.ejectIntake(false);
            break;

            case GOT_BALL:
            //Solenoids.ejectIntake(true); //Comment 2 of 2 when test loading station intake
            intake.set(ControlMode.PercentOutput, 0);
            indexer.set(ControlMode.PercentOutput, 0.5);
            ballQueuing.set(ControlMode.PercentOutput, 0.5);
            break;

            case GAP_BALL:
            intake.set(ControlMode.PercentOutput, 0);
            indexer.set(ControlMode.PercentOutput, 0);
            ballQueuing.set(ControlMode.PercentOutput, 0.5);
            break;

            case GROUND_GET_HALF_BALL:
            Solenoids.ejectIntake(true); //Comment 1 of 2 when test loading station intake
            intake.set(ControlMode.PercentOutput, 0.5);
            indexer.set(ControlMode.PercentOutput, 0.5);
            ballQueuing.set(ControlMode.PercentOutput, 0.0);
            break;
    
            case GROUND_GOT_BALL:
            // Solenoids.ejectIntake(false); //Comment 2 of 2 when test loading station intake
            intake.set(ControlMode.PercentOutput, 0.5);
            indexer.set(ControlMode.PercentOutput, 0.5);
            ballQueuing.set(ControlMode.PercentOutput, 0.5);
            break;

            case GROUND_GAP_BALL:
            intake.set(ControlMode.PercentOutput, 0.5);
            indexer.set(ControlMode.PercentOutput, 0);
            ballQueuing.set(ControlMode.PercentOutput, 0.5);
            break;

            case INTAKE_DONE:
            intake.set(ControlMode.PercentOutput, 0.5);
            indexer.set(ControlMode.PercentOutput, 0);
            ballQueuing.set(ControlMode.PercentOutput, 0);
            Solenoids.ejectIntake(false);
            break;

            case INTAKE_DONE2:
            intake.set(ControlMode.PercentOutput, 0);
            indexer.set(ControlMode.PercentOutput, 0);
            ballQueuing.set(ControlMode.PercentOutput, 0);
            Solenoids.ejectIntake(false);
            break;

            case PREPARE:
                // shooterLeft.set(-HumanInput.throttle);
                shooterPidController.setReference(targetShooterRPM, ControlType.kVelocity);

                if (SensorInput.queuedShooter) {
                    ballQueuing.set(ControlMode.PercentOutput, 0);
                } else {
                    ballQueuing.set(ControlMode.PercentOutput, 0.5);
                }

                // TODO: Review this change and the similar one below
                // I don't like this, but it may be the quick way to make this work
                if(!SensorInput.queuedTrack1 && !SensorInput.queuedTrack2) {
                    indexer.set(ControlMode.PercentOutput, 0.5);
                } else {
                    indexer.set(ControlMode.PercentOutput, 0);
                }
                // TODO: Review Change
                // not sure why we'd run this here
                //intake.set(ControlMode.PercentOutput, 0.25);
                intake.set(ControlMode.PercentOutput, 0);

                break;
        case FIRE_BALL_AUTO:
            // shooterLeft.set(-HumanInput.throttle);
            shooterPidController.setReference(targetShooterRPM, ControlType.kVelocity);
            ballQueuing.set(ControlMode.PercentOutput, queuingBeltSpeed);
            // TODO: Review this change and the similar one above
            // I don't like this, but it may be the quick way to make this work
            if(!SensorInput.queuedTrack1 && !SensorInput.queuedTrack2) {
                indexer.set(ControlMode.PercentOutput, 0.5);
            } else {
                indexer.set(ControlMode.PercentOutput, 0);
            }
                // TODO: Review Change
                // not sure why we'd run this here
                //intake.set(ControlMode.PercentOutput, 0.25);
                intake.set(ControlMode.PercentOutput, 0);
            break;

        case DONE:
            homedHood = false;
            hoodSetpoint = 0;
            break;
        }

        // Calc State Changes...
        switch (shooterStates) {
            case IDLE:
                shooterBusy = false;
                counter = 0;
                break;

            case GET_HALF_BALL:
                if(SensorInput.queuedTrack1){
                    shooterStates = ShooterStates.GOT_BALL;
                }
                break;

            case GET_BALL:
                if (SensorInput.queuedTrack1){
                    shooterStates = ShooterStates.GOT_BALL;
                }

                if (SensorInput.queuedShooter) {
                    shooterStates = ShooterStates.INTAKE_DONE;
                    counter = 0;
                }

                break;
            case GOT_BALL:
                if (SensorInput.queuedTrack2) {
                    shooterStates = ShooterStates.GAP_BALL;
                }

                if (SensorInput.queuedShooter) {
                    shooterStates = ShooterStates.INTAKE_DONE;
                    counter = 0;
                }

                break;
            case GAP_BALL:
                if (!SensorInput.queuedTrack2) {
                    shooterStates = ShooterStates.GET_HALF_BALL;
                }
    
                if (SensorInput.queuedShooter) {
                    shooterStates = ShooterStates.INTAKE_DONE;
                    counter = 0;
                }
    
                break;
            case GROUND_GET_HALF_BALL:
                if(SensorInput.queuedIntake && counter >= 25){
                    shooterStates = ShooterStates.GROUND_GOT_BALL;
                    counter = 0;
                }else if(SensorInput.queuedIntake){
                    counter++;
                }else{
                    counter = 0;
                }

                if(SensorInput.queuedTrack1){
                    shooterStates = ShooterStates.GROUND_GAP_BALL;
                }
                break;
            case GROUND_GOT_BALL:
                if (SensorInput.queuedTrack1) {
                    shooterStates = ShooterStates.GROUND_GAP_BALL;
                }

                if (SensorInput.queuedShooter) {
                    shooterStates = ShooterStates.INTAKE_DONE;
                    counter = 0;
                }
                break;
            case GROUND_GAP_BALL:
                if (!SensorInput.queuedTrack2) {
                    shooterStates = ShooterStates.GROUND_GET_HALF_BALL;
                }

                if (SensorInput.queuedShooter) {
                    shooterStates = ShooterStates.INTAKE_DONE;
                    counter = 0;
                }
                break;

            case INTAKE_DONE:
                //shooterStates = ShooterStates.IDLE;
                counter++;
                if(counter >= 150){
                    shooterStates = ShooterStates.INTAKE_DONE2;
                }
                break;

            case INTAKE_DONE2:
                shooterStates = ShooterStates.IDLE;
                break;

            case PREPARE:
                if (SensorInput.queuedShooter) {
                    shooterBusy = true;

                    // Confirm Firing Solution
                    // (consolidated conditions)
                    if ((Math.abs(shooterEncoder.getVelocity() - targetShooterRPM) <= shooterRPMTolerance) 
                        && (Math.abs(hoodEncoder - hoodSetpoint) <= Constants.hoodkTolerance) 
                        && (useGyro == false || (Math.abs(Robot.cleanGyro - desiredGyroAngle) <= gyroTolerance))) {
                        shooterStates = ShooterStates.FIRE_BALL_AUTO;
                    }
                } else if (counter >= 200) {
                    shooterStates = ShooterStates.DONE;
                }
                counter++;
    
                break;
    
            case FIRE_BALL_AUTO:
                counter = 0;
                if ((Math.abs(shooterEncoder.getVelocity() - targetShooterRPM) > shooterRPMTolerance)
                    && (Math.abs(hoodEncoder - hoodSetpoint) > Constants.hoodkTolerance) ) {
                    shooterStates = ShooterStates.PREPARE;
                }
                // TODO: Review Change
                // I believe that the shooter wasn't spinning down 
                // because the RPM didn't fall enough to trigger the above 
                // state change. So the counter is never checked and we just sit here. 
                // But, this state's job is to inject a ball into the shooting wheels. 
                // We should check that by seeing the sensor loose the ball. Once that 
                // happens we should go back to queue the next ball. Therefore if the 
                // SensorInput.queuedShooter is false, we should go back and prepare. 
                // (Actually, this is the start of a bigger change that will split Prepare
                // into two states... Queue_Ball and Prepare. The first should queue the ball
                // and the second should simply hold until the shooter is in the right condition.)
                if (!SensorInput.queuedShooter) {
                    shooterStates = ShooterStates.PREPARE;
                }
                break;
    
            case DONE:
                shooterBusy = false;
                shooterStates = ShooterStates.IDLE;
                break;
            }
    
        SmartDashboard.putNumber("Current RPM of the Shooter Motors", shooterEncoder.getVelocity());
        SmartDashboard.putNumber("Belt Queue Value", beltQueuingEncoder);
        SmartDashboard.putNumber("Hood Value", hoodEncoder);
        SmartDashboard.putString("Shoot All State", shooterStates.toString());
    }

    public void shootAll(double targetShooterRPM, double shooterRPMTolerance, double queuingBeltSpeed, boolean useGyro,
            double gyroAngleDesired, double gyroTolerance) {
        this.targetShooterRPM = targetShooterRPM;
        this.shooterRPMTolerance = shooterRPMTolerance;
        this.queuingBeltSpeed = queuingBeltSpeed;
        this.useGyro = useGyro;
        this.desiredGyroAngle = gyroAngleDesired;
        this.gyroTolerance = gyroTolerance;
        shooterBusy = true;
        SmartDashboard.putNumber("Shooter RPM Desired", targetShooterRPM);
        SmartDashboard.putNumber("Shooter RPM Tolerance Desired", shooterRPMTolerance);
        SmartDashboard.putNumber("Queuing Belt Speed", queuingBeltSpeed);
        SmartDashboard.putNumber("Gyro Tolerance", gyroTolerance);
        shooterStates = ShooterStates.PREPARE;
    }

    public void groundIntakeAll() {
        shooterStates = ShooterStates.GROUND_GET_HALF_BALL;
    }

    public void intakeAll(){
        shooterStates = ShooterStates.GET_HALF_BALL;
    }
    public boolean getShooterStatus() {
        return shooterBusy;
    }

    public void setHoodSetpoint(double hoodSetpoint){
        this.hoodSetpoint = hoodSetpoint;
    }

    public void abortIntake(){
        shooterStates = ShooterStates.INTAKE_DONE;
        counter = 0;
    }

    public void abortShooter(){
        shooterStates = ShooterStates.DONE;
        counter = 0;
    }
    
    public void reset() {
        shooterStates = ShooterStates.IDLE;
        ballQueuing.set(ControlMode.PercentOutput, 0);
        shooterLeft.set(0);
        // hood.setSelectedSensorPosition(0);
    }

    public void resetState() {
        shooterStates = ShooterStates.IDLE;
    }

    public void resetHood(){
        if(SensorInput.queuedHood){
            hoodEncoder = 0;
            hood.set(ControlMode.PercentOutput, 0);
        }else{
            hood.set(ControlMode.PercentOutput, 0.125);
        }
    }
}
