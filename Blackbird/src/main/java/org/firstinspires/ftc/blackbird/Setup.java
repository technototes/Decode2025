package org.firstinspires.ftc.blackbird;

import com.bylazar.configurables.annotations.Configurable;

@Configurable
public class Setup {

    @Configurable
    public static class Connected {

        public static final boolean TESTSUBSYSTEM = false;
        public static final boolean LIMELIGHTSUBSYSTEM = true;
        public static boolean DRIVEBASE = true;
        public static boolean ODOSUBSYSTEM = true;
        public static boolean SAFETYSUBSYSTEM = false;
        public static boolean EXTERNAL_IMU = false;
        public static boolean OTOS = false;
        public static boolean INTAKESUBSYSTEM = true;
        public static boolean LAUNCHERSUBSYSTEM = true;
        public static boolean AIMINGSUBSYSTEM = true;
        public static boolean BRAKESUBSYSTEM = true;
        public static boolean TURRETSUBSYSTEM = false;

        public static boolean BLACKBIRD = true;
    }

    @Configurable
    public static class HardwareNames {

        public static String FL_DRIVE_MOTOR = "fl";
        public static String FR_DRIVE_MOTOR = "fr";
        public static String RL_DRIVE_MOTOR = "rl";
        public static String RR_DRIVE_MOTOR = "rr";
        public static String IMU = "imu";
        public static String EXTERNAL_IMU = "adafruit-imu";
        public static String ODORL = "intake/odorl";
        public static String ODOFB = "launcher2/odofb";
        public static String OTOS = "sparky";
        public static String INTAKE_MOTOR = "intake/odorl";
        public static String LAUNCHER_MOTOR1 = "launcher1";
        public static String LAUNCHER_MOTOR2 = "launcher2";
        public static String BRAKE_SERVO = "brake";
        public static String HOOD_SERVO = "hood";
        public static String LEVER_SERVO = "lever";
        public static String TESTSERVO = "testservo";
        public static String TESTMOTOR = "testmotor";
        public static String TESTCRSERVO = "testcrservo";
        public static String LIMELIGHT = "limelight";
        public static String TURRET = "turret";
        public static int Green_Color_Pipeline = 0;
        // public static int Classifier_Pipeline = 2;
        // public static int Object_Detection_Pipeline = 3;
        public static int AprilTag_Pipeline = 1;
        public static int Purple_Color_Pipeline = 2;
        public static String[] Motif = { "1", "2", "3" };
    }

    @Configurable
    public static class OtherSettings {

        public static double AUTO_SCALING = 0.95;
        public static double NORMAL_SPEED = 1.0;
        public static double NORMAL_TURN = 0.8;
        public static double TURBO_SPEED = 1.0;
        public static double TURBO_TURN = 0.8;
        public static double SNAIL_SPEED = 0.5;
        public static double SNAIL_TURN = 0.25;
        public static double STRAIGHTEN_DEAD_ZONE = 0.08;
        public static double STICK_DEAD_ZONE = 0.05;
        public static double ANGULAR_VELOCITY_MAX = 0.6;
        public static double TRIGGER_THRESHOLD = 0.5;
    }
}
