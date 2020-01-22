/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import edu.wpi.first.wpilibj.Joystick;

public class Button implements BooleanSource{
    Joystick controller;
    int buttonID;
    
    public Button(Joystick controller, int buttonIndex){
        this.controller = controller;
        buttonID = buttonIndex;
    }

    public boolean getButtonValue(){
        return controller.getRawButton(buttonID);
    
    }

    @Override
    public boolean getBoolean() {
        return getButtonValue();
    }
}
