/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveController {
    public class MoveParameters {
        public double forward = 0;
        public double turn = 0;
        public double angle = 0;
        public double distance = 0;
        public DriveState currentState = DriveState.NONE;
        public boolean driverCameraToggle = false;

        public void LogHeader() {
            Logger.Header( "forward,turn,angle,distance,currentState,driverCameraToggle,");
        }
    
        public void LogData() {
            Logger.doubles(forward);
            Logger.doubles(turn);
            Logger.doubles(angle);
            Logger.doubles(distance);
            Logger.singleEnum(currentState); 
            Logger.booleans(driverCameraToggle);

        }
    }

    NetworkTable ballTargetTable, retroTapeTable;
    // int scaleForward = 1;
    // int scaleForward = -1;
    // double scaleTurn = 1;
    // // int camNum = 0;
    // double distanceToWall;
    PDController powerPortTracking, ballTracking, trenchTracking;

    public static enum DriveState {
        MANUAL, BALLCHASE, POWERPORTALIGNMENT, SHOOTERPOWERPORTALIGNMENT, CLIMBALIGNMENT, TRENCHRUNALIGNMENT, GYROLOCK,
        CONTROLPANELALIGNMENT, TURN_TO_GYRO, NONE
    }

    public DriveState lastDriveState = DriveState.NONE;

    private IDriveTrain drivetrain;
    // private double forward, turn;
    // private double angleOffset;
    int scaleForward = -1;
    double scaleTurn = 1;
    double distanceToWall;
    public static double angleOffset;
    private double gyroLockAngle;
    double encoderPos;

    public DriveController(IDriveTrain drivetrain, NetworkTable ballTargetTable, NetworkTable retrotapeTable) {
        this.drivetrain = drivetrain;
        this.ballTargetTable = ballTargetTable;
        this.retroTapeTable = retrotapeTable;
        powerPortTracking = new PDController(Constants.powerPortkP, Constants.powerPortkD);
        powerPortTracking.setToleranceValue(Constants.powerPortTolerance);
        powerPortTracking.setMaxCorrectionValue(Constants.drivetrainTrackingMaxCorrection);
        powerPortTracking.setMinCorrectionValue(0.085/*0.1*/);
        ballTracking = new PDController(Constants.ballkP, Constants.ballkD);
        ballTracking.setToleranceValue(Constants.ballTolerance);
        ballTracking.setMaxCorrectionValue(Constants.drivetrainTrackingMaxCorrection);
        ballTracking.setMinCorrectionValue(Constants.drivetrainTrackingMinCorrection);
        trenchTracking = new PDController(Constants.trenchkP, Constants.trenchkD);
        trenchTracking.setToleranceValue(Constants.trenchTolerance);
        trenchTracking.setMaxCorrectionValue(Constants.drivetrainTrackingMaxCorrection);
        trenchTracking.setMinCorrectionValue(Constants.drivetrainTrackingMinCorrection);
    }

    public void update(MoveParameters mP) {
        // scaleForward = 1;
        scaleForward = -1;
        encoderPos = drivetrain.getEncoderVal();

        if (mP.currentState != lastDriveState) {
            switch (mP.currentState) {
                case BALLCHASE:
                case CLIMBALIGNMENT:
                case CONTROLPANELALIGNMENT:
                case MANUAL:
                case NONE:
                case SHOOTERPOWERPORTALIGNMENT:
                case TRENCHRUNALIGNMENT:
                case TURN_TO_GYRO:
                    break;

                case POWERPORTALIGNMENT:
                    angleOffset = retroTapeTable.getEntry("X Angle").getDouble(0);
                    retroTapeTable.getEntry("gyro").setDouble(Robot.rawGyro);
                    double temp = SmartDashboard.getNumber("Target Offset", 0);
                    angleOffset += Robot.rawGyro + temp;
                    break;

                case GYROLOCK:
                    gyroLockAngle = Robot.cleanGyro;
                    break;
            }
        }

        distanceToWall = Robot.uSSensor.getDistanceFromWall2();

        switch (mP.currentState) {
        case MANUAL:
            break;
        case BALLCHASE:
            if (ballTargetTable == null)
                System.out.print("Table is Null");

            if (ballTargetTable.getEntry("Target Found") == null)
                System.out.print("Entry is Null");

            if (ballTargetTable.getEntry("Target Found").getBoolean(false)) {
                double centerX = ballTargetTable.getEntry("x").getDouble(0);
                // mP.forward = HumanInput.forward;
                mP.turn = -ballTracking.calculate(0, centerX * .6/*.8*/);
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

            SmartDashboard.putNumber("Turn Output Value", mP.turn);

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
                // mP.forward = HumanInput.forward;
                mP.turn = powerPortTracking.calculate(angleOffset, Robot.rawGyro);
                retroTapeTable.getEntry("PD turn").setDouble(powerPortTracking.calculate(angleOffset, Robot.rawGyro));
            } else {
                powerPortTracking.reset();
            }

            SmartDashboard.putNumber("Turn Output Value", mP.turn);

            break;
        case SHOOTERPOWERPORTALIGNMENT:
            if (retroTapeTable == null)
                System.out.print("Retro Tape Table is Null");

            if (retroTapeTable.getEntry("Retroreflective Target Found") == null){
                System.out.print("Retro Tape Entry is Null");
            }

            if (retroTapeTable.getEntry("Retroreflective Target Found").getBoolean(false)) {
                angleOffset = retroTapeTable.getEntry("X Angle").getDouble(0);
                retroTapeTable.getEntry("Set Point").setDouble(angleOffset);
                mP.turn = powerPortTracking.calculate(angleOffset, 0);
                retroTapeTable.getEntry("PD turn").setDouble(powerPortTracking.calculate(angleOffset, 0));
            } else {
                powerPortTracking.reset();
                mP.turn = 0;
            }

            SmartDashboard.putNumber("Turn Output Value", mP.turn);
            break;
        case CLIMBALIGNMENT:

            break;
        case TRENCHRUNALIGNMENT:
            mP.turn = -trenchTracking.calculate(Robot.cleanGyro, mP.angle);
            break;
        case GYROLOCK:
            mP.turn = -trenchTracking.calculate(Robot.cleanGyro, gyroLockAngle);
            break;

        case CONTROLPANELALIGNMENT:
            if (distanceToWall > 29) {
                mP.forward = -0.05;//0.05;
                mP.turn = 0;
            } else if (distanceToWall < 27) {
                mP.forward = 0.05;//-0.05;
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

        retroTapeTable.getEntry("Turn Value").setDouble(mP.turn);
        SmartDashboard.putNumber("Turn", mP.turn);
        SmartDashboard.putNumber("Forward Value", mP.forward);
        SmartDashboard.putNumber("Set Gyro Value", gyroLockAngle);
        SmartDashboard.putString("Drive State", mP.currentState.toString());

        double leftSetPoint = (mP.forward * scaleForward - mP.turn * scaleTurn);
        double rightSetPoint = (mP.forward * scaleForward + mP.turn * scaleTurn);

        lastDriveState = mP.currentState;
        drivetrain.update(leftSetPoint, rightSetPoint);
        
    }

    public void LogHeader() {
        Logger.Header("scaleForward,scaleTurn,distanceToWall,angleOffset,cleanGyro,gyroDiff,gyroLockAngle,encoderPos,");
    }

    public void LogData() {
        Logger.doubles(scaleForward);
        Logger.doubles(scaleTurn);
        Logger.doubles(distanceToWall);
        Logger.doubles(angleOffset);
        Logger.doubles(Robot.cleanGyro);
        Logger.doubles(Math.abs(angleOffset - Robot.cleanGyro));
        Logger.doubles(gyroLockAngle); 
        Logger.doubles(encoderPos);
    }
}
