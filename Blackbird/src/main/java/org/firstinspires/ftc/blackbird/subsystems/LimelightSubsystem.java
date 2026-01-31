package org.firstinspires.ftc.blackbird.subsystems;

import static org.firstinspires.ftc.blackbird.Setup.HardwareNames.AprilTag_Pipeline;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import java.util.List;
import org.firstinspires.ftc.blackbird.Hardware;
import org.firstinspires.ftc.blackbird.Setup;

@Configurable
public class LimelightSubsystem implements Loggable, Subsystem {

    boolean hasHardware;

    //data should not be flickering anymore on the driverstation because we are logging
    //instead of updating telemetry
    @Log.Number(name = "LLX angle")
    public static double Xangle = 0.0;

    @Log.Number(name = "LLY angle")
    public static double Yangle = 0.0;

    @Log.Number(name = "LL Area")
    public static double Area = 0.0;

    @Log.Number(name = "distance")
    public static double distance;

    @Log(name = "new data")
    public static boolean new_result;

    public boolean startup_done;

    public static double SIGN = 1.0;
    public static double DISTANCE_FROM_LIMELIGHT_TO_APRILTAG_VERTICALLY = 17.2;
    public static double CAMERA_TO_CENTER_OF_ROBOT = 2.08;
    public static double EXTRA_OFFSET = -3;
    // TODO: Measure this more accurately
    public static double LIMELIGHT_ANGLE = 15;
    public static Limelight3A limelight;
    LLResult result;

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
        result = limelight.getLatestResult();
        if (result != null) {
            recentItem = filterItem(result);
            if (recentItem == null) {
                return false;
            }
            //&& result.isValid()
            // Not sure this is the right angle, because the camera is mounted sideways
            // IIRC, you should be using getTy() instead.
            Xangle = recentItem.getTargetXDegrees();
            Yangle = recentItem.getTargetYDegrees() + LIMELIGHT_ANGLE;
            Area = recentItem.getTargetArea();
            return true;
            //            getLatestResult returns the x-angle, the y-angle,
            //             and the area of the apriltag on the camera
        } else {
            return false;
        }
    }

    //distance = DISTANCE_FROM_LIMELIGHT_TO_APRILTAG/arctan(result.getTx())

    public void selectPipeline(int pipelineIndex) {
        limelight.pipelineSwitch(pipelineIndex);
    }

    public double getLimelightRotation() {
        if (getLatestResult()) {
            return SIGN * Xangle;
        } else {
            return 0;
        }
        //its y-angle because we flipped the camera, we might need to invert the axis
        // if it start turning away from the apriltag
    }

    public double getTX() {
        return Xangle;
    }

    public void LimelightStartup() {
        limelight.setPollRateHz(100);
        limelight.pipelineSwitch(AprilTag_Pipeline);
        limelight.start();
        startup_done = true;
    }

    public void LimelightTurnOff() {
        limelight.stop();
        startup_done = false;
    }

    LLResultTypes.FiducialResult recentItem = null;

    // We want to ignore the tags on the obelisk
    public LLResultTypes.FiducialResult filterItem(LLResult result) {
        List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
        for (LLResultTypes.FiducialResult fr : fiducialResults) {
            if (fr.getFiducialId() == 21) {
                continue;
            } else if (fr.getFiducialId() == 22) {
                continue;
            } else if (fr.getFiducialId() == 23) {
                continue;
            }
            return fr;
        }
        return null;
    }

    public double getDistance() {
        if (getLatestResult()) {
            distance =
                (DISTANCE_FROM_LIMELIGHT_TO_APRILTAG_VERTICALLY /
                    Math.tan(Math.toRadians(Yangle))) +
                CAMERA_TO_CENTER_OF_ROBOT +
                EXTRA_OFFSET;
            return distance;
        }
        return -1;
        // measurements:
        // center of camera lens to floor - 12.3 inches
        // camera to center of robot(front-back) - 7.2 inches
        // apriltag height from floor- 29.5 inches
    }

    @Override
    public void periodic() {
        new_result = getLatestResult();
        distance = getDistance();
    }
}
