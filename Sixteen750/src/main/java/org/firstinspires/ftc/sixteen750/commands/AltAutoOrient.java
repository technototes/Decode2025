package org.firstinspires.ftc.sixteen750.commands;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierPoint;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.technototes.library.command.Command;
import com.technototes.library.logger.Log;
import com.technototes.library.util.PIDFController;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.subsystems.LauncherSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.LimelightSubsystem;

@Configurable
public class AltAutoOrient implements Command {

    public Robot robot;

    public Pose wantedPose;
    public boolean firsttime = true;
    public Pose currentPose;
    PIDFController pid;
    public static PIDFCoefficients pidvalues = new PIDFCoefficients(0, 0, 0, 0);

    public AltAutoOrient(Robot r) {
        robot = r;
        pid = new PIDFController(pidvalues);
    }

    //    @Override
    //    public void initialize() {
    //        robot.follower.startTeleOpDrive();
    //    }

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
                rotation = pid.update(LimelightSubsystem.Xangle);
                //                rotation = ((PedroDriver.VISION_TURN_SCALE * -LimelightSubsystem.Xangle) /
                //                    robot.limelightSubsystem.getDistance());
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
