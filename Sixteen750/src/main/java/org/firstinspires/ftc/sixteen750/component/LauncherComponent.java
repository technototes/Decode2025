package org.firstinspires.ftc.sixteen750.component;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.technototes.library.command.Command;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import com.technototes.library.util.PIDFController;
import java.util.Locale;
import java.util.function.DoubleSupplier;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;
import org.firstinspires.ftc.sixteen750.subsystems.TargetAcquisition;

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
        // ready = false;
        targetAcquisition = targetSubsystem;
        voltage = voltageSup;
        double ADDITION = (Config.PEAK_VOLTAGE - voltage.getAsDouble());
        if (ADDITION == 0) {
            Config.SPIN_VOLT_COMP = Config.SPIN_VOLT_COMP + 0.001;
        } else {
            Config.SPIN_VOLT_COMP = Config.SPIN_VOLT_COMP + (ADDITION * Config.DIFFERENCE);
        }
        launcherPID = new PIDFController(Config.launcherPI, target ->
            target == 0
                ? 0
                : (Config.SPIN_F_SCALE * target) +
                  (Config.SPIN_VOLT_COMP * Math.min(Config.PEAK_VOLTAGE, voltage.getAsDouble()))
        );
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
            power = -1;
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

    private boolean hasLaunch1() {
        return launcher1 != null;
    }

    private boolean hasLaunch2() {
        return launcher2 != null;
    }
}
