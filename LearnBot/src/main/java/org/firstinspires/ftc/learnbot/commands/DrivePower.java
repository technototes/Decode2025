package org.firstinspires.ftc.learnbot.commands;

import com.pedropathing.follower.Follower;
import com.technototes.library.command.Command;

public class DrivePower implements Command {

    public Follower follower;
    double[] p;

    public DrivePower(Follower f, double p1, double p2, double p3, double p4) {
        follower = f;
        p = new double[4];
        p[0] = p1;
        p[1] = p2;
        p[2] = p3;
        p[3] = p4;
    }

    public DrivePower(Follower f, double power) {
        this(f, power, power, power, power);
    }

    @Override
    public void execute() {
        follower.drivetrain.runDrive(p);
    }
}
