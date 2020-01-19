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

        shooterLeft.setSmartCurrentLimit(40, 40);
        shooterRight.setSmartCurrentLimit(40, 40);

        shooterLeft.setIdleMode(IdleMode.kCoast);
        shooterRight.setIdleMode(IdleMode.kCoast);

        encoder = shooterLeft.getEncoder();
        encoder.setVelocityConversionFactor(1.0);

        
    }

    public void update(Joystick stick, Joystick xboxController) {
        ntShooterMin = Robot.ntInst.getEntry("Shooter/Min Velocity");
        ntShooterVelocity = Robot.ntInst.getEntry("Shooter/Cur Velocity");
        ntShooterTargetRate = Robot.ntInst.getEntry("Shooter/Target Rate");

        
        shooterRate = ntShooterTargetRate.getDouble(0);
        
        shootSpeed = 0;

            double throttleSpeed = stick.getRawAxis(2)*1.0;
            double stickSpeed = stick.getRawAxis(1)*1.0;

            if(stick.getRawButton(10)) {  
                shootSpeed= throttleSpeed;
            }else if(stick.getRawButton(1)){
                shootSpeed = stickSpeed;
            }

            SmartDashboard.putNumber("echoStickSpeed", stickSpeed);
            SmartDashboard.putNumber("echoShootSpeed", shootSpeed);
            SmartDashboard.putNumber("echoThrottleSpeed", throttleSpeed);
            //shooterPidController.setReference(manualSpeed, ControlType.kVelocity);
            shooterLeft.set(shootSpeed);
            shooterRight.set(-shootSpeed);
          
      
          if(xboxController.getRawButton(3)){
            double v = encoder.getVelocity();
            if(v > velocity){
              velocity = v;
              SmartDashboard.putNumber("Minimum Velocity", (velocity));
            }   
          }
          if(xboxController.getRawButton(4)){
            velocity = -999999;
          }
          SmartDashboard.putNumber("Current Velocity", (encoder.getVelocity()));
          SmartDashboard.updateValues();    
    }
}
