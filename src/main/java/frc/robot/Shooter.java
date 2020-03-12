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

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.DriveController.MoveParameters;

/**
 * Add your docs here.
 */
public class Shooter {
    public enum ShooterStates {
        IDLE, GET_HALF_BALL, //GET_BALL, // Not Used 
        GOT_BALL, GAP_BALL, EXTRA_BALL, GROUND_GET_HALF_BALL, GROUND_GOT_BALL, GROUND_GAP_BALL, 
        GROUND_EXTRA_BALL, INTAKE_DONE, INTAKE_DONE2, PREPARE_ONLY, PREPARE, DELAY, FIRE_BALL_AUTO, DONE
    }

    public CANSparkMax shooterLeft, shooterRight;
    public TalonSRX ballQueuing, hood, indexer, intake;
    public CANEncoder shooterEncoder;

    CANPIDController shooterPidController;
    PDController hoodPDController;

    double targetShooterRPM, shooterRPMTolerance;
    double queuingBeltSpeed;
    ShooterStates currentState, nextState;
    public double hoodSetpoint;
    int hoodEncoder, beltQueuingEncoder;
    double lastEncoderVal;
    boolean shooterBusy;
    boolean homedHood;
    boolean useGyro;
    double desiredGyroAngle;
    double gyroTolerance;
    int counter, autoCounter, counterv2;
    boolean abortShooter;
    double intakeMotorSpeed = 1.00;//0.75;
    final boolean pullIntakeInBetweenBalls = false;
    int cyclesInState;

    public Shooter(int CANMcshooterLeft, int CANMcshooterRight, int CANMcBallQueuing, 
        int CANMcHood, int CANMcIndexer, int CANMcIntake) {

        currentState = ShooterStates.IDLE;
        nextState = currentState;

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

        counterv2 = 0;
        counter = 0;
        cyclesInState = 0;
    }

