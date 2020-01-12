package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Joystick;

public class Robot extends MyRobot{
  private Joystick xboxController = new Joystick(xboxControllerUSBPort);
  private Joystick stick = new Joystick(stickUSBPort);

  @Override
  public void RechargeRobotInit() {
    // TODO Auto-generated method stub

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
    double leftSetPoint = (xboxController.getRawAxis(1) - (xboxController.getRawAxis(4) * 0.5)) * Constants.maxRPM;
    double rightSetPoint = (xboxController.getRawAxis(1) + xboxController.getRawAxis(4) * 0.5) * Constants.maxRPM;

    drivetrain.update(leftSetPoint, rightSetPoint);
    shooter.update(stick, xboxController);
  }

  @Override
  public void RechargeTestInit() {
  }

  @Override
  public void RechargeTestPeriodic() {
  }
}
