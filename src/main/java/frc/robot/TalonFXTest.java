/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFXPIDSetConfiguration;

import edu.wpi.first.wpilibj.Joystick;

/**
 * Add your docs here.
 */
public class TalonFXTest {
TalonFX fx;
TalonFXConfiguration fxconfig;
TalonFXPIDSetConfiguration fxpid;

public TalonFXTest() {

    //this.joystick = joystick;
    //fxconfig = new TalonFXConfiguration();
    fx = new TalonFX(21);
    fx.configFactoryDefault();
    // fx.getAllConfigs(fxconfig);
    // fxconfig.closedloopRamp = 3; //seconds to go from 0 to full in closed loop 
    // fxconfig.motionAcceleration=(int)(1000.0*2048/60.0/10); // raw sensor units per 100 ms
    // fxconfig.motionCruiseVelocity = (int)(3000.0*2048/60.0/10); // raw sensor units per 100 ms
    // fxconfig.motionCurveStrength = 0; //0=>Trapezoidal, 1-8 for S-Curve
    // fxconfig.motionProfileTrajectoryPeriod = 50; // ms
    // fxconfig.nominalOutputForward = 0; // TODO: What is this? min movment value?
    // fxconfig.nominalOutputReverse = 0; // TODO: What is this
    // fxconfig.openloopRamp = 1; //seconds to go from 0 to full in "power" mode
    // fxconfig.peakOutputForward = 1; // [0,1]
    // fxconfig.peakOutputReverse = -1; // [-1, 0]
    // fxconfig.statorCurrLimit.currentLimit = 20; // continuous limit once triggered
    // fxconfig.statorCurrLimit.triggerThresholdCurrent = 30; // trigger threshold
    // fxconfig.statorCurrLimit.triggerThresholdTime = .5; // time in seconds
    // fxconfig.statorCurrLimit.enable = true;
    // fx.configAllSettings(fxconfig);
    // fx.getPIDConfigs(fxpid);
    // fx.config_kP(0, Constants.kP);
    // fx.config_kI(0, Constants.kI);
    // fx.config_kD(0, Constants.kD);
    //// fx.config_IntegralZone(0, izone);
    //// Set Current Limits
}

public void Update() {
    double value = HumanInput.driverController.getRawAxis(3);
    fx.set(TalonFXControlMode.PercentOutput, value);
}


}
