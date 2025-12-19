package org.firstinspires.ftc.sixteen750.swerveutil;

/**
 * Slew Rate Limiter - Limits the rate of change of a value
 *
 * This prevents sudden jumps in motor power or servo position,
 * providing smoother control and reducing mechanical stress.
 *
 * Example uses:
 * - Limiting drive motor acceleration
 * - Smoothing steering servo movements
 * - Preventing sudden direction changes
 */
public class SlewRateLimiter {

    private double maxRateOfChange;  // Maximum change per update
    private double previousValue;    // Last output value
    private long lastUpdateTime;     // Timestamp of last update (nanoseconds)

    /**
     * Creates a slew rate limiter with a maximum rate of change
     *
     * @param maxRateOfChange maximum change allowed per second
     *                        For example: 2.0 means value can change by 2.0 per second
     *                        (from 0 to 1.0 would take 0.5 seconds)
     */
    public SlewRateLimiter(double maxRateOfChange) {
        this(maxRateOfChange, 0.0);
    }

    /**
     * Creates a slew rate limiter with initial value
     *
     * @param maxRateOfChange maximum change allowed per second
     * @param initialValue starting value for the limiter
     */
    public SlewRateLimiter(double maxRateOfChange, double initialValue) {
        this.maxRateOfChange = maxRateOfChange;
        this.previousValue = initialValue;
        this.lastUpdateTime = System.nanoTime();
    }

    /**
     * Calculate the next output value with slew rate limiting applied
     *
     * @param input desired target value
     * @return limited output value that doesn't exceed max rate of change
     */
    public double calculate(double input) {
        // Calculate time since last update (in seconds)
        long currentTime = System.nanoTime();
        double deltaTime = (currentTime - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = currentTime;

        // Calculate maximum allowed change for this time step
        double maxChange = maxRateOfChange * deltaTime;

        // Calculate the difference between desired and current
        double difference = input - previousValue;

        // Limit the change to maxChange
        double limitedChange = Math.clamp(difference, -maxChange, maxChange);

        // Calculate new value
        previousValue = previousValue + limitedChange;

        return previousValue;
    }

    /**
     * Reset the limiter to a new value
     * Useful when you want to jump to a new value without limiting
     *
     * @param value new value to reset to
     */
    public void reset(double value) {
        previousValue = value;
        lastUpdateTime = System.nanoTime();
    }

    /**
     * Get the current output value without updating
     *
     * @return current limited value
     */
    public double getCurrentValue() {
        return previousValue;
    }

    /**
     * Set a new maximum rate of change
     *
     * @param maxRateOfChange new maximum rate (units per second)
     */
    public void setMaxRateOfChange(double maxRateOfChange) {
        this.maxRateOfChange = maxRateOfChange;
    }

    /**
     * Get the current maximum rate of change
     *
     * @return maximum rate of change (units per second)
     */
    public double getMaxRateOfChange() {
        return maxRateOfChange;
    }
}