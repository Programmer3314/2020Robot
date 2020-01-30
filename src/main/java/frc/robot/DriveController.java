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
    NetworkTable ballTargetTable, retroTapeTable;
    int scale = 1;
    boolean isForward = true;
    int camNum = 0;
    PDController powerPortTracking, ballTracking;

    public enum DriveState {
        MANUAL, 
        BALLCHASE, 
        POWERPORTALIGNMENT, 
        CLIMBALIGNMENT, CONTROLPANELALIGNMENT
    }

    private DriveState currentDriveState = DriveState.MANUAL;
    private IDriveTrain drivetrain;
    private double forward, turn, angleOffset;

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
    }

    public void update() {
        if (HumanInput.ballChaseButton) {
            // if(HumanInput.powerPortAlignmentButtonPressed){
            //     angleOffset = ballTargetTable.getEntry("x").getDouble(0);
            //     ballTargetTable.getEntry("gyro").setDouble(Robot.gyro);
            //     angleOffset += Robot.gyro;
            // }
            currentDriveState = DriveState.BALLCHASE;
        } else if (HumanInput.powerPortAlignmentButton) {
            if(HumanInput.powerPortAlignmentButtonPressed){
                angleOffset = retroTapeTable.getEntry("X Angle").getDouble(0);
                retroTapeTable.getEntry("gyro").setDouble(Robot.gyro);
                angleOffset += Robot.gyro;
            }
            currentDriveState = DriveState.POWERPORTALIGNMENT;
        } else if (HumanInput.controlPanelAlignmentButton) {
            currentDriveState = DriveState.CONTROLPANELALIGNMENT;
        } else if (HumanInput.climbAlignmentButton) {
            currentDriveState = DriveState.CLIMBALIGNMENT;
        } else {
            currentDriveState = DriveState.MANUAL;
        }

        switch (currentDriveState) {
        case MANUAL:
            forward = HumanInput.forward;
            turn = HumanInput.turn;
            break;
        case BALLCHASE:
            if (ballTargetTable == null)
                System.out.print("Table is Null");

            if (ballTargetTable.getEntry("Target Found") == null)
                System.out.print("Entry is Null");

            if (ballTargetTable.getEntry("Target Found").getBoolean(false)) {
                double centerX = ballTargetTable.getEntry("x").getDouble(0);
                forward = HumanInput.forward;
                turn = -ballTracking.calculate(0, centerX);
            }else{
                ballTracking.reset();
            }

            // if (ballTargetTable.getEntry("Target Found").getBoolean(false)) {
                
            //     //double angleOffset = ballTargetTable.getEntry("X Angle").getDouble(0);
            //     ballTargetTable.getEntry("Set Point").setDouble(angleOffset);
            //     ballTargetTable.getEntry("Actual Point").setDouble(Robot.gyro);
            //     forward = HumanInput.forward;
            //     turn = ballTracking.calculate(angleOffset, Robot.gyro);
            //     ballTargetTable.getEntry("PD turn").setDouble(ballTracking.calculate(angleOffset, Robot.gyro));
            // }else{
            //     ballTracking.reset();
            // }

            SmartDashboard.putNumber("Turn output Value: ", turn);

            break;
        case POWERPORTALIGNMENT:
            if (retroTapeTable == null)
                System.out.print("Retro Tape Table is Null");

            if (retroTapeTable.getEntry("Retroreflective Target Found") == null)
                System.out.print("Retro Tape Entry is Null");

            // if (retroTapeTable.getEntry("Retroreflective Target Found").getBoolean(false)) {
            //     double centerX = retroTapeTable.getEntry("Retro x").getDouble(0);
            //     forward = HumanInput.forward;
            //     turn = powerPortTracking.calculate(0, centerX);
            // }else{
            //     powerPortTracking.reset();
            // }

            if (retroTapeTable.getEntry("Retroreflective Target Found").getBoolean(false)) {
                
                //double angleOffset = retroTapeTable.getEntry("X Angle").getDouble(0);
                retroTapeTable.getEntry("Set Point").setDouble(angleOffset);
                retroTapeTable.getEntry("Actual Point").setDouble(Robot.gyro);
                forward = HumanInput.forward;
                turn = powerPortTracking.calculate(angleOffset, Robot.gyro);
                retroTapeTable.getEntry("PD turn").setDouble(powerPortTracking.calculate(angleOffset, Robot.gyro));
            }else{
                powerPortTracking.reset();
            }

            SmartDashboard.putNumber("Turn output Value: ", turn);

            break;
        case CLIMBALIGNMENT:

            break;
        case CONTROLPANELALIGNMENT:
            forward = HumanInput.forward;
            if(Robot.gyro > 10){
                turn = -0.4;
            }else if(Robot.gyro < -10){
                turn = 0.4;
            }else if(Robot.gyro > 1){
                turn = -0.05;
            }else if(Robot.gyro < -1){
                turn = 0.05;
            }else{
                turn = 0;
            }


            SmartDashboard.putNumber("Gyro value:", Robot.gyro);

            break;
        }

        if (HumanInput.cameraChangeButton) {
            isForward = !isForward;

            if (isForward) {
                camNum = 0;
                scale = 1;
            } else {
                camNum = 1;
                scale = -1;
            }

            Robot.ntInst.getEntry("chooseCam").setNumber(camNum);
        }

        retroTapeTable.getEntry("Turn value").setDouble(turn);
        SmartDashboard.putNumber("Forward Value", forward);

        double leftSetPoint = (forward * scale - turn * 0.5);
        double rightSetPoint = (forward * scale + turn * 0.5);

        drivetrain.update(leftSetPoint, rightSetPoint);
    }
}
