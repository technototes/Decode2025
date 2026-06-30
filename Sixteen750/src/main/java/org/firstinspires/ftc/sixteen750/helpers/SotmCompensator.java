package org.firstinspires.ftc.sixteen750.helpers;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Vector;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import org.firstinspires.ftc.sixteen750.subsystems.LauncherSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.LimelightSubsystem;

@Configurable
public class SotmCompensator implements Loggable {

    public static double FlightTime = .5; // time ball is in air might need to make scale with distance
    public static double MinSpeed = 5; // min speed in in/s at which sotm applies
    public static double DistanceCorrectionScale = 1; // use this to scale how aggressive sotm velocity correction is
    public static double AngleCorrectionScale = 1; // use this to scale how aggressive sotm angle compensation is

    @Log.Number(name = "SOTM Distance")
    public static double CorrectedDistance = 0;

    @Log.Number(name = "SOTM Angle")
    public static double CorrectedAngle = 0;

    @Log.Number(name = "SOTM Speed ")
    public static double speed = 0;

    public static double vRadial = 0;
    public static double vTangential = 0;

    public void update(Follower follower, double rawDistIn, double rawTxDeg) {
        if (rawDistIn <= 0) {
            // if dist is tweaking out we want sotm to stop
            CorrectedDistance = rawDistIn;
            CorrectedAngle = LimelightSubsystem.Xangle;
            speed = 0;
            vRadial = 0;
            vTangential = 0;
            return;
        }

        Vector vel = follower.getVelocity();
        double velMag = vel.getMagnitude();
        speed = velMag;
        if (velMag < MinSpeed) {
            // if robot is barely moving no need to use sotm
            CorrectedDistance = rawDistIn;
            CorrectedAngle = LimelightSubsystem.Xangle;
            vRadial = 0;
            vTangential = 0;
        }

        double robotHeading = follower.getPose().getHeading(); // maths that ai did for me i took time to understand it tho ik all the underlying principles just like fahhhhhhhhh so much math
        double vx_field = vel.getXComponent();
        double vy_field = vel.getYComponent();
        double cosH = Math.cos(robotHeading);
        double sinH = Math.sin(robotHeading);
        double vForwardRobot = vx_field * cosH + vy_field * sinH;
        double vRightRobot = -vx_field * sinH + vy_field * cosH;
        double txRad = Math.toRadians(LimelightSubsystem.Xangle);

        vRadial = vForwardRobot * Math.cos(txRad) + vRightRobot * Math.sin(txRad);
        vTangential = -vForwardRobot * Math.sin(txRad) + vRightRobot * Math.cos(txRad);

        double t = FlightTime;
        double distCorrection = -vRadial * t * DistanceCorrectionScale;
        CorrectedDistance = rawDistIn + distCorrection;
        if (CorrectedDistance < 1.0) CorrectedDistance = 1.0;
        double drift = vTangential * t;
        double angleCorrection =
            Math.toDegrees(Math.atan2(drift, rawDistIn)) * AngleCorrectionScale;
        CorrectedAngle = rawTxDeg + angleCorrection;
    }
}
