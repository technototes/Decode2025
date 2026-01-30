package org.firstinspires.ftc.learnbot.opmodes;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.MovingStatistics;
import com.qualcomm.robotcore.util.Range;

public class Gimbal {

    @Configurable
    public static class Config {

        // Configuration:
        public static String YAW_SERVO = "yaw";
        public static String PITCH_SERVO = "pitch";
        public static double YAW_INIT = 0.5;
        public static double YAW_LOW = 0.1;
        public static double YAW_HIGH = 0.9;
        public static double PITCH_INIT = 0.0;
        public static double PITCH_LOW = 0.0;
        public static double PITCH_HIGH = 0.65;
        public static Servo.Direction PITCH_DIR = Servo.Direction.REVERSE;
        public static Servo.Direction YAW_DIR = Servo.Direction.FORWARD;

        // OpMode testing configuration:
        public static double CHANGE = 0.025;
        public static double YAW_SCALING = 1.0;
        public static double PITCH_SCALING = 1.0;
        public static double DEAD_ZONE = 0.05;
        public static int ANALOG_SMOOTHING_LEVEL = 25;
    }

    @TeleOp(name = "Gimbal Testing")
    public static class Testing extends OpMode {

        // Hardware
        Servo yaw, pitch;

        // State
        double yawPos = Config.YAW_INIT;
        double pitchPos = Config.PITCH_INIT;
        boolean digitalMode = true;

        MovingStatistics yawAvg = new MovingStatistics(Config.ANALOG_SMOOTHING_LEVEL);
        MovingStatistics pitchAvg = new MovingStatistics(Config.ANALOG_SMOOTHING_LEVEL);

        @Override
        public void init() {
            yaw = hardwareMap.get(Servo.class, Config.YAW_SERVO);
            pitch = hardwareMap.get(Servo.class, Config.PITCH_SERVO);
            if (yaw instanceof ServoImplEx) {
                ((ServoImplEx) yaw).setPwmRange(new PwmControl.PwmRange(500, 2500));
            }
            pitch.setDirection(Config.YAW_DIR);
            yaw.setPosition(yawPos);
            if (pitch instanceof ServoImplEx) {
                ((ServoImplEx) pitch).setPwmRange(new PwmControl.PwmRange(500, 2500));
            }
            pitch.setDirection(Config.PITCH_DIR);
            pitch.setPosition(pitchPos);
            digitalMode = true;
        }

        @Override
        public void loop() {
            if (digitalMode) {
                if (gamepad1.dpadUpWasPressed()) {
                    pitchPos += Config.CHANGE;
                } else if (gamepad1.dpadDownWasPressed()) {
                    pitchPos -= Config.CHANGE;
                } else if (gamepad1.dpadRightWasPressed()) {
                    yawPos += Config.CHANGE;
                } else if (gamepad1.dpadLeftWasPressed()) {
                    yawPos -= Config.CHANGE;
                }
            } else {
                // Smooth the values from the sticks a bit
                yawAvg.add(
                    (Config.YAW_HIGH - Config.YAW_LOW) * read(gamepad1.right_stick_x) +
                        Config.YAW_INIT
                );
                yawPos = yawAvg.getMean();
                // Pitch always moves from vertical to facing up, because we never face the camera
                // down, because...why?
                pitchAvg.add(
                    Math.abs(read(gamepad1.left_stick_y)) * (Config.PITCH_HIGH - Config.PITCH_LOW) +
                        Config.PITCH_LOW
                );
                pitchPos = pitchAvg.getMean();
            }
            if (
                gamepad1.aWasPressed() ||
                gamepad1.bWasPressed() ||
                gamepad1.xWasPressed() ||
                gamepad1.yWasPressed()
            ) {
                yawPos = Config.YAW_INIT;
                pitchPos = Config.PITCH_INIT;
                digitalMode = !digitalMode;
            }

            yawPos = Range.clip(yawPos, Config.YAW_LOW, Config.YAW_HIGH);
            pitchPos = Range.clip(pitchPos, Config.PITCH_LOW, Config.PITCH_HIGH);

            yaw.setPosition(yawPos);
            pitch.setPosition(pitchPos);
            if (digitalMode) {
                telemetry.addLine("dpad up/dn: Pitch, dpad lt/rt: Yaw");
                telemetry.addLine("Press a button to switch to analog mode");
            } else {
                telemetry.addLine("Left stick pitch (u/d), Right stick yaw (l/r)");
                telemetry.addLine("Press a button to switch to digital mode");
            }
            telemetry.addData("Yaw", yawPos);
            telemetry.addData("Pitch", pitchPos);
            telemetry.update();
        }

        // Stick dead zone handling
        private static double read(double val) {
            double out =
                (Math.abs(val) < Config.DEAD_ZONE ? 0 : (Math.abs(val) - Config.DEAD_ZONE)) /
                (1 - Config.DEAD_ZONE);
            return Math.copySign(out, val);
        }
    }
}
