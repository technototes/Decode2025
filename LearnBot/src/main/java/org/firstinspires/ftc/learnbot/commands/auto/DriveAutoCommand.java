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

    public DriveAutoCommand(Follower f, double p1, double p2, double p3, double p4) {
        follower =f ;
        p = new double[4];
        p[0] = p1;
        p[1] = p2;
        p[2] = p3;
        p[3] = p4;
    }

    @Override
    public void execute() {
        follower.drivetrain.runDrive(p);
    }
}
