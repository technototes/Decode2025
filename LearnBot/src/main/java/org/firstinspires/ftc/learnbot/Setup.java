package org.firstinspires.ftc.learnbot;

import com.bylazar.configurables.annotations.Configurable;

public class Setup {

    @Configurable
    public static class Connected {

        public static boolean DRIVEBASE = true;
        public static boolean TESTSUBSYSTEM = false;
        public static boolean LIMELIGHT = true;
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
    public static class OtherSettings {

        public static int AUTOTIME = 25;
        public static double TRIGGER_THRESHOLD = 0.3;
    }
}
