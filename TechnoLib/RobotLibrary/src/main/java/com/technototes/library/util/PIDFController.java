package com.technototes.library.util;

import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

public class PIDFController {

    private final PIDFCoefficients coefficients;
    private final BinaryOperator<Double> feedforward;
    private double target;
    private double lastError;
    private double integralSum;
    private double lastTimestamp;
    private double minInput, maxInput;
    private boolean bounded;
    private boolean reset;

    // Constructor for PIDFController with PIDCoefficients
    // The feed-forward function takes values (in order) of *target* and *error*
    public PIDFController(PIDFCoefficients coeff, BinaryOperator<Double> ff) {
        coefficients = coeff;
        feedforward = ff;
        reset();
    }

    public PIDFController(PIDFCoefficients coeff, UnaryOperator<Double> ff) {
        this(coeff, (t, e) -> ff.apply(t));
    }

    public PIDFController(PIDFCoefficients coeff) {
        coefficients = coeff;
        feedforward = null;
        reset();
    }

    // Sets the target position (setpoint) for the controller
    public void setTarget(double t) {
        target = t;
    }

    // Sets bounds on the input variable (for wrapping around, e.g., IMU heading)
    public void setInputBounds(double min, double max) {
        minInput = min;
        maxInput = max;
        bounded = true;
    }

    // Resets the integral sum of the controller
    // This is used to prevent "wind up": Large early error can dominate small error as the
    // target is approached, thus rendering the utility of the I controller ineffective.
    // This also sets the rate of change back to flat for the next observation, so there shouldn't
    // be any slope-nuttiness going along with it...
    public void reset() {
        integralSum = 0.0;
        reset = true;
        lastTimestamp = System.nanoTime() / 1e9;
    }

    // Updates the controller and returns the calculated correction
    public double update(double measured) {
        double currentTimestamp = System.nanoTime() / 1e9;
        double dt = currentTimestamp - lastTimestamp;
        lastTimestamp = currentTimestamp;

        double error = target - measured;

        // Handle input wrapping if bounds are set
        if (bounded) {
            double errorRange = maxInput - minInput;
            while (error > errorRange / 2.0) {
                error -= errorRange;
            }
            while (error < -errorRange / 2.0) {
                error += errorRange;
            }
        }

        integralSum += error * dt;

        double derivative = reset ? 0 : (error - lastError) / dt;
        reset = false;
        lastError = error;

        // Calculate PID output
        double pTerm = coefficients.p * error;
        double iTerm = coefficients.i * integralSum;
        double dTerm = coefficients.d * derivative;

        double fTerm = feedforward == null ? coefficients.f * target : feedforward.apply(target, error);

        return pTerm + iTerm + dTerm + fTerm;
    }

    // Returns the error computed in the last call to update
    public double getLastError() {
        return lastError;
    }

    // Returns the target position (setpoint)
    public double getTarget() {
        return target;
    }
}
