package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends MyRobot {
  NetworkTable ballTargetTable;
  NetworkTable portalTapeTargetTable;
  TalonFXTest fxTest;
  DriveController driveController;
  ControlPanel controlPanel;
  Shooter shooter;
  

  @Override
  public void RechargeRobotInit() {
    ballTargetTable = ntInst.getTable("Ball Target");
    portalTapeTargetTable = ntInst.getTable("Retroreflective Tape Target");
    ntInst.getEntry("chooseCam").setNumber(0);
    driveController = new DriveController(drivetrain, ballTargetTable, portalTapeTargetTable);

    if (isShooter) {
      shooter = new Shooter(CANMcshooterLeft, CANMcshooterRight);
    }

    if (isTalonFXTest) {
      fxTest = new TalonFXTest();
    }

    if (isControlPanel) {
      controlPanel = new ControlPanel(CANMcctrlPanel);
    }
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
    controlPanel.update();
    // shooter.update(stick, xboxController);
  }

  @Override
  public void RechargeTestInit() {
    controlPanel.talon31.setSelectedSensorPosition(0);
  }

  @Override
  public void RechargeTestPeriodic() {
    HumanInput.update();
    fxTest.Update();
  }
}