package org.firstinspires.ftc.sixteen750.commands.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;

// so i created this cause i cause absolutely freaking tired and fed up of navigating paths v1 so yeah yay!!!
@Configurable
public class Poses {

    public static Follower follower;

    public static Pose RStart = new Pose(114,135);
    public static Pose getRStart() {
        return new Pose(114, 135, Math.toRadians(90));
    }
    public static Pose RLaunch = new Pose(88,82);
    public static double RLaunchHead = 42;
    public static Pose RInt1 = new Pose(131.5,58);
    public static double RInt1Head = 0;
    public static Pose RInt1CtrlPoint1 = new Pose(82.5,56);
    public static Pose RInt1CtrlPoint2 = new Pose(102,58);
    public static Pose RInt1ToLaunchCtrlPoint = new Pose(100,67);
    public static Pose RGateInt = new Pose(132.5,65.75);
    public static double RGateIntHead = 41;
    public static Pose RGateCycleCtrlPoint = new Pose(96,64);
    public static Pose RInt2 = new Pose(125,82);
    public static double RInt2Head = 0;
    public static Pose REnd = new Pose(120,75);
    public static double REndHead = 0;
    public static Pose BStart = new Pose(30,135);
    public static Pose getBStart() {
        return new Pose(30, 135, Math.toRadians(90));
    }
    public static Pose BLaunch = new Pose(56,82);
    public static double BLaunchHead = 138;
    public static Pose BInt1 = new Pose(12.5,58);
    public static double BInt1Head = 180;
    public static Pose BInt1CtrlPoint1 = new Pose(61.5,56);
    public static Pose BInt1CtrlPoint2 = new Pose(42,58);
    public static Pose BInt1ToLaunchCtrlPoint = new Pose(44,67);
    public static Pose BGateInt = new Pose(11.5,65.75);
    public static double BGateIntHead = 139;
    public static Pose BGateCycleCtrlPoint = new Pose(48,64);
    public static Pose BInt2 = new Pose(19,82);
    public static double BInt2Head = 180;
    public static Pose BEnd = new Pose(30,135);
    public static double BEndHead = 180;


}
