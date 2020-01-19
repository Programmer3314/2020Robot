/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.networktables.NetworkTable;

/**
 * Add your docs here.
 */
public class DriveController {
    NetworkTable table;
    int scale = 1;
    boolean isForward = true;
    int camNum = 0;

    public enum DriveState {
        MANUAL, BALLCHASE, POWERPORTALIGNMENT, CLIMBALIGNMENT, CONTROLPANELALIGNMENT
    }

    private DriveState currentDriveState = DriveState.MANUAL;
    private Drivetrain drivetrain;
    private double forward, turn;

    public DriveController(Drivetrain drivetrain, NetworkTable table) {
        this.drivetrain = drivetrain;
        this.table = table;
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
            if (table == null)
                System.out.print("Table is Null");

            if (table.getEntry("Target Found") == null)
                System.out.print("Entry is Null");

            if (table.getEntry("Target Found").getBoolean(false)) {
                double centerX = table.getEntry("x").getDouble(0);
                forward = HumanInput.forward;
                turn = centerX / 1000.0;
            }

            break;
        case POWERPORTALIGNMENT:

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
