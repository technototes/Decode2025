package org.firstinspires.ftc.sixteen750.component;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.MovingStatistics;
import com.technototes.library.command.Command;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import com.technototes.library.subsystem.TargetAcquisition;
import com.technototes.library.util.PIDFController;
import java.util.Locale;
import java.util.function.DoubleSupplier;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

@Configurable
public class LauncherComponent implements Loggable, Subsystem {

    // This is a little strange: It's a place to tuck away a reference to the Launcher Subsystem,
    // so that all the commands can get to it there.
    // It let's us do this:
    //    button.whenPressed(LauncherComponent.Commands.IncreaseMotor());
    // Instead of this:
    //    button.whenPressed(LauncherComponent.Commands.IncreaseMotor(r.launcherComponent));
    private static LauncherComponent self = null;

    // *ALL* the configuration should go in here. I moved some things that had been constants up
    // to here, as they are "bot build configuration": are the motors reversed.
    // TODO: Make "are there 1 or 2 motors" a configuration setting
    public static class Config {

        // Is the primary intake motor reversed?
        public static boolean PrimaryReversed = true;
        // Is the secondary intake motor reversed?
        public static boolean SecondaryReversed = false;

        //    @Log.Number(name = "Motor Power")
        //    public static double MOTOR_POWER = 0.65; // 0.5 1.0
        public static double CloseTargetLaunchVelocity = 1400;
        public static double FarTargetLaunchVelocity = 1850;
        public static double FarTargetLaunchVelocityForAuto = 2300;
        public static double TargetLaunchVelocityForAuto1 = 1950;
        public static double TargetLaunchVelocityForAuto2 = 1850;

        // TODO: Document this better
        public static PIDFCoefficients launcherPI = new PIDFCoefficients(0.004, 0.0002, 0.0, 0);
        public static double Near_P = 0.004;
        public static double Near_I = 0.0002;
        public static double Far_P = 0.004;
        public static double Far_I = 0.0002;
        public static double SPIN_F_SCALE = 0.00021;
        public static double SPIN_VOLT_COMP = 0.0216;
        public static double DIFFERENCE = 0.0046;
        public static double PEAK_VOLTAGE = 13;

        // TODO: Make this more configurable. Maybe just turn it into a little helper function that
        // lives in this class?
        public static double REGRESSION_A = 6.261; // multiplier for x for close zone launch speed formula
        public static double REGRESSION_B = 1550; // minimum velocity for close zone launch speed formula
        public static double REGRESSION_C = 20; // multiplier for x for far zone launch speed formula
        public static double REGRESSION_D = 320; // minimum velocity for far zone launch speed formula - 130, 255
        public static double REGRESSION_C_TELEOP = 20; // multiplier for x for far zone launch speed formula
        public static double REGRESSION_D_TELEOP = 130; // minimum velocity for far zone launch speed formula
        public static double REGRESSION_C_AUTO = 17; // multiplier for x for far zone launch speed formula
        public static double REGRESSION_D_AUTO = 115; // minimum velocity for far zone launch speed formula
    }

    // *All* commands for the subsystem belong in here. It's easy for the simple "call a method"
    // commands, but for more complicated commands, scroll down to see AutoVelocity/AutoVelocityImpl
    public static class LauncherCommand {

        public static Command Launch() {
            return Command.create(self::Launch);
        }

        public static Command SetFarShoot() {
            return Command.create(self::FarShoot);
        }

        public static Command SetCloseShoot() {
            return Command.create(self::CloseShoot);
        }

        public static Command AutoLaunch1() {
            return Command.create(self::AutoLaunch1);
        }

        public static Command AutoLaunch2() {
            return Command.create(self::AutoLaunch2);
        }

        public static Command FarAutoLaunch() {
            return Command.create(self::FarAutoLaunch);
        }

        public static Command StopLaunch() {
            return Command.create(self::Stop);
        }

        public static Command IncreaseMotor() {
            return Command.create(self::IncreaseMotorVelocity);
        }

        public static Command DecreaseMotor() {
            return Command.create(self::DecreaseMotorVelocity);
        }

        public static Command ReadVelocity() {
            return Command.create(self::readVelocity);
        }

        public static Command SetRegressionAuto() {
            return Command.create(self::setRegressionAuto);
        }

        public static Command SetRegressionTeleop() {
            return Command.create(self::setRegressionTeleop);
        }

