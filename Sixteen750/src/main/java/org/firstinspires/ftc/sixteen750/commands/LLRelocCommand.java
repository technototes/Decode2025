package org.firstinspires.ftc.sixteen750.commands;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
import com.technototes.library.command.Command;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.PIDFController;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.subsystems.LimelightSubsystem;

@Configurable
public class LLRelocCommand implements Command {

    public Robot robot;
    public LimelightSubsystem ll;
    Pose currentPose;

    public LLRelocCommand(Robot r) {
        robot = r;
        ll = r.limelightSubsystem;
        //pid.setInputBounds(-maxvalue, maxvalue);
    }

    public void execute() {
        if (robot.alliance == Alliance.RED) {
            if (ll.getRPose() != null) {
                robot.follower.setPose(ll.getRPose());
            } else currentPose = robot.follower.getPose();
        } else if (robot.alliance == Alliance.BLUE) {
            if (ll.getBPose() != null) {
                robot.follower.setPose(ll.getBPose());
            } else currentPose = robot.follower.getPose();
        } else currentPose = robot.follower.getPose();
    }
}
