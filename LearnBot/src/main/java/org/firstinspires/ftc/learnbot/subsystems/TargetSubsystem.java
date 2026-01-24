package org.firstinspires.ftc.learnbot.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;

@Configurable
public class TargetSubsystem implements Subsystem, Loggable {

    public static class Config {

        public static CameraOrientation Camera_Orientation = CameraOrientation.USB_UP;
        public static double Camera_Tilt_Degrees = 14.0;
    }

    public enum CameraOrientation {
        USB_BOT_LEFT, // This is 'normal'
        USB_UP,
        USB_BOT_RIGHT, // This is upside down
        USB_DOWN,
    }

    public enum Pipeline {
        GREEN_COLOR,
        APRIL_TAG,
        PURPLE_COLOR,
        UNKNOWN,
    }

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

        private void setXY(double obsX, double obsY) {
            // Translate based on Camera Orientation
            switch (Config.Camera_Orientation) {
                case USB_BOT_LEFT:
                    this.x = obsX;
                    this.y = obsY - Config.Camera_Tilt_Degrees;
                    break;
                case USB_UP:
                    this.x = -obsY;
                    this.y = obsX - Config.Camera_Tilt_Degrees;
                    break;
                case USB_BOT_RIGHT:
                    this.x = -obsX;
                    this.y = -obsY + Config.Camera_Tilt_Degrees;
                    break;
                case USB_DOWN:
                    this.x = obsY;
                    this.y = -obsX + Config.Camera_Tilt_Degrees;
                    break;
            }
        }

        public TargetInfo(LLResultTypes.FiducialResult fr) {
            this.a = fr.getTargetArea();
            setXY(fr.getTargetXDegrees(), fr.getTargetYDegrees());
        }

        public TargetInfo(LLResult result) {
            this.a = result.getTa();
            setXY(result.getTx(), result.getTy());
        }
    }

    public TargetSubsystem(Limelight3A ll) {
        limelight = ll;
        limelight.start();
        // I just start it with the AprilTag pipeline, I guess...
        setPipeline(Pipeline.APRIL_TAG);
        result = null;
        CommandScheduler.register(this);
    }

    public void setPipeline(Pipeline targetPipeline) {
        limelight.pipelineSwitch(targetPipeline.ordinal());
    }

    public Pipeline getCurrentPipeline() {
        switch (limelight.getLatestResult().getPipelineIndex()) {
            case 0:
                return Pipeline.GREEN_COLOR;
            case 1:
                return Pipeline.APRIL_TAG;
            case 2:
                return Pipeline.PURPLE_COLOR;
            default:
                return Pipeline.UNKNOWN;
        }
    }

    public TargetInfo getCurResult() {
        rotatedResult = null;
        if (limelight == null) {
            return null;
        }
        // TODO: Filter to a specific target (using getFiducialResults, IIRC)
        result = limelight.getLatestResult();
        if (result == null || !result.isValid()) {
            return null;
        }
        for (LLResultTypes.FiducialResult f : result.getFiducialResults()) {
            if (f.getFiducialId() == 20) {
                rotatedResult = new TargetInfo(f);
                break;
            }
            // Testing: We're ignoring target 24, and only look for target 20 (blue, not red)
        }
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
