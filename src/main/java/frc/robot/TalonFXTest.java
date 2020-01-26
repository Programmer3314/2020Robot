/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;
import com.ctre.phoenix.motorcontrol.can.TalonFXPIDSetConfiguration;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class TalonFXTest {
    TalonFX fx;
    TalonFXConfiguration fxconfig;
    TalonFXPIDSetConfiguration fxpid;

    public TalonFXTest() {

        // this.joystick = joystick;
        fxconfig = new TalonFXConfiguration();
        fx = new TalonFX(21);
        fx.configFactoryDefault();
        fx.getAllConfigs(fxconfig);
        fxconfig.closedloopRamp = .1; // seconds to go from 0 to full in closed loop
        fxconfig.motionAcceleration = (int) (1000.0 * 2048 / 60.0 / 10); // raw sensor units per 100 ms
        fxconfig.motionCruiseVelocity = (int) (3000.0 * 2048 / 60.0 / 10); // raw sensor units per 100 ms
        fxconfig.motionCurveStrength = 0; // 0=>Trapezoidal, 1-8 for S-Curve
        fxconfig.motionProfileTrajectoryPeriod = 50; // ms
        fxconfig.nominalOutputForward = 0; // minimum forward output
        fxconfig.nominalOutputReverse = 0; // minimum reverse output
        fxconfig.openloopRamp = .1; // seconds to go from 0 to full in "power" mode
        fxconfig.peakOutputForward = 1; // [0,1]
        fxconfig.peakOutputReverse = -1; // [-1, 0]

        SupplyCurrentLimitConfiguration supplyLimit = new SupplyCurrentLimitConfiguration(true, 23.1, 25, 1.4);
        fxconfig.supplyCurrLimit = supplyLimit;
        StatorCurrentLimitConfiguration statorLimit = new StatorCurrentLimitConfiguration(true, 12.1, 87.4, 0.4);
        fxconfig.statorCurrLimit = statorLimit;

        fxconfig.slot0.kP = 5.000000; //
        fxconfig.slot0.kI = 0.000000; //
        fxconfig.slot0.kD = 0.020000; //
        fxconfig.slot0.kF = 19.300000; //
        fxconfig.slot0.integralZone = 900; //
        fxconfig.slot0.allowableClosedloopError = 217; //
        fxconfig.slot0.maxIntegralAccumulator = 254.000000; //
        fxconfig.slot0.closedLoopPeakOutput = 1.0; //
        //fxconfig.slot0.closedLoopPeriod = 33; //

        fx.configAllSettings(fxconfig);
        // fx.getPIDConfigs(fxpid);
        // fx.config_kP(0, Constants.kP);
        // fx.config_kI(0, Constants.kI);
        // fx.config_kD(0, Constants.kD);
        //// fx.config_IntegralZone(0, izone);
        //// Set Current Limits
    }

    public void Update() {
        SmartDashboard.putNumber("TalonFXReq", HumanInput.TalonFxTestSpeed);
        //fx.set(TalonFXControlMode.Velocity, HumanInput.TalonFxTestSpeed*21777.0);
        fx.set(TalonFXControlMode.PercentOutput, HumanInput.TalonFxTestSpeed);
        SmartDashboard.putNumber("Measured FX Velocity",fx.getSelectedSensorVelocity());
    }

}
