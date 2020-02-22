/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;

/**
 * Add your docs here.
 */
public class SensorInput {
    public static DigitalInput IRSensor2 = new DigitalInput(9);
    public static AnalogInput m_ultrasonic = new AnalogInput(Constants.AIControlPanelSensor);
    public static boolean queuedShooter, queuedTrack1, queuedTrack2, queuedHood, queuedIntake;
    public static double sensor1Distance;

    public static DigitalInput IRSensor3 = new DigitalInput(7);
    public static DigitalInput IRSensor4 = new DigitalInput(8);
    public static DigitalInput IRSensor5 = new DigitalInput(4);
    public static DigitalInput IRSensor6 = new DigitalInput(6);

    public static void update(){
        queuedShooter = !IRSensor2.get();
        queuedTrack1 = !IRSensor3.get();
        queuedTrack2 = !IRSensor4.get();
        sensor1Distance = m_ultrasonic.getValue();
        queuedHood = !IRSensor5.get();
        queuedIntake = !IRSensor6.get();
    }
}