    public void update(MoveParameters mP) {
        // if(SensorInput.queuedHood){
        //     HumanInput.driverController.setRumble(RumbleType.kLeftRumble, 0);
        // } else {
        //     HumanInput.driverController.setRumble(RumbleType.kLeftRumble, 1);
        // }

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
        switch (currentState) {
            case IDLE:
            shooterLeft.set(0);
            intake.set(ControlMode.PercentOutput, 0);
            indexer.set(ControlMode.PercentOutput, 0);
            //ballQueuing.set(ControlMode.PercentOutput, 0);
            break;

            case GET_HALF_BALL:
            //Solenoids.ejectIntake(true); 
            intake.set(ControlMode.PercentOutput, -0.5);
            indexer.set(ControlMode.PercentOutput, 0.5);
            ballQueuing.set(ControlMode.PercentOutput, 0.0);
            Solenoids.ejectIntake(false);
            break;

            // case GET_BALL:
            // intake.set(ControlMode.PercentOutput, 0.0);
            // indexer.set(ControlMode.PercentOutput, 0.5);
            // // Solenoids.ejectIntake(false);
            // break;

            case GOT_BALL:
            //Solenoids.ejectIntake(true); 
            intake.set(ControlMode.PercentOutput, 0);
            indexer.set(ControlMode.PercentOutput, 0.5);
            ballQueuing.set(ControlMode.PercentOutput, 0.5);
            break;

            case GAP_BALL:
            intake.set(ControlMode.PercentOutput, 0);
            indexer.set(ControlMode.PercentOutput, 0);
            ballQueuing.set(ControlMode.PercentOutput, 0.5);
            break;

            case EXTRA_BALL:
            intake.set(ControlMode.PercentOutput, -0.5);
            indexer.set(ControlMode.PercentOutput, 0);
            ballQueuing.set(ControlMode.PercentOutput, 0.0);
            break;

            case GROUND_GET_HALF_BALL:
            Solenoids.ejectIntake(true); 
            intake.set(ControlMode.PercentOutput, intakeMotorSpeed);
            indexer.set(ControlMode.PercentOutput, 0.5);
            ballQueuing.set(ControlMode.PercentOutput, 0.0);
            break;
    
            case GROUND_GOT_BALL:
            if(pullIntakeInBetweenBalls){
                Solenoids.ejectIntake(false); 
            }
            intake.set(ControlMode.PercentOutput, intakeMotorSpeed/*0.35*/);
            indexer.set(ControlMode.PercentOutput, 0.5);
            ballQueuing.set(ControlMode.PercentOutput, 0.0); // 0.5);
            break;

            case GROUND_GAP_BALL:
            Solenoids.ejectIntake(true);
            intake.set(ControlMode.PercentOutput, intakeMotorSpeed);
            indexer.set(ControlMode.PercentOutput, 0);
            ballQueuing.set(ControlMode.PercentOutput, 0.5); 
            break;

            case GROUND_EXTRA_BALL:
            Solenoids.ejectIntake(true);
            intake.set(ControlMode.PercentOutput, intakeMotorSpeed);
            indexer.set(ControlMode.PercentOutput, 0);
            ballQueuing.set(ControlMode.PercentOutput, 0.0);
            break;
            
            case INTAKE_DONE:
            intake.set(ControlMode.PercentOutput, 0); // intakeMotorSpeed);
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

            case PREPARE_ONLY:
                shooterPidController.setReference(targetShooterRPM, ControlType.kVelocity);
                setHoodSetpoint(hoodSetpoint);
            break;

            case PREPARE:
                // shooterLeft.set(-HumanInput.throttle);
                shooterPidController.setReference(targetShooterRPM, ControlType.kVelocity);

                if (SensorInput.queuedShooter) {
                    ballQueuing.set(ControlMode.PercentOutput, 0);
                } else {
                    ballQueuing.set(ControlMode.PercentOutput, 0.9/*1.0*//*0.5*/);
                }


                if(!SensorInput.queuedTrack1 && !SensorInput.queuedTrack2) {
                    indexer.set(ControlMode.PercentOutput, 0.5);
                } else {
                    indexer.set(ControlMode.PercentOutput, 0);
                }
                // not sure why we'd run this here
                //intake.set(ControlMode.PercentOutput, 0.25);
                intake.set(ControlMode.PercentOutput, 0);

               
                break;

            case DELAY:
            break;

            case FIRE_BALL_AUTO:
                // shooterLeft.set(-HumanInput.throttle);
                shooterPidController.setReference(targetShooterRPM, ControlType.kVelocity);
                ballQueuing.set(ControlMode.PercentOutput, 0.9/*queuingBeltSpeed*/);

                if(!SensorInput.queuedTrack1 && !SensorInput.queuedTrack2) {
                    indexer.set(ControlMode.PercentOutput, 0.5);
                } else {
                    indexer.set(ControlMode.PercentOutput, 0);
                }
                    // not sure why we'd run this here
                    //intake.set(ControlMode.PercentOutput, 0.25);
                    intake.set(ControlMode.PercentOutput, 0);
                break;

            case DONE:
                homedHood = false;
                hoodSetpoint = 0;
                targetShooterRPM = 2100;
                //Robot.targetShooterRPM = 2100;
                break;
        }

        // Calc State Changes...
        // Calc State Changes...
        // Calc State Changes...
        nextState = currentState;
        switch (currentState) {
            case IDLE:
                abortShooter = false;
                shooterBusy = false;
                counter = 0;
                counterv2 = 0;
                break;

            case GET_HALF_BALL:
                if(SensorInput.queuedTrack1){
                    nextState = ShooterStates.GOT_BALL;
                }
                break;

            // case GET_BALL:
            //     if (SensorInput.queuedTrack1){
            //         nextState = ShooterStates.GOT_BALL;
            //     }
            //     if (SensorInput.queuedShooter) {
            //         nextState = ShooterStates.INTAKE_DONE;
            //         counter = 0;
            //     }
            //     break;
            case GOT_BALL:
                if (SensorInput.queuedTrack2) {
                    nextState = ShooterStates.GAP_BALL;
                }

                if (SensorInput.queuedShooter) {
                    nextState = ShooterStates.INTAKE_DONE;
                    counter = 0;
                }

                break;
            case GAP_BALL:
                if (!SensorInput.queuedTrack2) {
                    nextState = ShooterStates.GET_HALF_BALL;
                }
            
                // TODO: Please add an "extra ball" state like in Ground Intake
                // The new state should pull the ball down vertically, 
                // by running the intake motor (reverse of normal, same as above)
                // but none of the others, so that the ball gets pulled in, but not 
                // indexed.
                // if (SensorInput.queuedShooter) {
                //     nextState = ShooterStates.INTAKE_DONE;
                //     counter = 0;
                // }

                if (SensorInput.queuedShooter) {
                    nextState = ShooterStates.EXTRA_BALL;
                    counter = 0;
                }
    
                break;

            case EXTRA_BALL:
                //have to abort to get out of this state
            break;

            case GROUND_GET_HALF_BALL:
                if(SensorInput.queuedIntake && counter >= 5/*25*/){
                    nextState = ShooterStates.GROUND_GOT_BALL;
                    counter = 0;
                }else if(SensorInput.queuedIntake){
                    counter++;
                }else{
                    counter = 0;
                }

                if(SensorInput.queuedTrack1){
                    nextState = ShooterStates.GROUND_GAP_BALL;
                }
                break;
            case GROUND_GOT_BALL:
                if (SensorInput.queuedTrack1) {
                    nextState = ShooterStates.GROUND_GAP_BALL;
                }

                if (SensorInput.queuedShooter) {
                    nextState = ShooterStates.INTAKE_DONE;
                    counter = 0;
                }
                break;
            case GROUND_GAP_BALL:
                if (!SensorInput.queuedTrack1 && !SensorInput.queuedTrack2) {
                    nextState = ShooterStates.GROUND_GET_HALF_BALL;
                }

                if (SensorInput.queuedShooter) {
                    nextState = ShooterStates.GROUND_EXTRA_BALL;
                    counter = 0;
                }
                break;

            case GROUND_EXTRA_BALL:
                //only way out of this is to abort intake
                break;

            case INTAKE_DONE:
                //shooterStates = ShooterStates.IDLE;
                counter++;
                if(counter >= 150){
                    nextState = ShooterStates.INTAKE_DONE2;
                }
                break;

            case INTAKE_DONE2:
                nextState = ShooterStates.IDLE;
                break;

            case PREPARE_ONLY:
                if(abortShooter){
                    nextState = ShooterStates.DONE;
                }
                break;

            case PREPARE:
                if (SensorInput.queuedShooter) {
                    shooterBusy = true;
                    if(shooterEncoder.getVelocity() < 100){
                        HumanInput.operatorController.setRumble(RumbleType.kLeftRumble, 1);
                    } else {
                        HumanInput.operatorController.setRumble(RumbleType.kLeftRumble, 0);
                    }

                    // Confirm Firing Solution
                    // (consolidated conditions)

                    if ((Math.abs(shooterEncoder.getVelocity() - targetShooterRPM) <= shooterRPMTolerance) 
                        && (Math.abs(hoodEncoder - hoodSetpoint) <= Constants.hoodkTolerance) 
                        && (useGyro == false || (Math.abs(Robot.cleanGyro - DriveController.angleOffset) <= gyroTolerance))) {
                        //&& (useGyro == false || (Math.abs(Robot.cleanGyro - desiredGyroAngle) <= gyroTolerance))) {
                                counter = 0;
                            nextState = ShooterStates.FIRE_BALL_AUTO;
                    }
                } else if (counter >= 200) {
                    nextState = ShooterStates.DONE;
                }
                counter++;
    
                // if(autoCounter == 0){
                //     abortShooter = true;
                // }

                // if(autoCounter == 2){
                //     nextState = ShooterStates.DELAY;
                // }

                // TODO: Please go to a delay state before aborting shooter. 
                // This will prevent the last ball in the sequence from 
                // getting "half shot".
                // It takes about a half second (@3600 rpm) for the ball 
                // to move through the shooter. For about half of this (a little less)
                // the ball is blocking the sensor and the state remains in FIRE_BALL_AUTO.
                // If we hitting our max ball (autoCounter==0), then we should wait
                // about 15 cycles before actually shutting down the shooter. 
                // So please make a delay state that this will transfer to which will 
                // then (after 15 cycles) do the abort. 

                if(abortShooter){
                    nextState = ShooterStates.DONE;
                    counter = 0;
                    counterv2 = 0;
                    autoCounter = 5;
                    abortShooter = false;
                } else if(autoCounter == 0){
                    nextState = ShooterStates.DELAY;
                }
                break;

            case DELAY:
                counterv2++;
                if(counterv2 >= 15){
                    abortShooter = true;
                    nextState = ShooterStates.PREPARE;
                }
            break;
    
            case FIRE_BALL_AUTO:
                if(!SensorInput.queuedShooter){
                    autoCounter--;
                    nextState = ShooterStates.PREPARE;
                }
                break;
    
            case DONE:
                HumanInput.operatorController.setRumble(RumbleType.kLeftRumble, 0);
                shooterBusy = false;
                nextState = ShooterStates.IDLE;
                break;
            }

            cyclesInState++;
            if (currentState!=nextState) {
                cyclesInState = 0;
            }

            currentState = nextState;




        Solenoids.confirmShooterLightRing(SensorInput.queuedShooter);
    
        SmartDashboard.putNumber("Current RPM of the Shooter Motors", shooterEncoder.getVelocity());
        SmartDashboard.putNumber("Belt Queue Value", beltQueuingEncoder);
        SmartDashboard.putNumber("Hood Value", hoodEncoder);
        SmartDashboard.putString("Shoot All State", currentState.toString());
    }

