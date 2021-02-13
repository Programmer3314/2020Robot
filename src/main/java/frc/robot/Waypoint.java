package frc.robot;

public class Waypoint {
    public double forward;
    public double turn;
    public int gyroAngle;
    public long encoderValue;
    public double scale = 1.1;
    public ExitCondition exit;

    public static enum ExitCondition {
        DISTANCE, GYRO
    }

public Waypoint(){

}

public Waypoint(double forward, double turn, double feet){
    exit = ExitCondition.DISTANCE;
    this.forward = forward * scale;
    this.turn = turn * scale;
    this.encoderValue = (int)(feet * Constants.encoderTicksToFeet);
}

public Waypoint(double forward, double turn, int gyroAngle){
    exit = ExitCondition.GYRO;
    this.forward = forward * scale;
    this.turn = turn * scale;
    this.gyroAngle = gyroAngle;
    this.encoderValue = (int)(feet * Constants.encoderTicksToFeet);
}

}