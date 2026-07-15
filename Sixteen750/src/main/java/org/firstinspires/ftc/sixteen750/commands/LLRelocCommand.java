package org.firstinspires.ftc.sixteen750.commands;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
import com.technototes.library.command.Command;
import org.firstinspires.ftc.sixteen750.subsystems.LimelightSubsystem;
import org.firstinspires.ftc.sixteen750.Robot;

@Configurable
public class LLRelocCommand implements Command {
    public Robot robot;
    public LimelightSubsystem ll;
    public Pose currentPose;

    public void execute() {
        currentPose = ll.getPose();
        if (currentPose != null) {
            robot.follower.setPose(ll.getPose());
        }
        else {
            robot.follower.setPose(currentPose);
        }
    }
}
