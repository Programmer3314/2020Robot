package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends MyRobot {
  NetworkTable ballTargetTable;
  NetworkTable portalTapeTargetTable;

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
    driveController.update();
    if (hasControlPanel) {
      controlPanel.update();
    }
    if (hasShooter) {
      shooter.update();
    }
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