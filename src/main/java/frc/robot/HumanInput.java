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
    public static double forward, turn;
    public static boolean controlPanelAlignmentButton, powerPortAlignmentButton, ballChaseButton, climbAlignmentButton, cameraChangeButton; 
    public static double TalonFxTextSpeed;

    public HumanInput(){
    
    }

    public static void update(){
        forward = driverController.getRawAxis(1);
        TalonFxTextSpeed = driverController.getRawAxis(3);
        turn = driverController.getRawAxis(4);
        
        controlPanelAlignmentButton = driverController.getRawButton(1);
        powerPortAlignmentButton = driverController.getRawButton(2);
        ballChaseButton = driverController.getRawButton(3);
        climbAlignmentButton = driverController.getRawButton(4);
        cameraChangeButton = driverController.getRawButtonReleased(6);
    }
}