package org.firstinspires.ftc.swervebot.swerveutil;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.AnalogInput;

@Configurable
public class AbsoluteAnalogEncoder {

    public static double DEFAULT_RANGE = 3.3;

    private final AnalogInput encoder;
    private double offset;
    private double analogRange;
    private boolean inverted;

    // Low-pass filter for smoothing
    private double filteredVoltage;
    private double alpha = 0.3; // Smoothing factor (0.0-1.0). Lower = smoother but more lag
    private boolean filterInitialized = false;

    public AbsoluteAnalogEncoder(AnalogInput enc) {
        this(enc, DEFAULT_RANGE);
    }

    public AbsoluteAnalogEncoder(AnalogInput enc, double aRange) {
        encoder = enc;
        analogRange = aRange;
        offset = 0;
        inverted = false;
    }

    /**
     * Set the zero offset for this encoder
     * @param off offset in radians
     * @return this encoder for chaining
     */
    public AbsoluteAnalogEncoder zero(double off) {
        offset = off;
        return this;
    }

    /**
     * Set whether this encoder reads inverted
     * @param inv true if inverted
     * @return this encoder for chaining
     */
    public AbsoluteAnalogEncoder setInverted(boolean inv) {
        inverted = inv;
        return this;
    }

    /**
     * Set the smoothing factor for the low-pass filter
     * @param smoothing value between 0.0 (max smoothing, more lag) and 1.0 (no smoothing)
     * @return this encoder for chaining
     */
    public AbsoluteAnalogEncoder setSmoothing(double smoothing) {
        alpha = Math.max(0.0, Math.min(1.0, smoothing)); // Clamp to [0, 1]
        return this;
    }

    /**
     * Get the current position of the encoder in radians [0, 2π]
     * with offset applied and filtering
     * @return position in radians
     */
    public double getCurrentPosition() {
        double rawVoltage = getVoltage();

        // Initialize filter on first read
        if (!filterInitialized) {
            filteredVoltage = rawVoltage;
            filterInitialized = true;
        }

        // Apply low-pass filter to reduce jitter
        filteredVoltage = (alpha * rawVoltage) + ((1 - alpha) * filteredVoltage);

        // Convert voltage to ratio [0, 1]
        double ratio = filteredVoltage / analogRange;
        if (!inverted) {
            ratio = 1 - ratio;
        }

        // Convert to radians [0, 2π] and apply offset
        double pos = Angle.norm((ratio * Math.PI * 2) - offset);

        return pos;
    }

    /**
     * Get the raw angle without offset or filtering
     * Useful for calibration
     * @return raw angle in radians [0, 2π]
     */
    public double getRawAngle() {
        double ratio = getVoltage() / analogRange;
        if (!inverted) {
            ratio = 1 - ratio;
        }
        return ratio * Math.PI * 2;
    }

    /**
     * Get the raw voltage from the encoder
     * @return voltage in volts
     */
    public double getVoltage() {
        return encoder.getVoltage();
    }

    /**
     * Reset the filter (useful when encoder might have jumped)
     */
    public void resetFilter() {
        filterInitialized = false;
    }
}
