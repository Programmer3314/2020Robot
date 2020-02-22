package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends MyRobot {
  NetworkTable ballTargetTable;
  NetworkTable portalTapeTargetTable;
  ControlPanelAlignment trenchAlignment;
  AutoStateMachines auto1;
  boolean isForward = true;
  int camNum = 0;
  double targetShooterRPM, shooterRPMTolerance;
  double queuingBeltSpeed;
  boolean useGyro;
  double angleOffset;
  double gyroTolerance, gyroAngleDesired;
  DriveController.MoveParameters mP;
  boolean toggleLightRing = false;

  // public static DriveController.DriveState currentDriveState;

  @Override
  public void RechargeRobotInit() {
    ballTargetTable = ntInst.getTable("Ball Target");
    portalTapeTargetTable = ntInst.getTable("Retroreflective Tape Target");
    ntInst.getEntry("chooseCam").setNumber(0);
    driveController = new DriveController(drivetrain, ballTargetTable, portalTapeTargetTable);

    if (hasShooter) {
      shooter = new Shooter(CANMcshooterLeft, CANMcshooterRight, CANMcBallQueuing, CANMcHood, CANMcIndexer,
          CANMcIntake);
    }

    if (isTalonFXTest) {
      fxTest = new TalonFXTest();
    }

    if (hasControlPanel) {
      controlPanel = new ControlPanel(CANMcctrlPanel);
    }
    trenchAlignment = new ControlPanelAlignment();

    // targetShooterRPM = SmartDashboard.getNumber("Shooter RPM Desired", 0);
    shooterRPMTolerance = SmartDashboard.getNumber("Shooter RPM Tolerance Desired", 0);
    // SmartDashboard.putNumber("Shooter RPM Desired", targetShooterRPM);
    SmartDashboard.putNumber("Shooter RPM Tolerance Desired", shooterRPMTolerance);

    queuingBeltSpeed = SmartDashboard.getNumber("Queuing Belt Speed", 0.5);
    SmartDashboard.putNumber("Queuing Belt Speed", queuingBeltSpeed);

    Solenoids.startCompressor();

  }

  @Override
  public void RechargeAutonomousInit() {
    Solenoids.lightRing(true);

    HumanInput.update();
    if (HumanInput.autoNumber == 3) {
      auto1 = new ThreeBallAuto(shooter);
    }

    mP = driveController.new MoveParameters();
    shooter.resetState();
    auto1.reset();
    trenchAlignment.resetState();

    shooter.setHoodSetpoint(0.0);

    auto1.activate();
  }

  @Override
  public void RechargeAutonomousPeriodic() {
    SensorInput.update();

    auto1.update(mP);
    shooter.update(mP);
    driveController.update(mP);
  }

  @Override
  public void RechargeTeleopInit() {
    Solenoids.lightRing(true);

    if (hasControlPanel) {
      controlPanel = new ControlPanel(CANMcctrlPanel);
    }
    mP = driveController.new MoveParameters();

    mP.currentState = DriveController.DriveState.MANUAL;
    // if (HumanInput.autoNumber == 3) {
    //   auto1 = new ThreeBallAuto();
    // }
    auto1 = new ThreeBallAuto(shooter);
    trenchAlignment.resetState();
    shooter.resetState();
    auto1.reset();

    Solenoids.disengagePTO();

    shooter.setHoodSetpoint(0.0);
    shooter.homedHood = false;

  }

  @Override
  public void RechargeTeleopPeriodic() {
    HumanInput.update();
    Solenoids.update();
    SensorInput.update();

    if(HumanInput.closeShot){
      shooter.setHoodSetpoint(0);
      targetShooterRPM = 2100;
    }

    if(HumanInput.lineShot){
      shooter.setHoodSetpoint(-1400);
      targetShooterRPM = 3600;
    }

    if(HumanInput.trenchShot){
      shooter.setHoodSetpoint(-1450);
      targetShooterRPM = 3600;
    }

    SmartDashboard.putNumber("Shooter RPM: ", targetShooterRPM);

    if (HumanInput.ballChaseButton) {
      mP.currentState = DriveController.DriveState.BALLCHASE;
    } else if (HumanInput.shooterAllInTarget) {
      shooterRPMTolerance = SmartDashboard.getNumber("Shooter RPM Tolerance Desired", 0);
      queuingBeltSpeed = SmartDashboard.getNumber("Queuing Belt Speed", 0.5);
      useGyro = false;
      gyroTolerance = SmartDashboard.getNumber("Gyro Tolerance", 3);
      angleOffset = portalTapeTargetTable.getEntry("X Angle").getDouble(0);
      portalTapeTargetTable.getEntry("gyro").setDouble(Robot.rawGyro);
      angleOffset += Robot.rawGyro;
      gyroAngleDesired = angleOffset;
      
      shooter.shootAll(targetShooterRPM, shooterRPMTolerance, queuingBeltSpeed, useGyro, gyroAngleDesired,
          gyroTolerance);
    } else if (HumanInput.trenchRunAlignment) {
      mP.currentState = DriveController.DriveState.TRENCHRUNALIGNMENT;
    } else if(HumanInput.powerPortAlignment){
      mP.currentState = DriveController.DriveState.POWERPORTALIGNMENT;
      //mP.currentState = DriveController.DriveState.SHOOTERPOWERPORTALIGNMENT;
    }else if (HumanInput.climbAlignmentButton) {
      mP.currentState = DriveController.DriveState.CLIMBALIGNMENT;
    } else if (HumanInput.gyroLock) {
      mP.currentState = DriveController.DriveState.GYROLOCK;
    } else if (HumanInput.controlPanelAlignment) {
      trenchAlignment.activate();
    } else if (HumanInput.activateAuto) {
      SmartDashboard.putString("In active Auto", "Yes");
      auto1.activate();
      auto1.update(mP);
    } else if (HumanInput.shutDownAuto) {
      SmartDashboard.putString("In active Auto", "No");
      mP.currentState = DriveController.DriveState.NONE;
      auto1.reset();
    } else if(HumanInput.abortIntake){
      shooter.abortIntake();
    } else if(HumanInput.lightRing){
      toggleLightRing = !toggleLightRing;
      Solenoids.lightRing(toggleLightRing);
    } else {
      mP.currentState = DriveController.DriveState.MANUAL;
    }

    mP.forward = HumanInput.forward;
    mP.turn = HumanInput.turn;

    if (mP.cameraToggle) {
      isForward = !isForward;

      if (isForward) {
        camNum = 0;
        // mP.forward *= 1;
      } else {
        camNum = 1;
        mP.forward *= -1;
      }

      Robot.ntInst.getEntry("chooseCam").setNumber(camNum);
    }

    mP.cameraToggle = HumanInput.cameraChangeButton;
    trenchAlignment.update(mP);
    auto1.update(mP);

    if(HumanInput.activateGroundIntake){
      shooter.groundIntakeAll();
    }

    if(HumanInput.activateIntake){
      shooter.intakeAll();
    }

    if (hasControlPanel) {
      controlPanel.update();
    }

    if (hasShooter) {
      shooter.update(mP);
    }
    if (HumanInput.reset) {
      shooter.reset();
      auto1.reset();
    }

    driveController.update(mP);

  }

  @Override
  public void RechargeTestInit() {
    // controlPanel.talon31.setSelectedSensorPosition(0);
    Solenoids.startCompressor();
    Solenoids.init();
  }

  @Override
  public void RechargeTestPeriodic() {
    HumanInput.update();
    SensorInput.update();
    Solenoids.update();
    // fxTest.Update();
    SmartDashboard.putString("Shooter & Intake: ", "Inactive");
    SmartDashboard.putString("Solenoids: ", "Inactive");
    SmartDashboard.putString("Control Panel: ", "Inactive");
    SmartDashboard.putString("Climber: ", "Inactive");

    if (!(HumanInput.leftSwitch) && !(HumanInput.rightSwitch)) { // ball + intake
      SmartDashboard.putString("Shooter & Intake: ", "Active");
    } else if (!(HumanInput.leftSwitch) && HumanInput.rightSwitch) { // shooter
      SmartDashboard.putString("Solenoids: ", "Active");
    } else if (HumanInput.leftSwitch && !(HumanInput.rightSwitch)) { // control panel
      SmartDashboard.putString("Control Panel: ", "Active");
    } else if (HumanInput.leftSwitch && HumanInput.rightSwitch) { // climber + autos
      SmartDashboard.putString("Climber: ", "Active");
    }

  }
}