package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Climber.ClimbStates;

public class Robot extends MyRobot {
  NetworkTable ballTargetTable;
  NetworkTable portalTapeTargetTable;
  ControlPanelAlignment trenchAlignment;
  AutoStateMachines auto1;
  boolean isForward = true;
  int driverCamNum = 0;
  int operatorCamNum = 0;
  // TODO: These variable should be in Shooter. 
  // Robot should set them there via a method 
  // instead of keeping them here and passing 
  // into ShootAll... 
  // Something like shooter.setFiringSolution(...)
  //public static double targetShooterRPM, shooterRPMTolerance;
  double queuingBeltSpeed;
  boolean useGyro;
  double angleOffset;
  double gyroTolerance, gyroAngleDesired;
  DriveController.MoveParameters mP;
  boolean toggleLightRing = false;
  Climber climber = new Climber();
  int LEDCounter;

  // public static DriveController.DriveState currentDriveState;

  @Override
  public void RechargeRobotInit() {
    Solenoids.ejectIntake(false);

    ballTargetTable = ntInst.getTable("Ball Target");
    portalTapeTargetTable = ntInst.getTable("Retroreflective Tape Target");
    
    // Ensure that switched camera entry exists
    ntInst.getEntry("chooseCam").setNumber(0);
    ntInst.getEntry("PumpkinSwitch").setNumber(0);
    
    driveController = new DriveController(drivetrain, ballTargetTable, portalTapeTargetTable);

    //shooterRPMTolerance = Constants.shooterRPMTolerance;

    if (hasShooter) {
      shooter = new Shooter(CANMcshooterLeft, CANMcshooterRight, CANMcBallQueuing, 
          CANMcHood, CANMcIndexer, CANMcIntake);
    }

    shooter.setTargetShooterRPMTolerance(Constants.shooterRPMTolerance);

    if (isTalonFXTest) {
      fxTest = new TalonFXTest();
    }

    //TODO revert later
    // if (hasControlPanel) {
    //   controlPanel = new ControlPanel(CANMcctrlPanel);
    // }
    trenchAlignment = new ControlPanelAlignment();

    // targetShooterRPM = SmartDashboard.getNumber("Shooter RPM Desired", 0);
    // shooterRPMTolerance = SmartDashboard.getNumber("Shooter RPM Tolerance Desired", 0);
    // SmartDashboard.putNumber("Shooter RPM Desired", targetShooterRPM);
    SmartDashboard.putNumber("Shooter RPM Tolerance Desired", shooter.getTargetShooterRPMTolerance());

    queuingBeltSpeed = Constants.queuingBeltSpeed; //SmartDashboard.getNumber("Queuing Belt Speed", 0.5);
    SmartDashboard.putNumber("Queuing Belt Speed", queuingBeltSpeed);

    Solenoids.startCompressor();

  }

  @Override
  public void RechargeAutonomousInit() {
    Solenoids.targettingLightRing(true);
    Solenoids.ejectIntake(false);

    Solenoids.intakeOut.set(false);
    Solenoids.intakeIn.set(true);
    Solenoids.disengageRatchet.set(false);
    Solenoids.engageRatchet.set(true);
    Solenoids.disengagePTO.set(true);
    Solenoids.engagePTO.set(false);
    Solenoids.CPManipulatorDown.set(true);
    Solenoids.CPManipulatorUp.set(false);

    HumanInput.update();

    // set auto 
    auto1 = null;
    if (HumanInput.autoNumber == 3) {
      auto1 = new ThreeBallAuto(shooter);
    }

    if (HumanInput.autoNumber == 4) {
      auto1 = new ThreeBallAutoBack(shooter);
    }

    mP = driveController.new MoveParameters();
    shooter.resetState();

    if(auto1 != null){
      auto1.reset();
    }

    trenchAlignment.resetState();

    shooter.setHoodSetpoint(0.0);

    if(auto1 != null){
      auto1.activate();
    }

    
    isForward = true;
    driverCamNum = 1;

  }

  @Override
  public void RechargeAutonomousPeriodic() {
    SensorInput.update();

    if(auto1 != null){
      auto1.update(mP);
    }

    shooter.update(mP);
    driveController.update(mP);
  }

