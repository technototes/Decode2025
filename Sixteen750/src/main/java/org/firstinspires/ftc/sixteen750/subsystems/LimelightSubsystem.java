package org.firstinspires.ftc.sixteen750.subsystems;

import static org.firstinspires.ftc.sixteen750.Setup.HardwareNames.AprilTag_Pipeline;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import java.sql.Time;
import java.util.List;
import java.util.Timer;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.commands.PedroDriver;

@Configurable
public class LimelightSubsystem implements Loggable, Subsystem {

    boolean hasHardware;
    public Robot robot;

    //data should not be flickering anymore on the driverstation because we are logging
    //instead of updating telemetry
    @Log.Number(name = "LLX angle")
    public static double Xangle = 0.0;

    private static ElapsedTime Time = new ElapsedTime();

    Gamepad gamepad;
    public static double RumbleDistScalar = 110; // fyi this is inverted from whatd youd expect bigger makes it scale less agressively right now it should roughly double from the nearest we can see the april tag to the farthest spot in the far zone

    public static double RumbleDistScalarOffset = 32;

    @Log.Number(name = "RFieldHeadingToTag")
    public static double RFieldHeadingToTag;

    @Log.Number(name = "BFieldHeadingToTag")
    public static double BFieldHeadingToTag;

    @Log.Number(name = "LLY angle")
    public static double Yangle = 0.0;

    @Log.Number(name = "LL Area")
    public static double Area = 0.0;

    @Log.Number(name = "RawDistance")
    public static double RawDistance;

    public static double RTagX = 132;
    public static double RTagY = 132;
    public static double BTagX = 12;
    public static double BTagY = 132;

    @Log.Number(name = "Velocity")
    public static double Velocity = 0;

    @Log.Number(name = "SotmDistance")
    public static double SotmDistance;

    public double LastDistance = 0;
    public double LastTime = 0;
    public double deltaTime = 0;
    public double deltaDistance = 0;
    public static double FLIGHT_TIME = 500; // in milliseconds
    public double PredictedDistance = 0;
    public double LastPredictedDistance = 0;

    public static int dur;
    public static double DurationConstant = 80;

    public static double BHeadingOffset = 2.2;
    public static double RHeadingOffset = -2.2;

    @Log(name = "new data")
    public static boolean new_result;

    public boolean startup_done;

    public static double SIGN = 1.0;
    public static double DISTANCE_FROM_LIMELIGHT_TO_APRILTAG_VERTICALLY = 19.15;
    public static double CAMERA_TO_CENTER_OF_ROBOT = 7.2;
    public static double EXTRA_OFFSET = -3;
    public static double LL_DISTANCE_OFFSET = 2.62; // RawDistance offset forwards and backwards from the center of the ll lense the center of robot
    public static double LIMELIGHT_ANGLE = 26.51;
    public static Pose botPose;
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

    // none of this is used it was attempt at ll reloc rn it is sitting here till when i meet with kevin
    public Pose getRPose() {
        if (!hasHardware) {
            return null;
        } else {
            double RFieldHeadingToTag = Math.toDegrees(PedroDriver.curHeading) + Xangle;

            double RPosX = RTagX - RawDistance * Math.cos(Math.toRadians(RFieldHeadingToTag));
            double RPosY = RTagY - RawDistance * Math.sin(Math.toRadians(RFieldHeadingToTag));
            double RHead = PedroDriver.curHeading;

            return new Pose(RPosX, RPosY, RHead);
        }
    }

    public Pose getBPose() {
        if (!hasHardware) {
            return null;
        } else {
            double BFieldHeadingToTag = Math.toDegrees(PedroDriver.curHeading) + Xangle;

            double BPosX = BTagX - RawDistance * Math.cos(BFieldHeadingToTag);
            double BPosY = BTagY - RawDistance * Math.sin(BFieldHeadingToTag);
            double BHead = PedroDriver.curHeading;

            return new Pose(BPosX, BPosY, BHead);
        }
    }

    //RawDistance = DISTANCE_FROM_LIMELIGHT_TO_APRILTAG/arctan(result.getTx())

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

    public double getRawDistance() {
        if (getLatestResult()) {
            RawDistance =
                (DISTANCE_FROM_LIMELIGHT_TO_APRILTAG_VERTICALLY /
                    Math.tan(Math.toRadians(Yangle))) +
                LL_DISTANCE_OFFSET;
            return RawDistance;
        }
        return -1;
        // measurements:
        // center of camera lens to floor - 12.3 inches
        // camera to center of robot(front-back) - 7.2 inches
        // apriltag height from floor- 29.5 inches
    }

    public double getVelocity() {
        if (getLatestResult()) {
            Velocity = (deltaDistance) / (deltaTime);
        }
        return Velocity;
    }

    public double updateDeltaDistance() {
        if (LastTime != 0) {
            deltaDistance = RawDistance - LastDistance;
        }
        LastDistance = RawDistance;
        return deltaDistance;
    }

    public double updateDeltaTime() {
        if (LastTime != 0) {
            deltaTime = Time.milliseconds() - LastTime;
        }
        LastTime = Time.milliseconds();
        return deltaTime;
    }

    public double getPredictedDistance() {
        if (getLatestResult()) {
            PredictedDistance = RawDistance + (Velocity * FLIGHT_TIME);
        }
        return PredictedDistance;
    }

    public void setGamepad(Gamepad g) {
        gamepad = g;
    }

    public void setRumble() {
        DurationConstant = 80;
    }

    public void setRumbleOff() {
        DurationConstant = 1;
    }

    /*public void setduration() {
        if (DurationConstant == 80) {
            dur = (int) (DurationConstant / (Math.abs(Xangle)
                + (2.7* (RawDistance - 1 + RumbleDistScalarOffset) / RumbleDistScalar)));
        }
            else {
                dur = (int) (DurationConstant / (Math.abs(Xangle)
                    - (2.7* (RawDistance - 1 + RumbleDistScalarOffset) / RumbleDistScalar)));
            }

    }
    public void vibrate() {
        if (Xangle > -10 && Xangle < 10 && Xangle != 0) ;
        if (gamepad != null && dur != 0) {
            gamepad.rumble(800/dur);
        }
    }*/

    @Override
    public void periodic() {
        new_result = getLatestResult();
        RawDistance = getRawDistance();
        getRPose();
        getBPose();
        deltaDistance = updateDeltaDistance();
        deltaTime = updateDeltaTime();
        Velocity = getVelocity();
        PredictedDistance = getPredictedDistance();
        // setduration();
        //vibrate();
    }
}
