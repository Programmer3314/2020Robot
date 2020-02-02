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
    public static double forward, turn;
    public static boolean trenchRunAlignment, powerPortAlignmentButton, powerPortAlignmentButtonPressed, ballChaseButton, climbAlignmentButton, cameraChangeButton, controlPanelAlignment;
    public static double TalonFxTestSpeed;
    public static boolean gyroLock;

    public HumanInput(){
    
    }

    public static void update(){
        forward = driverController.getRawAxis(1);
        TalonFxTestSpeed = driverController.getRawAxis(3);
        turn = driverController.getRawAxis(4);
        
        trenchRunAlignment = driverController.getRawButton(1);
        powerPortAlignmentButton = driverController.getRawButton(2);
        powerPortAlignmentButtonPressed = driverController.getRawButtonPressed(2);       
        ballChaseButton = driverController.getRawButton(3);
        climbAlignmentButton = driverController.getRawButton(4);
        cameraChangeButton = driverController.getRawButtonReleased(6);
        gyroLock = driverController.getRawAxis(2) > 0.5;
        controlPanelAlignment = buttonBox1.getRawButtonPressed(1);
    }
}