        public static Command IncreaseRegressionDTeleop() {
            return Command.create(self::increaseRegressionDTeleop);
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
        //   button.whenPressed(LaunchCommand.AutoVelocity());
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
                LauncherComponent.self.Launch();
            }
        }
    }

    @Log.Number(name = "Target Velocity")
    public static double targetLaunchVelocity = 1150;

    @Log.Number(name = "Current Motor Velocity")
    public static double currentLaunchVelocity = 0.0;

    public static double motorVelocity;
    public static double additionAmount;
    public static double additionDelta = 5;

    //@Log(name = "Launcher Power: ")
    public static double power;

    //@Log(name = "Error")
    public static double err;
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

    public LauncherComponent(
        EncodedMotor<DcMotorEx> primary,
        EncodedMotor<DcMotorEx> secondary,
        TargetAcquisition targetSubsystem,
        DoubleSupplier voltageSup
    ) {
        self = this;
        launcher1 = primary;
        if (hasLaunch1()) {
            launcher1.setDirection(
                Config.PrimaryReversed
                    ? DcMotorSimple.Direction.REVERSE
                    : DcMotorSimple.Direction.FORWARD
            );
            launcher1.coast();
        }
        launcher2 = secondary;
        if (hasLaunch2()) {
            launcher2.setDirection(
                Config.SecondaryReversed
                    ? DcMotorSimple.Direction.REVERSE
                    : DcMotorSimple.Direction.FORWARD
            );
            launcher2.coast();
        }
        targetAcquisition = targetSubsystem;
        voltage = voltageSup != null ? voltageSup : () -> Config.PEAK_VOLTAGE;
        // All the Feedfwd stuff below seems...confused.
        // Notes: Addition will *increase* as voltage decreases (I think this is expected)
        // but it likely won't ever be zero, and it could (with a new battery) be *negative*
        // I've tried to model it here: https://www.desmos.com/calculator/gmzlhkyz5a

        // (I is the initial voltage delta (ADDITION), and V is the voltage as the bot  is runs.
        //  Don't animate I, just V, and you'll see that as voltage decreases, so does the output
        //  power. Drag I around to change the initial voltage delta)

        // The thing that saves us is I, which does what I believe we're expecting: It increase
        // power as initial voltage is lower. But then the FF function drops it.

        // My suggestion: rip out all the 'initial' stuff and make the core F function return higher
        // values as voltage *decreases*, which is I think what we're really looking for anyway.

        double ADDITION = (Config.PEAK_VOLTAGE - voltage.getAsDouble());
        if (ADDITION == 0) {
            // I'd be stunned if this code runs...ever. Not sure what's supposed to happen here.
            Config.SPIN_VOLT_COMP = Config.SPIN_VOLT_COMP + 0.001;
        } else {
            // This seems like a sensible thing: We're adding some amount of voltage delta to
            // compensate for a lower initial voltage
            Config.SPIN_VOLT_COMP = Config.SPIN_VOLT_COMP + (ADDITION * Config.DIFFERENCE);
        }
        launcherPID = new PIDFController(Config.launcherPI, target ->
            target == 0
                ? 0
                : (Config.SPIN_F_SCALE * target) +
                  // This is weird: We're going to *reduce* the output power slightly as voltage
                  // decreases over time. I don't think this is what we're trying to accomplish.
                  (Config.SPIN_VOLT_COMP * Math.min(Config.PEAK_VOLTAGE, voltage.getAsDouble()))
        );
        // A quick wander around google comes up with something like this:

        // launcherMyPID = new PIDFController(Config.launcherPID, target ->
        //    (Config.kStaticFriction + Config.kVelocityConstant * target) / voltage.getAsDouble());

        // The point is that motor RPM scales linearly with voltage, so to compensate, you should
        // divide by voltage: Don't try to scale something by a delta from peak. Just divide.

        // To get solve that formula, get a fresh battery, run it at full power and measure the RPM.
        // (Well, and figure out kStaticFriction, too: The lowest value that will still get the
        // launcher barely moving)

        // These capabilities should probably go in the "validation" function at the bottom :D

        setTargetSpeed(0);
        CommandScheduler.register(this);
    }

    public LauncherComponent() {
        this(null, null, null, () -> 0);
    }

    public LauncherComponent(
        EncodedMotor<DcMotorEx> primary,
        TargetAcquisition targetSubsystem,
        DoubleSupplier voltageSup
    ) {
        this(primary, null, targetSubsystem, voltageSup);
    }

    public void Launch() {
        // Spin the motors pid goes here
        setTargetSpeed(autoVelocity()); //change to auto aim velocity
    }

    public void CloseShoot() {
        // Spin the motors pid goes here
        targetLaunchVelocity = Config.CloseTargetLaunchVelocity;
    }

    public void FarShoot() {
        // Spin the motors pid goes here
        targetLaunchVelocity = Config.FarTargetLaunchVelocity;
    }

    public void AutoLaunch1() {
        // Spin the motors pid goes here
        setTargetSpeed(Config.TargetLaunchVelocityForAuto1); //change to auto aim velocity
    }

    public void AutoLaunch2() {
        // Spin the motors pid goes here
        setTargetSpeed(Config.TargetLaunchVelocityForAuto2); //change to auto aim velocity
    }

    public void FarAutoLaunch() {
        // Spin the motors pid goes here
        setTargetSpeed(Config.FarTargetLaunchVelocityForAuto); //change to auto aim velocity
    }

    public double readVelocity() {
        if (hasLaunch1()) {
            return launcher1.getVelocity();
        } else {
            return Double.NaN; // Nan = "Not a Number"
        }
    }

    public void setTargetSpeed(double speed) {
        targetSpeed = speed;
        launcherPID.setTarget(speed);
    }

    public double getTargetSpeed() {
        return targetSpeed;
    }

    private void setMotorPower(double pow) {
        double power = Math.clamp(pow, -1, 1);
        targetPower = power;
        if (hasLaunch1()) {
            launcher1.setPower(power);
        }
        if (hasLaunch2()) {
            launcher2.setPower(power);
        }
    }

    public double getMotorSpeed() {
        if (hasLaunch1()) {
            return launcher1.getVelocity();
        }
        return -1;
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

    public void IncreaseMotorVelocity() {
        // Spin the motors pid goes here
        additionAmount += additionDelta;
    }

    public void DecreaseMotorVelocity() {
        // Spin the motors pid goes here
        additionAmount -= additionDelta;
    }

    public double getMotor1Velocity() {
        if (hasLaunch1()) {
            return launcher1.getVelocity();
        } else {
            return Double.NaN; // Not a Number
        }
    }

    public double getMotor2Velocity() {
        if (hasLaunch2()) {
            return launcher2.getVelocity();
        } else {
            return Double.NaN; // Not a Number
        }
    }

    // This is wrong (and, as it turns out, unused), so I just commented it out
    // TODO: We should discuss how to accomplish what this is attempting to do
    /*
    public void VelocityShoot() {
        if (
            getMotor1Velocity() == targetLaunchVelocity &&
            getMotor2Velocity() == targetLaunchVelocity
        ) {
            // This doesn't do anything. It *creates* a command, but the command is never
            // scheduled, so it won't ever actually do anything.
            TeleCommands.GateDown(robot);
        }
    }
    */

    // TOOD: Turn this into a quadratic equation instead, since that's what it's mimicking.
    public double autoVelocity() {
        // x = distance in inches
        double x = getTargetDistance();

        if (x < 0) {
            return lastAutoVelocity;
        } else if (x < 100) {
            // launcherPI.p = 0.0015;
            // launcherPI.i = 0;
            lastAutoVelocity = Config.REGRESSION_A * x + Config.REGRESSION_B;
            return lastAutoVelocity;
        } else {
            // NOTE: These two lines don't appear to do anything, because we're not using the
            // Near_P and Near_I values anywhere (they were commented out above)
            // TODO: Assigning values to something in Config is 'bad form'. Instead, we should
            // have the coefficients copied into the Launcher itself in the constructor, and change
            // that here.
            Config.launcherPI.p = Config.Far_P;
            Config.launcherPI.i = Config.Far_I;
            return Config.REGRESSION_C * x + Config.REGRESSION_D;
        }

        //return ((RPM_PER_FOOT * ls.getDistance()) / 12 + MINIMUM_VELOCITY) + addtionamount;
    }

    public void setRegressionAuto() {
        // Spin the motors pid goes here
        Config.REGRESSION_C = Config.REGRESSION_C_AUTO;
        Config.REGRESSION_D = Config.REGRESSION_D_AUTO;
    }

    public void setRegressionTeleop() {
        // Spin the motors pid goes here
        Config.REGRESSION_C = Config.REGRESSION_C_TELEOP;
        Config.REGRESSION_D = Config.REGRESSION_D_TELEOP;
    }

    public void increaseRegressionDTeleop() {
        // Spin the motors pid goes here
        Config.REGRESSION_D += 15;
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
        autoVelocity = autoVelocity();
        currentLaunchVelocity = readVelocity();
        if (launcherPID.getTarget() != 0) {
            setMotorPower(launcherPID.update(getMotorSpeed()));
        } else {
            setMotorPower(0);
            launcherPID.update(getMotorSpeed());
            launcherPID.reset();
        }

        err = launcherPID.getLastError();
        motorVelocity = getMotorSpeed();
        if (hasLaunch1()) {
            power = launcher1.getPower();
        } else {
            power = Double.NaN;
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
            res += "(no launcher1)";
        }
        if (hasLaunch2()) {
            launcher2.setPower(power2);
        } else {
            res += "(no launcher2)";
        }
        res += String.format(
            Locale.ENGLISH,
            "Launcher Speed: %.2f, Current1: %.2f, Current2: %.2f",
            getMotorSpeed(),
            getMotor1Current(),
            getMotor2Current()
        );
        return res;
    }

    // TODO: Code that measures kStaticFriction and kVelocityConstant for a FeedFwd function
    public void feedFwdHelper(Telemetry tel, Gamepad gamepad) {
        // Wait until a button's been pressed, then first identify kStaticFriction, by watching to
        // see when the motor just barely starts moving (then back off by a couple percent)
        motorHelperPower(0);
        while (!anyButtonsPressed(gamepad)) {
            tel.addLine("Please press a button on the gamepad to begin test");
            tel.update();
        }
        while (anyButtonsPressed(gamepad)) {
            tel.addLine("Release the button to begin the test");
            tel.update();
        }
        // Okay, slowly raise the power applied to launcher1 until w
        double staticFriction = 0.001;
        double staticFrictionStep = 0.001;
        ElapsedTime lastUpdate = new ElapsedTime();
        while (true) {
            double v = voltage.getAsDouble();
            motorHelperPower(staticFriction / v);
            tel.addLine("Press a button to abort");
            tel.addData("kStaticFriction", staticFriction);
            tel.addData("Voltage", v);
            tel.update();
            if (anyButtonsPressed(gamepad)) {
                motorHelperPower(0);
                return;
            }
            if (lastUpdate.milliseconds() >= 50) {
                // We update every 25 milliseconds, just to give it time to trigger
                if (getMotor1Velocity() != 0) {
                    break;
                }
                lastUpdate.reset();
                staticFriction += staticFrictionStep;
            }
        }
        // If we're here, the system started moving. Stop the motors and set kStaticFriction to
        // just below what was necessary to start the system moving.
        staticFriction -= staticFrictionStep;
        motorHelperPower(0);
        // Display the kStaticFriction value we got:
        while (!anyButtonsPressed(gamepad)) {
            tel.addData("kStaticFriction-->>", staticFriction);
            tel.addLine("Press a button to start the velocity measurement");
            tel.update();
        }
        // Next up: Velocity!
        while (anyButtonsPressed(gamepad)) {
            tel.addData("kStaticFriction!", staticFriction);
            tel.addLine("Release the button to start the velocity measurement");
            tel.update();
        }
        lastUpdate.reset();
        double velocityConstant = 0;
        MovingStatistics velocityConstantStats = new MovingStatistics(50);
        while (!anyButtonsPressed(gamepad)) {
            motorHelperPower(1);
            if (lastUpdate.milliseconds() >= 50) {
                double vel = getMotor1Velocity();
                double vol = voltage.getAsDouble();
                if (vel != 0) {
                    // power = (kStaticFriction + kVelocity * RPM) / v
                    // So
                    //   1 = (kSF + kV * RPM) / v;
                    // Multiply both sides by v
                    //   v = kSF + kV * RPM
                    // move kStaticFriction over:
                    //   v - KSF = kV * RPM
                    // swap sides
                    //   kv * RPM = v - kSF
                    // and divide both sides by RPM
                    // kv = (v - kSF) / RPM
                    velocityConstant = (vol - staticFriction) / vel;
                    velocityConstantStats.add(velocityConstant);
                }
            }
            tel.addData("kStaticFriction!", staticFriction);
            tel.addLine("Press a button to stop velocity measurement");
            tel.addData("kVelocityConstant", velocityConstant);
            tel.addData("Average kV", velocityConstantStats.getMean());
            tel.update();
        }
    }

    private static boolean anyButtonsPressed(Gamepad g) {
        return (
            g.a ||
            g.b ||
            g.x ||
            g.y ||
            g.dpad_down ||
            g.dpad_up ||
            g.dpad_left ||
            g.dpad_right ||
            g.options ||
            g.share ||
            g.left_bumper ||
            g.right_bumper ||
            g.start
        );
    }

    private void motorHelperPower(double p) {
        if (hasLaunch1()) {
            launcher1.setPower(p);
        }
        if (hasLaunch2()) {
            launcher2.setPower(p);
        }
    }

    private boolean hasLaunch1() {
        return launcher1 != null;
    }

    private boolean hasLaunch2() {
        return launcher2 != null;
    }
}
