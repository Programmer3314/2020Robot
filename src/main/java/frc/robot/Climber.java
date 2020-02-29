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
public class Climber {
    public enum ClimbStates {
        IDLE,
        START1,
        START2,
        START3,
        DISENGAGE_RATCHET,
        ENGAGE_PTO,
        DRIVE,
        ENGAGE_RATCHET,
        DONE
    }

    ClimbStates climbStates;

    boolean checkRatchet;
    boolean checkPTO;
    double encoderPos;
    double originalEncoderPos;
    int counter = 0;

    public Climber(){
        climbStates = ClimbStates.IDLE;
    }

    public void update(MoveParameters mP){
        encoderPos = Robot.driveController.encoderPos;

        switch(climbStates){
            case IDLE:
            break;

            case START1:
                if(!HumanInput.operatorBack && !HumanInput.operatorStart){
                    climbStates = ClimbStates.START2;
                }
            break;
            
            case START2:
                if(HumanInput.operatorBack && HumanInput.operatorStart){
                    climbStates = ClimbStates.START3;
                }
            break;

            case START3:
                if(!HumanInput.operatorBack && !HumanInput.operatorStart){
                    climbStates = ClimbStates.DISENGAGE_RATCHET;
                }
            break;

            case DISENGAGE_RATCHET:
                Solenoids.disengageRatchet.set(true);
                Solenoids.engageRatchet.set(false);

                if(HumanInput.operatorBack && HumanInput.operatorStart){
                    counter = 0;
                    climbStates = ClimbStates.ENGAGE_PTO;
                }
            break;

            case ENGAGE_PTO:
                
                mP.currentState = DriveState.MANUAL;
                mP.forward = 0.0;
                mP.turn = 0.0;

                counter++;
                Solenoids.engagePTO.set(true);
                Solenoids.disengagePTO.set(false);

                if(counter >= 50){
                    encoderPos = 0;
                    counter = 0;
                    originalEncoderPos = Robot.driveController.encoderPos;
                    climbStates = ClimbStates.DRIVE;
                }
            break;

            case DRIVE:
                counter++;
                mP.currentState = DriveState.MANUAL;
                mP.forward = 0.4;
                mP.turn = 0.0;

                // delay engaging ratchet
                if(counter >= 100){
                    climbStates = ClimbStates.ENGAGE_RATCHET;
                }

                if(!HumanInput.operatorBack || !HumanInput.operatorStart){
                    climbStates = ClimbStates.DONE;
                }
            break;

            case ENGAGE_RATCHET:
                mP.currentState = DriveState.MANUAL;
                mP.forward = 0.3;
                mP.turn = 0.0;

                Solenoids.engageRatchet.set(true);
                Solenoids.disengageRatchet.set(false);

                if(!HumanInput.operatorBack || !HumanInput.operatorStart){
                    climbStates = ClimbStates.DONE;
                }
                // Add climb limit
                if(Math.abs(encoderPos - originalEncoderPos)>200000) {
                    climbStates = ClimbStates.DONE;
                }
            break;

            case DONE:
                mP.currentState = DriveState.MANUAL;
                mP.forward = 0.0;
                mP.turn = 0.0;
                // Disengage the PTO in case the driver hits the sticks
                Solenoids.engagePTO.set(false);
                Solenoids.disengagePTO.set(true);

                climbStates = ClimbStates.IDLE;
            break;
        }

        // //if(HumanInput.traverseClimbState){
        //     climbStates = ClimbStates.[NAME];
        // }

        SmartDashboard.putString("Climb State", climbStates.toString());
        SmartDashboard.putNumber("Climb Encoder", Math.abs(encoderPos - originalEncoderPos));
    }

    public void activate(){
        climbStates = ClimbStates.START1;
    }

    public void abortClimb(){
        climbStates = ClimbStates.DONE;
    }

}
