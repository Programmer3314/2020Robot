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

    //Motor Specific Constants
    public static final int neoMaxRPM       = 5700;
    public static final int falconMaxRPM    = 6380;
    public static final int bagMaxRPM       = 13000;

    // TODO: Review: change to constants below. 
    // We had raised min correction for the falcons, but it did 
    // nothing because the real values were being set in MyRobot.
    // They still are being set there, but now they are set with 
    // the additional constants below. 

    // Drivetrain Specific Constants...
    public static double drivetrainKP = 0;//5e-5;
    public static double drivetrainKI = 0;
    public static double drivetrainKD = 0; 
    public static double drivetrainKIz = 0; 
    public static double drivetrainKFF = 0; 
    public static double drivetrainKMaxOutput = 0; 
    public static double drivetrainKMinOutput = 0;
    public static double drivetrainTrackingMaxCorrection = 0.0; // = 0.1;
    public static double drivetrainTrackingMinCorrection = 0.0; // = 0.07;
    public static int    maxRPM = 0;
    public static int    encoderTicksToFeet = 1;
    // Falcon chassis set
    public static final double falconDrivetrainKP = 0.002500;//5e-5;
    public static final double falconDrivetrainKI = 0.000000;
    public static final double falconDrivetrainKD = 0; 
    public static final double falconDrivetrainKIz = 0; 
    public static final double falconDrivetrainKFF = 0.050000; 
    public static final double falconDrivetrainKMaxOutput =  0.869990; 
    public static final double falconDrivetrainKMinOutput = -0.869990;
    public static final double falconTrackingMaxCorrection = 0.4; // = 0.1;
    public static final double falconTrackingMinCorrection = 0.04; // = 0.07;
    public static final int    falconEncoderTicksToFeet = 12527;
    // Neo chassis set
    public static final double neoDrivetrainKP = 1.6e-5;//5e-5;
    public static final double neoDrivetrainKI = 0;
    public static final double neoDrivetrainKD = 0; 
    public static final double neoDrivetrainKIz = 0; 
    public static final double neoDrivetrainKFF = 1.9e-4; 
    public static final double neoDrivetrainKMaxOutput = 1; 
    public static final double neoDrivetrainKMinOutput = -1;
    public static final double neoMaxTrackingCorrection = 0.1;
    public static final double neoTrackingMinCorrection = 0.04;
    public static final int    neoEncoderTicksToFeet = 1; // this needs to be fixed...

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
    //public static final double shooterRPM = 3200;
    public static final double shooterkP = 7.0e-4;
    public static final double shooterkI = 0.000001;
    public static final double shooterkIz = 50;
    public static final double shooterkIMax = 0.1;
    public static final double shooterkD = 0;
    public static final double shooterkFF = 1.84e-4;
    public static final double shooterkMaxOutput = 1; 
    public static final double shooterkMinOutput = -1;
    //Shooter Encoder
    public static final double sparkShooterVelocityConversionFactor = 1.0;

    //Hood Encoder
    public static final double hoodkP = 0.0025;
    public static final double hoodkD = 0.00005;
    public static final double hoodkMinCorrection = 0.04;
    public static final double hoodkMaxCorrection = 0.2;
    public static final double hoodkTolerance = 10.0;

    //trench
    public static final double trenchTolerance = 1;
    public static final double trenchkP = 0.006;
    public static final double trenchkD = 0.012;

    //Control Panel
    public static final double controlPanelTolerance = 1;
    public static final double controlPanelkP = 0.02;
    public static final double controlPanelkD = 0;
    public static final double controlPanelMinCorrection = 0.05;
    public static final double controlPanelMaxCorrection = 0.1;

    //Ultra Sonic Sensor
    public static final int AIControlPanelSensor = 2;
    public static final double USSensorMB1013ToInchFactor = 0.0492126;
}