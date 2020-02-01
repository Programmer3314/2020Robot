/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.AnalogInput;

/**
 * Add your docs here.
 */
public class UltraSonicSensor {
    private final AnalogInput m_ultrasonic = new AnalogInput(2);
    private static final double kValueToInches = 0.0492126;

    public UltraSonicSensor(){

    }
    
    public double getDistance(){
        return m_ultrasonic.getValue() * kValueToInches;
    }
}
