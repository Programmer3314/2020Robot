/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.TimedRobot;

public abstract class AllRobots extends TimedRobot {
    public static NetworkTableInstance ntInst;

    @Override
    public void robotInit() {
        ntInst = NetworkTableInstance.getDefault();
        MyRobotInit();
    }

    @Override
    public void autonomousInit() {
        MyAutonomousInit();
    }

    @Override
    public void autonomousPeriodic() {
        MyAutonomousPeriodic();
    }

    @Override
    public void teleopInit() {
        MyTeleopInit();
    }

    @Override
    public void teleopPeriodic() {
        MyTeleopPeriodic();
    }

    @Override
    public void testInit() {
        MyTestInit();
    }

    @Override
    public void testPeriodic() {
        MyTestPeriodic();
    }

    public abstract void MyRobotInit();
    public abstract void MyAutonomousInit();
    public abstract void MyAutonomousPeriodic();
    public abstract void MyTeleopInit();
    public abstract void MyTeleopPeriodic();
    public abstract void MyTestInit();
    public abstract void MyTestPeriodic();
}
