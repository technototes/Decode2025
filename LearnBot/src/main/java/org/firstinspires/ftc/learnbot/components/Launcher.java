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

        // You can (should...) pull this from a Setup.* location:
        public static String getMotorName() {
            return "rr"; // return Setup.HardwareNames.MyLauncherMotor; for example
        }

        // Is the primary intake motor reversed?
        public static boolean PrimaryReversed = true;
        // Is the secondary intake motor reversed?
        public static boolean SecondaryReversed = false;

        // TODO: Document this better
        public static PIDFCoefficients launchPID = new PIDFCoefficients(0.004, 0.0002, 0.0, 0);
        public static double kStaticFriction = 0.15;
        public static double kVelocityConstant = 0.0043;
        public static double PeakVoltage = 13.6;
        // GoBilda says stall current of 9.2A at 12V, so V = I * R, R = 12 / 9.2 (about 1.3)
        // As the motor heats up, resistance also increase, so we could increase this a little bit
        // or maybe increase it over time to counteract that, but this is probably good enough for
        // now
        public static double MotorResistance = 12 / 9.2;

        // This is how much to add (or substract) when increase/decrease velocity is invoked
        public static double additionDelta = 10;
        public static double REGRESSION_M = 6.261; // multiplier for x for close zone launch speed formula
        public static double REGRESSION_B = 1550; // minimum velocity for close zone launch speed formula

        public static double CalcVelocity(double distInInches) {
            return REGRESSION_M * distInInches + REGRESSION_B;
        }
    }

    // *All* commands for the subsystem belong in here. It's easy for the simple "call a method"
    // commands, but for more complicated commands, scroll down to see AutoVelocity/AutoVelocityImpl
    public static class Commands {

        // This is a little strange: It's a place to tuck away a reference to the Launcher Subsystem,
        // so that all the commands can get to it there.
        // It let's us do this:
        //    button.whenPressed(Launcher.Commands.IncreaseMotor());
        // Instead of this:
        //    button.whenPressed(Launcher.Commands.IncreaseMotor(r.launcherComponent));
        // This doesn't work if we have *two* different launchers, but I *think* that's unlikely in an
        // FTC game ;)
        protected static Component component = null;

        // This command is a "while" thing: It sets it once.
        // If you want to keep it going, use AutoVelocity instead
        public static Command Launch() {
            return Command.create(component::setAutoVelocity);
        }

        public static Command StopLaunch() {
            return Command.create(component::Stop);
        }

        public static Command IncreaseVelocity() {
            return Command.create(component::IncreaseVelocity);
        }

        public static Command DecreaseVelocity() {
            return Command.create(component::DecreaseVelocity);
        }

        // This is just to make all commands look the same to the 'outside' user:
        // You just call LauncherCommands.AutoVelocity() instead of needing to differentiate
        // between simple Command.create's and more complex "class" commands.
        public static Command AutoVelocity() {
            return new AutoVelocityImpl();
        }

        // This class is protected to ensure that elsewhere, you can't ever use
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
                component.setAutoVelocity();
            }
        }
    }

    @Configurable
    public static class Component implements Loggable, Subsystem {

        @Log.Number(name = "Target Velocity")
        public static double targetLaunchVelocity = 1150;

        @Log.Number(name = "Current Motor Velocity")
        public static double currentLaunchVelocity = 0.0;

        public static double motorVelocity;
        public static double additionalAmount;

        public static double launcher1Current;
        public static double launcher2Current;

        @Log(name = "Target Speed: ")
        public static double targetSpeed;

        @Log(name = "Target Power: ")
        public static double targetPower;

        private static PIDFController launcherPID;
        public static double lastAutoVelocity = 0;

        @Log.Number(name = "AutoAim Velocity")
        public static double autoVelocity;

        // External dependencies this component requires:
        EncodedMotor<DcMotorEx> launcher1;
        EncodedMotor<DcMotorEx> launcher2;
        TargetAcquisition targetAcquisition;
        DoubleSupplier voltage;

        public Component(
            EncodedMotor<DcMotorEx> primary,
            EncodedMotor<DcMotorEx> secondary,
            TargetAcquisition targetSubsystem,
            DoubleSupplier voltageSup
        ) {
            // Save this off for commands to use
            Commands.component = this;
            launcher1 = primary;
            if (hasLaunch1()) {
                launcher1.setDirection(
                    Config.PrimaryReversed ? Direction.REVERSE : Direction.FORWARD
                );
                launcher1.coast();
            }
            launcher2 = secondary;
            if (hasLaunch2()) {
                launcher2.setDirection(
                    Config.SecondaryReversed ? Direction.REVERSE : Direction.FORWARD
                );
                launcher2.coast();
            }
            targetAcquisition = targetSubsystem;
            voltage = voltageSup != null ? voltageSup : () -> Config.PeakVoltage;

            // A quick wander around google comes up with something like this for motor feedfwd:

            // launcherMyPID = new PIDFController(Config.launcherPID, target ->
            //    (Config.kStaticFriction + Config.kVelocityConstant * target) / voltage.getAsDouble());

            // The point is that motor RPM scales linearly with voltage, so to compensate, you should
            // divide by voltage: Don't try to scale something by a delta from peak. Just divide.

            // To get solve that formula, get a fresh battery, run it at full power and measure the RPM.
            // (Well, and figure out kStaticFriction, too: The lowest value that will still get the
            // launcher barely moving)
            launcherPID = new PIDFController(Config.launchPID, target -> {
                if (target == 0) return 0.0;
                return (
                    (Math.copySign(
                            Config.kStaticFriction + getMotor1Current() * Config.MotorResistance,
                            target
                        ) +
                        Config.kVelocityConstant * target) /
                    voltage.getAsDouble()
                );
            });

            setTargetVelocity(0);
            CommandScheduler.register(this);
        }

        public Component() {
            this(null, null, null, () -> 0);
        }

        public Component(
            EncodedMotor<DcMotorEx> primary,
            TargetAcquisition targetSubsystem,
            DoubleSupplier voltageSup
        ) {
            this(primary, null, targetSubsystem, voltageSup);
        }

        public void setTargetVelocity(double speed) {
            targetSpeed = speed;
            launcherPID.setTarget(speed);
        }

        public double getTargetVelocity() {
            return targetSpeed;
        }

        public void setAutoVelocity() {
            // Spin the motors pid goes here
            setTargetVelocity(calculateVelocity()); //change to auto aim velocity
        }

        protected void setVelocity(double pow) {
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
                return launcher1.getVelocity();
            } else {
                return Double.NaN; // Not a Number
            }
        }

        public double getMotor1Current() {
            if (hasLaunch1()) {
                return launcher1.getAmperage(CurrentUnit.AMPS);
            }
            return -1;
        }

        public double getMotor2Current() {
            if (hasLaunch2()) {
                return launcher2.getAmperage(CurrentUnit.AMPS);
            }
            return -1;
        }

        public void Stop() {
            launcherPID.setTarget(0);
        }

        public void IncreaseVelocity() {
            // Spin the motors pid goes here
            additionalAmount += Config.additionDelta;
        }

        public void DecreaseVelocity() {
            // Spin the motors pid goes here
            additionalAmount -= Config.additionDelta;
        }

        public double calculateVelocity() {
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
            return -1;
        }

        @Override
        public void periodic() {
            autoVelocity = calculateVelocity();
            currentLaunchVelocity = getTargetVelocity();
            motorVelocity = getActualVelocity();
            if (launcherPID.getTarget() != 0) {
                double power = launcherPID.update(motorVelocity);
                power += Math.copySign(additionalAmount, power);
                setVelocity(power);
            } else {
                setVelocity(0);
                // When we want to stop, reset the PID controller, too
                launcherPID.update(motorVelocity);
                launcherPID.reset();
            }
            launcher1Current = getMotor1Current();
            launcher2Current = getMotor2Current();
        }

        // This should be used by a test opmode to check that the basics are working.
        public String hardwareValidation(double power1, double power2) {
            String res = "";
            if (hasLaunch1()) {
                launcher1.setPower(power1);
            } else {
                res += "(no launcher1) ";
            }
            if (hasLaunch2()) {
                launcher2.setPower(power2);
            } else {
                res += "(no launcher2) ";
            }
            res += String.format(
                Locale.ENGLISH,
                "Speed: %.2f, Current1: %.2f, Current2: %.2f",
                getActualVelocity(),
                getMotor1Current(),
                getMotor2Current()
            );
            return res;
        }

        private boolean hasLaunch1() {
            return launcher1 != null;
        }

        private boolean hasLaunch2() {
            return launcher2 != null;
        }
    }

    /*************************************************************************
     * Op modes: Can they be internal classes? I don't know! Let's find out! *
     *************************************************************************/
    @Configurable
    @SuppressWarnings("unused")
    @TeleOp(name = "Launcher Validation", group = "Launcher")
    public static class Validator extends ValidationOpMode {

        public Launcher.Component lc;

        @Override
        public void init() {
            super.init();
            EncodedMotor<DcMotorEx> m = new EncodedMotor<>(
                hardwareMap.get(DcMotorEx.class, Config.getMotorName()),
                Config.getMotorName()
            );
            lc = new Launcher.Component(m, null, this::getVoltage);
        }

        @Override
        public void loop() {
            super.loop();
            addLine(">>> Press left trigger for Launcher1 control");
            addLine(">>> Press right trigger for Launcher2 control");
            addLine(">>> Hit the dpad to stop");
            addLine(lc.hardwareValidation(gamepad1.left_trigger, gamepad1.right_trigger));
            if (anyDpadReleased()) {
                terminateOpModeNow();
            }
        }
    }

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

        @Override
        public void init() {
            super.init();
            EncodedMotor<DcMotorEx> m = new EncodedMotor<>(
                hardwareMap.get(DcMotorEx.class, Config.getMotorName()),
                Config.getMotorName()
            );
            lc = new Launcher.Component(m, null, this::getVoltage);
            state = State.MeasureStaticFriction;
            lc.setVelocity(0);
        }

        double staticFriction = 0.001;
        double velocityConstant = 0;

        double staticFrictionStep = 0.001;
        MovingStatistics velocityConstantStats = new MovingStatistics(50);
        double vel = 0;
        double targetVelocity = 0;
        MovingStatistics error = new MovingStatistics(1000);
        ElapsedTime lastUpdate = new ElapsedTime();

        // Start slowly increasing power until we detect motion, then back off ever so slightly
        private State MeasureStaticFriction() {
            double v = getVoltage();
            double amps = lc.getMotor1Current();
            double power = (staticFriction + amps * Config.MotorResistance) / v;
            lc.setVelocity(power);
            addLine("Press a button to abort (and just be patient)");
            addData("kStaticFriction", staticFriction);
            addData("Voltage", v);
            addData("Power", power);
            if (lastUpdate.milliseconds() >= 100) {
                lastUpdate.reset();
                // We update every 100 milliseconds, just to give it time to trigger the encoder
                if (lc.getActualVelocity() != 0) {
                    lc.setVelocity(0);
                    staticFriction -= staticFrictionStep;
                    return State.DoneWithFriction;
                }
                staticFriction += staticFrictionStep;
            }
            if (anyButtonsReleased()) {
                lc.setVelocity(0);
                return State.Abort;
            }
            return State.MeasureStaticFriction;
        }

        private State DoneWithFriction() {
            // Display results of Static Friction calculator & wait for user
            addData("kStaticFriction-->>", staticFriction);
            addLine("Hit a button to start the velocity measurement");
            lastUpdate.reset();
            return (anyButtonsReleased() || anyDpadReleased())
                ? State.MeasureVelocity
                : State.DoneWithFriction;
        }

        private State MeasureVelocity() {
            // If we're here, the system started moving. Stop the motors and set kStaticFriction to
            // just below what was necessary to start the system moving.
            // Display the kStaticFriction value we got:
            // Next up: Velocity!
            double vol = 0;
            double amps;
            if (anyButtonsReleased()) {
                velocityConstant = velocityConstantStats.getMean();
                lc.setVelocity(0);
                return State.DoneWithVelocity;
            }
            lc.setVelocity(1);
            if (lastUpdate.milliseconds() >= 50) {
                lastUpdate.reset();
                vel = lc.getActualVelocity();
                double curVol = getVoltage();
                if (curVol > 0) {
                    vol = curVol;
                }
                amps = lc.getMotor1Current();
                if (vel != 0) {
                    // power = (kStaticFriction + kVelocityConstant * RPM) / v
                    // So
                    //   1 = (kSF + kV * RPM + motorAmperage * motorResistance) / v;
                    // solve for kV:
                    //   kV = (v - kSF - motorAmperage * motorResistance) / RPM
                    velocityConstant = (vol - staticFriction - amps * Config.MotorResistance) / vel;
                    velocityConstantStats.add(velocityConstant);
                }
            }
            addData("kStaticFriction!", staticFriction);
            addLine("Press a button to stop velocity measurement");
            addData("kVelocityConstant", velocityConstant);
            addData("Average kV", velocityConstantStats.getMean());
            addData("Velocity", vel);
            addData("Voltage", vol);
            addData("Power", 1.0);
            return State.MeasureVelocity;
        }

        private State DoneWithVelocity() {
            // Display results of Velocity Constant calculator & wait for user
            addData("Measured kStaticFriction", staticFriction);
            addData("Measured kVelocityConstant", velocityConstant);
            addLine("Hit a button or move the dpad to move to testing");
            targetVelocity = vel * 0.5;
            error.clear();
            error.add(0);
            lastUpdate.reset();
            return (anyButtonsReleased() || anyDpadReleased())
                ? State.Testing
                : State.DoneWithVelocity;
        }

        private State Testing() {
            double pow = targetVelocity == 0
                ? 0
                : ((Math.copySign(staticFriction, targetVelocity) +
                          velocityConstant * targetVelocity) /
                      getVoltage());
            lc.setVelocity(pow);
            if (lastUpdate.milliseconds() > 250) {
                lastUpdate.reset();
                vel = lc.getActualVelocity();
                error.add(targetVelocity - vel);
            }
            addData("kStaticFriction", staticFriction);
            addData("kVelocityConstant", velocityConstant);
            addLine("Press any button to stop");
            addLine("Press the dpad to change target velocity");
            addData("target", targetVelocity);
            addData("measured", vel);
            addData("Avg Err (after 0.25s)", error.getMean());
            addData("stddev", error.getStandardDeviation());
            addData("Power", pow);
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
            return anyButtonsReleased() ? State.Abort : State.Testing;
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
