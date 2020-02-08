// /*----------------------------------------------------------------------------*/
// /* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
// /* Open Source Software - may be modified and shared by FRC teams. The code   */
// /* must be accompanied by the FIRST BSD license file in the root directory of */
// /* the project.                                                               */
// /*----------------------------------------------------------------------------*/

// package frc.robot;

// import frc.robot.Shooter;
// import frc.robot.DriveController;
// import frc.robot.DriveController.DriveState;
// import frc.robot.DriveController.MoveParameters;

// /**
//  * Add your docs here.
//  */
// public class ThreeBallAuto implements AutoStateMachines{
//     public enum AutoStates {
//         IDLE,
//         ALIGN, 
//         SHOOT,
//         TURN_TO_GYRO,
//         FORWARD, 
//         DONE
//     }

//     int counter;
//     double pastGyro;
//     AutoStates autoStates;
//     Shooter shooten;

//     public ThreeBallAuto(){
//         autoStates = AutoStates.IDLE;
//         shooter = new Shooter(7, 8, 11, 9)
//         counter = 0;
//     }

//     @Override
//     public void update(MoveParameters mP){
//         pastGyro = Robot.cleanGyro;
//         switch(autoStates){
//             case IDLE:
//                 counter = 0;
//             break;

//             case ALIGN:
//                 mP.currentState = DriveState.POWERPORTALIGNMENT;
//                 autoStates = AutoStates.SHOOT;
//             break;

//             case SHOOT:
//                 Shooter.shootAll();
//                 autoStates = AutoStates.TURN_TO_GYRO;
//             break;

//             case TURN_TO_GYRO:
//                 //want to code a gyro to 0 case in drive controller but don't know how to. want to call it in this case
//                 if (Math.abs(Robot.cleanGyro - pastGyro) < 0.5) {
//                     counter++;
//                 } else {
//                     counter = 0;
//                 }

//                 if (counter >= 10) {
//                     autoStates = AutoStates.FORWARD;
//                     counter = 0;
//                 }
//             break;

//             case FORWARD:
//                 //go 2 - 5 inches forward or something
//                 mP.currentState = DriveState.GYROLOCK;
//                 mP.forward = 0.2;

//                 autoStates = AutoStates.DONE;
//             break;

//             case DONE:
//                 counter = 0;
//                 mP.currentState = DriveState.NONE;
//                 autoStates = AutoStates.IDLE;
//             break;
//         }
//     }

// }
