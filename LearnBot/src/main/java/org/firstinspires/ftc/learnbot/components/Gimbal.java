package org.firstinspires.ftc.learnbot.components;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo.Direction;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.MovingStatistics;
import com.qualcomm.robotcore.util.Range;
import com.technototes.library.hardware.servo.Servo;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import com.technototes.library.subsystem.TargetAcquisition;
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
        public static Direction PITCH_DIR = Direction.REVERSE;
        public static Direction YAW_DIR = Direction.FORWARD;

        // OpMode testing configuration:
        public static double CHANGE = 0.025;
        public static double DEAD_ZONE = 0.05;
        public static int ANALOG_SMOOTHING_LEVEL = 25;
    }

    public static class Component implements Subsystem, Loggable, TargetAcquisition {

        private final Servo yaw, pitch;
        private final TargetAcquisition camera;

        // Things I want the gimbal to do:
        // Track a target when it's visible (as it moves, follow it, unless you can't
        // Scan when it can't find a target (what 'scan' means is still an implementation question)
        // Scan in the direction where it thinks a target might be? This one is iffy.
        // Indicate where a target is currently located, offset from the Vision subsystem/component
        public Component(Servo yawServo, Servo pitchServo, TargetAcquisition vision) {
            yaw = yawServo;
            pitch = pitchServo;
            if (yaw != null) {
                yaw.setInverted(Config.YAW_DIR == Direction.REVERSE);
            }
            if (pitch != null) {
                pitch.setInverted(Config.PITCH_DIR == Direction.REVERSE);
            }
            camera = vision;
        }

        @Override
        public double getDistance() {
            // TODO: Make this use the gimbal position and the h/v position from the camera.
            return camera.getDistance();
        }

        @Override
        public double getHorizontalPosition() {
            // TODO: Make this consider gimbal position
            return camera.getHorizontalPosition();
        }

        @Override
        public double getVerticalPosition() {
            // TODO: Make this consider gimbal position
            return camera.getVerticalPosition();
        }
    }

    @TeleOp(name = "Gimbal Testing")
    public static class Testing extends OpMode {

        // Hardware
        com.qualcomm.robotcore.hardware.Servo yaw, pitch;

        // State
        double yawPos = Config.YAW_INIT;
        double pitchPos = Config.PITCH_INIT;
        boolean digitalMode = true;

        MovingStatistics yawAvg = new MovingStatistics(Config.ANALOG_SMOOTHING_LEVEL);
        MovingStatistics pitchAvg = new MovingStatistics(Config.ANALOG_SMOOTHING_LEVEL);

        @Override
        public void init() {
            yaw = hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, Config.YAW_SERVO);
            pitch = hardwareMap.get(
                com.qualcomm.robotcore.hardware.Servo.class,
                Config.PITCH_SERVO
            );
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
