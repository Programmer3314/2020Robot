package frc.robot;

public class Waypoint {
    public double forward;
    public double turn;
    public long encoderValue;

public Waypoint(){

}

public Waypoint(double forward, double turn, long encoderValue){
    this.forward = forward;
    this.turn = turn;
    this.encoderValue = encoderValue;

}

}