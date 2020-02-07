package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.DriveController.DriveState;

public class Robot extends MyRobot {
  NetworkTable ballTargetTable;
  NetworkTable portalTapeTargetTable;
  ControlPanelAlignment trenchAlignment;
  boolean isForward = true;
  int camNum = 0;
  double shooterRPM, shooterRPMTolerance;
  //public static DriveController.DriveState currentDriveState;

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
    trenchAlignment = new ControlPanelAlignment();

    shooterRPM = SmartDashboard.getNumber("Shooter RPM Desired", 0);
    shooterRPMTolerance = SmartDashboard.getNumber("Shooter RPM Tolerance Desired", 0);
    SmartDashboard.putNumber("Shooter RPM Desired", shooterRPM);
    SmartDashboard.putNumber("Shooter RPM Tolerance Desired", shooterRPMTolerance);

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
    trenchAlignment.resetState();
  }

  @Override
  public void RechargeTeleopPeriodic() {
    HumanInput.update();
    DriveController.MoveParameters mP = driveController.new MoveParameters();
    mP.currentState = DriveController.DriveState.MANUAL;

    if (HumanInput.ballChaseButton) {
      mP.currentState = DriveController.DriveState.BALLCHASE;
    } else if (HumanInput.powerPortAlignmentButton) {
      shooter.activate();
    } else if (HumanInput.trenchRunAlignment) {
      mP.currentState = DriveController.DriveState.TRENCHRUNALIGNMENT;
    } else if (HumanInput.climbAlignmentButton) {
      mP.currentState = DriveController.DriveState.CLIMBALIGNMENT;
    } else if (HumanInput.gyroLock) {
      mP.currentState = DriveController.DriveState.GYROLOCK;
    } else if (HumanInput.controlPanelAlignment) {
      trenchAlignment.activate();
    } else {
      mP.currentState = DriveController.DriveState.MANUAL;
    }

    mP.forward = HumanInput.forward;
    mP.turn = HumanInput.turn;

    if (mP.cameraToggle) {
      isForward = !isForward;

      if (isForward) {
          camNum = 0;
          //mP.forward *= 1;
      } else {
          camNum = 1;
          mP.forward *= -1;
      }

      Robot.ntInst.getEntry("chooseCam").setNumber(camNum);
  }

    mP.cameraToggle = HumanInput.cameraChangeButton;
    trenchAlignment.update(mP);
    
    if (hasControlPanel) {
      controlPanel.update();
    }

    if (hasShooter) {
      shooter.update();
    }

    if(HumanInput.reset){
      shooter.reset();
    }

    //driveController.update(mP);
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