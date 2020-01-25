/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * Add your docs here.
 */
public class BagMotorTest {
    private TalonSRX talon31;

    public BagMotorTest(){
        talon31 = new TalonSRX(31);

        talon31.configFactoryDefault();
    }

    public void update(){
        talon31.set(ControlMode.PercentOutput, HumanInput.driverController.getRawAxis(0));
        Robot.ntInst.getEntry("Encoder Value").setDouble(talon31.getSelectedSensorPosition());
    }
}