/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.kauailabs.navx.frc.AHRS;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public abstract class MyRobot extends AllRobots {
    IDriveTrain drivetrain;
    public static Shooter shooter;
    TalonFXTest fxTest;
    public static DriveController driveController;
    ControlPanel controlPanel;
    public static UltraSonicSensor uSSensor;
    AnalogInput IRSensor;
    AnalogInput AInput;
    DigitalInput DInput;

    public static AHRS navx;
    public static double rawGyro, cleanGyro, ultraSonicDistance;
    public boolean isFalcon, hasShooter, hasControlPanel, isTalonFXTest;

    // Sprak Max CAN IDs NEO
    final int CANMcleftDriveFront = 1;
    final int CANMcleftDriveMiddle = 2;
    final int CANMcleftDriveBack = 3;
    final int CANMcrightDriveFront = 4;
    final int CANMcrightDriveMiddle = 5;
    final int CANMcrightDriveBack = 6;
    final int CANMcshooterLeft = 7;
    final int CANMcshooterRight = 8;

    // Talon FX CAN IDs Falcons
    final int CANMcFalconFrontLeft = 1;
    final int CANMcFalconBackLeft = 2;
    final int CANMcFalconFrontRight = 3;
    final int CANMcFalconBackRight = 4;

    // Talon SRX CAN IDs Bag Motors
    final int CANMcctrlPanel = 31;
    final int CANMcBallQueuing = 12;
    final int CANMcHood = 9;
    final int CANMcIndexer = 11;
    final int CANMcIntake = 10;

    public int counter = 0;

    @Override
    public void MyRobotInit() {
        SmartDashboard.putNumber("Target Offset", Constants.targettingOffset);

        navx = new AHRS(SPI.Port.kMXP);
        navx.reset();

        String ControllerVersion = "";
        try {
            CANSparkMax controllerCheck = new CANSparkMax(5, MotorType.kBrushless);
            SmartDashboard.putString("Controller Check 1", "Passed");
            ControllerVersion = controllerCheck.getFirmwareString();
            SmartDashboard.putString("Controller Check 4", ControllerVersion);
            controllerCheck.close();
        } catch (Exception cc) {
            SmartDashboard.putString("Controller Check 3", "CATCH");
        }
        if (ControllerVersion.equalsIgnoreCase("v0.0.0")) {
            isFalcon = true;
            hasShooter = true;
            isTalonFXTest = false;
            hasControlPanel = true;
        } else {
            isFalcon = false;
            hasShooter = true;
            isTalonFXTest = true;
            hasControlPanel = true;
        }

        if (isFalcon) {
            SmartDashboard.putString("DriveTrain Type", "Falcons");
            Constants.drivetrainTrackingMaxCorrection = Constants.falconTrackingMaxCorrection;
            Constants.drivetrainTrackingMinCorrection = Constants.falconTrackingMinCorrection;
            Constants.encoderTicksToFeet = Constants.falconEncoderTicksToFeet;
            Constants.maxRPM = Constants.falconMaxRPM;
            Constants.drivetrainKP = Constants.falconDrivetrainKP;
            Constants.drivetrainKI = Constants.falconDrivetrainKI;
            Constants.drivetrainKD = Constants.falconDrivetrainKD;
            Constants.drivetrainKIz = Constants.falconDrivetrainKIz;
            Constants.drivetrainKFF = Constants.falconDrivetrainKFF;
            Constants.drivetrainKMaxOutput = Constants.falconDrivetrainKMaxOutput;
            Constants.drivetrainKMinOutput = Constants.falconDrivetrainKMinOutput;
            drivetrain = new DrivetrainFalcon(CANMcFalconFrontLeft, CANMcFalconBackLeft, CANMcFalconFrontRight,
                    CANMcFalconBackRight);
        } else {
            SmartDashboard.putString("DriveTrain Type", "Neos");
            Constants.drivetrainTrackingMaxCorrection = Constants.neoMaxTrackingCorrection;
            Constants.drivetrainTrackingMinCorrection = Constants.neoTrackingMinCorrection;
            Constants.encoderTicksToFeet = Constants.neoEncoderTicksToFeet;
            Constants.maxRPM = Constants.neoMaxRPM;
            Constants.drivetrainKP = Constants.neoDrivetrainKP;
            Constants.drivetrainKI = Constants.neoDrivetrainKI;
            Constants.drivetrainKD = Constants.neoDrivetrainKD;
            Constants.drivetrainKIz = Constants.neoDrivetrainKIz;
            Constants.drivetrainKFF = Constants.neoDrivetrainKFF;
            Constants.drivetrainKMaxOutput = Constants.neoDrivetrainKMaxOutput;
            Constants.drivetrainKMinOutput = Constants.neoDrivetrainKMinOutput;
            drivetrain = new DrivetrainNEO(CANMcleftDriveFront, CANMcleftDriveMiddle, CANMcleftDriveBack,
                    CANMcrightDriveFront, CANMcrightDriveMiddle, CANMcrightDriveBack);
        }

        IRSensor = new AnalogInput(3);
        DInput = new DigitalInput(1);
        AInput = new AnalogInput(0);

        uSSensor = new UltraSonicSensor(Constants.USSensorMB1013ToInchFactor);
        RechargeRobotInit();
    }

    @Override
    public void MyAutonomousInit() {
        navx.reset();
        RechargeAutonomousInit();
    }

    @Override
    public void MyAutonomousPeriodic() {
        periodicInit();
        RechargeAutonomousPeriodic();
    }

    @Override
    public void MyTeleopInit() {
        navx.reset();
        RechargeTeleopInit();
    }

    @Override
    public void MyTeleopPeriodic() {
        periodicInit();

        // SmartDashboard.putBoolean("useFixedSpeed", shooter.useFixedSpeed);
        RechargeTeleopPeriodic();
    }

    @Override
    public void MyTestInit() {
        navx.reset();
        RechargeTestInit();
    }

    @Override
    public void MyTestPeriodic() {
        periodicInit();

        RechargeTestPeriodic();
    }

    private void periodicInit() {
        boolean IRSensorValue;
        if (IRSensor.getValue() > 3500) {
            IRSensorValue = false;
        } else {
            IRSensorValue = true;
        }

        if(!HumanInput.KILLALLGYROS){
            rawGyro = navx.getAngle();
        } else {
            rawGyro = 0;
        }
        
        cleanGyro = (rawGyro + 180 * Math.signum(rawGyro)) % 360 - 180 * Math.signum(rawGyro);
        ultraSonicDistance = uSSensor.getDistance();
        SmartDashboard.putNumber("Gyro Value", Robot.cleanGyro);
        SmartDashboard.putNumber("Ultra Sonic Distance in Inches", ultraSonicDistance);
        SmartDashboard.putNumber("Center of Robot to Wall", uSSensor.getDistanceFromWall2());
        SmartDashboard.putBoolean("IR Sensor is Blocked", IRSensorValue);
        SmartDashboard.putNumber("IR Sensor Value", IRSensor.getValue());
        // SmartDashboard.putNumber("Counter for Rich", counter++);
        SmartDashboard.putBoolean("New IR Sensor is Blocked", SensorInput.queuedShooter);
        SmartDashboard.putBoolean("Digital Input Test", DInput.get());
        SmartDashboard.putNumber("Analog Input Test", AInput.getValue());
        SmartDashboard.putBoolean("Track 1 Sensor", SensorInput.queuedTrack1);
        SmartDashboard.putBoolean("Track 2 Sensor", SensorInput.queuedTrack2);
        SmartDashboard.putBoolean("Shooter Sensor", SensorInput.queuedShooter);
        SmartDashboard.putBoolean("Hood Sensor", SensorInput.queuedHood);
        SmartDashboard.putBoolean("Intake Sensor", SensorInput.queuedIntake);
        SmartDashboard.putNumber("Operator POV Degrees", HumanInput.operatorController.getPOV());
    }

    public abstract void RechargeRobotInit();

    public abstract void RechargeAutonomousInit();

    public abstract void RechargeAutonomousPeriodic();

    public abstract void RechargeTeleopInit();

    public abstract void RechargeTeleopPeriodic();

    public abstract void RechargeTestInit();

    public abstract void RechargeTestPeriodic();
}