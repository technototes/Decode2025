package org.firstinspires.ftc.sixteen750.swerveutil;

public final class Angle {
    private static final double PI = Math.PI;
    private static final double TAU = PI * 2;

    private Angle() {
        // Prevent instantiation
    }

    /**
     * Returns angle clamped to [0, 2pi].
     *
     * @param angle angle measure in radians
     */
    public static double norm(double angle) {
        double modifiedAngle = angle % TAU;
        modifiedAngle = (modifiedAngle + TAU) % TAU;
        return modifiedAngle;
    }

    /**
     * Returns angleDelta clamped to [-pi, pi].
     *
     * @param angleDelta angle delta in radians
     */
    public static double normDelta(double angleDelta) {
        double modifiedAngleDelta = norm(angleDelta);

        if (modifiedAngleDelta > PI) {
            modifiedAngleDelta -= TAU;
        }

        return modifiedAngleDelta;
    }
}
