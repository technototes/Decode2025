package org.firstinspires.ftc.sixteen750.helpers;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.technototes.library.command.Command;
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;

@Configurable
public class HeadingHelper {

    public double headingUpdateTime;
    public double lastHeading;
    public double lastXPosition;
    public double lastYPosition;

    public HeadingHelper(double lastX, double lastY, double heading) {
        headingUpdateTime = System.currentTimeMillis() / 1000.0;
        lastXPosition = lastX;
        lastYPosition = lastY;
        lastHeading = heading;
    }

    public static void saveHeading(double x, double y, double h) {
        FtcRobotControllerActivity.SaveBetweenRuns = new HeadingHelper(x, y, h);
    }

    public static Command SaveCurrentPosition(Follower f) {
        return Command.create(() -> savePose(f.getPose()));
    }

    public static Command RestorePreviousPosition(Follower f) {
        return Command.create(() -> {
            Pose p = getSavedPose();
            if (p != null) {
                f.setPose(getSavedPose());
            }
        });
    }

    public static void savePose(Pose p) {
        saveHeading(p.getX(), p.getY(), p.getHeading());
    }

    public static Pose getSavedPose() {
        HeadingHelper hh = (HeadingHelper) FtcRobotControllerActivity.SaveBetweenRuns;
        if (hh != null) {
            return new Pose(hh.lastXPosition, hh.lastYPosition, hh.lastHeading);
        }
        return null;
    }

    public static void clearSavedInfo() {
        FtcRobotControllerActivity.SaveBetweenRuns = null;
    }

    public static boolean validHeading() {
        HeadingHelper hh = (HeadingHelper) FtcRobotControllerActivity.SaveBetweenRuns;
        if (hh == null) {
            return false;
        }
        double now = System.currentTimeMillis() / 1000.0;
        return now < hh.headingUpdateTime + 45;
    }

    public static double getSavedHeading() {
        HeadingHelper hh = (HeadingHelper) FtcRobotControllerActivity.SaveBetweenRuns;
        if (hh != null) {
            return hh.lastHeading;
        }
        return 0.0;
    }

    public static double getSavedX() {
        HeadingHelper hh = (HeadingHelper) FtcRobotControllerActivity.SaveBetweenRuns;
        if (hh != null) {
            return hh.lastXPosition;
        }
        return 0.0;
    }

    public static double getSavedY() {
        HeadingHelper hh = (HeadingHelper) FtcRobotControllerActivity.SaveBetweenRuns;
        if (hh != null) {
            return hh.lastYPosition;
        }
        return 0.0;
    }
}
