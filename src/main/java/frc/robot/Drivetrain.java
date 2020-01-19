
package frc.robot;

import com.revrobotics.CANEncoder;
import com.revrobotics.CANPIDController;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;
import com.revrobotics.ControlType;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Drivetrain {
    public CANSparkMax spark1, spark2, spark3, spark4, spark5, spark6;
    private CANPIDController leftPidController, rightPidController;
    private CANEncoder leftEncoder, rightEncoder;


    public Drivetrain( int leftDriveFront, int leftDriveMiddle, int leftDriveBack, int rightDriveFront, int rightDriveMiddle, int rightDriveBack){
        spark1 = new CANSparkMax(leftDriveFront, CANSparkMaxLowLevel.MotorType.kBrushless);
        spark2 = new CANSparkMax(leftDriveMiddle, CANSparkMaxLowLevel.MotorType.kBrushless);
        spark3 = new CANSparkMax(leftDriveBack, CANSparkMaxLowLevel.MotorType.kBrushless);
        spark4 = new CANSparkMax(rightDriveFront, CANSparkMaxLowLevel.MotorType.kBrushless);
        spark5 = new CANSparkMax(rightDriveMiddle, CANSparkMaxLowLevel.MotorType.kBrushless);
        spark6 = new CANSparkMax(rightDriveBack, CANSparkMaxLowLevel.MotorType.kBrushless);

        spark1.restoreFactoryDefaults();
        spark2.restoreFactoryDefaults();
        spark3.restoreFactoryDefaults();
        spark4.restoreFactoryDefaults();
        spark5.restoreFactoryDefaults();
        spark6.restoreFactoryDefaults();

        spark4.setInverted(true);

        spark2.follow(spark1);
        spark3.follow(spark1);
        spark5.follow(spark4);
        spark6.follow(spark4);

        spark1.setSmartCurrentLimit(Constants.stallLimit, Constants.freeLimit);
        spark2.setSmartCurrentLimit(Constants.stallLimit, Constants.freeLimit);
        spark3.setSmartCurrentLimit(Constants.stallLimit, Constants.freeLimit);
        spark4.setSmartCurrentLimit(Constants.stallLimit, Constants.freeLimit);
        spark5.setSmartCurrentLimit(Constants.stallLimit, Constants.freeLimit);
        spark6.setSmartCurrentLimit(Constants.stallLimit, Constants.freeLimit);

        leftPidController = spark1.getPIDController();
        rightPidController = spark4.getPIDController();

        leftPidController.setP(Constants.kP);
        leftPidController.setI(Constants.kI);
        leftPidController.setD(Constants.kD);
        leftPidController.setIZone(Constants.kIz);
        leftPidController.setFF(Constants.kFF);
        leftPidController.setOutputRange(Constants.kMinOutput, Constants.kMaxOutput);

        rightPidController.setP(Constants.kP);
        rightPidController.setI(Constants.kI);
        rightPidController.setD(Constants.kD);
        rightPidController.setIZone(Constants.kIz);
        rightPidController.setFF(Constants.kFF);
        rightPidController.setOutputRange(Constants.kMinOutput, Constants.kMaxOutput);

        leftEncoder = spark1.getEncoder();
        rightEncoder = spark4.getEncoder();

        leftEncoder.setVelocityConversionFactor(1);
        rightEncoder.setVelocityConversionFactor(1);
    }

    public void update(double leftSetPoint, double rightSetPoint){
        leftPidController.setReference(leftSetPoint, ControlType.kVelocity);
        rightPidController.setReference(rightSetPoint, ControlType.kVelocity);

        Robot.ntInst.getEntry("RPM Left").setDouble(leftEncoder.getVelocity());
        Robot.ntInst.getEntry("Set Point Left").setDouble(leftSetPoint);
        SmartDashboard.putNumber("Current Flow ", spark1.getOutputCurrent());
    }
}