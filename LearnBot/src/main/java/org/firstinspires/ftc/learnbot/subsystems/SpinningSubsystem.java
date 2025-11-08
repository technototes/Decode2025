package org.firstinspires.ftc.learnbot.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.util.Range;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import com.technototes.library.util.PIDFController;
import org.firstinspires.ftc.learnbot.Hardware;

@Configurable
public class SpinningSubsystem implements Subsystem, Loggable {

    // These are blind guesses, and are guaranteed to be wrong.
    // Do remember that the output is in "power" terms,
    // while the target & error are in "RPM" terms.
    // F is unused, as we're going to have a function for it, instead
    public static PIDFCoefficients PID_VALUES = new PIDFCoefficients(1e-4, 1e-8, 1e-6, 0);

    // The idea is that at full power, it spins at 6000 RPM, so the "scale" is 1/6000
    // Change this to be correct for the gear ratio of your motor.
    public static double SPIN_F_SCALE = 1.0 / 6000;
    // This should be the voltage compensation factor in F. For every 1V below "peak" voltage,
    // we'll increase the FF value by 0.1 (which simply raises power that much)
    public static double SPIN_F_VOLTAGE_COMP = 0.1;
    public static double PEAK_VOLTAGE = 13.0;

    private final EncodedMotor<DcMotorEx> motor;
    private final PIDFController spinningPID;

    // Some data to log:
    @Log(name = "Current Speed")
    public double curSpeed;

    @Log(name = "Target Speed")
    public double target;

    @Log(name = "Motor Power")
    public double power;

    public SpinningSubsystem(EncodedMotor<DcMotorEx> m, Hardware hw) {
        motor = m;
        if (motor != null) {
            motor.coast();
        }
        spinningPID = new PIDFController(
            PID_VALUES,
            target ->
                // Do we need to take current error into account for this? I don't *think* so?
                // If we do, you can add it as a second parameter to this lambda...
                SPIN_F_SCALE * target + SPIN_F_VOLTAGE_COMP * Math.min(PEAK_VOLTAGE, hw.voltage())
        );
    }

    public void setTargetSpeed(double speed) {
        target = speed;
        spinningPID.setTarget(speed);
    }

    public double getTargetSpeed() {
        return target;
    }

    @Override
    public void periodic() {
        setMotorPower(spinningPID.update(getMotorSpeed()));
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
