package org.firstinspires.ftc.learnbot.components;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.MovingStatistics;
import com.qualcomm.robotcore.util.Range;
import com.technototes.library.util.MathUtils;

// TODO: This "component" doesn't yet have a subsystem implementation. Fix that.
public class Gimbal {

    @Configurable
    public static class Config {

        // Hardware Configuration:
        public static String YAW_SERVO = "yaw";
        public static String PITCH_SERVO = "pitch";
        public static double YAW_INIT = 0.45;
        public static double YAW_LOW = 0.1;
        public static double YAW_HIGH = 0.9;
        public static double PITCH_INIT = 0.2;
        public static double PITCH_LOW = 0.0;
        public static double PITCH_HIGH = 0.8;
        public static Servo.Direction PITCH_DIR = Servo.Direction.REVERSE;
        public static Servo.Direction YAW_DIR = Servo.Direction.FORWARD;

        // OpMode testing configuration:
        public static double CHANGE = 0.025;
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
                    read(Config.YAW_HIGH, Config.YAW_LOW, Config.YAW_INIT, gamepad1.right_stick_x)
                );
                yawPos = yawAvg.getMean();

                pitchAvg.add(
                    read(
                        Config.PITCH_HIGH,
                        Config.PITCH_LOW,
                        Config.PITCH_INIT,
                        gamepad1.left_stick_y
                    )
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

        // Stick dead zone handling, plus scaling through a potentially uneven range of positive
        // values (servo's scale from 0 to 1, controllers scale from -1 to 1)
        private static double read(double hi, double lo, double init, double val) {
            // Remove the dead zone for the stick, and scale the remainder between 0-1
            double scaledStick = MathUtils.deadZoneScale(val, Config.DEAD_ZONE);
            // Get the higher of the two ranges (mid to high, mid to low)
            double maxRange = Math.max(Math.abs(init - hi), Math.abs(init - lo));
            // This clip is redundant for the current uses of read, but it doesn't hurt
            return Range.clip(scaledStick * maxRange + init, lo, hi);
        }
    }
}
