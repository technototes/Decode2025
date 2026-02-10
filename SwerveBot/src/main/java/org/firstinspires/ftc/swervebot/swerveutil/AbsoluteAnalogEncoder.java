package org.firstinspires.ftc.swervebot.swerveutil;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.ftc.localization.Encoder;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.technototes.library.util.MathUtils;

@Configurable
public class AbsoluteAnalogEncoder {

    public static double DEFAULT_RANGE = 3.3;

    private final AnalogInput encoder;
    private double offset;
    private double analogRange;
    private boolean inverted;

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
     * Get the current position of the encoder in radians [0, 2π]
     * with offset applied and filtering
     * @return position in radians
     */
    public double getCurrentPosition() {
        return  MathUtils.normalizeRadians(
            (!inverted ? 1 - getVoltage() / analogRange : getVoltage() / analogRange) * (Math.PI*2) - offset);
    }


    /**
     * Get the raw voltage from the encoder
     * @return voltage in volts
     */
    public double getVoltage() {
        return encoder.getVoltage();
    }

}
