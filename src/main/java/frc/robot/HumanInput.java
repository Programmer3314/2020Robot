/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Add your docs here.
 */
public class HumanInput {
    public static Joystick driverController = new Joystick(5);
    public static Joystick operatorController = new Joystick(4);
    public static Joystick buttonBox1 = new Joystick(2);
    public static Joystick buttonBox2 = new Joystick(3);
    public static Joystick stick = new Joystick(1);
    public static double forward, turn, throttle;
    public static boolean trenchRunAlignment, shooterAllInTarget, powerPortAlignmentButtonPressed, ballChaseButton, climbAlignmentButton, cameraChangeButton, controlPanelAlignment, reset, hoodUp, hoodDown;
    public static double TalonFxTestSpeed;
    public static boolean gyroLock;
    public static int autoNumber;
    public static boolean activateAuto;
    public static boolean shutDownAuto;

    public HumanInput(){
    
    }

    public static void update(){
        forward = driverController.getRawAxis(1);
        TalonFxTestSpeed = driverController.getRawAxis(3);
        turn = driverController.getRawAxis(4);
        throttle = stick.getRawAxis(2);
        autoNumber = (booleanToInt(buttonBox1.getRawButton(13)) * 1) + (booleanToInt(buttonBox1.getRawButton(14)) * 2) + (booleanToInt(buttonBox1.getRawButton(15)) * 4) + (booleanToInt(buttonBox1.getRawButton(16)) * 8);
        
        trenchRunAlignment = driverController.getRawButton(1);
        shooterAllInTarget = driverController.getRawButton(2);
        powerPortAlignmentButtonPressed = driverController.getRawButtonPressed(2);       
        ballChaseButton = driverController.getRawButton(3);
        climbAlignmentButton = driverController.getRawButton(4);
        cameraChangeButton = driverController.getRawButtonReleased(6);
        gyroLock = driverController.getRawAxis(2) > 0.5;
        controlPanelAlignment = buttonBox1.getRawButtonPressed(1);
        reset = buttonBox1.getRawButton(10);
        hoodUp = buttonBox1.getRawButton(2);
        hoodDown = buttonBox1.getRawButton(3);
        activateAuto = buttonBox1.getRawButtonPressed(7);
        shutDownAuto  = buttonBox1.getRawButtonReleased(7);
    }
    public static int booleanToInt(boolean gate){
        if(gate){
            return 1;
        }
        return 0;
    }
}