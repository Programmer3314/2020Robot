package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends MyRobot {
  NetworkTable ballTargetTable;
  NetworkTable portalTapeTargetTable;
  public static DriveController.DriveState currentDriveState;

  @Override
  public void RechargeRobotInit() {
    ballTargetTable = ntInst.getTable("Ball Target");
    portalTapeTargetTable = ntInst.getTable("Retroreflective Tape Target");
    ntInst.getEntry("chooseCam").setNumber(0);
    driveController = new DriveController(drivetrain, ballTargetTable, portalTapeTargetTable);

    if (hasShooter) {
      shooter = new Shooter(CANMcshooterLeft, CANMcshooterRight);
    }

    if (isTalonFXTest) {
      fxTest = new TalonFXTest();
    }

    if (hasControlPanel) {
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
    if (hasControlPanel) {
      controlPanel = new ControlPanel(CANMcctrlPanel);
    }
  }

  @Override
  public void RechargeTeleopPeriodic() {
    HumanInput.update();
    currentDriveState= DriveController.DriveState.MANUAL;

    if (HumanInput.ballChaseButton) {
      currentDriveState = DriveController.DriveState.BALLCHASE;
    } else if (HumanInput.powerPortAlignmentButton) {
      currentDriveState = DriveController.DriveState.POWERPORTALIGNMENT;
    } else if (HumanInput.controlPanelAlignmentButton) {
      currentDriveState = DriveController.DriveState.CONTROLPANELALIGNMENT;
    } else if (HumanInput.climbAlignmentButton) {
      currentDriveState = DriveController.DriveState.CLIMBALIGNMENT;
    } else if (HumanInput.gyroLock) {
      currentDriveState = DriveController.DriveState.GYROLOCK;
    } else if (HumanInput.wallAlignment) {
      currentDriveState = DriveController.DriveState.TRENCHALIGNMENT;
    } else {
      currentDriveState = DriveController.DriveState.MANUAL;
    }

    if (hasControlPanel) {
      controlPanel.update();
    }
    if (hasShooter) {
      shooter.update();
    }

    driveController.update(currentDriveState);
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