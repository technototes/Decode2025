package org.firstinspires.ftc.sixteen750;

import com.acmerobotics.dashboard.config.Config;

public class Setup {

    @Config
    public static class Connected {

        public static boolean DRIVEBASE = true;
        public static boolean ODOSUBSYSTEM = false;
        public static boolean SAFETYSUBSYSTEM = true;
        public static boolean EXTERNAL_IMU = true;
        public static boolean OTOS = true;
    }

    @Config
    public static class HardwareNames {

        public static String FL_DRIVE_MOTOR = "fl";
        public static String FR_DRIVE_MOTOR = "fr";
        public static String RL_DRIVE_MOTOR = "rl";
        public static String RR_DRIVE_MOTOR = "rr";
        public static String IMU = "imu";
        public static String EXTERNAL_IMU = "adafruit-imu";
        public static String ODOF = "odof";
        public static String ODOR = "odor";
        public static String OTOS = "sparky";
    }

    @Config
    public static class OtherSettings {

        public static double STRAIGHTEN_DEAD_ZONE = 0.08;
    }
}
