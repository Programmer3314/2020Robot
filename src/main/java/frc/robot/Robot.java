package frc.robot;

import javax.swing.text.StyleContext.SmallAttributeSet;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.DriveController.DriveState;

public class Robot extends MyRobot {
  NetworkTable ballTargetTable;
  NetworkTable portalTapeTargetTable;
  ControlPanelAlignment trenchAlignment;
  ThreeBallAuto auto1;
  boolean isForward = true;
  int camNum = 0;
  double targetShooterRPM, shooterRPMTolerance;
  double queuingBeltSpeed;
  boolean useGyro;
  double angleOffset;
  double gyroTolerance, gyroAngleDesired;
  
  //public static DriveController.DriveState currentDriveState;

  @Override
  public void RechargeRobotInit() {
    ballTargetTable = ntInst.getTable("Ball Target");
    portalTapeTargetTable = ntInst.getTable("Retroreflective Tape Target");
    ntInst.getEntry("chooseCam").setNumber(0);
    driveController = new DriveController(drivetrain, ballTargetTable, portalTapeTargetTable);

    if (hasShooter) {
      shooter = new Shooter(CANMcshooterLeft, CANMcshooterRight, CANMcBallQueuing, CANMcHood);
    }

    if (isTalonFXTest) {
      fxTest = new TalonFXTest();
    }

    if (hasControlPanel) {
      controlPanel = new ControlPanel(CANMcctrlPanel);
    }
    trenchAlignment = new ControlPanelAlignment();

    targetShooterRPM = SmartDashboard.getNumber("Shooter RPM Desired", 0);
    shooterRPMTolerance = SmartDashboard.getNumber("Shooter RPM Tolerance Desired", 0);
    SmartDashboard.putNumber("Shooter RPM Desired", targetShooterRPM);
    SmartDashboard.putNumber("Shooter RPM Tolerance Desired", shooterRPMTolerance);

    queuingBeltSpeed = SmartDashboard.getNumber("Queuing Belt Speed", 0.5);
    SmartDashboard.putNumber("Queuing Belt Speed", queuingBeltSpeed);



     
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
    auto1 = new ThreeBallAuto();
    trenchAlignment.resetState();
    shooter.resetState();
  }

  @Override
  public void RechargeTeleopPeriodic() {
    HumanInput.update();
    SensorInput.update();
    DriveController.MoveParameters mP = driveController.new MoveParameters();
    mP.currentState = DriveController.DriveState.MANUAL;

    if (HumanInput.ballChaseButton) {
        mP.currentState = DriveController.DriveState.BALLCHASE;
    } else if (HumanInput.shooterAllInTarget) {
        targetShooterRPM = SmartDashboard.getNumber("Shooter RPM Desired", 0);
        shooterRPMTolerance = SmartDashboard.getNumber("Shooter RPM Tolerance Desired", 0);
        queuingBeltSpeed = SmartDashboard.getNumber("Queuing Belt Speed", 0.5);
        useGyro = false;
        gyroTolerance = SmartDashboard.getNumber("Gyro Tolerance" , 3);
        angleOffset = portalTapeTargetTable.getEntry("X Angle").getDouble(0);
        portalTapeTargetTable.getEntry("gyro").setDouble(Robot.rawGyro);
        angleOffset += Robot.rawGyro;
        gyroAngleDesired = angleOffset; 
        shooter.shootAll(targetShooterRPM, shooterRPMTolerance, queuingBeltSpeed, useGyro, gyroAngleDesired, gyroTolerance);
    } else if (HumanInput.trenchRunAlignment) {
        mP.currentState = DriveController.DriveState.TRENCHRUNALIGNMENT;
    } else if (HumanInput.climbAlignmentButton) {
        mP.currentState = DriveController.DriveState.CLIMBALIGNMENT;
    } else if (HumanInput.gyroLock) {
        mP.currentState = DriveController.DriveState.GYROLOCK;
    } else if (HumanInput.controlPanelAlignment) {
        trenchAlignment.activate();
    } else if(HumanInput.activateAuto){
        auto1.update(mP);
    }else if(HumanInput.shutDownAuto){
        mP.currentState = DriveState.NONE;
        auto1.reset();
    }else {
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

    driveController.update(mP);
  }

  @Override
  public void RechargeTestInit() {
    controlPanel.talon31.setSelectedSensorPosition(0);
  }

  @Override
  public void RechargeTestPeriodic() {
    HumanInput.update();
    SensorInput.update();
    fxTest.Update();
  }
}