package org.firstinspires.ftc.crossbones;

import com.bylazar.configurables.annotations.Configurable;
import com.technototes.library.logger.Log;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class Setup {

    @Configurable
    public static class Connected {

        public static boolean DRIVEBASE = true;
        public static boolean TESTSUBSYSTEM = false;
        public static boolean SAFETYSUBSYSTEM = false;
        public static boolean EXTERNALIMU = true;
        public static boolean LAUNCHER = true;
        public static boolean FEED = true;
        public static boolean LIMELIGHT = false;
        public static boolean OTOS = true;
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
        public static String TOP = "top";
        public static String BOTTOML = "bottoml";
        public static String BOTTOMR = "bottomr";
        public static String OTOS = "sparky";
        public static String LIMELIGHT = "limelight";
        public static String TESTSERVO = "testservo";
        public static String TESTMOTOR = "testmotor";
        public static String TESTCRSERVO = "testcrservo";

        //        public static int Barcode_Pipeline = 0;
        public static int Green_Color_Pipeline = 0;
        //        public static int Classifier_Pipeline = 2;
        //        public static int Object_Detection_Pipeline = 3;
        public static int AprilTag_Pipeline = 1;
        public static int Purple_Color_Pipeline = 2;
        public static String[] Motif = { "1", "2", "3" };
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
        public static double STICK_DEAD_ZONE = 0.05;
        public static double TRIGGER_THRESHOLD = 0.3;
        public static double AUTO_SPEED = 0.95;
        public static double SNAIL_SPEED = 0.35;
        public static double SNAIL_TURN = 0.25;
        public static double NORMAL_SPEED = 0.85;
        public static double NORMAL_TURN = 0.4;
        public static double TURBO_SPEED = 1.0;
        public static double TURBO_TURN = 0.7;
    }
}
