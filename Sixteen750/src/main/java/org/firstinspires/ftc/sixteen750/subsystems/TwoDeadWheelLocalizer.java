package org.firstinspires.ftc.sixteen750.subsystems;

import static com.technototes.library.hardware.sensor.encoder.MotorEncoder.Direction.FORWARD;
import static com.technototes.library.hardware.sensor.encoder.MotorEncoder.Direction.REVERSE;

import androidx.annotation.NonNull;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.TwoTrackingWheelLocalizer;
import com.technototes.library.hardware.sensor.encoder.MotorEncoder;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.LogConfig;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import com.technototes.path.subsystem.DeadWheelConstants;
import java.util.Arrays;
import java.util.List;

/*
 * Sample tracking wheel localizer implementation assuming the standard configuration:
 *
 *    /--------------\
 *    |     ____     |
 *    |     ----     |
 *    | ||        || |
 *    | ||        || |
 *    |              |
 *    |              |
 *    \--------------/
 *
 */
public class TwoDeadWheelLocalizer
    extends TwoTrackingWheelLocalizer
    implements Subsystem, Loggable {

    @Config
    public abstract static class OdoDeadWheelConstants implements DeadWheelConstants {

        // I don't think this is used in a 2 wheel localizer
        public static double LateralDistance = 152.4 / 25.4; // mm -> in

        // I don't think this is used in a 2 wheel localizer
        public static double ForwardOffset = 63.5 / 25.4; // mm -> in

        // I'm guessing this has to do with the REV hub recording changes in
        // 16 bit integers (+/32768) per second, so if your bot is moving moderately fast,
        // the encoders will be incorrect and potentially overflow
        public static boolean EncoderOverflow = true;

        // Our odo pods appear to drive the encoder directly, so no gear ratio
        public static double GearRatio = 1; // output (wheel) speed / input (encoder) speed

        public static double TicksPerRev = 8192;

        public static double WheelRadius = 17.5 / 25.4; // millimeters -> inches

        public static double perpAngle = 90;
        public static double paraAngle = 0;

        public static boolean perpReverse = true;
        public static boolean paraReverse = true;

        // Parallel/Perpendicular to the forward axis
        // Parallel wheel is parallel to the forward axis
        // Perpendicular is perpendicular to the forward axis

        public static double PARALLEL_X = -1.3 / 2.54; // X is the fwd/bkwd direction
        public static double PARALLEL_Y = -6.1 / 2.54; // Y is the side-to-side/strafe direction

        public static double PERPENDICULAR_X = 4.5 / 2.54; // Was 3 before
        public static double PERPENDICULAR_Y = 4.5 / 2.54; // Was 3.5 before
    }

    // Parallel moves parallel to the axles of the drive base
    @LogConfig.Run(duringRun = true, duringInit = true)
    @Log(name = "parOdo")
    public MotorEncoder parallelEncoder;

    // Perpendicular moves perpendicular to the axles of the drive base
    @LogConfig.Run(duringRun = true, duringInit = true)
    @Log(name = "perpOdo")
    public MotorEncoder perpendicularEncoder;

    protected double lateralDistance, forwardOffset, gearRatio, wheelRadius, ticksPerRev;
    protected DrivebaseSubsystem drive;

    //1862.5 per inch
    protected boolean encoderOverflow;

    public TwoDeadWheelLocalizer(MotorEncoder r, MotorEncoder f) {
        super(
            Arrays.asList(
                new Pose2d(
                    OdoDeadWheelConstants.PARALLEL_X,
                    OdoDeadWheelConstants.PARALLEL_Y,
                    Math.toRadians(OdoDeadWheelConstants.paraAngle)
                ),
                new Pose2d(
                    OdoDeadWheelConstants.PERPENDICULAR_X,
                    OdoDeadWheelConstants.PERPENDICULAR_Y,
                    Math.toRadians(OdoDeadWheelConstants.perpAngle)
                )
            )
        );
        drive = null;

        parallelEncoder = r;
        parallelEncoder.setDirection(OdoDeadWheelConstants.paraReverse ? REVERSE : FORWARD);
        perpendicularEncoder = f;
        perpendicularEncoder.setDirection(OdoDeadWheelConstants.perpReverse ? REVERSE : FORWARD);

        lateralDistance = OdoDeadWheelConstants.LateralDistance;
        forwardOffset = OdoDeadWheelConstants.ForwardOffset;
        encoderOverflow = OdoDeadWheelConstants.EncoderOverflow;
        gearRatio = OdoDeadWheelConstants.GearRatio;
        ticksPerRev = OdoDeadWheelConstants.TicksPerRev;
        wheelRadius = OdoDeadWheelConstants.WheelRadius;
    }

    public void setDrivebase(DrivebaseSubsystem sub) {
        drive = sub;
    }

    public double encoderTicksToInches(double ticks) {
        return ((getWheelRadius() * 2 * Math.PI * getGearRatio() * ticks) / getTicksPerRev());
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        return Arrays.asList(
            encoderTicksToInches(parallelEncoder.getCurrentPosition()),
            encoderTicksToInches(perpendicularEncoder.getCurrentPosition())
        );
    }

    @NonNull
    @Override
    public List<Double> getWheelVelocities() {
        // TODO: If your encoder velocity can exceed 32767 counts / second (such as the REV Through Bore and other
        //  competing magnetic encoders), change Encoder.getRawVelocity() to Encoder.getCorrectedVelocity() to enable a
        //  compensation method

        return Arrays.asList(
            encoderTicksToInches(parallelEncoder.getCorrectedVelocity()),
            encoderTicksToInches(perpendicularEncoder.getCorrectedVelocity())
        );
    }

    public double getTicksPerRev() {
        return ticksPerRev;
    }

    public double getWheelRadius() {
        return wheelRadius;
    }

    public double getGearRatio() {
        return gearRatio;
    }

    @Override
    public double getHeading() {
        return drive.getRawExternalHeading();
    }

    @Override
    public Double getHeadingVelocity() {
        return drive.getExternalHeadingVelocity();
    }
}
