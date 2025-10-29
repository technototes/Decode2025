package org.firstinspires.ftc.rrquickstart.drive;

import androidx.annotation.NonNull;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.localization.ThreeTrackingWheelLocalizer;
import com.acmerobotics.roadrunner.localization.TwoTrackingWheelLocalizer;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import java.util.Arrays;
import java.util.List;
import org.firstinspires.ftc.rrquickstart.util.Encoder;

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
@Config
public class StandardTrackingWheelLocalizer extends TwoTrackingWheelLocalizer {

    public static double TICKS_PER_REV = 8192;
    public static double WHEEL_RADIUS = 17.5 / 25.4; // in
    public static double GEAR_RATIO = 1; // output (wheel) speed / input (encoder) speed

    public static double LATERAL_DISTANCE = 152.4 / 25.4; // in; distance between the left and right wheels
    public static double FORWARD_OFFSET = 63.5 / 25.4; // in; offset of the lateral wheel

    private Encoder leftEncoder, rightEncoder, frontEncoder;

    private List<Integer> lastEncPositions, lastEncVels;

    public StandardTrackingWheelLocalizer(
        HardwareMap hardwareMap,
        List<Integer> lastTrackingEncPositions,
        List<Integer> lastTrackingEncVels
    ) {
        super(
            Arrays.asList(
                //new Pose2d(0, LATERAL_DISTANCE / 2, 0), // left
                new Pose2d(0, -LATERAL_DISTANCE / 2, 0), // right
                new Pose2d(FORWARD_OFFSET, 0, Math.toRadians(90)) // front
            )
        );
        lastEncPositions = lastTrackingEncPositions;
        lastEncVels = lastTrackingEncVels;

        rightEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "odof"));
        frontEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "intake/odo")); //odo names, possibly need to delete one of these encoders

        // TODO: reverse any encoders using Encoder.setDirection(Encoder.Direction.REVERSE)
        frontEncoder.setDirection(Encoder.Direction.FORWARD);
        rightEncoder.setDirection(Encoder.Direction.REVERSE);
    }

    public static double encoderTicksToInches(double ticks) {
        return (WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks) / TICKS_PER_REV;
    }

    @NonNull
    @Override
    public List<Double> getWheelPositions() {
        int rightPos = rightEncoder.getCurrentPosition();
        int frontPos = frontEncoder.getCurrentPosition();

        lastEncPositions.clear();
        lastEncPositions.add(rightPos);
        lastEncPositions.add(frontPos);

        return Arrays.asList(
            encoderTicksToInches(rightPos),
            encoderTicksToInches(frontPos)
        );
    }

    @NonNull
    @Override
    public List<Double> getWheelVelocities() {
        int rightVel = (int) rightEncoder.getCorrectedVelocity();
        int frontVel = (int) frontEncoder.getCorrectedVelocity();

        lastEncVels.clear();
        lastEncVels.add(rightVel);
        lastEncVels.add(frontVel);

        return Arrays.asList(
            encoderTicksToInches(rightVel),
            encoderTicksToInches(frontVel)
        );
    }

    @Override
    public double getHeading() {
        return 0;
    }
}
