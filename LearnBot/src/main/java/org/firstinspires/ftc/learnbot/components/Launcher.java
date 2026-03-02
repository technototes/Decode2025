package org.firstinspires.ftc.learnbot.components;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.MovingStatistics;
import com.technototes.library.command.Command;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.structure.ValidationOpMode;
import com.technototes.library.subsystem.Subsystem;
import com.technototes.library.subsystem.TargetAcquisition;
import com.technototes.library.util.PIDFController;
import java.util.Locale;
import java.util.function.DoubleSupplier;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

public class Launcher {

    // *ALL* the configuration should go in here. I moved some things that had been constants up
    // to here, as they are "bot build configuration": are the motors reversed.
    @Configurable
    public static class Config {

        // You can pull this from a Setup.* location
        public static String getMotorName() {
            return "top";
        }

        // You can pull this from a Setup.* location. Return 'null' if there's only one motor
        public static String get2ndMotorName() {
            return null;
        }

        // Is the primary intake motor reversed?
        public static boolean PrimaryReversed = true;
        // This should probably track the PrimaryReversed value
        // but if you're using the encoder from the secondary, it might not.
        // The FeedFwd helper tell you the correct value.
        public static boolean ReverseEncoder = true;
        // Is the secondary intake motor reversed?
        public static boolean SecondaryReversed = false;

        // The distance to use when auto-calculating velocity, but we don't have a target
        public static double DefaultDistance = 50;

        // The input value is the error of the target velocity that ranges from
        // -2800 to +2800 for a goBilda motor.
        // It's output is a power value in the -1 to +1 range.
        // So, P is probably in the range of .001-ish.
        // For a velocity-targeting PIDF, we probably want an I value, not a D value.
        public static PIDFCoefficients launchPID = new PIDFCoefficients(0.004, 0.0002, 0.0, 0);

        // Stuff used for the Feed Forward function.
        // This one is highly variable, based on the amount of friction in the system
        public static double kStaticFriction = 0.15;
        // This one tends to be somewhere between 0.0038 to 0.0046 or so.
        public static double kVelocityConstant = 0.0043;

        // This is only used if we can't read the voltage.
        public static double PeakVoltage = 13.6;

        // GoBilda says stall current of 9.2A at 12V, so V = I * R, R = 12 / 9.2 (about 1.3 ohms)
        // As the motor heats up, resistance also increase, so we could increase this a little bit
        // or maybe increase it over time to counteract that, but this is probably good enough.
        public static double MotorResistance = 12 / 9.2;

        // This is how much to add/subtract to *setMotorPower* when inc/dec velocity is invoked
        public static double PowerDelta = 0.025;

        // multiplier for x for launch speed formula
        public static double Regression_M = 6.261;
        // minimum velocity for launch speed formula
        public static double Regression_B = 1250;

        public static double CalcVelocity(double distInInches) {
            return Regression_M * distInInches + Regression_B;
        }
    }

    // *All* commands for the subsystem belong in here. It's easy for the simple "call a method"
    // commands, but for more complicated commands, scroll down to see AutoVelocity/AutoVelocityImpl
    public static class Commands {

        // This is a little strange: It's a place to tuck away a reference to the Launcher Subsystem,
        // so that all the commands can get to it there.
        // It lets us do this:
        //    button.whenPressed(Launcher.Commands.IncreaseMotor());
        // Instead of this:
        //    button.whenPressed(Launcher.Commands.IncreaseMotor(r.launcherComponent));
        // This doesn't work if we have *two* different launchers, but I *think* that's unlikely in an
        // FTC game ;)
        protected static Component component = null;

        // This command is a "while" thing: It sets it once.
        // If you want to keep it going, use AutoVelocity instead
        public static Command Launch() {
            return Command.create(component::autoSetVelocityTarget);
        }

        public static Command StopLaunch() {
            return Command.create(component::stop);
        }

        public static Command IncreaseVelocity() {
            return Command.create(component::increasePower);
        }

        public static Command DecreaseVelocity() {
            return Command.create(component::decreasePower);
        }

        // This is just to make all commands look the same to the 'outside' user:
        // You just call LauncherCommands.AutoVelocity() instead of needing to differentiate
        // between simple Command.create's and more complex "class" commands.
        public static Command AutoVelocity() {
            return new AutoVelocityImpl();
        }

        // This class is protected to ensure consistency: you don't use
        //   button.whenPressed(new AutoVelocityImpl());
        // but instead you have to use
        //   button.whenPressed(Launcher.Commands.AutoVelocity());
        protected static class AutoVelocityImpl implements Command {

            public AutoVelocityImpl() {}

            // This command is designed to *never* finish.
            // It should be run in a parallel command group/alongWith/raceWith group.
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public void execute() {
                component.autoSetVelocityTarget();
            }
        }
    }

