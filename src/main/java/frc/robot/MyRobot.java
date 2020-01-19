/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;

/**
 * Add your docs here.
 */
public abstract class MyRobot extends AllRobots {
    Drivetrain drivetrain;
    //Shooter shooter;

    // Sprak Max CAN IDs
    final int CANMcleftDriveFront = 1;
    final int CANMcleftDriveMiddle = 2;
    final int CANMcleftDriveBack = 3;
    final int CANMcrightDriveFront = 4;
    final int CANMcrightDriveMiddle = 5;
    final int CANMcrightDriveBack = 6;
    final int CANMcshooterLeft = 7;
    final int CANMcshooterRight = 8;
    final int CANMcctrlPanel = 67;

    // Color Sensor
    private final I2C.Port i2cPort = I2C.Port.kOnboard;
    private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);

    private final Color kBlueTarget = ColorMatch.makeColor(0.143, 0.427, 0.429);
    private final Color kGreenTarget = ColorMatch.makeColor(0.197, 0.561, 0.240);
    private final Color kRedTarget = ColorMatch.makeColor(0.561, 0.232, 0.114);
    private final Color kYellowTarget = ColorMatch.makeColor(0.361, 0.524, 0.113);
    private final ColorMatch m_colorMatcher = new ColorMatch();

    @Override
    public void MyRobotInit() {
        
        drivetrain = new Drivetrain(CANMcleftDriveFront, CANMcleftDriveMiddle, CANMcleftDriveBack, CANMcrightDriveFront, CANMcrightDriveMiddle, CANMcrightDriveBack);
        //shooter = new Shooter(CANMcshooterLeft, CANMcshooterRight);
        m_colorMatcher.addColorMatch(kBlueTarget);
        m_colorMatcher.addColorMatch(kGreenTarget);
        m_colorMatcher.addColorMatch(kRedTarget);
        m_colorMatcher.addColorMatch(kYellowTarget);
        

        RechargeRobotInit();
    }

    @Override
    public void MyAutonomousInit() {
        RechargeAutonomousInit();
    }

    @Override
    public void MyAutonomousPeriodic() {
        RechargeAutonomousPeriodic();
    }

    @Override
    public void MyTeleopInit() {
        RechargeTeleopInit();
    }

    @Override
    public void MyTeleopPeriodic() {
        Color detectedColor = m_colorSensor.getColor();
        String colorString;
        ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);

        if (match.color == kBlueTarget) {
            colorString = "Blue";
        } else if (match.color == kRedTarget) {
            colorString = "Red";
        } else if (match.color == kGreenTarget) {
            colorString = "Green";
        } else if (match.color == kYellowTarget) {
            colorString = "Yellow";
        } else {
            colorString = "Unknown";
        }

        SmartDashboard.putNumber("Red", detectedColor.red);
        SmartDashboard.putNumber("Green", detectedColor.green);
        SmartDashboard.putNumber("Blue", detectedColor.blue);
        SmartDashboard.putNumber("Confidence", match.confidence);
        SmartDashboard.putString("Detected Color", colorString);

        //SmartDashboard.putBoolean("useFixedSpeed", shooter.useFixedSpeed);
        RechargeTeleopPeriodic();
    }

    @Override
    public void MyTestInit() {
        RechargeTestInit();
    }

    @Override
    public void MyTestPeriodic() {
        RechargeTestPeriodic();
    }

    public abstract void RechargeRobotInit();

    public abstract void RechargeAutonomousInit();

    public abstract void RechargeAutonomousPeriodic();

    public abstract void RechargeTeleopInit();

    public abstract void RechargeTeleopPeriodic();

    public abstract void RechargeTestInit();

    public abstract void RechargeTestPeriodic();
}