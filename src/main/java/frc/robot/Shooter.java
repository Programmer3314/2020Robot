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

/**
 * Add your docs here.
 */
public class Shooter {
    public enum ShooterStates {
        IDLE, GET_BALL, GOT_BALL, GAP_BALL,PREPARE, FIRE_BALL_AUTO, DONE
    }

    double targetShooterRPM, shooterRPMTolerance;
    double queuingBeltSpeed;
    ShooterStates shooterStates;
    public CANSparkMax shooterLeft, shooterRight;
    public TalonSRX ballQueuing, hood, indexer, intake;
    public CANEncoder shooterEncoder;
    int hoodEncoder, beltQueuingEncoder;
    double lastEncoderVal;
    boolean shooterBusy;
    boolean useGyro;
    double desiredGyroAngle;
    double gyroTolerance;
    int counter;
    CANPIDController shooterPidController;

    public Shooter(int CANMcshooterLeft, int CANMcshooterRight, int CANMcBallQueuing, int CANMcHood, int CANMcIndexer,
            int CANMcIntake) {
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

        shooterPidController = shooterLeft.getPIDController();
        shooterPidController.setP(Constants.shooterkP);
        shooterPidController.setI(Constants.shooterkI);
        shooterPidController.setIAccum(Constants.shooterkIMax);
        shooterPidController.setIZone(Constants.shooterkIz);
        shooterPidController.setD(Constants.shooterkD);
        shooterPidController.setFF(Constants.shooterkFF);
        shooterPidController.setOutputRange(Constants.shooterkMinOutput, Constants.shooterkMaxOutput);

        shooterRight.follow(shooterLeft, true);

        shooterLeft.setSmartCurrentLimit(Constants.sparkShooterStallLimit, Constants.sparkShooterFreeLimit);

        shooterLeft.setIdleMode(IdleMode.kCoast);
        hood.setNeutralMode(NeutralMode.Brake);
        ballQueuing.setNeutralMode(NeutralMode.Brake);
        indexer.setNeutralMode(NeutralMode.Brake);
        intake.setNeutralMode(NeutralMode.Brake);

        shooterEncoder = shooterLeft.getEncoder();
        hoodEncoder = hood.getSelectedSensorPosition();
        beltQueuingEncoder = ballQueuing.getSelectedSensorPosition();

        shooterEncoder.setVelocityConversionFactor(Constants.sparkShooterVelocityConversionFactor);

        counter = 0;
    }

