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
public class PDController {
    private double kP;
    private double kD;
    private double lastError;
    private double currentError;
    private double correction;
    private double toleranceValue;
    private boolean isLastErrorSet;

    public PDController(double newKP, double newKD) {
        kP = newKP;
        kD = newKD;
        isLastErrorSet = false;
    }

    public double calculate(double setPoint, double actualPoint) {
        currentError = actualPoint - setPoint;

        if (Math.abs(currentError) < toleranceValue) {
            correction = 0.5;
        } else {
            if (isLastErrorSet) {
                correction = currentError * kP + (currentError - lastError) * kD;
            }else{
                correction = currentError * kP;
            }
        }

        lastError = currentError;
        return correction;
    }

    public void setToleranceValue(double tolerance) {
        toleranceValue = tolerance;
    }

    public void reset() {
        isLastErrorSet = false;
    }
}
