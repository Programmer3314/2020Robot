/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration;
import com.ctre.phoenix.motorcontrol.TalonFXControlMode;
import com.ctre.phoenix.motorcontrol.TalonFXInvertType;
import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.ctre.phoenix.motorcontrol.can.TalonFXConfiguration;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class DrivetrainFalcon implements IDriveTrain{
    public TalonFX talon1, talon2, talon3, talon4;
    
    public DrivetrainFalcon(int topLeft, int bottomLeft, int topRight, int bottomRight){
        talon1 = new TalonFX(topLeft);
        talon2 = new TalonFX(bottomLeft);
        talon3 = new TalonFX(topRight);
        talon4 = new TalonFX(bottomRight);

        setMotors(talon1);
        setMotors(talon3);

        talon3.setInverted(true);

        talon2.follow(talon1);
        talon4.follow(talon3);

        talon2.setNeutralMode(NeutralMode.Brake);
        talon4.setNeutralMode(NeutralMode.Brake);

        // for safety... let's be explicit
        talon2.setInverted(TalonFXInvertType.FollowMaster);
        talon4.setInverted(TalonFXInvertType.FollowMaster);

    }

    public void setMotors(TalonFX fx){
        TalonFXConfiguration fxconfig = new TalonFXConfiguration();

        fx.configFactoryDefault();
        fx.getAllConfigs(fxconfig);
        fxconfig.closedloopRamp = 0.1; // seconds to go from 0 to full in closed loop
        // fxconfig.motionAcceleration = (int) (1000.0 * 2048 / 60.0 / 10); // raw sensor units per 100 ms
        // fxconfig.motionCruiseVelocity = (int) (3000.0 * 2048 / 60.0 / 10); // raw sensor units per 100 ms
        // fxconfig.motionCurveStrength = 0; // 0=>Trapezoidal, 1-8 for S-Curve
        // fxconfig.motionProfileTrajectoryPeriod = 50; // ms
        fxconfig.nominalOutputForward = 0; // minimum forward output
        fxconfig.nominalOutputReverse = 0; // minimum reverse output
        fxconfig.openloopRamp = 0.1; // seconds to go from 0 to full in "power" mode
        fxconfig.peakOutputForward = Constants.falconDrivetrainKMaxOutput; // [0,1]
        fxconfig.peakOutputReverse = Constants.falconDrivetrainKMinOutput; // [-1, 0]

        SupplyCurrentLimitConfiguration supplyLimit = new SupplyCurrentLimitConfiguration(true, 23.1, 25, 1.4);
        fxconfig.supplyCurrLimit = supplyLimit;
        //StatorCurrentLimitConfiguration statorLimit = new StatorCurrentLimitConfiguration(true, 12.1, 87.4, 0.4);
        //fxconfig.statorCurrLimit = statorLimit;

        fxconfig.slot0.kP = Constants.drivetrainKP;//5.000000; //
        fxconfig.slot0.kI = Constants.drivetrainKI; //
        fxconfig.slot0.kD = Constants.drivetrainKD;//0.020000; //
        fxconfig.slot0.kF = Constants.drivetrainKFF; //
        //fxconfig.slot0.integralZone = Constants.drivetrainKIz; //
        fxconfig.slot0.allowableClosedloopError = 217; //
        //fxconfig.slot0.maxIntegralAccumulator = 254.000000; //
        fxconfig.slot0.closedLoopPeakOutput = Constants.falconDrivetrainKMaxOutput; //    

        fx.configAllSettings(fxconfig);
    }


    // The "maxSpeed" for the falcons should be 
    // calculated as follows:
    // (from docs:  https://github.com/CrossTheRoadElec/Phoenix-Documentation/blob/master/source/ch14_MCSensor.rst#sensor-resolution)
    // maxSpeed = (kMaxRPM  / 600) * (kSensorUnitsPerRotation / kGearRatio)
    // where:
    //  kMaxRPM is the free speed of the Falocon (6380)
    //  kSensorUnitsPerRotation = 2048 
    //  kGearRatio = 1 
    //      This is the ratio of any gears between the motor and the sensor.
    //      Since the sensor is internal to the motor there are no gears separating 
    //      the sensor and the motor. 
    // Please adjust the below and test. 
    // 
    @Override
    public void update(double leftSetPoint, double rightSetPoint) {
        SmartDashboard.putNumber("Left Set Point", leftSetPoint);
        SmartDashboard.putNumber("Right Set Point", rightSetPoint);
        SmartDashboard.putNumber("Velocity of Left", talon1.getSelectedSensorVelocity());
        SmartDashboard.putNumber("Velocity of Right", talon3.getSelectedSensorVelocity());
        talon1.set(TalonFXControlMode.PercentOutput, leftSetPoint);
        talon3.set(TalonFXControlMode.PercentOutput, rightSetPoint);
        
    }
    
    @Override
    public double getEncoderVal(){
        return talon1.getSelectedSensorPosition();
    }
}
