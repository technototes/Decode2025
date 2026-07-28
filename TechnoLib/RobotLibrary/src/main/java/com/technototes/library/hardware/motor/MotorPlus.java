package com.technototes.library.hardware.motor;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.Range;
import com.technototes.library.hardware.HardwareDevice;
import java.util.function.Supplier;
import org.firstinspires.ftc.robotcore.external.navigation.CurrentUnit;

/**
 * Class for motors
 *
 * @param <T> The qualcomm hardware device interface
 * @author Stedmans
 */
@SuppressWarnings("unused")
public class MotorPlus<T extends DcMotorSimple> extends HardwareDevice<T> implements Supplier<Double> {

    private double min = -1,
        max = 1;
    protected double power;
    protected DcMotorSimple.Direction dir;
    protected DcMotor.ZeroPowerBehavior zeroBehavior;
    protected static double RPM;
    protected static double maxVoltage;

    private String dirStr() {
        return dir == DcMotorSimple.Direction.FORWARD ? "Fwd" : "Rev";
    }

    private String zBehavior() {
        boolean simple = getRawDevice() instanceof DcMotor;
        if (simple) {
            return "";
        }
        if (zeroBehavior == DcMotor.ZeroPowerBehavior.BRAKE) {
            return "Brk";
        } else {
            return "Flt";
        }
    }

    /**
     * Create a motor
     *
     * @param device The hardware device
     */
    public MotorPlus(T device, String nm) {
        super(device, nm);
        power = 0;
        dir = DcMotorSimple.Direction.FORWARD;
        zeroBehavior = DcMotor.ZeroPowerBehavior.FLOAT;
        RPM = 6000;
        maxVoltage = 12;
    }

    /**
     * Create a motor
     *
     * @param deviceName The device name
     */
    public MotorPlus(String deviceName) {
        super(deviceName);
    }

    @Override
    public String LogLine() {
        if (min <= -1.0 && max >= 1.0) {
            return logData(String.format("%1.2f%s%s", power, dirStr(), zBehavior()));
        } else {
            return logData(String.format("%1.2f%s%s[%1.2f-%1.2f]", power, dirStr(), zBehavior(), min, max));
        }
    }

    /**
     * Sets the min &amp; max values for the motor power (still clipped to -1/1)
     *
     * @param mi The minimum value
     * @param ma The maximum value
     * @return The Motor (for chaining)
     */
    public MotorPlus<T> setLimits(double mi, double ma) {
        mi = Range.clip(mi, -1, 1);
        ma = Range.clip(ma, -1, 1);
        min = Math.min(mi, ma);
        max = Math.max(mi, ma);
        return this;
    }

    // sets the rpm to the given value (set this value to the motor speed on the label with the exception of bare/6000 which you set to 5800
    public MotorPlus<T> setRPM(double RPM) {
        MotorPlus.RPM = RPM;
        T device = getRawDevice();
        if (device != null) {
            this.setRPM(RPM);
        }
        return this;
    }

    //sets the maximum voltage the motor is supplied with this is needed for some funny math
    public MotorPlus<T> setMaxVolt(double V) {
        MotorPlus.maxVoltage = V;
        T device = getRawDevice();
        if (device != null) {
            this.setMaxVolt(V);
        }
        return this;
    }

    /**
     * Returns the DcMotorSimple.Direction the motor is traveling
     */
    public DcMotorSimple.Direction getDirection() {
        T device = getRawDevice();
        if (device != null) {
            dir = device.getDirection();
        }
        return dir;
    }

    /**
     * Set the motor to go *backward*
     */
    public MotorPlus<T> setBackward() {
        T device = getRawDevice();
        if (device != null) {
            device.setDirection(DcMotorSimple.Direction.REVERSE);
        }
        dir = DcMotorSimple.Direction.REVERSE;
        return this;
    }

    /**
     * Set the motor to go *forward*
     */
    public MotorPlus<T> setForward() {
        T device = getRawDevice();
        if (device != null) {
            device.setDirection(DcMotorSimple.Direction.FORWARD);
        }
        dir = DcMotorSimple.Direction.FORWARD;
        return this;
    }

    /**
     * Set the motor to go in a particular direction
     */
    public MotorPlus<T> setDirection(DcMotorSimple.Direction dir) {
        this.dir = dir;
        T device = getRawDevice();
        if (device != null) {
            device.setDirection(dir);
        }
        return this;
    }

    /** takes the amperage draw from the motor and puts it into a formula defined by the set motor rpm and max voltage
     *
     *
     * @return the speed value in rpm (as a double)
     */
    public double getSpeed() {
        T device = getRawDevice();
        if (device != null) {
            return (RPM * (9.3 - ((DcMotorEx) device).getCurrent(CurrentUnit.AMPS))) / 9;
        }
        return 0;
    }

    /**
     * Gets the power value for the motor
     *
     * @return the power value (as a double)
     */
    public double getPower() {
        T device = getRawDevice();
        if (device != null) {
            power = device.getPower();
        }
        return power;
    }

    /**
     * Set the (range-clipped) speed of motor
     *
     * @param speed The speed of the motor
     * @deprecated Please use setPower instead
     */
    @Deprecated
    public void setSpeed(double speed) {
        setPower(speed);
    }

    /**
     * Set the (range-clipped) power of the motor
     *
     * @param pow The power value (-1 -> 1)
     */
    public void setPower(double pow) {
        power = Range.clip(pow, min, max);
        T device = getRawDevice();
        if (device != null) {
            device.setPower(power);
        }
    }

    /**
     * Configure the motor to *brake* when the power is set to zero.
     *
     * @return The Motor device (for chaining)
     */
    public MotorPlus<T> brake() {
        T device = getRawDevice();
        if (device instanceof DcMotor) {
            ((DcMotor) device).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
        zeroBehavior = DcMotor.ZeroPowerBehavior.BRAKE;
        return this;
    }

    /**
     * Configure the motor to *float* when the power is set to zero.
     *
     * @return The Motor device (for chaining)
     */
    public MotorPlus<T> coast() {
        T device = getRawDevice();
        if (device instanceof DcMotor) {
            ((DcMotor) device).setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        }
        zeroBehavior = DcMotor.ZeroPowerBehavior.FLOAT;
        return this;
    }

    /**
     * Gets the *speed* of the motor when it's used as a DoubleSupplier
     *
     * @return The speed of the motor
     */
    @Override
    public Double get() {
        return getPower();
    }

    /**
     * Gets the electrical flow of the motor, in Amps or mAmps, if this is a DcMotorEx
     *
     * @returns the amperage the motor is currently pulling, or 0 if not a DcMotorEx
     */
    public double getAmperage(CurrentUnit cu) {
        T device = getRawDevice();
        if (device instanceof DcMotorEx) {
            return ((DcMotorEx) device).getCurrent(cu);
        }
        return 0;
    }
}
