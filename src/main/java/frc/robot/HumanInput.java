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
    //public static Joystick stick = new Joystick(1);
    public static double forward, turn; //, throttle;
    public static boolean trenchRunAlignment, shooterAllInTarget, powerPortAlignment, ballChaseButton, climbAlignmentButton, driverCameraChange, operatorCameraChange, controlPanelAlignment, reset;
    public static boolean hoodUp, hoodDown, hoodUpReleased, hoodDownReleased;
    public static double TalonFxTestSpeed;
    public static boolean gyroLock, gyroReset;
    public static int autoNumber;
    public static boolean activateAuto;
    public static boolean shutDownAuto;
    public static boolean leftSwitch, rightSwitch;
    public static boolean activateIntake, activateGroundIntake, spinIntake, reverseIntake; //
    public static boolean spinBallQueue, reverseBallQueue;
    public static boolean lightRing; //
    public static boolean fourSpins, spinToColor, spinToBlue, spinToRed, spinToGreen, spinToYellow, manualControlPanel, CPManipulatorDown, CPManipulatorUp;
    public static double spinCP;
    public static boolean winchItDown, creepOnBar, stopCreep, abortClimb;
    public static boolean intakeOut, intakeIn, engageRatchet, disengageRatchet, PTODisengage, PTOEngage, lightRingOn, lightRingOff;
    public static boolean abortIntake;
    public static boolean testButton;
    public static boolean closeShot, lineShot, trenchShot;
    public static boolean traverseClimbState, operatorStart, operatorBack;
    private static boolean driverCameraChangeValue, lastDriverCameraChangeValue;

    public HumanInput(){    
    }

    public static void update(){
        //throttle = stick.getRawAxis(2);

        // Xbox Controller Mapping
        // The buttons on the controller follow this mapping
        // 1: A
        // 2: B
        // 3: X
        // 4: Y
        // 5: Left Bumper
        // 6: Right Bumper
        // 7: Back
        // 8: Start
        // 9: Left Joystick
        // 10: Right Joystick
      
        // Driver's Controller
        forward = driverController.getRawAxis(1) * 0.6;
        turn = driverController.getRawAxis(4) * 0.5;
        gyroLock = driverController.getRawAxis(2) > 0.5;
        trenchRunAlignment = driverController.getRawButton(3); // X
        ballChaseButton = driverController.getRawButton(4);    // Y
        driverCameraChangeValue = driverController.getPOV() == 0; // start
        driverCameraChange = (!lastDriverCameraChangeValue && driverCameraChangeValue);
        lastDriverCameraChangeValue = driverCameraChangeValue;
        activateGroundIntake = driverController.getRawButtonPressed(6);
        activateIntake = driverController.getRawButtonPressed(5);
        abortIntake = driverController.getRawButtonReleased(5) 
            || driverController.getRawButtonReleased(6);


        // Operator's Controller
        // Opps!!! Can you find the error in the commented line below? (no peeking at the line below that)
        //powerPortAlignment = (driverController.getRawButton(1) && HumanInput.operatorController.getRawAxis(2) < 0.5); // A
        powerPortAlignment = (operatorController.getRawButton(1) && operatorController.getRawAxis(2) < 0.5); // A
        closeShot = operatorController.getPOV() == 0;
        lineShot = operatorController.getPOV() == 90;
        trenchShot = operatorController.getPOV() == 180;
        shooterAllInTarget = (operatorController.getRawButton(2) && operatorController.getRawAxis(2) < 0.5); // B 
        operatorCameraChange = operatorController.getRawButtonReleased(6);
        operatorBack = operatorController.getRawButton(7);
        operatorStart = operatorController.getRawButton(8);

        CPManipulatorUp = operatorController.getRawAxis(2) > 0.5;
        CPManipulatorDown = !CPManipulatorUp;

        if(CPManipulatorUp){
            fourSpins = operatorController.getRawButtonReleased(9);
            spinCP = operatorController.getRawAxis(0);
            spinToGreen = operatorController.getRawButtonReleased(1);
            spinToRed = operatorController.getRawButtonReleased(2);
            spinToBlue = operatorController.getRawButtonReleased(3);
            spinToYellow = operatorController.getRawButtonReleased(4);
        }

        // Button Box
        autoNumber = (booleanToInt(buttonBox1.getRawButton(13)) * 1) + (booleanToInt(buttonBox1.getRawButton(14)) * 2) + (booleanToInt(buttonBox1.getRawButton(15)) * 4) + (booleanToInt(buttonBox1.getRawButton(16)) * 8);
        leftSwitch = buttonBox1.getRawButton(11);
        rightSwitch = buttonBox1.getRawButton(12);

        if(!(leftSwitch) && !(rightSwitch)){ //ball + intake
            hoodUp = buttonBox1.getRawButtonPressed(2);
            hoodUpReleased = buttonBox1.getRawButtonReleased(2);
            hoodDown = buttonBox1.getRawButtonPressed(3);
            hoodUpReleased = buttonBox1.getRawButtonReleased(3);
            spinBallQueue = buttonBox1.getRawButton(4);
            reverseBallQueue = buttonBox1.getRawButton(5);
            // spinIntake = buttonBox1.getRawButton(6);
            // reverseIntake = buttonBox1.getRawButton(7);
            activateIntake = buttonBox1.getRawButtonReleased(8);
            activateGroundIntake = buttonBox1.getRawButtonReleased(9);
            abortIntake = buttonBox1.getRawButtonReleased(10);
        } else if(!(leftSwitch) && rightSwitch){ //shooter
            intakeOut = buttonBox1.getRawButtonReleased(1);
            intakeIn = buttonBox1.getRawButtonReleased(2);
            lightRing = buttonBox1.getRawButtonReleased(9);
            // lightRingOff = buttonBox1.getRawButtonReleased(10);
        } else if(leftSwitch && !(rightSwitch)){ //control panel
            controlPanelAlignment = buttonBox1.getRawButtonPressed(1);
            // CPManipulatorDown = buttonBox1.getRawButtonReleased(7);
            // CPManipulatorUp = buttonBox1.getRawButtonReleased(8);
        } else if(leftSwitch && rightSwitch){ //climber + autos
            gyroReset = buttonBox1.getRawButtonReleased(1);
            engageRatchet = buttonBox1.getRawButtonReleased(3);
            disengageRatchet = buttonBox1.getRawButtonReleased(4);
            PTODisengage = buttonBox1.getRawButtonReleased(5);
            PTOEngage = buttonBox1.getRawButtonReleased(6);
            // activateAuto = buttonBox1.getRawButtonPressed(7);
            // shutDownAuto  = buttonBox1.getRawButtonReleased(7);
            reset = buttonBox1.getRawButtonReleased(9);
            abortClimb = buttonBox1.getRawButtonReleased(10);
        }
    }
    public static int booleanToInt(boolean gate){
        if(gate){
            return 1;
        }
        return 0;
    }
}