  @Override
  public void RechargeTeleopInit() {
    // SmartDashboard.putNumber("Target Offset", Constants.targettingOffset);

    Solenoids.targettingLightRing(true);

    Solenoids.intakeOut.set(false);
    Solenoids.intakeIn.set(true);
    Solenoids.disengageRatchet.set(false);
    Solenoids.engageRatchet.set(true);
    Solenoids.disengagePTO.set(true);
    Solenoids.engagePTO.set(false);
    Solenoids.CPManipulatorDown.set(true);
    Solenoids.CPManipulatorUp.set(false);

    Solenoids.backLED.set(false);
    Solenoids.frontLED.set(false);

    LEDCounter = 0;

    // if (hasControlPanel) {
    //   controlPanel = new ControlPanel(CANMcctrlPanel);
    // }

    mP = driveController.new MoveParameters();

    mP.currentState = DriveController.DriveState.MANUAL;

    if (HumanInput.autoNumber == 3) {
       auto1 = new ThreeBallAuto(shooter);
    }

    if (HumanInput.autoNumber == 4) {
      auto1 = new ThreeBallAutoBack(shooter);
   }

    trenchAlignment.resetState();
    shooter.resetState();

    if(auto1 != null){
      auto1.reset();
    }

    Solenoids.disengagePTO();
    Solenoids.startCompressor();

    shooter.setHoodSetpoint(0.0);
    shooter.homedHood = false;

    isForward = true;
    driverCamNum = 1;
    operatorCamNum = 0;
  }

  @Override
  public void RechargeTeleopPeriodic() {

    // LEDCounter++;

    // if(LEDCounter >= 4750){
    //   Solenoids.backLED(true);
    //   Solenoids.frontLED(true);
    // }

    HumanInput.update();
    Solenoids.update();
    SensorInput.update();

    if(HumanInput.closeShot){
      shooter.setHoodSetpoint(0);
      shooter.setTargetShooterRPM(2100);
    //targetShooterRPM = 2100;
    }

    if(HumanInput.lineShot){
      shooter.setHoodSetpoint(-1400);
      shooter.setTargetShooterRPM(3600);
      //targetShooterRPM = 3600;

    }

    if(HumanInput.trenchShot){
      shooter.setHoodSetpoint(-1450);
      shooter.setTargetShooterRPM(3600);
      //targetShooterRPM = 3600;
    }

    if(HumanInput.farShot){
      shooter.setHoodSetpoint(-1450);
      shooter.setTargetShooterRPM(4400);
      //targetShooterRPM = 4400;
    }

    SmartDashboard.putNumber("Target Shooter RPM:", shooter.getTargetShooterRPM());

    if (HumanInput.ballChaseButton) {
      mP.currentState = DriveController.DriveState.BALLCHASE;
    } else if (HumanInput.shooterAllInTarget) {
      // shooterRPMTolerance = SmartDashboard.getNumber("Shooter RPM Tolerance Desired", 0);
      //queuingBeltSpeed = SmartDashboard.getNumber("Queuing Belt Speed", 0.5);
      useGyro = false;
      gyroTolerance = SmartDashboard.getNumber("Gyro Tolerance", 3);
      angleOffset = portalTapeTargetTable.getEntry("X Angle").getDouble(0);
      portalTapeTargetTable.getEntry("gyro").setDouble(Robot.rawGyro);
      angleOffset += Robot.rawGyro;
      gyroAngleDesired = angleOffset;
      
      shooter.shootAll(/*targetShooterRPM, shooterRPMTolerance, */queuingBeltSpeed, useGyro, gyroAngleDesired,
          gyroTolerance);
    } else if (HumanInput.trenchRunAlignment) {
      mP.currentState = DriveController.DriveState.TRENCHRUNALIGNMENT;
    } else if(HumanInput.powerPortAlignment){
      mP.currentState = DriveController.DriveState.POWERPORTALIGNMENT;
      // mP.currentState = DriveController.DriveState.SHOOTERPOWERPORTALIGNMENT;
    } else if (HumanInput.climbAlignmentButton) {
      mP.currentState = DriveController.DriveState.CLIMBALIGNMENT;
    } else if (HumanInput.gyroLock) {
      mP.currentState = DriveController.DriveState.GYROLOCK;
    } else if (HumanInput.controlPanelAlignment) {
      trenchAlignment.activate();
    // } else if (HumanInput.activateAuto) {
    //   SmartDashboard.putString("In active Auto", "Yes");

    //   if(auto1 != null){
    //     auto1.activate();
    //   }

    //   if(auto1 != null){
    //     auto1.update(mP);
    //   }

    // } else if (HumanInput.shutDownAuto) {
    //   SmartDashboard.putString("In Active Auto", "No");
    //   mP.currentState = DriveController.DriveState.NONE;

    //   if(auto1 != null){
    //     auto1.reset();
    //   }
    } else if(HumanInput.abortIntake){
      shooter.abortIntake();
    } else if(HumanInput.lightRing){
      toggleLightRing = !toggleLightRing;
      Solenoids.targettingLightRing(toggleLightRing);
    } else {
      mP.currentState = DriveController.DriveState.MANUAL;
    }

    mP.forward = HumanInput.forward;
    mP.turn = HumanInput.turn;

    mP.driverCameraToggle = HumanInput.driverCameraChange;

    if (mP.driverCameraToggle) {
      isForward = !isForward;

      if (isForward) {
        driverCamNum = 1;
        // mP.forward *= 1;
      } else {
        driverCamNum = 0;
      } 

      Robot.ntInst.getEntry("chooseCam").setNumber(driverCamNum);
      //Robot.ntInst.getEntry("PumpkinSwitch").setNumber(1);
    }

    // if (!isForward) {
    //   mP.forward *= -1;
    // } 

    if (HumanInput.operatorCameraChange) {

      if (operatorCamNum == 0) {
          operatorCamNum = 1;
        // mP.forward *= 1;
      } else {
        operatorCamNum = 0;
      } 

      Robot.ntInst.getEntry("PumpkinSwitch").setNumber(operatorCamNum);
      // Robot.ntInst.getEntry("PumpkinSwitch").setNumber(1);
    }

    trenchAlignment.update(mP);

    // if(auto1 != null){
    //   auto1.update(mP);
    // }

    if(HumanInput.activateGroundIntake){
      shooter.groundIntakeAll();
    }

    if(HumanInput.activateIntake){
      shooter.intakeAll();
    }

    if (hasShooter) {
      shooter.update(mP);
    }
    
    if(HumanInput.reverseIntake){
      shooter.reverseIntake();
    } else if(HumanInput.reverseIntakeReleased){
      shooter.reverseIntakeRelease();
    }

    if(HumanInput.reverseAll){
      shooter.reverseAll();
    } else if(HumanInput.reverseAllReleased){
      shooter.reverseAllRelease();
    }

    if (HumanInput.reset) {
      shooter.reset();
      if(auto1 != null){
        auto1.reset();
      }
    }

    if (HumanInput.gyroReset){
      navx.reset();
    }

    if(HumanInput.CPManipulatorUp){
      if(HumanInput.fourSpins){
        controlPanel.spinFourTimes();
      }

      // if(HumanInput.spinToYellow){
      //   controlPanel.spinToYellow();
      // }

      // if(HumanInput.spinToGreen){
      //   controlPanel.spinToGreen();
      // }

      // if(HumanInput.spinToBlue){
      //   controlPanel.spinToBlue();
      // }

      // if(HumanInput.spinToRed){
      //   controlPanel.spinToRed();
      // }

      // if(HumanInput.spinToFMSColor){
      //   controlPanel.spinToFMSColor();
      // }        
    }

    //TODO revert later
    // if (hasControlPanel) {
    //   controlPanel.update();
    // }

    if(HumanInput.operatorBack && HumanInput.operatorStart && climber.climbStates == ClimbStates.IDLE){
      climber.activate();
    }

    if(HumanInput.abortClimb){
      climber.abortClimb();
    }

    if(HumanInput.abortShooter){
      shooter.abortShooter();
    }

    climber.update(mP);
    driveController.update(mP);

    SmartDashboard.putString("Ball Intake Config", "Inactive");
    SmartDashboard.putString("Shooter Config", "Inactive");
    SmartDashboard.putString("Control Panel Config", "Inactive");
    SmartDashboard.putString("Climber Config", "Inactive");

    if (!(HumanInput.leftSwitch) && !(HumanInput.rightSwitch)) { // ball + intake
      SmartDashboard.putString("Ball Intake Config", "Active");
    } else if (!(HumanInput.leftSwitch) && HumanInput.rightSwitch) { // shooter
      SmartDashboard.putString("Shooter Config", "Active");
    } else if (HumanInput.leftSwitch && !(HumanInput.rightSwitch)) { // control panel
      SmartDashboard.putString("Control Panel Config", "Active");
    } else if (HumanInput.leftSwitch && HumanInput.rightSwitch) { // climber + autos
      SmartDashboard.putString("Climber Config", "Active");
    }
  }

