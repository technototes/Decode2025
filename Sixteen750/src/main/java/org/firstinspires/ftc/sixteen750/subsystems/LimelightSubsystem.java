package org.firstinspires.ftc.sixteen750.subsystems;

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
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Setup;

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

    public static double SIGN = -1.0;
    public static double DISTANCE_FROM_LIMELIGHT_TO_APRILTAG_VERTICALLY = 17.2;
    public static double CAMERA_TO_CENTER_OF_ROBOT = 7.2;
    public static double EXTRA_OFFSET = -3;
    public static double LIMELIGHT_ANGLE = 11;
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
        if (result != null) {
            //&& result.isValid()
            // Not sure this is the right angle, because the camera is mounted sideways
            // IIRC, you should be using getTy() instead.
            Xangle = result.getTy();
            Yangle = result.getTx() + LIMELIGHT_ANGLE;
            Area = result.getTa();
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
            return SIGN * Yangle;
        } else {
            return 0;
        }
        //its y-angle because we flipped the camera, we might need to invert the axis
        // if it start turning away from the apriltag
    }
    public double getTX() {
        return Xangle;
    }
    public double getDistance() {
        LLResult result = limelight.getLatestResult();
        if (result != null) {
            List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
            for (LLResultTypes.FiducialResult fr : fiducialResults) {
                if (fr.getFiducialId() == 21) {
                    return -1;
                } else if (fr.getFiducialId() == 22) {
                    return -1;
                } else if (fr.getFiducialId() == 23) {
                    return -1;
                }
            }
        }
        distance =
            (DISTANCE_FROM_LIMELIGHT_TO_APRILTAG_VERTICALLY /
                Math.tan(Math.toRadians(Yangle) + Math.toRadians(LIMELIGHT_ANGLE))) +
            CAMERA_TO_CENTER_OF_ROBOT +
            EXTRA_OFFSET;
        return distance;

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
