/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class Solenoids {
    public static Compressor compressor = new Compressor();
    public static Solenoid sol0 = new Solenoid(0, 0);
    public static Solenoid sol1 = new Solenoid(0, 1);
    public static Solenoid sol2 = new Solenoid(0, 2);
    public static Solenoid sol3 = new Solenoid(0, 3);
    public static Solenoid sol4 = new Solenoid(0, 4);
    public static Solenoid sol5 = new Solenoid(0, 5);
    public static Solenoid sol6 = new Solenoid(0, 6);
    public static Solenoid sol7 = new Solenoid(0, 7);
    

    public Solenoids() {
    }

    public static void init(){
        sol0.set(true);
        sol1.set(false);
        sol2.set(true);
        sol3.set(false);
        sol4.set(true);
        sol5.set(false);
        sol6.set(true);
        sol7.set(false);
    }

    public static void update() {

        if (HumanInput.sol0) {
            sol0.set(true);
            sol1.set(false);
        }

        if (HumanInput.sol1) {
            sol0.set(false);
            sol1.set(true);
        }

        if (HumanInput.sol2) {
            sol2.set(true);
            sol3.set(false);
        }

        if (HumanInput.sol3) {
            sol2.set(false);
            sol3.set(true);
        }

        if (HumanInput.sol4) {
            sol4.set(true);
            sol5.set(false);
        }

        if (HumanInput.sol5) {
            sol4.set(false);
            sol5.set(true);
        }

        if (HumanInput.sol6) {
            sol6.set(true);
            sol7.set(false);

        }

        if (HumanInput.sol7) {
            sol6.set(false);
            sol7.set(true);
        }

        SmartDashboard.putBoolean("Solenoid 0:", sol0.get());
        SmartDashboard.putBoolean("Solenoid 1:", sol1.get());
        SmartDashboard.putBoolean("Solenoid 2:", sol2.get());
        SmartDashboard.putBoolean("Solenoid 3:", sol3.get());
        SmartDashboard.putBoolean("Solenoid 4:", sol4.get());
        SmartDashboard.putBoolean("Solenoid 5:", sol5.get());
        SmartDashboard.putBoolean("Solenoid 6:", sol6.get());
        SmartDashboard.putBoolean("Solenoid 7:", sol7.get());

    }

    public static void startCompressor() {
        compressor.start();
    }

}