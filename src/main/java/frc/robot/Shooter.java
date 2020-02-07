/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

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

    double shooterRPM, shooterRPMTolerance;
    ShooterStates shooterStates;
    public CANSparkMax shooterLeft, shooterRight;
    public CANEncoder encoder;
    double lastEncoderVal;

    public Shooter(int CANMcshooterLeft, int CANMcshooterRight) {
        shooterStates = ShooterStates.IDLE;

        shooterLeft = new CANSparkMax(CANMcshooterLeft, CANSparkMaxLowLevel.MotorType.kBrushless);
        shooterRight = new CANSparkMax(CANMcshooterRight, CANSparkMaxLowLevel.MotorType.kBrushless);

        shooterLeft.restoreFactoryDefaults();
        shooterRight.restoreFactoryDefaults();

        shooterRight.follow(shooterLeft, true);

        shooterLeft.setSmartCurrentLimit(Constants.sparkShooterStallLimit, Constants.sparkShooterFreeLimit);

        shooterLeft.setIdleMode(IdleMode.kCoast);

        encoder = shooterLeft.getEncoder();

        encoder.setVelocityConversionFactor(Constants.sparkShooterVelocityConversionFactor);

    }

    public void update() {
        switch (shooterStates) {
        case IDLE:

            break;
        case PREPARE:
            if (Math.abs(HumanInput.stick.getRawAxis(2) * 1.0 - shooterRPM) <= shooterRPMTolerance) {
                shooterStates = ShooterStates.FIRE_BALL_AUTO;
            }

            break;
        case FIRE_BALL_AUTO:
            if ((HumanInput.stick.getRawAxis(2) * 1.0 - shooterRPM) > shooterRPMTolerance) {
                shooterStates = ShooterStates.PREPARE;
            }
            break;
        case DONE:
            break;
        }

        switch (shooterStates) {
        case IDLE:
            shooterLeft.set(0);
            break;
        case PREPARE:
            shooterLeft.set(HumanInput.throttle);
            break;
        case FIRE_BALL_AUTO:
            shooterLeft.set(HumanInput.throttle);
            break;  
        case DONE:
            
            break;
        }

        SmartDashboard.putNumber("Current RPM of the Shooter Motors", encoder.getVelocity());
    }

    public void activate() {
        SmartDashboard.putNumber("Shooter RPM Desired", shooterRPM);
        SmartDashboard.putNumber("Shooter RPM Tolerance Desired", shooterRPMTolerance);
        shooterStates = ShooterStates.PREPARE;
    }

    public double getShooterRPMDesired(){
        return shooterRPM;
    }
    
    public double getShooterRPMToleranceDesired(){
        return shooterRPMTolerance;
    }

    public void setShooterRPMDesired(){
        shooterRPM = 0;
    }
    
    public void setShooterRPMToleranceDesired(){
        shooterRPMTolerance = 0;
    }


    public void reset(){
        shooterStates = ShooterStates.IDLE;
    }
}