    @Configurable
    public static class Component implements Loggable, Subsystem {

        @Log.Number(name = "Target Velocity")
        public static double targetVelocity = 0.0;

        @Log.Number(name = "Current Velocity")
        public static double motorVelocity;

        @Log.Number(name = "Target Power")
        public static double targetPower;

        @Log.Number(name = "Manual Extra Power")
        public static double additionalAmount;

        @Log.Number(name = "AutoAim Velocity")
        public static double autoVelocity;

        private static PIDFController pidfController;

        // External dependencies this component requires:
        EncodedMotor<DcMotorEx> launcher1;
        EncodedMotor<DcMotorEx> launcher2;
        TargetAcquisition targetAcquisition;
        DoubleSupplier voltage;

        private static EncodedMotor<DcMotorEx> configMotor(EncodedMotor<DcMotorEx> m, boolean rev) {
            return (m == null)
                ? null
                : m.setDirection(rev ? Direction.REVERSE : Direction.FORWARD).coast();
        }

        public Component(
            EncodedMotor<DcMotorEx> primary,
            EncodedMotor<DcMotorEx> secondary,
            TargetAcquisition targetSubsystem,
            DoubleSupplier voltageSup
        ) {
            // Save this off for commands to use
            Commands.component = this;
            launcher1 = configMotor(primary, Config.PrimaryReversed);
            launcher2 = configMotor(secondary, Config.SecondaryReversed);
            targetAcquisition = targetSubsystem;
            voltage = voltageSup != null ? voltageSup : () -> Config.PeakVoltage;

            // A quick wander around google comes up with something like this for motor feedfwd:

            // launcherMyPID = new PIDFController(Config.launcherPID, target ->
            //    (Config.kStaticFriction + Config.kVelocityConstant * target) / voltage.getAsDouble());

            // The point is that motor RPM scales linearly with voltage, so to compensate, you should
            // divide by voltage: Don't try to scale something by a delta from peak. Just divide.

            // To solve that formula, get a fresh battery, run it at full power and measure the RPM.
            // (Well, and figure out kStaticFriction, too: The lowest value that will still get the
            // launcher barely moving)

            // NOTE The FeedForward Helper opmode calculates these numbers for you automatically!
            pidfController = new PIDFController(
                Config.launchPID,
                target ->
                    (Math.signum(target) * // signum(<0) = -1, signum(>0) = 1, signum(0) = *0*
                            (Config.kStaticFriction + getMotor1Current() * Config.MotorResistance) +
                        Config.kVelocityConstant * target) /
                    voltage.getAsDouble()
            );

            setVelocityTarget(0);
            CommandScheduler.register(this);
        }

        public Component() {
            this(null, null, null, null);
        }

        public Component(
            EncodedMotor<DcMotorEx> primary,
            TargetAcquisition targetSubsystem,
            DoubleSupplier voltageSup
        ) {
            this(primary, null, targetSubsystem, voltageSup);
        }

        public Component(EncodedMotor<DcMotorEx> primary, DoubleSupplier voltageSup) {
            this(primary, null, null, voltageSup);
        }

        public Component(
            EncodedMotor<DcMotorEx> primary,
            EncodedMotor<DcMotorEx> secondary,
            DoubleSupplier voltageSup
        ) {
            this(primary, secondary, null, voltageSup);
        }

        public void setVelocityTarget(double speed) {
            pidfController.setTarget(speed);
        }

        public double getVelocityTarget() {
            return pidfController.getTarget();
        }

        public void autoSetVelocityTarget() {
            // Spin the motors pid goes here
            setVelocityTarget(calculateVelocityTarget()); //change to auto aim velocity
        }

        protected void setPower(double pow) {
            double power = Math.clamp(pow, -1, 1);
            targetPower = power;
            if (hasLaunch1()) {
                launcher1.setPower(power);
            }
            if (hasLaunch2()) {
                launcher2.setPower(power);
            }
        }

        public double getActualVelocity() {
            if (hasLaunch1()) {
                return launcher1.getVelocity() * (Config.ReverseEncoder ? -1 : 1);
            } else {
                return Double.NaN; // Not a Number
            }
        }

        public double getMotor1Current() {
            return hasLaunch1() ? launcher1.getAmperage(CurrentUnit.AMPS) : -1;
        }

        public double getMotor2Current() {
            return hasLaunch2() ? launcher2.getAmperage(CurrentUnit.AMPS) : -1;
        }

        public void stop() {
            setVelocityTarget(0);
        }

        public void increasePower() {
            // Spin the motors pid goes here
            additionalAmount += Config.PowerDelta;
        }

        public void decreasePower() {
            // Spin the motors pid goes here
            additionalAmount -= Config.PowerDelta;
        }

