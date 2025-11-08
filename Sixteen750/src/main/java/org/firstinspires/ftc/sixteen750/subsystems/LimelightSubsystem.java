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

    @Log.Number(name = "texas angle")
    public static double angle = 0.0;
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

    public static void getLatestResult(){
        LLResult result = limelight.getLatestResult();
        angle = result.getTx();
    }

    public void selectPipeline(int todo){
        //TODO
    }

    public double getLimelightRotation(){
        LLResult result = limelight.getLatestResult();
        if (result != null && result.isValid()) {
            double tx = result.getTx(); // horizontal offset in degrees
            double kP_TagAlign = 0.03; // tune this gain
            return -kP_TagAlign * tx; // rotate until tx ~ 0
        } else {
            return 0.0; // no target → don't spin
        }
    }



}
