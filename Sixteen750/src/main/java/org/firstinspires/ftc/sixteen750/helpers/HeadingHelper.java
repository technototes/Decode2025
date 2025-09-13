package org.firstinspires.ftc.sixteen750.helpers;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;

@Config
public class HeadingHelper {

    public static double DEFAULT_START_HEADING = 180;
    public static double DEFAULT_START_X = 53;
    public static double DEFAULT_START_Y = 63;
    public static int EXPIRATION_TIME = 20;

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

    public static void savePose(Pose2d p) {
        saveHeading(p.getX(), p.getY(), p.getHeading());
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
        return now < hh.headingUpdateTime + EXPIRATION_TIME;
    }

    public static Pose2d getSavedPose() {
        return new Pose2d(getSavedX(), getSavedY(), getSavedHeading());
    }

    public static double getSavedHeading() {
        HeadingHelper hh = (HeadingHelper) FtcRobotControllerActivity.SaveBetweenRuns;
        if (hh != null) {
            return hh.lastHeading;
        }
        return DEFAULT_START_HEADING;
    }

    public static double getSavedX() {
        HeadingHelper hh = (HeadingHelper) FtcRobotControllerActivity.SaveBetweenRuns;
        if (hh != null) {
            return hh.lastXPosition;
        }
        return DEFAULT_START_X;
    }

    public static double getSavedY() {
        HeadingHelper hh = (HeadingHelper) FtcRobotControllerActivity.SaveBetweenRuns;
        if (hh != null) {
            return hh.lastYPosition;
        }
        return DEFAULT_START_Y;
    }
}
