/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018-2019 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

public class PDController {
    private double kP;
    private double kD;
    private double lastError;
    private double currentError;
    private double correction;
    private double toleranceValue;
    private boolean isLastErrorSet;
    private double minCorrectionVal;
    private double maxCorrectionVal;

    public PDController(double newKP, double newKD) {
        kP = newKP;
        kD = newKD;
        isLastErrorSet = false;
    }

    public double calculate(double setPoint, double actualPoint) {
        currentError = setPoint - actualPoint;

        if (Math.abs(currentError) < toleranceValue) {
            correction = 0;
        } else {
            if (isLastErrorSet) {
                correction = currentError * kP + (currentError - lastError) * kD;
            }else{
                correction = currentError * kP;
            }

            if(Math.abs(correction) < minCorrectionVal){
                correction = minCorrectionVal * Math.signum(correction);
            }

            if(Math.abs(correction) > maxCorrectionVal){
                correction = maxCorrectionVal * Math.signum(correction);
            }
        }

        lastError = currentError;
        isLastErrorSet = true;
        return correction;
    }

    public void setToleranceValue(double tolerance) {
        toleranceValue = tolerance;
    }

    public void setMinCorrectionValue(double minCorrection){
        minCorrectionVal = minCorrection;
    }

    public void setMaxCorrectionValue(double maxCorrection){
        maxCorrectionVal = maxCorrection;
    }

    public void reset() {
        isLastErrorSet = false;
    }
}
