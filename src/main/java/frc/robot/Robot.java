package frc.robot;

import edu.wpi.first.networktables.NetworkTable;

public class Robot extends MyRobot{
  NetworkTable ballTargetTable;
  NetworkTable portalTapeTargetTable;
  TalonFXTest fxTest;
  DriveController driveController;
  BagMotorTest test;

  @Override
  public void RechargeRobotInit() {
    ballTargetTable = ntInst.getTable("Ball Target");
    portalTapeTargetTable = ntInst.getTable("Retroreflective Tape Target");
    fxTest = new TalonFXTest();
    ntInst.getEntry("chooseCam").setNumber(0);
    driveController = new DriveController(drivetrain, ballTargetTable, portalTapeTargetTable);
    test = new BagMotorTest();
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
    test.update();
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