package org.firstinspires.ftc.sixteen750.commands.auto;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;

// so i created this cause im absolutely freaking tired and fed up of navigating paths v1 so yeah yay!!!
@Configurable
public class Poses {

    public static Follower follower;

    @Configurable
    public static class StartPoses {

        public static Pose RStart = new Pose(114, 135);

        public static Pose getRStart() {
            return new Pose(114, 135, Math.toRadians(90));
        }

        public static Pose getRFStart() {
            return new Pose(88.5, 9, Math.toRadians(90));
        }

        public static Pose RFStart = new Pose(88.5, 9);
        public static Pose BStart = new Pose(30, 135);

        public static Pose getBStart() {
            return new Pose(30, 135, Math.toRadians(90));
        }

        public static Pose getBFStart() {
            return new Pose(55.5, 9, Math.toRadians(90));
        }

        public static Pose BFStart = new Pose(55.5, 9);
    }

    @Configurable
    public static class RNear18PartnerPoses {

        public static Pose RLaunch = new Pose(88, 82);
        public static double RLaunchHead = Math.toRadians(49);
        public static Pose RInt1 = new Pose(135, 64.8);
        public static double RInt1Head = Math.toRadians(348);
        public static Pose RInt1CtrlPoint1 = new Pose(82.5, 64);
        public static Pose RInt1CtrlPoint2 = new Pose(102, 66);
        public static Pose RInt1ToLaunchCtrlPoint = new Pose(96, 42);
        public static Pose RGateInt = new Pose(134.4, 65.1);
        public static Pose RGateInt2 = new Pose(134.4, 66.9);
        public static Pose RGateInt3 = new Pose(134.4, 67.2);
        public static double RGateIntHead = Math.toRadians(18);
        public static Pose RGateCycleCtrlPoint = new Pose(96, 64);
        public static Pose RInt2 = new Pose(125.7, 90);
        public static Pose RInt2CtrlPoint = new Pose(90, 90);
        public static double RInt2Head = Math.toRadians(0);
        public static Pose REnd = new Pose(120, 90);
        public static double REndHead = Math.toRadians(0);
    }

    @Configurable
    public static class RNear18SafePoses {

        public static Pose RLaunch = new Pose(88, 82);
        public static double RLaunchHead = Math.toRadians(49);
        public static Pose RInt1 = new Pose(135, 64.8);
        public static double RInt1Head = Math.toRadians(354);
        public static Pose RInt1CtrlPoint1 = new Pose(82.5, 64);
        public static Pose RInt1CtrlPoint2 = new Pose(102, 66);
        public static Pose RInt1ToLaunchCtrlPoint = new Pose(96, 42);
        public static Pose RGateInt = new Pose(134.4, 65);
        public static Pose RInt2 = new Pose(135, 42);
        public static double RInt2Head = Math.toRadians(0);
        public static Pose RInt2CtrlPoint1 = new Pose(92, 42);
        public static Pose RInt2CtrlPoint2 = new Pose(96, 42);
        public static Pose RGateInt2 = new Pose(134.4, 66.5);
        public static double RGateIntHead = Math.toRadians(18);
        public static Pose RGateCycleCtrlPoint = new Pose(96, 64);
        public static Pose RInt3 = new Pose(125.7, 90);
        public static Pose RInt3CtrlPoint = new Pose(90, 90);
        public static double RInt3Head = Math.toRadians(0);
        public static Pose REnd = new Pose(120, 90);
        public static double REndHead = Math.toRadians(0);
    }

    @Configurable
    public static class RFar15PartnerPoses {

        public static Pose RFLaunch = new Pose(82, 16);
        public static double RFLaunchHead = Math.toRadians(62);
        public static Pose RFInt1 = new Pose(132, 35);
        public static Pose RFInt1CtrlPoint = new Pose(75, 38);
        public static double RFInt1Head = Math.toRadians(0);
        public static Pose RFInt2 = new Pose(132, 6); //sussy bezier go brrrr
        public static Pose RFInt2CtrlPoint1 = new Pose(170, 15);
        public static Pose RFInt2CtrlPoint2 = new Pose(108, 18);
        public static double RFInt2Head = Math.toRadians(355);
        public static Pose RFInt3 = new Pose(131.5, 42); // might work?
        public static Pose RFInt3CtrlPoint1 = new Pose(120, 10);
        public static Pose RFInt3CtrlPoint2 = new Pose(132, 4);
        public static double RFInt3Head = Math.toRadians(60);
        public static Pose RFEnd = new Pose(128, 12);
        public static double RFEndHead = Math.toRadians(0);
    }

