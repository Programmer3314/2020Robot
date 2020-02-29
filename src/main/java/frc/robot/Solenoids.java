/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Solenoid;
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
    public static Solenoid disengagePTO = new Solenoid(0, 4);
    public static Solenoid engagePTO = new Solenoid(0, 5);
    public static Solenoid CPManipulatorDown = new Solenoid(0, 6);
    public static Solenoid CPManipulatorUp = new Solenoid(0, 7);
    public static Solenoid targettingLightRing = new Solenoid(1, 0);
    public static Solenoid confirmShooterLightRing = new Solenoid(1, 1);
    

    public Solenoids() {
    }

    public static void init(){
        intakeOut.set(false);
        intakeIn.set(true);
        disengageRatchet.set(false);
        engageRatchet.set(true);
        disengagePTO.set(true);
        engagePTO.set(false);
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

        if (HumanInput.disengagePTO) {
            disengagePTO.set(true);
            engagePTO.set(false);
        }

        if (HumanInput.engagePTO) {
            disengagePTO.set(false);
            engagePTO.set(true);
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

        SmartDashboard.putBoolean("Solenoid 0", intakeOut.get());
        SmartDashboard.putBoolean("Solenoid 1", intakeIn.get());
        SmartDashboard.putBoolean("Solenoid 2", engageRatchet.get());
        SmartDashboard.putBoolean("Solenoid 3", disengageRatchet.get());
        SmartDashboard.putBoolean("Solenoid 4", disengagePTO.get());
        SmartDashboard.putBoolean("Solenoid 5", engagePTO.get());
        SmartDashboard.putBoolean("Solenoid 6", CPManipulatorDown.get());
        SmartDashboard.putBoolean("Solenoid 7", CPManipulatorUp.get());

    }

    public static void targettingLightRing(boolean toggle){
        targettingLightRing.set(toggle);
    }

    public static void confirmShooterLightRing(boolean toggle){
        confirmShooterLightRing.set(toggle);
    }

    public static void startCompressor() {
        compressor.start();
    }

    public static void ejectIntake(boolean eject){
        intakeOut.set(eject);
        intakeIn.set(!eject);
    }

    public static void disengagePTO(){
        disengagePTO.set(true);
        engagePTO.set(false);
    }
}