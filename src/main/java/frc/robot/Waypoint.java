package frc.robot;

import frc.robot.DriveController.MoveParameters;

public abstract class Waypoint {
    public static double scale = 1.0;

public abstract void update(MoveParameters mP); 

public abstract boolean isComplete();

}


