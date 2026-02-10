package com.technototes.library.util;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

/**
 * Class with various math functions
 *
 * @author Alex Stedman
 */
public class MathUtils {

    /**
     * Get the max of supplied doubles
     *
     * @param args The doubles
     * @return The max
     */
    public static double getMax(double... args) {
        double max = args[0];
        for (int i = 1; i < args.length; i++) {
            max = Math.max(max, args[i]);
        }
        return max;
    }

    /**
     * Get the max of supplied ints
     *
     * @param args The ints
     * @return The max
     */
    public static int getMax(int... args) {
        int max = args[0];
        for (int i = 1; i < args.length; i++) {
            max = Math.max(max, args[i]);
        }
        return max;
    }

    /**
     * Calculate pythagorean theorem of any number of sides
     *
     * @param vals The sides
     * @return The hypotenuse
     */
    public static double pythag(double... vals) {
        double total = 0;
        for (double d : vals) {
            total += d * d;
        }
        return Math.sqrt(total);
    }

    /**
     * Constrain the supplied int
     * Please use com.qualcomm.robotcore.util.Range.clip
     *
     * @param min The minimum of the constraint
     * @param num The number to constrain
     * @param max The maximum of the constraint
     * @return The constrained number
     * @deprecated
     */
    @Deprecated
    public static int constrain(int min, int num, int max) {
        return num < min ? min : (num > max ? max : num);
    }

    /**
     * Constrain the supplied double
     * Please use com.qualcomm.robotcore.util.Range.clip
     *
     * @param min The minimum of the constraint
     * @param num The number to constrain
     * @param max The maximum of the constraint
     * @return The constrained number
     * @deprecated
     */
    @Deprecated
    public static double constrain(double min, double num, double max) {
        return num < min ? min : (num > max ? max : num);
    }

    /**
     * Calculate if the supplied number is prime
     *
     * @param number The number to check
     * @return If number is prime
     */
    public static boolean isPrime(int number) {
        if ((number % 2) == 0) {
            return false;
        }
        for (int i = 3; i * i <= number; i += 2) {
            if (number % i == 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Scale a value originally between in_min and in_max to be the same ratio when mapped to
     * out_min and out_max. For example:
     * * map(0.1, -0.2, 0.2, -1.0, 1.0) will return 0.5
     * * map(.5, 0, 1, -1, 1) will return 0
     *
     * @param x
     * @param in_min
     * @param in_max
     * @param out_min
     * @param out_max
     * @return
     */
    public static double map(double x, double in_min, double in_max, double out_min, double out_max) {
        return ((x - in_min) * (out_max - out_min)) / (in_max - in_min) + out_min;
    }

    /**
     * Returns the value from a list which is closed to the first value.
     * This is useful for trying to identify an angle you'd like a drivebase to 'snap to'.
     * If you can accomplish this through a simple mathematical expression, do that instead.
     * It will probably be faster.
     *
     * @param d      The current value
     * @param values The list of 'snapped' values we want to
     * @return the element from 'values' that is closest to 'd'
     */
    public static double closestTo(double d, double... values) {
        int lowestDif = 0;
        for (int i = 1; i < values.length; i++) {
            if (Math.abs(values[lowestDif] - d) > Math.abs(values[i] - d)) lowestDif = i;
        }
        return values[lowestDif];
    }

    /**
     * Returns the value from a list which is closed to the first value.
     * This is useful for trying to identify an angle you'd like a drivebase to 'snap to'.
     * If you can accomplish this through a simple mathematical expression, do that instead.
     * It will probably be faster.
     *
     * @param d      The current value
     * @param values The list of 'snapped' values we want to
     * @return the element from 'values' that is closest to 'd'
     */
    public static int closestTo(double d, int... values) {
        int lowestDif = 0;
        for (int i = 1; i < values.length; i++) {
            if (Math.abs(values[lowestDif] - d) > Math.abs(values[i] - d)) lowestDif = i;
        }
        return values[lowestDif];
    }

    // Always returns a value between 0 and 359.999 (or 2pi, depending on AngleUnit)
    public static double snapToNearestAngleMultiple(double input, double angleMultiple, AngleUnit angleUnit) {
        int quadrant = (int) Math.round(input / angleMultiple);
        return (quadrant * angleMultiple) % fullCircle(angleUnit);
    }

    public static double snapToNearestRadiansMultiple(double input, double angleMultiple) {
        return snapToNearestAngleMultiple(input, angleMultiple, AngleUnit.RADIANS);
    }

    public static double snapToNearestDegreesMultiple(double input, double angleMultiple) {
        return snapToNearestAngleMultiple(input, angleMultiple, AngleUnit.DEGREES);
    }

    public static double normalizeAngle(double angle, AngleUnit angleUnit) {
        double twoPi = fullCircle(angleUnit);
        return ((angle % twoPi) + twoPi) % twoPi;
    }

    public static double normalizeRadians(double radians) {
        return normalizeAngle(radians, AngleUnit.RADIANS);
    }

    public static double normalizeDegrees(double degrees) {
        return normalizeAngle(degrees, AngleUnit.DEGREES);
    }

    /**
     * Returns angleDelta clamped to [-pi, pi].
     *
     * @param angleDelta angle delta in radians
     */
    public static double normalizeDeltaRadians(double angleDelta) {
        double modifiedAngleDelta = normalizeRadians(angleDelta);

        if (modifiedAngleDelta > Math.PI) {
            modifiedAngleDelta -= Math.PI * 2;
        }

        return modifiedAngleDelta;
    }

    /**
     * Returns angleDelta clamped to [-pi, pi].
     *
     * @param angleDelta angle delta in radians
     */
    public static double normalizeDeltaDegrees(double angleDelta) {
        return Math.toDegrees(normalizeDeltaRadians(Math.toRadians(angleDelta)));
    }

    public static double posNegAngle(double angle, AngleUnit angleUnit) {
        double half = halfCircle(angleUnit);
        return normalizeAngle(angle + half, angleUnit) - half;
    }

    public static double posNegRadians(double radians) {
        return posNegAngle(radians, AngleUnit.RADIANS);
    }

    public static double posNegDegrees(double randians) {
        return posNegAngle(randians, AngleUnit.DEGREES);
    }

    public static double fullCircle(AngleUnit angleUnit) {
        return angleUnit == AngleUnit.RADIANS ? Math.PI * 2 : 360;
    }

    public static double halfCircle(AngleUnit angleUnit) {
        return angleUnit == AngleUnit.RADIANS ? Math.PI : 180;
    }

    // Helper to make dead zones on sticks still allow good scaling
    public static double deadZoneScale(double val, double deadZone) {
        // Okay, we want a small dead zone in the middle of the stick, but that also means that
        // you can't have a value any smaller than that value, so instead, we're going to scale
        // the value after compensating for the dead zone

        // If the value is inside the dead zone, just make it zero
        if (Math.abs(val) <= deadZone) {
            return 0.0;
        }
        // If the value is outside the dead zone, scale it
        return ((val - Math.copySign(deadZone, val)) / (1.0 - deadZone));
    }
}
