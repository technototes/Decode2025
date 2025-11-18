package org.firstinspires.ftc.learnbot.subsystems;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import org.firstinspires.ftc.learnbot.Setup;

public class VisionSubsystem implements Subsystem, Loggable {

    @Log(name = "Viz")
    public static String status = "";

    public Limelight3A limelight;
    private LLResult result;
    private TargetInfo rotatedResult;

    // Currently, the camera orientation is:
    // USB port up, with a 14 degree upward tilt

    public static class TargetInfo {

        public double x;
        public double y;
        public double a;

        public TargetInfo(LLResult result) {
            this.a = result.getTa();
            // Translate based on Camera Orientation
            switch (Setup.Vision.Camera_Orientation) {
                case USB_BOT_LEFT:
                    this.x = result.getTx();
                    this.y = result.getTy();
                    break;
                case USB_UP:
                    this.x = -result.getTy();
                    this.y = result.getTx();
                    break;
                case USB_BOT_RIGHT:
                    this.x = -result.getTx();
                    this.y = -result.getTy();
                    break;
                case USB_DOWN:
                    this.x = result.getTy();
                    this.y = -result.getTx();
                    break;
            }
        }
    }

    public VisionSubsystem(Limelight3A ll) {
        limelight = ll;
        limelight.start();
        result = null;
    }

    public void setPipeline(int targetPipeline) {
        limelight.pipelineSwitch(targetPipeline);
    }

    public int getCurrentPipeline() {
        return limelight.getLatestResult().getPipelineIndex();
    }

    // TODO: Handle orientation changes in here, rather than with the user?
    public TargetInfo getCurResult() {
        result = limelight.getLatestResult();
        rotatedResult = (result == null || !result.isValid()) ? null : new TargetInfo(result);
        return rotatedResult;
    }

    // TODO: Could add some sort of diagnostics to the periodic function
    @Override
    public void periodic() {
        if (result != null && rotatedResult != null) {
            status = resString() + "\n" + statString();
        } else {
            status = statString();
        }
    }

    private String resString() {
        return String.format(
            "X: %.2f(%.2f), Y: %.2f(%.2f), A: %.2f",
            rotatedResult.x,
            result.getTx(),
            rotatedResult.y,
            result.getTy(),
            result.getTa()
        );
    }

    private String statString() {
        LLStatus lls = limelight.getStatus();
        return String.format(
            "Y: %.2f, N: %s, C: %d, I: %d, T: %s",
            lls.getFinalYaw(),
            lls.getName(),
            lls.getPipeImgCount(),
            lls.getPipelineIndex(),
            lls.getPipelineType()
        );
    }
}
