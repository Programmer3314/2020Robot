/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Solenoid;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class Solenoids {
    public static Compressor compressor = new Compressor();
    public static Solenoid intakeOut = new Solenoid(0, 0);
    public static Solenoid intakeIn = new Solenoid(0, 1);
    public static Solenoid engageRatchet = new Solenoid(0, 2);
    public static Solenoid disengageRatchet = new Solenoid(0, 3);
    public static Solenoid PTODisengage = new Solenoid(0, 4);
    public static Solenoid PTOEngage = new Solenoid(0, 5);
    public static Solenoid CPManipulatorDown = new Solenoid(0, 6);
    public static Solenoid CPManipulatorUp = new Solenoid(0, 7);
    public static Solenoid lightRingOn = new Solenoid(1, 0);
    public static Solenoid lightRingOff = new Solenoid(1, 1);
    

    public Solenoids() {
    }

    public static void init(){
        intakeOut.set(true);
        intakeIn.set(false);
        disengageRatchet.set(true);
        engageRatchet.set(false);
        PTODisengage.set(true);
        PTOEngage.set(false);
        CPManipulatorDown.set(true);
        CPManipulatorUp.set(false);
    }

    public static void update() {

        if (HumanInput.intakeOut) {
            intakeOut.set(true);
            intakeIn.set(false);
        }

        if (HumanInput.intakeIn) {
            intakeOut.set(false);
            intakeIn.set(true);
        }

        if (HumanInput.engageRatchet) {
            engageRatchet.set(true);
            disengageRatchet.set(false);
        }

        if (HumanInput.disengageRatchet) {
            engageRatchet.set(false);
            disengageRatchet.set(true);
        }

        if (HumanInput.PTODisengage) {
            PTODisengage.set(true);
            PTOEngage.set(false);
        }

        if (HumanInput.PTOEngage) {
            PTODisengage.set(false);
            PTOEngage.set(true);
        }

        if (HumanInput.CPManipulatorDown) {
            CPManipulatorDown.set(true);
            CPManipulatorUp.set(false);
        
        }

        if (HumanInput.CPManipulatorUp) {
            CPManipulatorDown.set(false);
            CPManipulatorUp.set(true);
        }

        // if(HumanInput.lightRing){
        //     lightRingOn.set(true);
        //     lightRingOff.set(false);
        // }

        // if(HumanInput.lightRingOff){
        //     lightRingOn.set(false);
        //     lightRingOff.set(true);
        // }

        SmartDashboard.putBoolean("Solenoid 0:", intakeOut.get());
        SmartDashboard.putBoolean("Solenoid 1:", intakeIn.get());
        SmartDashboard.putBoolean("Solenoid 2:", engageRatchet.get());
        SmartDashboard.putBoolean("Solenoid 3:", disengageRatchet.get());
        SmartDashboard.putBoolean("Solenoid 4:", PTODisengage.get());
        SmartDashboard.putBoolean("Solenoid 5:", PTOEngage.get());
        SmartDashboard.putBoolean("Solenoid 6:", CPManipulatorDown.get());
        SmartDashboard.putBoolean("Solenoid 7:", CPManipulatorUp.get());

    }

    public static void lightRing(boolean toggle){
        lightRingOn.set(toggle);
        lightRingOff.set(!toggle);
    }

    public static void startCompressor() {
        compressor.start();
    }

    public static void ejectIntake(boolean eject){
        intakeOut.set(eject);
        intakeIn.set(!eject);
    }

    public static void disengagePTO(){
        PTODisengage.set(true);
        PTOEngage.set(false);
    }
}