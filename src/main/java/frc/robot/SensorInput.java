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
    public static DigitalInput IRSensor2 = new DigitalInput(0);
    public static AnalogInput m_ultrasonic = new AnalogInput(Constants.AIControlPanelSensor);
    public static boolean hasBall;
    public static double sensor1Distance;

    public static void update(){
        hasBall = !IRSensor2.get();
        sensor1Distance = m_ultrasonic.getValue();
    }
}
