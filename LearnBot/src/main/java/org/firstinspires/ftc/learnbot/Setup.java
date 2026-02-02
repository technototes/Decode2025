package org.firstinspires.ftc.learnbot;

import com.bylazar.configurables.annotations.Configurable;

public class Setup {

    @Configurable
    public static class Connected {

        public static boolean DRIVEBASE = true;
        public static boolean TESTSUBSYSTEM = false;
        public static boolean LIMELIGHT = false;
        public static boolean OTOS = true;
        public static boolean PINPOINT = false;
    }

    @Configurable
    public static class HardwareNames {

        public static String IMU = "imu";
        public static String LIMELIGHT = "limelight";
        public static String ALLIANCE_SWITCH_RED = "asr";
        public static String ALLIANCE_SWITCH_BLUE = "asb";
    }

    @Configurable
    public static class OtherSettings {

        public static int AUTOTIME = 25;
        public static double TRIGGER_THRESHOLD = 0.3;
    }
}
