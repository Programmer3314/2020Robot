/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.DriveController.DriveState;
import frc.robot.DriveController.MoveParameters;

/**
 * Add your docs here.
 */
public class ControlPanelAlignment {
    public enum ControlPanelStates {
        START, ADJUST_TO_GYRO, CENTER_ALIGN, GYRO_0_ADJUST, FORWARD, DONE
    }

    ControlPanelStates currentState = ControlPanelStates.START;
    int scale = 0;
    int counter = 0;
    double distanceToWall;
    double pastGyro;
    double distanceOffSet;
    PDController controlPanel;

    public ControlPanelAlignment() {
        currentState = ControlPanelStates.START;

        controlPanel = new PDController(Constants.controlPanelkP, Constants.controlPanelkD);
        controlPanel.setToleranceValue(Constants.controlPanelTolerance);
        controlPanel.setMaxCorrectionValue(Constants.controlPanelMaxCorrection);
        controlPanel.setMinCorrectionValue(Constants.controlPanelMinCorrection);
    }

    public void update(MoveParameters mP) {
        distanceToWall = Robot.uSSensor.getDistanceFromWall2();

        switch (currentState) {
        case START:
            counter = 0;
            break;
        case ADJUST_TO_GYRO:
            if (Math.abs(Robot.cleanGyro - pastGyro) < 0.5) {
                counter++;
            } else {
                counter = 0;
            }

            if (counter >= 10) {
                currentState = ControlPanelStates.CENTER_ALIGN;
                counter = 0;
            }
            break;
        case CENTER_ALIGN:
            if (Math.abs(distanceToWall - 28) < 1) {
                currentState = ControlPanelStates.GYRO_0_ADJUST;
            }
            break;
        case GYRO_0_ADJUST:

            if (Math.abs(Robot.cleanGyro - pastGyro) < 0.5) {
                counter++;
            } else {
                counter = 0;
            }
            if (counter >= 10) {
                currentState = ControlPanelStates.FORWARD;
                counter = 0;
            }
            break;
        case FORWARD:
            if (counter >= 35) {
                currentState = ControlPanelStates.DONE;
            } else {
                counter++;
            }
            break;
        case DONE:

            break;
        }

        switch (currentState) {
        case START:
            break;
        case ADJUST_TO_GYRO:
            if (distanceToWall > 29) {
                mP.angle = distanceOffSet;
            } else if (distanceToWall < 27) {
                mP.angle = distanceOffSet;
            }
            mP.currentState = DriveState.TRENCHRUNALIGNMENT;
            mP.forward = 0;
            break;
        case CENTER_ALIGN:
            mP.currentState = DriveState.GYROLOCK;
            mP.forward = controlPanel.calculate(0, Math.abs(distanceToWall - 28));//-controlPanel.calculate(0, Math.abs(distanceToWall - 28));
            break;
        case GYRO_0_ADJUST:
            mP.currentState = DriveState.TRENCHRUNALIGNMENT;
            mP.forward = 0;
            mP.angle = 0;
            break;
        case FORWARD:
            mP.currentState = DriveState.GYROLOCK;
            mP.forward = -0.2;//0.2;
            break;
        case DONE:
            mP.currentState = DriveState.MANUAL;
            currentState = ControlPanelStates.START;
            break;

        }

        pastGyro = Robot.cleanGyro;
        SmartDashboard.putString("Control Panel Alignment State", currentState.toString());
    }

    public void activate() {
        currentState = ControlPanelStates.ADJUST_TO_GYRO;
        if((distanceToWall - 28) * 3 > 30){
            distanceOffSet = 30;
        }else{
            distanceOffSet = (distanceToWall - 28) * 3;
        }
    }

    public void resetState() {
        currentState = ControlPanelStates.START;
    }
}