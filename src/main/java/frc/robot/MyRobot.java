/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;
import com.revrobotics.SparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj.SPI;

public abstract class MyRobot extends AllRobots {
    IDriveTrain drivetrain;
    //Shooter shooter;

    public static AHRS navx;
    public static double gyro;
    public boolean isFalcon, isShooter, isControlPanel, isTalonFXTest;

    //Sprak Max CAN IDs NEO
    final int CANMcleftDriveFront = 1;
    final int CANMcleftDriveMiddle = 2;
    final int CANMcleftDriveBack = 3;
    final int CANMcrightDriveFront = 4;
    final int CANMcrightDriveMiddle = 5;
    final int CANMcrightDriveBack = 6;
    final int CANMcshooterLeft = 7;
    final int CANMcshooterRight = 8;

    //Talon FX CAN IDs Falcons
    final int CANMcFalconFrontLeft = 1;
    final int CANMcFalconBackLeft = 2;
    final int CANMcFalconFrontRight = 3;
    final int CANMcFalconBackRight = 4;
    
    //Talon SRX CAN IDs Bag Motors
    final int CANMcctrlPanel = 31;

    @Override
    public void MyRobotInit() {
        
        navx = new AHRS(SPI.Port.kMXP);
        navx.reset();

        String ControllerVersion="";

        try {
            CANSparkMax controllerCheck=new CANSparkMax(5, MotorType.kBrushless);
            SmartDashboard.putString("Controller Check1","Passed");
            if (controllerCheck==null) {
                SmartDashboard.putString("Controller Check2","IS NULL");
            }
            ControllerVersion = controllerCheck.getFirmwareString();
            SmartDashboard.putString("Controller Check 4", ControllerVersion);
            controllerCheck.close();
        }
        catch(Exception cc) {
            SmartDashboard.putString("Controller Check 3","CATCH");
        }
        if(ControllerVersion.equalsIgnoreCase("v0.0.0")) {
            isFalcon = false;
            isShooter = false;
            isTalonFXTest = true;
            isControlPanel = false;
        }
        else {
            isFalcon = true;
            isShooter = true;
            isTalonFXTest = false;
            isControlPanel = true;
        }

        if(isFalcon){
            drivetrain = new DrivetrainFalcon(CANMcFalconFrontLeft, CANMcFalconBackLeft, CANMcFalconFrontRight, CANMcFalconBackRight);
            SmartDashboard.putString("DriveTrain Type:", "Falcons");
        }else{
            drivetrain = new DrivetrainNEO(CANMcleftDriveFront, CANMcleftDriveMiddle, CANMcleftDriveBack, CANMcrightDriveFront, CANMcrightDriveMiddle, CANMcrightDriveBack);
            SmartDashboard.putString("DriveTrain Type:", "Neos");
        }
        
        RechargeRobotInit();
    }

    @Override
    public void MyAutonomousInit() {
        RechargeAutonomousInit();
    }

    @Override
    public void MyAutonomousPeriodic() {
        gyro = navx.getAngle();
        RechargeAutonomousPeriodic();
    }

    @Override
    public void MyTeleopInit() {
        RechargeTeleopInit();
    }

    @Override
    public void MyTeleopPeriodic() {
        gyro = navx.getAngle();

        //SmartDashboard.putBoolean("useFixedSpeed", shooter.useFixedSpeed);
        RechargeTeleopPeriodic();
    }

    @Override
    public void MyTestInit() {
        RechargeTestInit();
    }

    @Override
    public void MyTestPeriodic() {
        gyro = navx.getAngle();
        RechargeTestPeriodic();
    }

    public abstract void RechargeRobotInit();

    public abstract void RechargeAutonomousInit();

    public abstract void RechargeAutonomousPeriodic();

    public abstract void RechargeTeleopInit();

    public abstract void RechargeTeleopPeriodic();

    public abstract void RechargeTestInit();

    public abstract void RechargeTestPeriodic();
}