        public double calculateVelocityTarget() {
            // x = distance in inches
            double x = getTargetDistance();
            return Config.CalcVelocity(x);
        }

        // This lets the 'no hardware' or 'subsystem disabled' thing still work without a functional
        // target acquisition subsystem
        private double getTargetDistance() {
            if (targetAcquisition != null) {
                return targetAcquisition.getDistance();
            }
            return Config.DefaultDistance;
        }

        @Override
        public void periodic() {
            autoVelocity = calculateVelocityTarget();
            targetVelocity = getVelocityTarget();
            motorVelocity = getActualVelocity();
            if (pidfController.getTarget() != 0) {
                double power = pidfController.update(motorVelocity);
                setPower(power + Math.copySign(additionalAmount, power));
            } else {
                setPower(0);
                // When we want to stop, reset the PID controller, too
                pidfController.update(motorVelocity);
                pidfController.reset();
            }
        }

        boolean hasLaunch1() {
            return launcher1 != null;
        }

        boolean hasLaunch2() {
            return launcher2 != null;
        }
    }

    @SuppressWarnings("unused")
    @TeleOp(name = "Launcher Validation", group = "Launcher")
    public static class Validator extends ValidationOpMode {

        public Launcher.Component lc;

        @Override
        public void init() {
            super.init();
            String name2 = Config.get2ndMotorName();
            lc = new Launcher.Component(
                new EncodedMotor<>(Config.getMotorName()),
                name2 == null ? null : new EncodedMotor<>(name2),
                this::getVoltage
            );
        }

        @Override
        public void loop() {
            super.loop();
            addLine(">>> Press left trigger for Launcher1 control");
            addLine(">>> Press right trigger for Launcher2 control");
            addLine(">>> Hit the dpad to stop");
            String res = "";
            double p1 = gamepad1.left_trigger;
            double p2 = gamepad1.right_trigger;
            if (lc.hasLaunch1()) {
                lc.launcher2.setPower(p1);
                res += "lt " + p1;
            } else {
                res += "(no launcher1) ";
            }
            if (lc.hasLaunch2()) {
                lc.launcher1.setPower(p2);
                res += "rt " + p2;
            } else {
                res += "(no launcher2) ";
            }
            res += String.format(
                Locale.ENGLISH,
                "Speed: %.2f, Current1: %.2f, Current2: %.2f",
                lc.getActualVelocity(),
                lc.getMotor1Current(),
                lc.getMotor2Current()
            );
            addLine(res);
            if (anyDpadReleased()) {
                terminateOpModeNow();
            }
        }
    }

    @SuppressWarnings("unused")
    @TeleOp(name = "Launcher FeedFwd Helper", group = "Launcher")
    public static class FeedFwdHelper extends ValidationOpMode {

        private enum State {
            MeasureStaticFriction,
            DoneWithFriction,
            MeasureVelocity,
            DoneWithVelocity,
            Testing,
            Abort,
        }

        Launcher.Component lc = null;
        State state = State.MeasureStaticFriction;
        String extra = "";

        @Override
        public void init() {
            super.init();
            String name2 = Config.get2ndMotorName();
            lc = new Launcher.Component(
                new EncodedMotor<>(Config.getMotorName()),
                name2 == null ? null : new EncodedMotor<>(name2),
                this::getVoltage
            );
            state = State.MeasureStaticFriction;
            lc.setPower(0);
        }

        double staticFriction = 0.001;
        double velocityConstant = 0;

        double staticFrictionStep = 0.001;
        MovingStatistics velocityConstantStats = new MovingStatistics(50);
        double vel = 0;
        double peakVel = 0;
        double targetVelocity = 0;
        MovingStatistics error = new MovingStatistics(1000);
        ElapsedTime lastUpdate = new ElapsedTime();

        // Start slowly increasing power until we detect motion, then back off ever so slightly
        // We could try a more elaborate binary-search algorithm, but this works pretty reliably
        private State MeasureStaticFriction() {
            double v = getVoltage();
            double amps = lc.getMotor1Current();
            double power = (staticFriction + amps * Config.MotorResistance) / v;
            lc.setPower(power);
            addData("kStaticFriction", staticFriction);
            addData("Voltage", v);
            addData("Power", power);
            addLine("************");
            addLine("*** Please be patient");
            addLine("*** (press a button to abort)");
            addLine("************");
            if (lastUpdate.milliseconds() >= 100) {
                lastUpdate.reset();
                // We update every 100 milliseconds, just to give it time to trigger the encoder
                double measuredVelocity = lc.getActualVelocity();
                if (measuredVelocity != 0) {
                    lc.setPower(0);
                    staticFriction -= staticFrictionStep;
                    if (measuredVelocity < 0) {
                        extra = "Make sure to set Reverse the encoder!";
                        Config.ReverseEncoder = true;
                    }
                    return State.DoneWithFriction;
                }
                staticFriction += staticFrictionStep;
            }
            if (anyButtonsReleased()) {
                lc.setPower(0);
                return State.Abort;
            }
            return State.MeasureStaticFriction;
        }

