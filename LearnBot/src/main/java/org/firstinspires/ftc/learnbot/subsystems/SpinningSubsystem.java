package org.firstinspires.ftc.learnbot.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.Range;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import com.technototes.library.util.PIDFController;
import org.firstinspires.ftc.learnbot.Hardware;

// Untested, not really workingg...
@Configurable
public class SpinningSubsystem implements Subsystem, Loggable {

    // These are blind guesses, and are guaranteed to be wrong.
    // Do remember that the output is in "power" terms,
    // while the target & error are in "RPM" terms.
    // F is unused, as we're going to have a function for it, instead
    public static PIDFCoefficients PID_VALUES = new PIDFCoefficients(1e-8, 1e-11, 1e-9, 0);

    // The idea is that at full power, if it spins at 3000 tps the "scale" is 1/3000
    // Change this to be correct for the gear ratio of your motor.
    public static double SPIN_TO_RPM_SCALE = 1.0 / 2800;
    // This should be the voltage compensation factor in F. For every 1V below "peak" voltage,
    // we'll increase the FF value by 0.1 (which simply raises power that much)
    public static double INITIAL_VOLTAGE_COMP_FACTOR = 0.03;
    public static double PEAK_VOLTAGE = 13.2;

    public static double DELTA = 50;

    private final EncodedMotor<DcMotorEx> motor;
    private final PIDFController spinningPID;

    // Some data to log:
    @Log(name = "Current Speed")
    public double curSpeed;

    @Log(name = "Target Speed")
    public double target;

    @Log(name = "Motor Power")
    public double power;

    @Log(name = "Motor Delta")
    public double delta;

    @Log(name="Running")
    public boolean running;
    public Hardware hardware;

    public SpinningSubsystem(EncodedMotor<DcMotorEx> m, Hardware hw, boolean dontSchedule) {
        motor = m;
        hardware = hw;
        running = false;

        if (motor != null) {
            motor.coast();
        }
        spinningPID = new PIDFController(PID_VALUES);
        if (!dontSchedule) {
            CommandScheduler.register(this);
        }
    }

    public SpinningSubsystem(EncodedMotor<DcMotorEx> m, Hardware hw) {
        this(m, hw, false);
    }

    public void setTargetSpeed(double speed) {
        target = speed;
        // Set the power to an initial "best guess" target
        power =
            speed * SPIN_TO_RPM_SCALE +
            INITIAL_VOLTAGE_COMP_FACTOR * (PEAK_VOLTAGE - hardware.voltage());
        setMotorPower(power);
        spinningPID.setTarget(speed);
    }

    public double getTargetSpeed() {
        return target;
    }

    @Override
    public void periodic() {
        if (running) {
            delta = spinningPID.update(getMotorSpeed());
            setMotorPower(power + delta);
        } else {
            setMotorPower(0);
        }
    }

    // 54 = 500
    // 90 = 840
    // 148 = 1320
    // 262 = 2360

    public void start() {
        running = true;
        setTargetSpeed(target);
    }

    public void stop() {
        running = false;
    }

    public void increase() {
        setTargetSpeed(target + DELTA);
    }

    public void decrease() {
        setTargetSpeed(target - DELTA);
    }

    public double getMotorSpeed() {
        if (motor != null) {
            curSpeed = motor.getVelocity();
        }
        return curSpeed;
    }

    private void setMotorPower(double pow) {
        power = Range.clip(pow, -1, 1);
        if (motor != null) {
            motor.setPower(power);
        }
    }
}
