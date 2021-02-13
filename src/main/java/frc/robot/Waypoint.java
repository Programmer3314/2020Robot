package frc.robot;

public class Waypoint {
    public double forward;
    public double turn;
    public long encoderValue;
    public double scale = 1.1;

public Waypoint(){

}

public Waypoint(double forward, double turn, double feet){
    this.forward = forward * scale;
    this.turn = turn * scale;
    this.encoderValue = (int)(feet * Constants.encoderTicksToFeet);

}

}