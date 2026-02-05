package org.firstinspires.ftc.learnbot.components;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.PwmControl;
import com.qualcomm.robotcore.hardware.Servo.Direction;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.util.MovingStatistics;
import com.technototes.library.hardware.servo.Servo;
import com.technototes.library.logger.Loggable;
import com.technototes.library.structure.ValidationOpMode;
import com.technototes.library.subsystem.Subsystem;
import com.technototes.library.subsystem.TargetAcquisition;
import com.technototes.library.util.MathUtils;

public class Gimbal {

    public static class ServoInfo {

        public double low;
        public double high;
        public double init;
        public double rangeDegrees;
        public boolean flip;

        public ServoInfo(double lo, double hi, double init, double angleRange, boolean flip) {
            low = lo;
            high = hi;
            this.init = init;
            rangeDegrees = angleRange;
            this.flip = flip;
        }

        public double Clip(double val) {
            return com.qualcomm.robotcore.util.Range.clip(val, low, high);
        }

        // This normalizes an angle to "bot relative" from something mounted on the gimbal
        // I'm 100% sure this is kinda wrong, hopefully only because 'flip' isn't used yet...
        public double Adjust(double externalDegrees, double servoPos) {
            double servoTicksOffCenter = servoPos - init;
            double ticksToDegrees = rangeDegrees / (high - low);
            return externalDegrees - servoTicksOffCenter * ticksToDegrees;
        }

        // Stick dead zone handling, plus scaling through a potentially uneven range of positive
        // values (servo's scale from 0 to 1, controllers scale from -1 to 1)
        double Stick(double val) {
            // Remove the dead zone for the stick, and scale the remainder between 0-1
            double scaledStick = MathUtils.deadZoneScale(val, Config.TESTING_DEAD_ZONE);
            // Get the higher of the two ranges (mid to high, mid to low)
            double maxRange = Math.max(Math.abs(init - high), Math.abs(init - low));
            return Clip(scaledStick * maxRange + init);
        }
    }

    @Configurable
    public static class Config {

        // Hardware Configuration:
        public static String YAW_SERVO = "yaw";
        public static ServoInfo Yaw = new ServoInfo(0.1, 0.9, 0.45, 80.0, false);
        public static String PITCH_SERVO = "pitch";
        public static ServoInfo Pitch = new ServoInfo(0.0, 0.8, 0.2, 70.0, true);

        // Stuff for TargetAcquisition
        public static double TARGET_HEIGHT = 23.5; // Inches: This is a blind guess
        public static double CAMERA_HEIGHT = 5; // I could go look at the CAD...

        // OpMode testing configuration:
        public static double TESTING_DELTA = 0.025;
        public static double TESTING_DEAD_ZONE = 0.05;
        public static int TESTING_ANALOG_SMOOTHING_LEVEL = 25;
    }

    // This doesn't support  movement yet, but *does* implement the TargetAcquisition interface
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
                yaw.setInverted(Config.Yaw.flip);
            }
            if (pitch != null) {
                pitch.setInverted(Config.Pitch.flip);
            }
            camera = vision;
        }

        @Override
        public double getDistance() {
            // In Decode (2025) we use the fixed target height and camera height to calculate the
            // distance pretty accurately. To do that with a gimbal, you have to get the *accurate*
            // angle, because the camera itself doesn't know the gimbal angle.
            double angle = getVerticalPosition();
            // tan(angle) = height / distance
            return (Config.TARGET_HEIGHT - Config.CAMERA_HEIGHT) / Math.tan(Math.toRadians(angle));
        }

        @Override
        public double getHorizontalPosition() {
            double fromCamera = camera.getHorizontalPosition();
            double gimbalPosition = yaw.getPosition();
            return Config.Yaw.Adjust(fromCamera, gimbalPosition);
        }

        @Override
        public double getVerticalPosition() {
            double fromCamera = camera.getVerticalPosition();
            double gimbalPosition = pitch.getPosition();
            return Config.Pitch.Adjust(fromCamera, gimbalPosition);
        }

        // TODO: Implement either target tracking when a target is visible
        //   OR
        //  target *scanning* when a target isn't visible
    }

    @TeleOp(name = "Gimbal Testing")
    public static class TestingOpMode extends ValidationOpMode {

        // Hardware
        com.qualcomm.robotcore.hardware.Servo yaw, pitch;

        // State
        double yawPos = Config.Yaw.init;
        double pitchPos = Config.Pitch.init;
        boolean digitalMode = true;

        MovingStatistics yawAvg = new MovingStatistics(Config.TESTING_ANALOG_SMOOTHING_LEVEL);
        MovingStatistics pitchAvg = new MovingStatistics(Config.TESTING_ANALOG_SMOOTHING_LEVEL);

        @Override
        public void init() {
            super.init();
            yaw = hardwareMap.get(com.qualcomm.robotcore.hardware.Servo.class, Config.YAW_SERVO);
            pitch = hardwareMap.get(
                com.qualcomm.robotcore.hardware.Servo.class,
                Config.PITCH_SERVO
            );
            if (yaw instanceof ServoImplEx) {
                ((ServoImplEx) yaw).setPwmRange(new PwmControl.PwmRange(500, 2500));
            }
            pitch.setDirection(Config.Yaw.flip ? Direction.REVERSE : Direction.FORWARD);
            yaw.setPosition(yawPos);
            if (pitch instanceof ServoImplEx) {
                ((ServoImplEx) pitch).setPwmRange(new PwmControl.PwmRange(500, 2500));
            }
            pitch.setDirection(Config.Pitch.flip ? Direction.REVERSE : Direction.FORWARD);
            pitch.setPosition(pitchPos);
            digitalMode = true;
        }

        @Override
        public void loop() {
            if (digitalMode) {
                if (gamepad1.dpadUpWasPressed()) {
                    pitchPos += Config.TESTING_DELTA;
                } else if (gamepad1.dpadDownWasPressed()) {
                    pitchPos -= Config.TESTING_DELTA;
                } else if (gamepad1.dpadRightWasPressed()) {
                    yawPos += Config.TESTING_DELTA;
                } else if (gamepad1.dpadLeftWasPressed()) {
                    yawPos -= Config.TESTING_DELTA;
                }
            } else {
                // Smooth the values from the sticks a bit
                yawAvg.add(Config.Yaw.Stick(gamepad1.right_stick_x));
                yawPos = yawAvg.getMean();

                pitchAvg.add(Config.Pitch.Stick(gamepad1.left_stick_y));
                pitchPos = pitchAvg.getMean();
            }
            if (anyButtonsReleased()) {
                yawPos = Config.Yaw.init;
                pitchPos = Config.Pitch.init;
                digitalMode = !digitalMode;
            }

            yawPos = Config.Yaw.Clip(yawPos);
            pitchPos = Config.Pitch.Clip(pitchPos);

            yaw.setPosition(yawPos);
            pitch.setPosition(pitchPos);
            if (digitalMode) {
                addLine("dpad up/dn: Pitch, dpad lt/rt: Yaw");
                addLine("Press a button to switch to analog mode");
            } else {
                addLine("Left stick pitch (u/d), Right stick yaw (l/r)");
                addLine("Press a button to switch to digital mode");
            }
            addData("Yaw", yawPos);
            addData("Pitch", pitchPos);
            super.loop();
        }
    }
}
