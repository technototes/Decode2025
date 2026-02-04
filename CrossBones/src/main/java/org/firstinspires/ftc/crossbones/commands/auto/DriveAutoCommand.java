package org.firstinspires.ftc.crossbones.commands.auto;

import com.pedropathing.follower.Follower;
import com.technototes.library.command.Command;

public class DriveAutoCommand implements Command {

    public Follower follower;
    double[] powers = new double[4];

    public DriveAutoCommand(Follower f, double power) {
        follower = f;
        powers[0] = power;
        powers[1] = power;
        powers[2] = power;
        powers[3] = power;
    }

    public DriveAutoCommand(Follower f, double flp, double rlp, double frp, double rrp) {
        follower = f;
        powers[0] = flp;
        powers[1] = frp;
        powers[2] = rlp;
        powers[3] = rrp;
    }

    @Override
    public void execute() {
        follower.drivetrain.runDrive(powers);
    }
}
