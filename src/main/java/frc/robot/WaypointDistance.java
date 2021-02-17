package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.DriveController.MoveParameters;

public class WaypointDistance extends Waypoint {
    public double forward;
    public double turn;
    public int gyroAngle;
    public long encoderValue;

public WaypointDistance(double forward, double turn, double feet){
    this.forward = forward * scale;
    this.turn = turn * scale;
    this.encoderValue = (int)(feet * Constants.encoderTicksToFeet);
}

    @Override
    public void update(MoveParameters mP) {
        mP.forward = forward;
        mP.turn = turn;
    }

    @Override
    public boolean isComplete() {
        if(forward > 0){
        SmartDashboard.putNumber("isComplete encoderValue: ", encoderValue);
        SmartDashboard.putNumber("isComplete encoderPos:", Robot.driveController.encoderPos);
        }   
        
        return ((forward >= 0 && Robot.driveController.encoderPos < encoderValue)
        || (forward < 0 && Robot.driveController.encoderPos > encoderValue));
}

}