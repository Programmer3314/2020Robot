/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * Add your docs here.
 */
public class TrenchAlignment {
    public enum ControlPanelAlignment {
        CENTER_ALIGN, ADJUST, FORWARD, DONE
    }

    ControlPanelAlignment currentState = ControlPanelAlignment.CENTER_ALIGN;
    int scale = 0;
    int counter = 0;
    double distanceToWall;
    double pastGyro;

    public void update() {
        distanceToWall = Robot.uSSensor.getDistanceFromWall2();
        
        switch (currentState) {
        case CENTER_ALIGN:
            if (distanceToWall > 29) {
                scale = 1;
            } else if (distanceToWall < 27) {
                scale = -1;
            } else {
                currentState = ControlPanelAlignment.ADJUST;
            }
            break;
        case ADJUST:
            if (Robot.cleanGyro == pastGyro) {
                counter++;
            }else{
                counter = 0;
            }
            if (Robot.cleanGyro > -0.5 && Robot.cleanGyro < 0.5 && counter >= 5) {
                currentState = ControlPanelAlignment.FORWARD;
            }
            break;
        case FORWARD:
            if(counter >= 50){
                currentState = ControlPanelAlignment.DONE;
            }else{
                counter++;
            }
            break;
        case DONE:

            break;
        }

        switch(currentState){
            
        }

        pastGyro = Robot.cleanGyro;
    }
}