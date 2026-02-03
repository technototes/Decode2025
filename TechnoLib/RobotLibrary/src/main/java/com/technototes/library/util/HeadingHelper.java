package com.technototes.library.util;

import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.technototes.library.command.Command;

public class HeadingHelper {

    public static HeadingHelper SavedBetweenRuns = null;

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
        SavedBetweenRuns = new HeadingHelper(x, y, h);
    }

    public static Command SaveCurrentPosition(Follower f) {
        return Command.create(() -> savePose(f.getPose()));
    }

    public static Command RestorePreviousPosition(Follower f) {
        return Command.create(() -> {
            Pose p = getSavedPose();
            if (p != null) {
                f.setPose(p);
            }
        });
    }

    public static void savePose(Pose p) {
        saveHeading(p.getX(), p.getY(), p.getHeading());
    }

    public static Pose getSavedPose() {
        HeadingHelper hh = HeadingHelper.SavedBetweenRuns;
        if (hh != null) {
            return new Pose(hh.lastXPosition, hh.lastYPosition, hh.lastHeading);
        }
        return null;
    }

    public static void clearSavedInfo() {
        HeadingHelper.SavedBetweenRuns = null;
    }

    public static boolean validHeading() {
        HeadingHelper hh = HeadingHelper.SavedBetweenRuns;
        if (hh == null) {
            return false;
        }
        double now = System.currentTimeMillis() / 1000.0;
        return now < hh.headingUpdateTime + 45;
    }

    public static double getSavedHeading() {
        HeadingHelper hh = HeadingHelper.SavedBetweenRuns;
        if (hh != null) {
            return hh.lastHeading;
        }
        return 0.0;
    }

    public static double getSavedX() {
        HeadingHelper hh = HeadingHelper.SavedBetweenRuns;
        if (hh != null) {
            return hh.lastXPosition;
        }
        return 0.0;
    }

    public static double getSavedY() {
        HeadingHelper hh = HeadingHelper.SavedBetweenRuns;
        if (hh != null) {
            return hh.lastYPosition;
        }
        return 0.0;
    }
}
