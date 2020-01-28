/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;

/**
 * Add your docs here.
 */
public class ControlPanel {
  private String desiredColor;
  private String colorString;
  public TalonSRX talon31;
  public boolean reset;
  public int[][] colorTable = {{0, 1, 1, -1}, {-1, 0, 1, 1}, {1, -1, 0, 1}, {1, 1, -1, 0}};
  public SetColor currentState, nextState;
  public int destinationColor, currentColor; 
  public int scale;

  public enum SetColor{
    START,
    SPINTOCOLOR,
    SPININCOLOR,
    DONE
  }

  // Color Sensor
  private final I2C.Port i2cPort = I2C.Port.kOnboard;
  private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);

  private final Color kBlueTarget = ColorMatch.makeColor(0.217, 0.470, 0.312);
  private final Color kGreenTarget = ColorMatch.makeColor(0.237, 0.51, 0.251);
  private final Color kRedTarget = ColorMatch.makeColor(0.315, 0.457, 0.226);
  private final Color kYellowTarget = ColorMatch.makeColor(0.289, 0.518, 0.192);
  private final ColorMatch m_colorMatcher = new ColorMatch();

  public ControlPanel(int talonID) {
    talon31 = new TalonSRX(talonID);

    talon31.configFactoryDefault();
    reset = true;

    m_colorMatcher.addColorMatch(kBlueTarget);
    m_colorMatcher.addColorMatch(kGreenTarget);
    m_colorMatcher.addColorMatch(kRedTarget);
    m_colorMatcher.addColorMatch(kYellowTarget);


    currentState = SetColor.START;

  }

  public void update() {
    // Color Sensor

    Color detectedColor = m_colorSensor.getColor();
    ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);

    if (match.color == kBlueTarget) {
      colorString = "Blue";
      currentColor = 2;
    } else if (match.color == kRedTarget) {
      colorString = "Red";
      currentColor = 0;
    } else if (match.color == kGreenTarget) {
      colorString = "Green";
      currentColor = 1;
    } else if (match.color == kYellowTarget) {
      colorString = "Yellow";
      currentColor = 3;
    } else {
      colorString = "Unknown";
      currentColor = -1;
    }

    SmartDashboard.putNumber("Red", detectedColor.red);
    SmartDashboard.putNumber("Green", detectedColor.green);
    SmartDashboard.putNumber("Blue", detectedColor.blue);
    SmartDashboard.putNumber("Confidence", match.confidence);
    SmartDashboard.putString("Detected Color", colorString);

    if (HumanInput.operatorController.getRawButton(1)) {
      desiredColor = "Yellow";
      destinationColor = 1;
    } else if (HumanInput.operatorController.getRawButton(2)) {
      desiredColor = "Blue";
      destinationColor = 0;
    } else if (HumanInput.operatorController.getRawButton(3)) {
      desiredColor = "Red";
      destinationColor = 2;
    } else if (HumanInput.operatorController.getRawButton(4)) {
      desiredColor = "Green";
      destinationColor = 3;
    } else {
      desiredColor = "null";
      destinationColor = -1;
    }

    SmartDashboard.putString("Desired Color", desiredColor);

    if (!colorString.equalsIgnoreCase(desiredColor) && !desiredColor.equalsIgnoreCase("null")) {
      talon31.set(ControlMode.PercentOutput, 0.3);
    } else {
      talon31.set(ControlMode.PercentOutput, 0.0);
    }

    // Rotation four times

    if (HumanInput.operatorController.getRawButtonPressed(6)) {
      if (reset) {
        talon31.setSelectedSensorPosition(0);
      }

      if (talon31.getSelectedSensorPosition() <= 412000) {
        talon31.set(ControlMode.PercentOutput, 0.4);
      } else {
        talon31.set(ControlMode.PercentOutput, 0.0);
      }
      reset = false;
    } else {
      reset = true;
    }

      nextState = currentState;
    switch(currentState){
      case START:
            if(currentColor >=0 && destinationColor >= 0){
                  scale = colorTable[destinationColor][currentColor];
                  if (scale == 0){
                    nextState = SetColor.DONE;
                  }else{
                    nextState = SetColor.SPINTOCOLOR;
                  }
            }
        break;
      
      case SPINTOCOLOR:
        if(!(colorString.equalsIgnoreCase(desiredColor))){
          talon31.set(ControlMode.PercentOutput, (0.2)*Math.signum(scale));
        }else{
          talon31.setSelectedSensorPosition(0);
          nextState = SetColor.SPININCOLOR;
        }

        break;

      case SPININCOLOR:
          if((Math.abs(talon31.getSelectedSensorPosition())<= 6437.5)){
            talon31.set(ControlMode.PercentOutput, (0.1)*Math.signum(scale));
          }else{
            nextState = SetColor.DONE;
          }

        break;

      case DONE:
          talon31.set(ControlMode.PercentOutput, 0.0);
          nextState = SetColor.START;
        break;
    }

    SmartDashboard.putNumber("Encoder Value", talon31.getSelectedSensorPosition());
    currentState = nextState;
  }

}
