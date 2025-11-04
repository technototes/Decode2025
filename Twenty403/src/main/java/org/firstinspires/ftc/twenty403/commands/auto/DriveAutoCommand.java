package org.firstinspires.ftc.twenty403.commands.auto;

import com.pedropathing.follower.Follower;
import com.technototes.library.command.Command;

public class DriveAutoCommand implements Command {

    public Follower follower;
    double p;

    public DriveAutoCommand(Follower f, double power) {
        follower = f;
        p = power;
    }

    @Override
    public void execute() {
        double[] powers = new double[4];
        powers[0] = p;
        powers[1] = p;
        powers[2] = p;
        powers[3] = p;
        follower.drivetrain.runDrive(powers);
    }
}