    public void shootAll(/*double targetShooterRPM, double shooterRPMTolerance,*/ double queuingBeltSpeed, boolean useGyro,
        double gyroAngleDesired, double gyroTolerance) {
        // Now that the params don't exist. These lines do nothing. 
        // this.targetShooterRPM = targetShooterRPM;
        // this.shooterRPMTolerance = shooterRPMTolerance;
        this.queuingBeltSpeed = queuingBeltSpeed;
        this.useGyro = useGyro;
        this.desiredGyroAngle = gyroAngleDesired;
        this.gyroTolerance = gyroTolerance;
        shooterBusy = true;
        SmartDashboard.putNumber("Shooter RPM Desired", targetShooterRPM);
        SmartDashboard.putNumber("Shooter RPM Tolerance Desired", shooterRPMTolerance);
        SmartDashboard.putNumber("Queuing Belt Speed", queuingBeltSpeed);
        SmartDashboard.putNumber("Gyro Tolerance", gyroTolerance);
        currentState = ShooterStates.PREPARE;
    }
    // public void setFiringSolution(double targetShooterRPM, double shooterRPMTolerance){
    //     this.targetShooterRPM = targetShooterRPM;
    //     this.shooterRPMTolerance = shooterRPMTolerance;
    // }

    public void setTargetShooterRPM(double targetShooterRPM){
        this.targetShooterRPM = targetShooterRPM;
    }

