package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Setup;

@Configurable
public class LimelightSubsystem implements Loggable {

    boolean hasHardware;
    //data should not be flickering anymore on the driverstation because we are logging
    //instead of updating telemetry
    @Log.Number(name = "LLX angle")
    public static double Xangle = 0.0;

    @Log.Number(name = "LLY angle")
    public static double Yangle = 0.0;

    @Log.Number(name = "LL Area")
    public static double Area = 0.0;

    public static double SIGN = 1.0;
    public static Limelight3A limelight;

    public LimelightSubsystem(Hardware h) {
        hasHardware = Setup.Connected.LIMELIGHTSUBSYSTEM;
        // Do stuff in here
        if (hasHardware) {
            limelight = h.limelight;
            limelight.start();
            setPipeline(1);
        } else {
            limelight = null;
        }
    }

    public void setPipeline(int targetPipeline) {
        limelight.pipelineSwitch(targetPipeline);
    }

    public boolean getLatestResult() {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            // Not sure this is the right angle, because the camera is mounted sideways
            // IIRC, you should be using getTy() instead.
            Xangle = result.getTx();
            Yangle = result.getTy();
            Area = result.getTa();
            return true;
            //getLatestResult returns the x-angle, the y-angle,
            // and the area of the apriltag on the camera
        }
        else {
            return false;
        }
    }

    public void selectPipeline(int pipelineIndex) {
        limelight.pipelineSwitch(pipelineIndex);
    }

    public double getLimelightRotation() {
        if (getLatestResult()) {
            return SIGN * Yangle;
        }

        else {
            return 0;
        }
        //its y-angle because we flipped the camera, we might need to invert the axis
        // if it start turning away from the apriltag
    }
}