        private State DoneWithFriction() {
            // If we're here, the system started moving. Stop the motors and set kStaticFriction to
            // just below what was necessary to start the system moving.
            // Display results of Static Friction calculator & wait for user.
            addData("kStaticFriction-->>", staticFriction);
            if (!extra.isBlank()) addLine(extra);
            addLine("************");
            addLine("*** Hit a button to begin velocity measurement");
            addLine("************");
            lastUpdate.reset();
            return (anyButtonsReleased() || anyDpadReleased())
                ? State.MeasureVelocity
                : State.DoneWithFriction;
        }

        private State MeasureVelocity() {
            // We're measuring the kVelocityConstant:
            double vol = 0;
            double amps;
            if (anyButtonsReleased()) {
                velocityConstant = velocityConstantStats.getMean();
                lc.setPower(0);
                return State.DoneWithVelocity;
            }
            lc.setPower(1);
            if (lastUpdate.milliseconds() >= 50) {
                lastUpdate.reset();
                vel = lc.getActualVelocity();
                peakVel = Math.max(peakVel, vel);
                double curVol = getVoltage();
                if (curVol > 0) {
                    vol = curVol;
                }
                amps = lc.getMotor1Current();
                if (vel != 0) {
                    // power = (kStaticFriction + kVelocityConstant * RPM + motorAmperage * motorResistance) / v
                    // So
                    //   1 = (kSF + kV * RPM + motorAmperage * motorResistance) / v;
                    // solve for kV:
                    //   kV = (v - kSF - motorAmperage * motorResistance) / RPM
                    velocityConstant = (vol - staticFriction - amps * Config.MotorResistance) / vel;
                    velocityConstantStats.add(velocityConstant);
                }
            }
            addData("kStaticFriction!", staticFriction);
            addData("Current kV", velocityConstant);
            addData("Average kV", velocityConstantStats.getMean());
            addData("Velocity", vel);
            addData("Voltage", vol);
            addData("Peak Velocity", peakVel);
            addLine("************");
            addLine("*** Press a button to stop velocity measurement");
            addLine("************");
            return State.MeasureVelocity;
        }

        private State DoneWithVelocity() {
            // Update the values from the Velocity Constant calculator & go to testing
            lc.setVelocityTarget(vel * 0.5);
            error.clear();
            error.add(0);
            lastUpdate.reset();
            Config.kStaticFriction = staticFriction;
            Config.kVelocityConstant = velocityConstant;
            return State.Testing;
        }

        private State Testing() {
            // DoneWithVelocity sets the config values, so let's use the launcher's periodic
            // function to test the results.
            lc.periodic();
            if (lastUpdate.milliseconds() > 100) {
                lastUpdate.reset();
                vel = lc.getActualVelocity();
                error.add(targetVelocity - vel);
            }
            addData("kStaticFriction", staticFriction);
            addData("kVelocityConstant", velocityConstant);
            addData("Current", lc.getMotor1Current());
            addData("Voltage", getVoltage());
            addLine(
                String.format(Locale.ENGLISH, "Vel target: %f (actual: %f)", targetVelocity, vel)
            );
            addData("Power", Component.targetPower);
            if (!extra.isBlank()) addLine(extra);
            addLine(
                String.format(
                    Locale.ENGLISH,
                    "Error Mean %.2f, stdev %03f",
                    error.getMean(),
                    error.getStandardDeviation()
                )
            );
            addLine("************");
            addLine("*** Press the dpad to change target velocity");
            addLine("************");
            if (gamepad1.dpadLeftWasPressed()) {
                lastUpdate.reset();
                targetVelocity -= 100;
            } else if (gamepad1.dpadRightWasPressed()) {
                lastUpdate.reset();
                targetVelocity += 100;
            } else if (gamepad1.dpadUpWasPressed()) {
                lastUpdate.reset();
                targetVelocity += 10;
            } else if (gamepad1.dpadDownWasPressed()) {
                lastUpdate.reset();
                targetVelocity -= 10;
            }
            return State.Testing;
        }

        public void loop() {
            // Look at that, a silly little state machine...
            switch (state) {
                case MeasureStaticFriction:
                    state = MeasureStaticFriction();
                    break;
                case DoneWithFriction:
                    state = DoneWithFriction();
                    break;
                case MeasureVelocity:
                    state = MeasureVelocity();
                    break;
                case DoneWithVelocity:
                    state = DoneWithVelocity();
                    break;
                case Testing:
                    state = Testing();
                    break;
                case Abort:
                default:
                    terminateOpModeNow();
                    break;
            }

            super.loop();
        }
    }
}
