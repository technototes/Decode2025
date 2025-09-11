package org.firstinspires.ftc.twenty403;

import com.acmerobotics.dashboard.config.Config;
import com.bylazar.configurables.annotations.Configurable;

public class Setup {

    @Configurable
    public static class Connected {

        public static boolean DRIVEBASE = true;
        public static boolean ODOSUBSYSTEM = true;
        public static boolean SAFETYSUBSYSTEM = false;
        public static boolean EXTERNALIMU = true;
        public static boolean OCTOQUAD = true;
        public static boolean LAUNCHER = true;
    }

    @Configurable
    public static class HardwareNames {

        public static String FLMOTOR = "fl";
        public static String FRMOTOR = "fr";
        public static String RLMOTOR = "rl";
        public static String RRMOTOR = "rr";
        public static String IMU = "imu";
        public static String EXTERNALIMU = "adafruit-imu";
        public static String OCTOQUAD = "octoquad";
        public static String ODOF = "odof";
        public static String ODOR = "odor";
        public static String TOP = "top";
        public static String BOTTOML = "bottoml";
        public static String BOTTOMR  = "bottomr";
    }

    @Configurable
    public static class OctoQuadPorts {

        public static int ARMENCODER = 2;
        public static int ODO_STRAFE = 0; //TODO: verify with robot, r & l may be swapped
        public static int ODO_FWD_BK = 1;
    }

    @Configurable
    public static class OtherSettings {

        public static int AUTOTIME = 25;
        public static double STRAIGHTEN_DEAD_ZONE = 0.01;
    }
}
