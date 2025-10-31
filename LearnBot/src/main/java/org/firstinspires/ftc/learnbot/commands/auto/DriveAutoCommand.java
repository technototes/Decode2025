package org.firstinspires.ftc.learnbot.commands.auto;

import com.pedropathing.follower.Follower;
import com.technototes.library.command.Command;

public class DriveAutoCommand implements Command {

    public Follower follower;
    double[] p;

    public DriveAutoCommand(Follower f, double power) {
        follower = f;
        p = new double[4];
        p[0] = power;
        p[1] = power;
        p[2] = power;
        p[3] = power;
    }

    @Override
    public void execute() {
        follower.drivetrain.runDrive(p);
    }
}
