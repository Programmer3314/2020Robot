package frc.robot;

import edu.wpi.first.networktables.NetworkTable;

public class Robot extends MyRobot{
  NetworkTable table;
  TalonFXTest fxTest;
  DriveController driveController;

  @Override
  public void RechargeRobotInit() {
    table = ntInst.getTable("Ball Target");
    fxTest = new TalonFXTest();
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
    HumanInput.update();
    fxTest.Update();
  }
}