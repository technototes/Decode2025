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

    @Log.Number(name = "LLX angle")
    public static double Xangle = 0.0;

    @Log.Number(name = "LLX angle")
    public static double Yangle = 0.0;

    @Log.Number(name = "LL Area")
    public static double Area = 0.0;

    public static Limelight3A limelight;

    public LimelightSubsystem(Hardware h) {
        hasHardware = Setup.Connected.LIMELIGHTSUBSYSTEM;
        // Do stuff in here
        if (hasHardware) {
            limelight = h.limelight;
        } else {
            limelight = null;
        }
    }

    public static void getLatestResult() {
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            // Not sure this is the right angle, because the camera is mounted sideways
            // IIRC, you should be using getTy() instead.
            Xangle = result.getTx();
            Yangle = result.getTy();
            Area = result.getTa();
        }
    }

    public void selectPipeline(int pipelineIndex) {
        limelight.pipelineSwitch(pipelineIndex);
    }

    public double getLimelightRotation() {
        getLatestResult();
        return Yangle;
    }
}
