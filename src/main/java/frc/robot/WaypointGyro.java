package frc.robot;

import frc.robot.DriveController.MoveParameters;

public class WaypointGyro extends Waypoint {
    public double forward;
    public double turn;
    public int gyroAngle;

    public WaypointGyro(double forward, double turn, int gyroAngle){
        this.forward = forward * scale;
        this.turn = turn * scale;
        this.gyroAngle = gyroAngle;
    }

    @Override
    public void update(MoveParameters mP) {
        mP.forward = forward;
        mP.turn = turn;
    }

    @Override
    public boolean isComplete() {
        return ((forward >= 0 && turn >= 0 && gyroAngle <= Robot.cleanGyro)
        || (forward >= 0 && turn < 0 && gyroAngle >= Robot.cleanGyro)
        || (forward < 0 && turn >= 0 && gyroAngle <= Robot.cleanGyro)
        || (forward < 0 && turn < 0 && gyroAngle >= Robot.cleanGyro));
    }

}