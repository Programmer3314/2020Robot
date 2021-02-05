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
    public static boolean activateIntake, activateGroundIntake, spinIntake, reverseIntake, reverseIntakeReleased; //
    public static boolean spinBallQueue, reverseBallQueue;
    public static boolean lightRing; //
    public static boolean fourSpins, spinToColor, spinToFMSColor, spinToBlue, spinToRed, spinToGreen, spinToYellow, manualControlPanel, CPManipulatorDown, CPManipulatorUp;
    public static double spinCP;
    public static boolean winchItDown, creepOnBar, stopCreep, abortClimb;
    public static boolean intakeOut, intakeIn, engageRatchet, disengageRatchet, disengagePTO, engagePTO, lightRingOn, lightRingOff;
    public static boolean abortIntake;
    public static boolean testButton;
    public static boolean closeShot, lineShot, trenchShot, farShot;
    public static boolean traverseClimbState, operatorStart, operatorBack;
    // private static boolean driverCameraChangeValue, lastDriverCameraChangeValue;
    public static boolean abortShooter;
    public static boolean sLeft, KILLALLGYROS;
    public static boolean reverseAll, reverseAllReleased;

    // public static boolean KILLALLGYROS;

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
        forward = driverController.getRawAxis(1) * 1.0; //0.6
        turn = driverController.getRawAxis(4) * 0.5;
        if(Math.abs(turn) < Constants.humanInputDeadband){
            turn = 0;
        }else if(Math.abs(turn)< Constants.humanInputMinTurn){
            turn = Constants.humanInputMinTurn * Math.signum(turn);
        }
        gyroLock = driverController.getRawAxis(2) > 0.5;
        trenchRunAlignment = driverController.getRawButton(3); // X
        ballChaseButton = driverController.getRawButton(4);    // Y
        // driverCameraChangeValue = driverController.getPOV() == 0; // start
        // driverCameraChange = (!lastDriverCameraChangeValue && driverCameraChangeValue);
        // lastDriverCameraChangeValue = driverCameraChangeValue;
        driverCameraChange = driverController.getRawButtonPressed(2);
        activateGroundIntake = driverController.getRawButtonPressed(6);
        activateIntake = driverController.getRawButtonPressed(5);
        abortIntake = driverController.getRawButtonReleased(5) || driverController.getRawButtonReleased(6);
        reverseIntake = driverController.getRawButton(9);
        reverseIntakeReleased = driverController.getRawButtonReleased(9);


        // Operator's Controller
        // Opps!!! Can you find the error in the commented line below? (no peeking at the line below that)
        //powerPortAlignment = (driverController.getRawButton(1) && HumanInput.operatorController.getRawAxis(2) < 0.5); // A
        powerPortAlignment = (operatorController.getRawButton(1) && operatorController.getRawAxis(2) < 0.5); // A
        closeShot = operatorController.getPOV() == 0;
        lineShot = operatorController.getPOV() == 270;
        trenchShot = operatorController.getPOV() == 180;
        farShot = operatorController.getPOV() == 90;
        
        shooterAllInTarget = (operatorController.getRawButton(2) && operatorController.getRawAxis(2) < 0.5); // B 
        abortShooter = operatorController.getRawButtonReleased(5);
        operatorCameraChange = operatorController.getRawButtonReleased(6);
        operatorBack = operatorController.getRawButton(7);
        operatorStart = operatorController.getRawButton(8);

        CPManipulatorUp = operatorController.getRawAxis(2) > 0.5;
        CPManipulatorDown = !CPManipulatorUp;

        if(CPManipulatorUp){
            fourSpins = operatorController.getRawButtonReleased(9);
            spinCP = operatorController.getRawAxis(0);
            // spinToGreen = operatorController.getRawButtonReleased(1);
            // spinToRed = operatorController.getRawButtonReleased(2);
            // spinToBlue = operatorController.getRawButtonReleased(3);
            // spinToYellow = operatorController.getRawButtonReleased(4);
            // spinToFMSColor = operatorController.getRawButtonReleased(10);
        }

        // Button Box
        autoNumber = (booleanToInt(buttonBox1.getRawButton(13)) * 1) + (booleanToInt(buttonBox1.getRawButton(14)) * 2) + (booleanToInt(buttonBox1.getRawButton(15)) * 4) + (booleanToInt(buttonBox1.getRawButton(16)) * 8);
        leftSwitch = buttonBox1.getRawButton(11);
        rightSwitch = buttonBox1.getRawButton(12);
        sLeft = buttonBox2.getRawButton(13);
        KILLALLGYROS = buttonBox2.getRawButton(14);

        hoodUp = false;
        hoodUpReleased = false;
        hoodDown = false;
        hoodUpReleased = false;
        spinBallQueue = false;
        reverseBallQueue = false;
        intakeOut = false;
        intakeIn = false;
        lightRing = false;
        controlPanelAlignment = false;
        engageRatchet = false;
        disengageRatchet = false;
        disengagePTO = false;
        engagePTO = false;
        gyroReset = false;
        reset = false;
        abortClimb = false;
        
        if(!(leftSwitch) && !(rightSwitch)){ //ball + intake
            intakeOut = buttonBox1.getRawButtonReleased(1);
            intakeIn = buttonBox1.getRawButtonReleased(2);
            // activateIntake = buttonBox1.getRawButtonReleased(4);
            // activateGroundIntake = buttonBox1.getRawButtonReleased(5);
            spinIntake = buttonBox1.getRawButton(3);
            spinBallQueue = buttonBox1.getRawButton(4);
            reverseBallQueue = buttonBox1.getRawButton(4);
            reverseAll = buttonBox1.getRawButton(7);
            reverseAllReleased = buttonBox1.getRawButtonReleased(7);
            // spinIntake = buttonBox1.getRawButton(6);
            // reverseIntake = buttonBox1.getRawButton(7);
            abortIntake = buttonBox1.getRawButtonReleased(10) || abortIntake;
        } else if(!(leftSwitch) && rightSwitch){ //shooter
            hoodUp = buttonBox1.getRawButtonPressed(1);
            hoodUpReleased = buttonBox1.getRawButtonReleased(1);
            hoodDown = buttonBox1.getRawButtonPressed(2);
            hoodUpReleased = buttonBox1.getRawButtonReleased(2);
            lightRing = buttonBox1.getRawButtonReleased(7);
            abortShooter = abortShooter || buttonBox1.getRawButtonReleased(10);
            // lightRingOff = buttonBox1.getRawButtonReleased(10);
        } else if(leftSwitch && !(rightSwitch)){ //control panel
            controlPanelAlignment = buttonBox1.getRawButtonPressed(1);
            // CPManipulatorDown = buttonBox1.getRawButtonReleased(7);
            // CPManipulatorUp = buttonBox1.getRawButtonReleased(8);
        } else if(leftSwitch && rightSwitch){ //climber + autos
            engageRatchet = buttonBox1.getRawButtonReleased(1);
            disengageRatchet = buttonBox1.getRawButtonReleased(2);
            disengagePTO = buttonBox1.getRawButtonReleased(4);
            engagePTO = buttonBox1.getRawButtonReleased(5);
            // activateAuto = buttonBox1.getRawButtonPressed(7);
            // shutDownAuto  = buttonBox1.getRawButtonReleased(7);
            gyroReset = buttonBox1.getRawButtonReleased(7);
            reset = buttonBox1.getRawButtonReleased(8);
            abortClimb = buttonBox1.getRawButtonReleased(10);
        }
    }
    public static int booleanToInt(boolean gate){
        if(gate){
            return 1;
        }
        return 0;
    }

    public static void LogHeader() {
        Logger.Header("forward,turn,trenchRunAlignment,shooterAllInTarget,powerPortAlignment,"
            +"ballChaseButton,climbAlignmentButton,driverCameraChange,operatorCameraChange,"
            +"controlPanelAlignment,reset,"
            +"hoodUp,hoodDown,hoodUpReleased,hoodDownReleased,"
            +"gyroLock,gyroReset,autoNumber,"
            +"activateAuto,shutDownAuto,leftSwitch,rightSwitch,activateIntake, activateGroundIntake,"
            +"spinIntake,reverseIntake,reverseIntakeReleased,spinBallQueue,reverseBallQueue,lightRing,"
            +"fourSpins,spinToColor,spinToFMSColor,spinToBlue,spinToRed,spinToGreen,spinToYellow,"
            +"manualControlPanel,CPManipulatorDown,CPManipulatorUp,"
            +"spinCP,winchItDown,creepOnBar,stopCreep,abortClimb,"
            +"intakeOut,intakeIn,engageRatchet,disengageRatchet,disengagePTO,engagePTO,"
            +"lightRingOn,lightRingOff,"
            +"abortIntake,testButton,closeShot,lineShot,trenchShot,farShot,"
            +"traverseClimbState,operatorStart,operatorBack,"
            +"abortShooter,sLeft,KILLALLGYROS,reverseAll,reverseAllReleased,"
        );
    }

    public static void LogData() {

        Logger.doubles(forward, turn);
        Logger.booleans(trenchRunAlignment, shooterAllInTarget, powerPortAlignment, ballChaseButton, climbAlignmentButton, driverCameraChange, operatorCameraChange, controlPanelAlignment, reset);
        Logger.booleans(hoodUp, hoodDown, hoodUpReleased, hoodDownReleased);
        Logger.booleans(gyroLock, gyroReset);
        Logger.doubles(autoNumber);
        Logger.booleans(activateAuto);
        Logger.booleans(shutDownAuto);
        Logger.booleans(leftSwitch, rightSwitch);
        Logger.booleans(activateIntake, activateGroundIntake, spinIntake, reverseIntake, reverseIntakeReleased);
        Logger.booleans(spinBallQueue, reverseBallQueue);
        Logger.booleans(lightRing);
        Logger.booleans(fourSpins, spinToColor, spinToFMSColor, spinToBlue, spinToRed, spinToGreen, spinToYellow, manualControlPanel, CPManipulatorDown, CPManipulatorUp);
        Logger.doubles(spinCP);
        Logger.booleans(winchItDown, creepOnBar, stopCreep, abortClimb);
        Logger.booleans(intakeOut, intakeIn, engageRatchet, disengageRatchet, disengagePTO, engagePTO, lightRingOn, lightRingOff);
        Logger.booleans(abortIntake);
        Logger.booleans(testButton);
        Logger.booleans(closeShot, lineShot, trenchShot, farShot);
        Logger.booleans(traverseClimbState, operatorStart, operatorBack);
        Logger.booleans(abortShooter);
        Logger.booleans(sLeft, KILLALLGYROS);
        Logger.booleans(reverseAll, reverseAllReleased);

    }

}