    @Configurable
    public static class BNear18PartnerPoses {

        public static Pose BLaunch = new Pose(56, 82);
        public static double BLaunchHead = Math.toRadians(131);
        public static Pose BInt1 = new Pose(9, 64.8);
        public static double BInt1Head = Math.toRadians(192);
        public static Pose BInt1CtrlPoint1 = new Pose(61.5, 56);
        public static Pose BInt1CtrlPoint2 = new Pose(42, 58);
        public static Pose BInt1ToLaunchCtrlPoint = new Pose(48, 42);
        public static Pose BGateInt1 = new Pose(9.6, 65.1);
        public static Pose BGateInt2 = new Pose(9.6, 66.9);
        public static Pose BGateInt3 = new Pose(9.6, 67.2);
        public static double BGateIntHead = Math.toRadians(162);
        public static Pose BGateCycleCtrlPoint = new Pose(48, 64);
        public static Pose BInt2 = new Pose(18.3, 90);
        public static Pose BInt2CtrlPoint = new Pose(54, 90);
        public static double BInt2Head = Math.toRadians(180);
        public static Pose BEnd = new Pose(24, 90);
        public static double BEndHead = Math.toRadians(180);
    }

    @Configurable
    public static class BNear18SafePoses {

        public static Pose BLaunch = new Pose(56, 82);
        public static double BLaunchHead = Math.toRadians(131);
        public static Pose BInt1 = new Pose(9, 65.2);
        public static double BInt1Head = Math.toRadians(186);
        public static Pose BInt1CtrlPoint1 = new Pose(61.5, 56);
        public static Pose BInt1CtrlPoint2 = new Pose(42, 58);
        public static Pose BInt1ToLaunchCtrlPoint = new Pose(48, 42);
        public static Pose BGateInt1 = new Pose(9.6, 65);
        public static Pose BInt2 = new Pose(9, 42);
        public static double BInt2Head = Math.toRadians(180);
        public static Pose BInt2CtrlPoint1 = new Pose(52, 42);
        public static Pose BInt2CtrlPoint2 = new Pose(48, 42);
        public static Pose BGateInt2 = new Pose(9.6, 66.5);
        public static double BGateIntHead = Math.toRadians(162);
        public static Pose BGateCycleCtrlPoint = new Pose(48, 64);
        public static Pose BInt3 = new Pose(18.3, 90);
        public static Pose BInt3CtrlPoint = new Pose(54, 90);
        public static double BInt3Head = Math.toRadians(180);
        public static Pose BEnd = new Pose(24, 90);
        public static double BEndHead = Math.toRadians(180);
    }

    @Configurable
    public static class BFar15PartnerPoses {

        public static Pose BFLaunch = new Pose(62, 16);
        public static double BFLaunchHead = Math.toRadians(118);
        public static Pose BFInt1 = new Pose(12, 35);
        public static Pose BFInt1CtrlPoint = new Pose(69, 38);
        public static double BFInt1Head = Math.toRadians(180);
        public static Pose BFInt2 = new Pose(12, 6);
        public static Pose BFInt2CtrlPoint1 = new Pose(-26, 15);
        public static Pose BFInt2CtrlPoint2 = new Pose(36, 18);
        public static double BFInt2Head = Math.toRadians(185);
        public static Pose BFInt3 = new Pose(12.5, 35);
        public static Pose BFInt3CtrlPoint1 = new Pose(24, 10);
        public static Pose BFInt3CtrlPoint2 = new Pose(12, 4);
        public static double BFInt3Head = Math.toRadians(120);
        public static Pose BFEnd = new Pose(16, 12);
        public static double BFEndHead = Math.toRadians(180);
    }
}
