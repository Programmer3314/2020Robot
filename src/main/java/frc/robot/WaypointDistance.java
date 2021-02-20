package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.DriveController.MoveParameters;

public class WaypointDistance extends Waypoint {
    public double forward;
    public double turn;
    public int gyroAngle;
    public long encoderValue;
    public long originalEncoderValue;

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
        long currentEncoderPos = (long)Robot.driveController.encoderPos - originalEncoderValue;

        return ((forward > 0 && currentEncoderPos < encoderValue)
        || (forward < 0 && currentEncoderPos > encoderValue)
        || (forward == 0));
}

    @Override
    public void init() {
        originalEncoderValue = (long)Robot.driveController.encoderPos;

    }

}