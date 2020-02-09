/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Add your docs here.
 */
public class DriveController {
    public class MoveParameters {
        public double forward = 0;
        public double turn = 0;
        public double angle = 0;
        public double distance = 0;
        public DriveState currentState = DriveState.NONE;
        public boolean cameraToggle = false;
    }

    NetworkTable ballTargetTable, retroTapeTable;
    int scaleForward = 1;
    double scaleTurn = 1;
    //int camNum = 0;
    double distanceToWall;
    PDController powerPortTracking, ballTracking, trenchTracking;

    public static enum DriveState {
        MANUAL, BALLCHASE, POWERPORTALIGNMENT, CLIMBALIGNMENT, TRENCHRUNALIGNMENT, GYROLOCK, CONTROLPANELALIGNMENT, TURN_TO_GYRO, NONE
    }

    public DriveState lastDriveState = DriveState.NONE;

    private IDriveTrain drivetrain;
    // private double forward, turn;
    private double angleOffset;
    private double gyroLockAngle;
    double encoderPos;

    public DriveController(IDriveTrain drivetrain, NetworkTable ballTargetTable, NetworkTable retrotapeTable) {
        this.drivetrain = drivetrain;
        this.ballTargetTable = ballTargetTable;
        this.retroTapeTable = retrotapeTable;
        powerPortTracking = new PDController(Constants.powerPortkP, Constants.powerPortkD);
        powerPortTracking.setToleranceValue(Constants.powerPortTolerance);
        powerPortTracking.setMaxCorrectionValue(Constants.maxCorrection);
        powerPortTracking.setMinCorrectionValue(Constants.minCorrection);
        ballTracking = new PDController(Constants.ballkP, Constants.ballkD);
        ballTracking.setToleranceValue(Constants.ballTolerance);
        ballTracking.setMaxCorrectionValue(Constants.maxCorrection);
        ballTracking.setMinCorrectionValue(Constants.minCorrection);
        trenchTracking = new PDController(Constants.trenchkP, Constants.trenchkD);
        trenchTracking.setToleranceValue(Constants.trenchTolerance);
        trenchTracking.setMaxCorrectionValue(Constants.maxCorrection);
        trenchTracking.setMinCorrectionValue(Constants.minCorrection);
    }

    public void update(MoveParameters mP) {
        scaleForward = 1;
        encoderPos = drivetrain.getEncoderVal();
        
        if (mP.currentState != lastDriveState) {
            switch (mP.currentState) {
            case POWERPORTALIGNMENT:
                angleOffset = retroTapeTable.getEntry("X Angle").getDouble(0);
                retroTapeTable.getEntry("gyro").setDouble(Robot.rawGyro);
                angleOffset += Robot.rawGyro;
                break;
            case GYROLOCK:
                gyroLockAngle = Robot.cleanGyro;
            }
        }

        distanceToWall = Robot.uSSensor.getDistanceFromWall2();

        switch (mP.currentState) {
        case MANUAL:
            // mP.forward = HumanInput.forward;
            // mP.turn = HumanInput.turn;
            break;
        case BALLCHASE:
            if (ballTargetTable == null)
                System.out.print("Table is Null");

            if (ballTargetTable.getEntry("Target Found") == null)
                System.out.print("Entry is Null");

            if (ballTargetTable.getEntry("Target Found").getBoolean(false)) {
                double centerX = ballTargetTable.getEntry("x").getDouble(0);
                //mP.forward = HumanInput.forward;
                mP.turn = -ballTracking.calculate(0, centerX);
            } else {
                ballTracking.reset();
            }

            // if (ballTargetTable.getEntry("Target Found").getBoolean(false)) {

            // //double angleOffset = ballTargetTable.getEntry("X Angle").getDouble(0);
            // ballTargetTable.getEntry("Set Point").setDouble(angleOffset);
            // ballTargetTable.getEntry("Actual Point").setDouble(Robot.gyro);
            // forward = HumanInput.forward;
            // turn = ballTracking.calculate(angleOffset, Robot.gyro);
            // ballTargetTable.getEntry("PD
            // turn").setDouble(ballTracking.calculate(angleOffset, Robot.gyro));
            // }else{
            // ballTracking.reset();
            // }

            SmartDashboard.putNumber("Turn output Value: ", mP.turn);

            break;
        case POWERPORTALIGNMENT:
            if (retroTapeTable == null)
                System.out.print("Retro Tape Table is Null");

            if (retroTapeTable.getEntry("Retroreflective Target Found") == null)
                System.out.print("Retro Tape Entry is Null");

            if (retroTapeTable.getEntry("Retroreflective Target Found").getBoolean(false)) {

                // double angleOffset = retroTapeTable.getEntry("X Angle").getDouble(0);
                retroTapeTable.getEntry("Set Point").setDouble(angleOffset);
                retroTapeTable.getEntry("Actual Point").setDouble(Robot.rawGyro);
                //mP.forward = HumanInput.forward;
                mP.turn = powerPortTracking.calculate(angleOffset, Robot.rawGyro);
                retroTapeTable.getEntry("PD turn").setDouble(powerPortTracking.calculate(angleOffset, Robot.rawGyro));
            } else {
                powerPortTracking.reset();
            }

            SmartDashboard.putNumber("Turn output Value: ", mP.turn);

            break;
        case CLIMBALIGNMENT:

            break;
        case TRENCHRUNALIGNMENT:
            //mP.forward = HumanInput.forward;
            mP.turn = -trenchTracking.calculate(Robot.cleanGyro, mP.angle);
            // if(Robot.cleanGyro > 15){
            // turn = -0.5;
            // }else if(Robot.cleanGyro < -15){
            // turn = 0.5;
            // }else if(Robot.cleanGyro > 0.5){
            // turn = -0.05;
            // }else if(Robot.cleanGyro < -0.5){
            // turn = 0.05;
            // }else{
            // turn = 0;
            // }

            break;
        case GYROLOCK:
            //mP.forward = HumanInput.forward;
            mP.turn = -trenchTracking.calculate(Robot.cleanGyro, gyroLockAngle);
            break;

        case CONTROLPANELALIGNMENT:
            if (distanceToWall > 29) {
                mP.forward = 0.05;
                mP.turn = 0;
            } else if (distanceToWall < 27) {
                mP.forward = -0.05;
                mP.turn = 0;
            } else {
                mP.forward = 0;
                mP.turn = -trenchTracking.calculate(Robot.cleanGyro, 0);
            }
            break;

        case TURN_TO_GYRO:
            mP.turn = powerPortTracking.calculate(mP.angle, Robot.cleanGyro);
            break;
        case NONE:
            mP.forward = 0;
            mP.turn = 0;
            break;
        }

        retroTapeTable.getEntry("Turn value").setDouble(mP.turn);
        SmartDashboard.putNumber("Turn", mP.turn);
        SmartDashboard.putNumber("Forward Value", mP.forward);
        SmartDashboard.putNumber("Set Gyro Value", gyroLockAngle);
        SmartDashboard.putString("Case State", mP.currentState.toString());

        double leftSetPoint = (mP.forward * scaleForward - mP.turn * scaleTurn);
        double rightSetPoint = (mP.forward * scaleForward + mP.turn * scaleTurn);

        lastDriveState = mP.currentState;
        drivetrain.update(leftSetPoint, rightSetPoint);
    }

}
