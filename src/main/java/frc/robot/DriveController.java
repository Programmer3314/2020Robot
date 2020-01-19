/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

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
    PDController powerPortTracking;

    public enum DriveState {
        MANUAL, BALLCHASE, POWERPORTALIGNMENT, CLIMBALIGNMENT, CONTROLPANELALIGNMENT
    }

    private DriveState currentDriveState = DriveState.MANUAL;
    private Drivetrain drivetrain;
    private double forward, turn;

    public DriveController(Drivetrain drivetrain, NetworkTable ballTargetTable, NetworkTable retrotapeTable) {
        this.drivetrain = drivetrain;
        this.ballTargetTable = ballTargetTable;
        this.retroTapeTable = retrotapeTable;
        powerPortTracking = new PDController(Constants.powerPortkP, Constants.powerPortkD);
        powerPortTracking.setToleranceValue(Constants.powerPortTolerance);
    }

    public void update() {
        if (HumanInput.ballChaseButton) {
            currentDriveState = DriveState.BALLCHASE;
        } else if (HumanInput.powerPortAlignmentButton) {
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
                turn = centerX / 1000.0;
            }

            break;
        case POWERPORTALIGNMENT:
            if (retroTapeTable == null)
                System.out.print("Retro Tape Table is Null");

            if (retroTapeTable.getEntry("Retroreflective Target Found") == null)
                System.out.print("Retro Tape Entry is Null");

            if (retroTapeTable.getEntry("Retroreflective Target Found").getBoolean(false)) {
                double centerX = retroTapeTable.getEntry("Retro x").getDouble(0);
                forward = HumanInput.forward;
                turn = powerPortTracking.calculate(0, centerX);
            }else{
                powerPortTracking.reset();
            }

            SmartDashboard.putNumber("Turn output Value: ", turn);

            break;
        case CLIMBALIGNMENT:

            break;
        case CONTROLPANELALIGNMENT:

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

        double leftSetPoint = (forward * scale - turn * 0.5) * Constants.maxRPM;
        double rightSetPoint = (forward * scale + turn * 0.5) * Constants.maxRPM;

        drivetrain.update(leftSetPoint, rightSetPoint);
    }
}
