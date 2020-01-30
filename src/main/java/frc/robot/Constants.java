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
    public static final double maxRPMNeo = 5700;
    public static final double maxRPMFX = 6380;
    public static final double maxCorrection = 0.2;
    public static final double minCorrection = 0.04;

    //Motor Controllers
    public static final int sparkDriveTrainStallLimit = 20;
    public static final int sparkDriveTrainFreeLimit = 20;
    
    //Power Port
    public static final double powerPortTolerance = 0.5;
    public static final double powerPortkP = 0.0225;
    public static final double powerPortkD = 0;//0.04;

    //Ball Chasing
    public static final double ballTolerance = 8;
    public static final double ballkP = 0.0008;//.00065->.0009->.0008
    public static final double ballkD = 0.0009;//.00009->.0009

    //Shooter Motors
    public static final int sparkShooterStallLimit = 40;
    public static final int sparkShooterFreeLimit = 40;

    //Shooter Motor Controllers


    //Shooter Encoder
    public static final double sparkShooterVelocityConversionFactor = 1.0;
}