    public void setTargetShooterRPMTolerance(double shooterRPMTolerance){
        this.shooterRPMTolerance = shooterRPMTolerance;
    }

    public double getTargetShooterRPM(){
        return targetShooterRPM;
    }

    public double getTargetShooterRPMTolerance(){
        return shooterRPMTolerance;
    }
    public void groundIntakeAll() {
        currentState = ShooterStates.GROUND_GET_HALF_BALL;
    }

    public void intakeAll(){
        if(SensorInput.queuedShooter){
            currentState = ShooterStates.EXTRA_BALL;
        } else {
            currentState = ShooterStates.GET_HALF_BALL;
        }
    }
    public boolean getShooterStatus() {
        return shooterBusy;
    }

    public void setHoodSetpoint(double hoodSetpoint){
        this.hoodSetpoint = hoodSetpoint;
    }

    public void abortIntake(){
        currentState = ShooterStates.INTAKE_DONE;
        counter = 0;
    }

    public void abortShooter(){
        // shooterStates = ShooterStates.DONE;
        // counter = 0;
        abortShooter = true;
    }
    
    public void prepareShooter(){
        currentState = ShooterStates.PREPARE_ONLY;
    }

    public void reverseIntake(){
        Solenoids.ejectIntake(true);
        intake.set(ControlMode.PercentOutput, -1.0);
    }

    public void reverseIntakeRelease(){
        Solenoids.ejectIntake(false);
        intake.set(ControlMode.PercentOutput, 0);
    }

    public void reverseAll(){
        reverseIntake();
        ballQueuing.set(ControlMode.PercentOutput, -0.75);
        indexer.set(ControlMode.PercentOutput, -0.375);
    }

    public void reverseAllRelease(){
        reverseIntakeRelease();
        ballQueuing.set(ControlMode.PercentOutput, 0);
        indexer.set(ControlMode.PercentOutput, 0);
    }

    public void reset() {
        currentState = ShooterStates.IDLE;
        ballQueuing.set(ControlMode.PercentOutput, 0);
        shooterLeft.set(0);
        // hood.setSelectedSensorPosition(0);
    }

    public void resetState() {
        currentState = ShooterStates.IDLE;
    }
 
    public void resetHood(){
        if(SensorInput.queuedHood){
            hoodEncoder = 0;
            hood.set(ControlMode.PercentOutput, 0);
        }else{
            hood.set(ControlMode.PercentOutput, 0.5);
        }
    }

    public void LogHeader() {
        Logger.Header("targetShooterRPM,shooterRPMTolerance,queuingBeltSpeed,"
            + "currentState,nextState,"
            + "hoodSetpoint,hoodEncoder,beltQueuingEncoder,lastEncoderVal,shooterBusy,"
            + "homedHood,useGyro,desiredGyroAngle,gyroTolerance,counter,autoCounter,"
            + "abortShooter,intakeMotorSpeed,pullIntakeInBetweenBalls,shooterRPMDiff,"
        );
    }

    public void LogData() {
        Logger.doubles(targetShooterRPM, shooterRPMTolerance,queuingBeltSpeed);
        Logger.singleEnum(currentState);
        Logger.singleEnum(nextState);
        Logger.doubles(hoodSetpoint,hoodEncoder, beltQueuingEncoder,lastEncoderVal);
        Logger.booleans(shooterBusy,homedHood,useGyro);
        Logger.doubles(desiredGyroAngle,gyroTolerance,counter, autoCounter);
        Logger.booleans(abortShooter);
        Logger.doubles(intakeMotorSpeed);
        Logger.booleans(pullIntakeInBetweenBalls);
        Logger.doubles(Math.abs(shooterEncoder.getVelocity() - targetShooterRPM));
    }
}
