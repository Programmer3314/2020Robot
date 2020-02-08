/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.CANEncoder;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class Shooter {
    public enum ShooterStates {
        IDLE,
        PREPARE, 
        FIRE_BALL_AUTO, 
        DONE
    }


    double targetShooterRPM, shooterRPMTolerance;
    double queuingBeltSpeed;
    ShooterStates shooterStates;
    public CANSparkMax shooterLeft, shooterRight;
    public TalonSRX ballQueuing;
    public CANEncoder encoder;
    double lastEncoderVal;
    boolean shooterBusy;

    public Shooter(int CANMcshooterLeft, int CANMcshooterRight, int CANMcBallQueuing) {
        shooterStates = ShooterStates.IDLE;

        shooterLeft = new CANSparkMax(CANMcshooterLeft, CANSparkMaxLowLevel.MotorType.kBrushless);
        shooterRight = new CANSparkMax(CANMcshooterRight, CANSparkMaxLowLevel.MotorType.kBrushless);
        ballQueuing = new TalonSRX(CANMcBallQueuing);

        shooterLeft.restoreFactoryDefaults();
        shooterRight.restoreFactoryDefaults();
        ballQueuing.configFactoryDefault();

        shooterRight.follow(shooterLeft, true);

        shooterLeft.setSmartCurrentLimit(Constants.sparkShooterStallLimit, Constants.sparkShooterFreeLimit);

        shooterLeft.setIdleMode(IdleMode.kCoast);

        encoder = shooterLeft.getEncoder();

        encoder.setVelocityConversionFactor(Constants.sparkShooterVelocityConversionFactor);

        
    }

    public void update() {
        switch (shooterStates) {
            case IDLE:
                shooterBusy = false;
                break;

            case PREPARE:
                if(SensorInput.hasBall){
                    shooterBusy = true;
                    if (Math.abs(HumanInput.stick.getRawAxis(2) * 1.0 - targetShooterRPM) <= shooterRPMTolerance) {
                        shooterStates = ShooterStates.FIRE_BALL_AUTO;
                    }
                }else{
                    shooterStates = ShooterStates.DONE;
                }

                break;

            case FIRE_BALL_AUTO:
                if ((HumanInput.stick.getRawAxis(2) * 1.0 - targetShooterRPM) > shooterRPMTolerance) {
                    shooterStates = ShooterStates.PREPARE;
                }
                break;
            case DONE:
                shooterBusy = false;
                shooterStates = shooterStates.IDLE;
                break;
        }

        switch (shooterStates) {
            case IDLE:
                shooterLeft.set(0);
                break;
            case PREPARE:
                shooterLeft.set(HumanInput.throttle);
                ballQueuing.set(ControlMode.PercentOutput, 0);
                break;
            case FIRE_BALL_AUTO:
                shooterLeft.set(HumanInput.throttle);
                ballQueuing.set(ControlMode.PercentOutput, queuingBeltSpeed);
                break;
            case DONE:
                ballQueuing.set(ControlMode.PercentOutput, 0);

                break;
        }

        SmartDashboard.putNumber("Current RPM of the Shooter Motors", encoder.getVelocity());
    }

    public void shootAll() {
        targetShooterRPM = SmartDashboard.getNumber("Shooter RPM Desired", 0);
        shooterRPMTolerance = SmartDashboard.getNumber("Shooter RPM Tolerance Desired", 0);
        queuingBeltSpeed = SmartDashboard.getNumber("Queuing Belt Speed", 0.5);
        shooterBusy = true;
        SmartDashboard.putNumber("Shooter RPM Desired", targetShooterRPM);
        SmartDashboard.putNumber("Shooter RPM Tolerance Desired", shooterRPMTolerance);
        SmartDashboard.putNumber("Queuing Belt Speed", queuingBeltSpeed);
        shooterStates = ShooterStates.PREPARE;
    }

    public double getShooterRPMDesired() {
        return targetShooterRPM;
    }

    public double getShooterRPMToleranceDesired() {
        return shooterRPMTolerance;
    }

    public void setShooterRPMDesired() {
        targetShooterRPM = 0;
    }

    public void setShooterRPMToleranceDesired() {
        shooterRPMTolerance = 0;
    }

    public boolean getShooterStatus(){
        return shooterBusy;
    }
    public void reset() {
        shooterStates = ShooterStates.IDLE;
    }
}
