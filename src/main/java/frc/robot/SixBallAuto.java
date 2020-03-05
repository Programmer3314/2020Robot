
// package frc.robot;

// import edu.wpi.first.networktables.NetworkTable;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import frc.robot.DriveController.DriveState;
// import frc.robot.DriveController.MoveParameters;
// import frc.robot.ThreeBallAuto.AutoStates;

// public class SixBallAuto implements AutoStateMachines{
//     public enum AutoStates{
//         IDLE,
//         DELAY,
//         START,
//         SHOOT,
//         TURN_PERPENDICULAR_TO_ALLIANCE_STATION_WALL,
//         DRIVE_BACKWARDS,
//         DRIVE_FORWARDS,
//         SHOOT_2
        
//     }
//     AutoStates autoStates;
//     int counter;

//     public SixBallAuto(){
//         autoStates = AutoStates.IDLE;
//         counter = 0;
//     }
//     @Override
//     public void update(MoveParameters mP){
//         switch(autoStates){
//             case IDLE:
//             break;

//             case DELAY:

//             break;
            
//             case START:
//                 Robot.shooter.setHoodSetpoint(-1400);
//                 Robot.shooter.setTargetShooterRPM(3600);

//                 Robot.shooter.shootAll(queuingBeltSpeed, useGyro, gyroAngleDesired, gyroTolerance);
//                 autoStates = AutoStates.SHOOT;
//             break;
            
//             case SHOOT:
//             break;
            
//             case TURN_PERPENDICULAR_TO_ALLIANCE_STATION_WALL:
//             break;
            
//             case DRIVE_BACKWARDS:
//             break;
            
//             case DRIVE_FORWARDS:
//             break;
            
//             case SHOOT_2:
//             break;
        
//         }
//     }
    
//     @Override
//     public void activate(){
//         counter = 0;
//         autoStates = AutoStates.DELAY;
//     }

//     public void reset(){
//         autoStates = AutoStates.IDLE;
//         Robot.shooter.reset();
//     }
// }
