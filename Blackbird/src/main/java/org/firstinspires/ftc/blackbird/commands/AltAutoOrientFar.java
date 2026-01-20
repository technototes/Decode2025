package org.firstinspires.ftc.blackbird.commands;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.technototes.library.command.Command;
import com.technototes.library.util.PIDFController;
import org.firstinspires.ftc.blackbird.Robot;
import org.firstinspires.ftc.blackbird.Setup;
import org.firstinspires.ftc.blackbird.subsystems.LimelightSubsystem;

@Configurable
public class AltAutoOrientFar implements Command {

    public Robot robot;

    public Pose wantedPose;
    public boolean firsttime = true;
    public Pose currentPose;
    public static double SIGN = 1;
    public static double maxvalue = 2;
    public static double offsetDegrees = 2;
    PIDFController pid;
    public static PIDFCoefficients pidvalues = new PIDFCoefficients(0.017, 0, 0.0017, 0);

    public AltAutoOrientFar(Robot r) {
        robot = r;
        pid = new PIDFController(pidvalues);
        pid.setTarget(0);
        //pid.setInputBounds(-maxvalue, maxvalue);
    }

    @Override
    public void initialize() {
        robot.follower.startTeleOpDrive();
    }

    @Override
    public boolean isFinished() {
        //return !robot.follower.isBusy();
        return false;
        // I *believe* that a properly tuned robot shouldn't need all this stuff
        /*
        if (
            follower.atParametricEnd() &&
            follower.getHeadingError() < follower.getCurrentPath().getPathEndHeadingConstraint()
        ) {
            return true;
        } else if (
            follower.getVelocity().getMagnitude() <
                follower.getCurrentPath().getPathEndVelocityConstraint() &&
            follower.getPose().distanceFrom(follower.getCurrentPath().endPose()) < 2.54 &&
            follower.getAngularVelocity() < 0.055
        ) {
            return true;
        } else {
            return false;
        }*/
    }

    @Override
    public void execute() {
        //if (firsttime) {
        //            robot.follower.update();
        //            wantedPose = new Pose(
        //                robot.follower.getPose().getX(),
        //                robot.follower.getPose().getY(),
        //                robot.follower.getPose().getHeading() -
        //                    Math.toRadians(robot.limelightSubsystem.getTX()) //.getTX .getLimelightRotation()
        //            );
        //            robot.follower.holdPoint(new BezierPoint(wantedPose), wantedPose.getHeading(), false);
        //            LauncherSubsystem.targetPower = 1;
        //            firsttime = false;
        //}
        currentPose = robot.follower.getPose();
        double rotation = 0;
        if (Setup.Connected.LIMELIGHTSUBSYSTEM) {
            // --- Face AprilTag using Limelight ---
            //                    targetHeading =
            //                        curHeading - Math.toRadians(limelightSubsystem.getLimelightRotation());
            // Kooolpool here below was my original prototype for auto orient and it worked decently well
            if (robot.limelightSubsystem.getDistance() >= 0) {
                rotation = pid.update((LimelightSubsystem.Xangle + offsetDegrees) * SIGN);
                //                                rotation = ((PedroDriver.VISION_TURN_SCALE * -LimelightSubsystem.Xangle) /
                //                                    robot.limelightSubsystem.getDistance());
            }
            //lowkey forgot what kevin said but i think it just sets the target heading to
            //where the limelight is so that vision can make the bot turn that way
        }
        //robot.follower.holdPoint(new BezierPoint(currentPose), currentPose.getHeading()-rotation, false);
        robot.follower.setTeleOpDrive(0, 0, rotation, false, 0);
        robot.follower.update();
    }

    //        @Override
    //        public void end(boolean s) {
    //           // robot.follower.drivetrain.breakFollowing();
    //            robot.follower.breakFollowing();
    //        }
}
