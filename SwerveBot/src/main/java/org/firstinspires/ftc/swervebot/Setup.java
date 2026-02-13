package org.firstinspires.ftc.swervebot;

import com.bylazar.configurables.annotations.Configurable;

@Configurable
public class Setup {

    @Configurable
    public static class Connected {

        public static boolean LIMELIGHTSUBSYSTEM = false;
        public static boolean DRIVEBASE = false;
        public static boolean ODOSUBSYSTEM = false;
        public static boolean EXTERNAL_IMU = false;
        public static boolean INTAKESUBSYSTEM = false;
        public static boolean LAUNCHERSUBSYSTEM = false;
        public static boolean SWERVESUBSYSTEM = true;
    }

    @Configurable
    public static class HardwareNames {

        public static String FL_DRIVE_MOTOR = "fl";
        public static String FR_DRIVE_MOTOR = "fr";
        public static String RL_DRIVE_MOTOR = "rl";
        public static String RR_DRIVE_MOTOR = "rr";
        public static String IMU = "imu";
        public static String EXTERNAL_IMU = "adafruit-imu";
        public static String ODORL = "intake/odo";
        public static String ODOFB = "odof";
        public static String INTAKE_MOTOR = "intake/odo";
        public static String LAUNCHER_MOTOR1 = "launcher1";
        public static String LAUNCHER_MOTOR2 = "launcher2";
        public static String BRAKE_SERVO = "brake";
        public static String HOOD_SERVO = "hood";
        public static String LEVER_SERVO = "lever";
        public static String LIMELIGHT = "limelight";
        public static final String FL_SWERVO_ENCODER = "servoenc0";
        public static final String FR_SWERVO_ENCODER = "servoenc1";
        public static final String RL_SWERVO_ENCODER = "servoenc2";
        public static final String RR_SWERVO_ENCODER = "servoenc3";
        public static final String FL_SWERVO = "flsteer";
        public static final String FR_SWERVO = "frsteer";
        public static final String RL_SWERVO= "rlsteer";
        public static final String RR_SWERVO = "rrsteer";
        public static final String FL_SWERVEDRIVE = "fldrive";
        public static final String FR_SWERVEDRIVE = "frdrive";
        public static final String RL_SWERVEDRIVE = "rldrive";
        public static final String RR_SWERVEDRIVE = "rrdrive";
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
