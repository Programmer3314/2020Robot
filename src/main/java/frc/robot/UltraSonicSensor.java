/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class UltraSonicSensor {
    
    private static double kValueToInches;
    private double sensorWidthFromCenter = 10.5;
    private double sensorLengthFromCenter = 8;
    private double sensorHypFromCenter = Math.sqrt(Math.pow(sensorWidthFromCenter,2) + Math.pow(sensorLengthFromCenter,2));
    private double theta2;
    private double theta3;
    private double perpDistanceFromSensorToWall;
    private double insideTriangleLength;
    private double distanceFromRobotCenterToWall; 
    private double distance;
    //private double distance 
    
    public UltraSonicSensor( double conversionFactor){ 
        kValueToInches = conversionFactor;
    }

    public double getDistance(){
        return SensorInput.sensor1Distance * kValueToInches;
    }

    public double getDistanceFromWall2(){
        distance = getDistance();
        double gyro = Robot.cleanGyro;
        theta2 = (Math.atan(sensorWidthFromCenter / sensorLengthFromCenter) * Robot.radtodeg);
        theta3 = 90 - gyro - theta2;
        perpDistanceFromSensorToWall = (Math.sin((90-gyro)*Robot.degtorad)* distance);
        insideTriangleLength = Math.cos(theta3*Robot.degtorad)* sensorHypFromCenter;
        distanceFromRobotCenterToWall =  insideTriangleLength + perpDistanceFromSensorToWall;


        SmartDashboard.putNumber("Get Distance", distance);
        SmartDashboard.putNumber("Gyro Angle For Sensor",gyro);
        SmartDashboard.putNumber("Inside length", insideTriangleLength);
        SmartDashboard.putNumber("Outside length", perpDistanceFromSensorToWall);
        SmartDashboard.putNumber("Sensor Hyp. form center",sensorHypFromCenter);
        SmartDashboard.putNumber("Theta2",theta2);
        SmartDashboard.putNumber("Thata3",theta3);
        SmartDashboard.putNumber("Melvin's number for distance", (distance * Math.cos(gyro * Robot.degtorad) + (Math.sqrt(174.25) * Math.cos(Math.atan(8 / 10.5) - (gyro * Robot.degtorad)))));
        return distanceFromRobotCenterToWall;
    }
   
}
