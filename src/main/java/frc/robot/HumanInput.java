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
    public static boolean leftSwitch, rightSwitch;
    public static boolean ejectIntake, spinIntake, reverseIntake; //
    public static boolean spinBallQueue, reverseBallQueue;
    public static boolean lightRing; //
    public static boolean fourSpins, spinToColor, manualControlPanel, putUpControlPanelManipulator;
    public static boolean winchItDown, creepOnBar, stopCreep, abortClimb;
    public static boolean sol0, sol1, sol2, sol3, sol4, sol5, sol6, sol7;

    public HumanInput(){
    
    }

    public static void update(){
        forward = driverController.getRawAxis(1);
        TalonFxTestSpeed = driverController.getRawAxis(3);
        turn = driverController.getRawAxis(4);
        throttle = stick.getRawAxis(2);
        autoNumber = (booleanToInt(buttonBox1.getRawButton(13)) * 1) + (booleanToInt(buttonBox1.getRawButton(14)) * 2) + (booleanToInt(buttonBox1.getRawButton(15)) * 4) + (booleanToInt(buttonBox1.getRawButton(16)) * 8);
        leftSwitch = buttonBox1.getRawButton(11);
        rightSwitch = buttonBox1.getRawButton(12);

        trenchRunAlignment = driverController.getRawButton(1);
        shooterAllInTarget = driverController.getRawButton(2);
        powerPortAlignmentButtonPressed = driverController.getRawButtonPressed(2);       
        ballChaseButton = driverController.getRawButton(3);
        climbAlignmentButton = driverController.getRawButton(4);
        cameraChangeButton = driverController.getRawButtonReleased(6);
        gyroLock = driverController.getRawAxis(2) > 0.5;

        if(!(leftSwitch) && !(rightSwitch)){ //ball + intake
            hoodUp = buttonBox1.getRawButton(2);
            hoodDown = buttonBox1.getRawButton(3);
            spinBallQueue = buttonBox1.getRawButton(4);
            reverseBallQueue = buttonBox1.getRawButton(5);
            // spinIntake = buttonBox1.getRawButton(6);
            // reverseIntake = buttonBox1.getRawButton(7);
        } else if(!(leftSwitch) && rightSwitch){ //shooter
            sol0 = buttonBox1.getRawButtonReleased(1);
            sol1 = buttonBox1.getRawButtonReleased(2);
            sol2 = buttonBox1.getRawButtonReleased(3);
            sol3 = buttonBox1.getRawButtonReleased(4);
            sol4 = buttonBox1.getRawButtonReleased(5);
            sol5 = buttonBox1.getRawButtonReleased(6);
            sol6 = buttonBox1.getRawButtonReleased(7);
            sol7 = buttonBox1.getRawButtonReleased(8);
        } else if(leftSwitch && !(rightSwitch)){ //control panel
            controlPanelAlignment = buttonBox1.getRawButtonPressed(1);
        } else if(leftSwitch && rightSwitch){ //climber + autos
            activateAuto = buttonBox1.getRawButtonPressed(7);
            shutDownAuto  = buttonBox1.getRawButtonReleased(7);
            reset = buttonBox1.getRawButton(10);
        }
    }
    public static int booleanToInt(boolean gate){
        if(gate){
            return 1;
        }
        return 0;
    }
}