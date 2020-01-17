package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.Joystick;

public class Robot extends MyRobot{
  private Joystick xboxController = new Joystick(xboxControllerUSBPort);
  private Joystick stick = new Joystick(stickUSBPort);
  NetworkTable table;

  @Override
  public void RechargeRobotInit() {
    // TODO Auto-generated method stub
    table = ntInst.getTable("Ball Target");
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
    double forward, turn;
    forward = 0;
    turn = 0;

    if(table == null)
      System.out.print("Table is Null");

    if(table.getEntry("Target Found") == null)
      System.out.print("Entry is Null");

    if(!xboxController.getRawButton(2)){
      forward = xboxController.getRawAxis(1);
      turn = xboxController.getRawAxis(4);
    } else {
      if(table.getEntry("Target Found").getBoolean(false)){
        double centerX = table.getEntry("x").getDouble(0);
        forward = xboxController.getRawAxis(1) *.5;
        turn = centerX / 400.0;
      }
    }

    double leftSetPoint = (forward - turn * 0.5) * Constants.maxRPM;
    double rightSetPoint = (forward + turn * 0.5) * Constants.maxRPM;

    drivetrain.update(leftSetPoint, rightSetPoint);
    //shooter.update(stick, xboxController);
  }

  @Override
  public void RechargeTestInit() {
  }

  @Override
  public void RechargeTestPeriodic () {
  }
}