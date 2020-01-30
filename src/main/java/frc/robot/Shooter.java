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

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class Shooter {
    public CANEncoder encoder;
    double lastEncoderVal;
    public CANSparkMax shooterLeft, shooterRight;
    NetworkTable ntShooter;
    NetworkTableEntry ntShooterTargetRate;
    NetworkTableEntry ntShooterVelocity;
    NetworkTableEntry ntShooterMin;
    boolean useFixedSpeed;
    double shooterRate;
    double velocity;
    double shootSpeed;

    public Shooter(int CANMcshooterLeft, int CANMcshooterRight) {
        velocity = -999999;

        shooterLeft = new CANSparkMax(CANMcshooterLeft, CANSparkMaxLowLevel.MotorType.kBrushless);
        shooterRight = new CANSparkMax(CANMcshooterRight, CANSparkMaxLowLevel.MotorType.kBrushless);

        shooterLeft.restoreFactoryDefaults();
        shooterRight.restoreFactoryDefaults();

        shooterLeft.setSmartCurrentLimit(Constants.sparkShooterStallLimit, Constants.sparkShooterFreeLimit);
        shooterRight.setSmartCurrentLimit(Constants.sparkShooterStallLimit, Constants.sparkShooterFreeLimit);

        shooterLeft.setIdleMode(IdleMode.kCoast);
        shooterRight.setIdleMode(IdleMode.kCoast);

        encoder = shooterLeft.getEncoder();
        
        encoder.setVelocityConversionFactor(Constants.sparkShooterVelocityConversionFactor);

        
    }

    public void update() {
        ntShooterMin = Robot.ntInst.getEntry("Shooter/Min Velocity");
        ntShooterVelocity = Robot.ntInst.getEntry("Shooter/Cur Velocity");
        ntShooterTargetRate = Robot.ntInst.getEntry("Shooter/Target Rate");

        
        shooterRate = ntShooterTargetRate.getDouble(0);
        
        shootSpeed = 0;

            //TODO: Change to use HumanInput (throughout class)
            //TODO: Eliminate the need for xboxController parameter
            double throttleSpeed = HumanInput.operatorController.getRawAxis(2)*1.0;

            if(HumanInput.operatorController.getRawButton(8)) {  
                shootSpeed= throttleSpeed;
            }

            SmartDashboard.putNumber("echoShootSpeed", shootSpeed);
            SmartDashboard.putNumber("echoThrottleSpeed", throttleSpeed);
            //shooterPidController.setReference(manualSpeed, ControlType.kVelocity);
            shooterLeft.set(shootSpeed);
            shooterRight.set(-shootSpeed);
          
      
          if(HumanInput.operatorController.getRawButton(7)){
            double v = encoder.getVelocity();
            if(v > velocity){
              velocity = v;
              SmartDashboard.putNumber("Minimum Velocity", (velocity));
            }   
          }
          if(HumanInput.operatorController.getRawButton(5)){
            velocity = -999999;
          }
          SmartDashboard.putNumber("Current Velocity", (encoder.getVelocity()));
          SmartDashboard.updateValues();    
    }
}
