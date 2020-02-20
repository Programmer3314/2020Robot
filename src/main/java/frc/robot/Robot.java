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

  // public static DriveController.DriveState currentDriveState;

  @Override
  public void RechargeRobotInit() {
    ballTargetTable = ntInst.getTable("Ball Target");
    portalTapeTargetTable = ntInst.getTable("Retroreflective Tape Target");
    ntInst.getEntry("chooseCam").setNumber(0);
    driveController = new DriveController(drivetrain, ballTargetTable, portalTapeTargetTable);

    if (hasShooter) {
      shooter = new Shooter(CANMcshooterLeft, CANMcshooterRight, CANMcBallQueuing, CANMcHood, CANMcIndexer, CANMcIntake);
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

    Solenoids.startCompressor();

  }

  @Override
  public void RechargeAutonomousInit() {
    HumanInput.update();
    if (HumanInput.autoNumber == 3) {
      auto1 = new ThreeBallAuto();
    }

    mP = driveController.new MoveParameters();
    shooter.resetState();
    auto1.reset();
    trenchAlignment.resetState();
    
    auto1.activate();
  }

  @Override
  public void RechargeAutonomousPeriodic() {
    SensorInput.update();
    
    auto1.update(mP);
    shooter.update();
    driveController.update(mP);
  }

  @Override
  public void RechargeTeleopInit() {
    /*
    if (hasControlPanel) {
      controlPanel = new ControlPanel(CANMcctrlPanel);
    }
    mP = driveController.new MoveParameters();

    mP.currentState = DriveController.DriveState.MANUAL;
    if (HumanInput.autoNumber == 3) {
      auto1 = new ThreeBallAuto();
    }
    trenchAlignment.resetState();
    shooter.resetState();
    auto1.reset();
    */
  }

  @Override
  public void RechargeTeleopPeriodic() {
    HumanInput.update();
    
    // SensorInput.update();

    // if (HumanInput.ballChaseButton) {
    //   mP.currentState = DriveController.DriveState.BALLCHASE;
    // } else if (HumanInput.shooterAllInTarget) {
    //   targetShooterRPM = SmartDashboard.getNumber("Shooter RPM Desired", 0);
    //   shooterRPMTolerance = SmartDashboard.getNumber("Shooter RPM Tolerance Desired", 0);
    //   queuingBeltSpeed = SmartDashboard.getNumber("Queuing Belt Speed", 0.5);
    //   useGyro = false;
    //   gyroTolerance = SmartDashboard.getNumber("Gyro Tolerance", 3);
    //   angleOffset = portalTapeTargetTable.getEntry("X Angle").getDouble(0);
    //   portalTapeTargetTable.getEntry("gyro").setDouble(Robot.rawGyro);
    //   angleOffset += Robot.rawGyro;
    //   gyroAngleDesired = angleOffset;
    //   shooter.shootAll(targetShooterRPM, shooterRPMTolerance, queuingBeltSpeed, useGyro, gyroAngleDesired,
    //       gyroTolerance);
    // } else if (HumanInput.trenchRunAlignment) {
    //   mP.currentState = DriveController.DriveState.TRENCHRUNALIGNMENT;
    // } else if (HumanInput.climbAlignmentButton) {
    //   mP.currentState = DriveController.DriveState.CLIMBALIGNMENT;
    // } else if (HumanInput.gyroLock) {
    //   mP.currentState = DriveController.DriveState.GYROLOCK;
    // } else if (HumanInput.controlPanelAlignment) {
    //   trenchAlignment.activate();
    // } else if (HumanInput.activateAuto) {
    //   SmartDashboard.putString("In active Auto", "Yes");
    //   auto1.activate();
    //   // auto1.update(mP);
    // } /*
    //    * else if(HumanInput.shutDownAuto){ SmartDashboard.putString("In active Auto",
    //    * "No"); mP.currentState = DriveState.NONE; auto1.reset(); }
    //    */else {
    //   mP.currentState = DriveController.DriveState.MANUAL;
    // }

    // mP.forward = HumanInput.forward;
    // mP.turn = HumanInput.turn;

    // if (mP.cameraToggle) {
    //   isForward = !isForward;

    //   if (isForward) {
    //     camNum = 0;
    //     // mP.forward *= 1;
    //   } else {
    //     camNum = 1;
    //     mP.forward *= -1;
    //   }

    //   Robot.ntInst.getEntry("chooseCam").setNumber(camNum);
    // }

    // mP.cameraToggle = HumanInput.cameraChangeButton;
    // trenchAlignment.update(mP);
    // auto1.update(mP);

    // if (hasControlPanel) {
    //   controlPanel.update();
    // }

    // if (hasShooter) {
    //   shooter.update();
    // }
    // if (HumanInput.reset) {
    //   shooter.reset();
    //   auto1.reset();
    // }

    // driveController.update(mP);
    
  }

  @Override
  public void RechargeTestInit() {
    //controlPanel.talon31.setSelectedSensorPosition(0);
    Solenoids.startCompressor();
    Solenoids.init();
  }

  @Override
  public void RechargeTestPeriodic() {
    HumanInput.update();
    SensorInput.update();
    Solenoids.update();
    //fxTest.Update();
    SmartDashboard.putString("Stage 1: ", "No");
    SmartDashboard.putString("Stage 2: ", "No");
    SmartDashboard.putString("Stage 3: ", "No");
    SmartDashboard.putString("Stage 4: ", "No");

    if(!(HumanInput.leftSwitch) && !(HumanInput.rightSwitch)){ //ball + intake
      SmartDashboard.putString("Stage 1: ", "Yes");
    } else if(!(HumanInput.leftSwitch) && HumanInput.rightSwitch){ //shooter
      SmartDashboard.putString("Stage 2: ", "Yes");
    } else if(HumanInput.leftSwitch && !(HumanInput.rightSwitch)){ //control panel
      SmartDashboard.putString("Stage 3: ", "Yes");
    } else if(HumanInput.leftSwitch && HumanInput.rightSwitch){ //climber + autos
      SmartDashboard.putString("Stage 4: ", "Yes");
    }

  }
}