    public void update() {
        hoodEncoder = hood.getSelectedSensorPosition();
        beltQueuingEncoder = ballQueuing.getSelectedSensorPosition();

        if (HumanInput.hoodUp) {
            hood.set(ControlMode.PercentOutput, -0.1);
        } else if (HumanInput.hoodDown) {
            hood.set(ControlMode.PercentOutput, 0.1);
        } else {
            hood.set(ControlMode.PercentOutput, 0);
        }
        if (HumanInput.spinBallQueue) {
            ballQueuing.set(ControlMode.PercentOutput, 0.5);
        } else if (HumanInput.reverseBallQueue) {
            ballQueuing.set(ControlMode.PercentOutput, -0.5);
        } else {
            ballQueuing.set(ControlMode.PercentOutput, 0);
        }

        switch (shooterStates) {
            case IDLE:
                shooterBusy = false;
                counter = 0;
                break;
            case GET_BALL:
                if(SensorInput.queuedTrack1){
                    shooterStates = ShooterStates.GOT_BALL;
                }
                // if(SensorInput.queuedTrack1){
                //     shooterStates = ShooterStates.BALL_QUEUING;
                // } else if(!SensorInput.queuedTrack1){
                //     intake.set(ControlMode.PercentOutput, 0.75);
                //     indexer.set(ControlMode.PercentOutput, 0.25);
                // }
                break;
            case GOT_BALL:
                if(SensorInput.queuedTrack2){
                    shooterStates = ShooterStates.GAP_BALL;
                }
                break;

            case GAP_BALL:
            break;
            case PREPARE:
                if (SensorInput.queuedShooter) {
                    shooterBusy = true;
                    if (Math.abs(shooterEncoder.getVelocity() - targetShooterRPM) <= shooterRPMTolerance) {
                        if (useGyro == false) {
                            shooterStates = ShooterStates.FIRE_BALL_AUTO;
                        } else if (Math.abs(Robot.cleanGyro - desiredGyroAngle) <= gyroTolerance) {
                            shooterStates = ShooterStates.FIRE_BALL_AUTO;
                        }
                    }
                } else if (counter >= 200) {
                    shooterStates = ShooterStates.DONE;
                }
                counter++;

                break;

            case FIRE_BALL_AUTO:
                if (Math.abs(shooterEncoder.getVelocity() - targetShooterRPM) > shooterRPMTolerance) {
                    shooterStates = ShooterStates.PREPARE;
                }
                counter = 0;
                break;

            case DONE:
                shooterBusy = false;
                shooterStates = ShooterStates.IDLE;
                break;
        }



        switch (shooterStates) {
            case IDLE:
                shooterLeft.set(0);

                break;
            case GET_BALL:
                HumanInput.ejectIntake = true;

                intake.set(ControlMode.PercentOutput,0.75);
                indexer.set(ControlMode.PercentOutput,0.25);

                // if(SensorInput.queuedTrack1){
                //     intake.set(ControlMode.PercentOutput, 0);
                //     indexer.set(ControlMode.PercentOutput, 0);
                //     //shooterStates = ShooterStates.BALL_QUEUING;
                // } else if(!SensorInput.queuedTrack1){
                //     intake.set(ControlMode.PercentOutput, 0.75);
                //     indexer.set(ControlMode.PercentOutput, 0.25);
                // } 
                
                break;
            case GOT_BALL:
                intake.set(ControlMode.PercentOutput,0);
                indexer.set(ControlMode.PercentOutput,0);
                if (SensorInput.queuedShooter){
                    ballQueuing.set(ControlMode.PercentOutput, 0);
                    shooterStates = ShooterStates.PREPARE;
                }

                if (SensorInput.queuedTrack2){
                    ballQueuing.set(ControlMode.PercentOutput, 0);
                    shooterStates = ShooterStates.GET_BALL;
                } else if(!SensorInput.queuedTrack2){
                    ballQueuing.set(ControlMode.PercentOutput, 0.5);
                }

                break;
            case GAP_BALL:
            break;
            case PREPARE:
                // shooterLeft.set(-HumanInput.throttle);
                shooterPidController.setReference(targetShooterRPM, ControlType.kVelocity);

                // if (SensorInput.hasBall) {
                //     ballQueuing.set(ControlMode.PercentOutput, 0);
                // } else {
                //     ballQueuing.set(ControlMode.PercentOutput, 0.5);
                // }

                break;
            case FIRE_BALL_AUTO:
                // shooterLeft.set(-HumanInput.throttle);
                shooterPidController.setReference(targetShooterRPM, ControlType.kVelocity);
                ballQueuing.set(ControlMode.PercentOutput, queuingBeltSpeed);
                break;
            case DONE:

                break;
        }

        SmartDashboard.putNumber("Current RPM of the Shooter Motors", shooterEncoder.getVelocity());
        SmartDashboard.putNumber("Belt Queue Value", beltQueuingEncoder);
        SmartDashboard.putNumber("Hood value", hoodEncoder);
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

    public void intakeAll(){
        shooterStates = ShooterStates.GET_BALL;
    }

    public boolean getShooterStatus() {
        return shooterBusy;
    }

    public void reset() {
        shooterStates = ShooterStates.IDLE;
        ballQueuing.set(ControlMode.PercentOutput, 0);
        shooterLeft.set(0);
        hood.setSelectedSensorPosition(0);
    }

    public void resetState() {
        shooterStates = ShooterStates.IDLE;
    }
}
