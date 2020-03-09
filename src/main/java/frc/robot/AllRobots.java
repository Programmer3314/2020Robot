/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;

public abstract class AllRobots extends TimedRobot {
    public static NetworkTableInstance ntInst;
    public AutoStateMachines auto;
    public static final double degtorad = Math.PI /180;
    public static final double radtodeg = 180/Math.PI;
    public static DriverStation driverStation;

    @Override
    public void robotInit() {
        ntInst = NetworkTableInstance.getDefault();
        driverStation = DriverStation.getInstance();
        MyRobotInit();
    }

    @Override
    public void autonomousInit() {
        Logger.OpenLog("Auto");   
        MyAutonomousInit();
        Logger.EndLine();
    }

    @Override
    public void autonomousPeriodic() {
        Logger.StartLine();
        MyAutonomousPeriodic();
        Logger.EndLine();
    }

    @Override
    public void teleopInit() {
        Logger.OpenLog("Tele");   
        MyTeleopInit();
        Logger.EndLine();
    }

    @Override
    public void teleopPeriodic() {
        Logger.StartLine();
        MyTeleopPeriodic();
        Logger.EndLine();
    }

    @Override
    public void testInit() {
        MyTestInit();
    }

    @Override
    public void testPeriodic() {
        MyTestPeriodic();
    }

    @Override
    public void disabledPeriodic() {
        Logger.CloseLog();
    }

    public abstract void MyRobotInit();
    public abstract void MyAutonomousInit();
    public abstract void MyAutonomousPeriodic();
    public abstract void MyTeleopInit();
    public abstract void MyTeleopPeriodic();
    public abstract void MyTestInit();
    public abstract void MyTestPeriodic();
}
