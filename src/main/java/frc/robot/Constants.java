/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * Add your docs here.
 */
public class Constants{
    //Motor PID Control
    public static final double driveTrainkP = 1.6e-5;//5e-5;
    public static final double driveTrainkI = 0;
    public static final double driveTrainkD = 0; 
    public static final double driveTrainkIz = 0; 
    public static final double driveTrainkFF = 1.9e-4; 
    public static final double driveTrainkMaxOutput = 1; 
    public static final double driveTrainkMinOutput = -1;

    //Motors
    public static final double maxRPM = 5700;

    //Motor Controllers
    public static final int motorControllerStallLimit = 20;
    public static final int motorControllerFreeLimit = 20;
    
    //Power Port
    public static final double powerPortTolerance = 0.5;
    public static final double powerPortkP = 0.0225;
    public static final double powerPortkD = 0;//0.04;

    //Ball Chasing
    public static final double ballTolerance = 0;
    public static final double ballkP = 0.0013;
    public static final double ballkD = 0.004;
}