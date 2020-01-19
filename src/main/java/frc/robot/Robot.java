package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Joystick;

public class Robot extends MyRobot{
  NetworkTable table;
  DriveController driveController;

  @Override
  public void RechargeRobotInit() {
    // TODO Auto-generated method stub
    table = ntInst.getTable("Ball Target");
    ntInst.getEntry("chooseCam").setNumber(0);
    driveController = new DriveController(drivetrain, table);
  }

  @Override
  public void RechargeAutonomousInit() {
  }

  @Override
  public void RechargeAutonomousPeriodic() {
  }

  @Override
  public void RechargeTeleopInit() {
  }

  @Override
  public void RechargeTeleopPeriodic() {
    HumanInput.update();
    driveController.update();
    //shooter.update(stick, xboxController);
  }

  @Override
  public void RechargeTestInit() {
  }

  @Override
  public void RechargeTestPeriodic () {
  }
}