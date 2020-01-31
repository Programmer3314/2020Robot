/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SPI;

public abstract class MyRobot extends AllRobots {
    IDriveTrain drivetrain;
    Shooter shooter;
    TalonFXTest fxTest;
    DriveController driveController;
    ControlPanel controlPanel;

    public static AHRS navx;
    public static double rawGyro, cleanGyro;
    public boolean isFalcon, hasShooter, hasControlPanel, isTalonFXTest;

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
            // if (controllerCheck==null) {
            //     SmartDashboard.putString("Controller Check2","IS NULL");
            // }
            ControllerVersion = controllerCheck.getFirmwareString();
            SmartDashboard.putString("Controller Check 4", ControllerVersion);
            controllerCheck.close();
        }
        catch(Exception cc) {
            SmartDashboard.putString("Controller Check 3","CATCH");
        }
        if(ControllerVersion.equalsIgnoreCase("v0.0.0")) {
            isFalcon = true;
            hasShooter = true;
            isTalonFXTest = false;
            hasControlPanel = false;
        }
        else {
            isFalcon = false;
            hasShooter = false;
            isTalonFXTest = true;
            hasControlPanel = true;
        }

        if(isFalcon){
            drivetrain = new DrivetrainFalcon(CANMcFalconFrontLeft, CANMcFalconBackLeft, CANMcFalconFrontRight, CANMcFalconBackRight);
            SmartDashboard.putString("DriveTrain Type:", "Falcons");
            Constants.maxCorrection = 0.4;
            Constants.minCorrection = 0.04;
        }else{
            drivetrain = new DrivetrainNEO(CANMcleftDriveFront, CANMcleftDriveMiddle, CANMcleftDriveBack, CANMcrightDriveFront, CANMcrightDriveMiddle, CANMcrightDriveBack);
            SmartDashboard.putString("DriveTrain Type:", "Neos");
            Constants.maxCorrection = 0.2;
            Constants.minCorrection = 0.04;
        }
        
        RechargeRobotInit();
    }

    @Override
    public void MyAutonomousInit() {
        navx.reset();
        RechargeAutonomousInit();
    }

    @Override
    public void MyAutonomousPeriodic() {
        periodicInit();
        RechargeAutonomousPeriodic();
    }

    @Override
    public void MyTeleopInit() {
        navx.reset();
        RechargeTeleopInit();
    }

    @Override
    public void MyTeleopPeriodic() {
        periodicInit();

        //SmartDashboard.putBoolean("useFixedSpeed", shooter.useFixedSpeed);
        RechargeTeleopPeriodic();
    }

    @Override
    public void MyTestInit() {
        navx.reset();
        RechargeTestInit();
    }

    @Override
    public void MyTestPeriodic() {
        periodicInit();

        RechargeTestPeriodic();
    }

    private void periodicInit(){
        rawGyro = navx.getAngle();
        cleanGyro = (rawGyro + 180 * Math.signum(rawGyro)) % 360 - 180 * Math.signum(rawGyro);
        SmartDashboard.putNumber("Gyro value:", Robot.cleanGyro);
    }

    public abstract void RechargeRobotInit();

    public abstract void RechargeAutonomousInit();

    public abstract void RechargeAutonomousPeriodic();

    public abstract void RechargeTeleopInit();

    public abstract void RechargeTeleopPeriodic();

    public abstract void RechargeTestInit();

    public abstract void RechargeTestPeriodic();
}