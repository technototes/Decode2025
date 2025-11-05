package org.firstinspires.ftc.learnbot;

import com.bylazar.configurables.annotations.Configurable;
import com.technototes.library.logger.Log;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Setup {

    @Configurable
    public static class Connected {

        public static boolean DRIVEBASE = true;
        public static boolean TESTSUBSYSTEM = false;
        public static boolean LIMELIGHT = false;
        public static boolean OTOS = false;
    }

    @Configurable
    public static class HardwareNames {

        public static String FLMOTOR = "fl";
        public static String FRMOTOR = "fr";
        public static String RLMOTOR = "rl";
        public static String RRMOTOR = "rr";
        public static String IMU = "imu";
        public static String OTOS = "sparky";
        public static String PINPOINT = "pinpoint";
        public static String LIMELIGHT = "limelight";
        public static int Green_Color_Pipeline = 0;
        public static int AprilTag_Pipeline = 1;
        public static int Purple_Color_Pipeline = 2;
        public static String[] Motif = { "1", "2", "3" };
    }

    @Configurable
    public static class DriveSettings {

        /*** Stuff for teleop driving ***/
        public static double SNAIL_SPEED = 0.25;
        public static double NORMAL_SPEED = 0.75;
        public static double TURBO_SPEED = 1.0;
        // The 'fastest' the robot can turn (0: not turning, 1.0: Fastest possible)
        public static double TURN_SCALING = 0.4;
        public static double DEAD_ZONE = 0.1;

        // The amount to multiply the degrees of offset by to turn the bot to
        // face the apriltag for the target. This is effectively "P" in a PID,
        // but we don't have I or D implemented
        public static double TAG_ALIGNMENT_GAIN = 0.03;

        /*** Constraints for auto path running ***/
        public static double AUTO_SCALING = 1.0;
    }

    @Configurable
    public static class OtherSettings {

        public static int AUTOTIME = 25;
        public static double STRAIGHTEN_DEAD_ZONE = 0.01;
        public static double TRIGGER_THRESHOLD = 0.3;
    }
}