  @Override
  public void RechargeTestInit() {
    // SmartDashboard.putNumber("Target Offset", Constants.targettingOffset);
    // controlPanel.talon31.setSelectedSensorPosition(0);
    Solenoids.startCompressor();
    Solenoids.init();
  }

  @Override
  public void RechargeTestPeriodic() {
    HumanInput.update();
    SensorInput.update();
    Solenoids.update();
    Solenoids.startCompressor();
    // fxTest.Update();
    SmartDashboard.putString("Ball Intake Config", "Inactive");
    SmartDashboard.putString("Shooter Config", "Inactive");
    SmartDashboard.putString("Control Panel Config", "Inactive");
    SmartDashboard.putString("Climber Config", "Inactive");

    if (!(HumanInput.leftSwitch) && !(HumanInput.rightSwitch)) { // ball + intake
      SmartDashboard.putString("Ball Intake Config", "Active");
    } else if (!(HumanInput.leftSwitch) && HumanInput.rightSwitch) { // shooter
      SmartDashboard.putString("Shooter Config", "Active");
    } else if (HumanInput.leftSwitch && !(HumanInput.rightSwitch)) { // control panel
      SmartDashboard.putString("Control Panel Config", "Active");
    } else if (HumanInput.leftSwitch && HumanInput.rightSwitch) { // climber + autos
      SmartDashboard.putString("Climber Config", "Active");
    }

  }
}