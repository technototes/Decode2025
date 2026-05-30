package org.firstinspires.ftc.sixteen750.commands.auto;

import com.pedropathing.follower.Follower;
import com.technototes.library.command.Command;
import com.technototes.library.hardware.sensor.IGyro;

public class TurnAutoCommand implements Command {

    public Follower follower;
    public IGyro imu;
    double[] p;
    double startHeading;

    public TurnAutoCommand(Follower f, double power) {
        follower = f;
        p = new double[4];
        p[0] = power;
        p[1] = power;
        p[2] = -power;
        p[3] = -power;
    }

    @Override
    public void initialize() {
        startHeading = follower.getHeading();
    }

    @Override
    public boolean isFinished() {
        double curHeading = follower.getHeading();
        if (Math.abs(startHeading - curHeading) > Math.PI / 2) {
            return true;
        }
        return false;
    }

    @Override
    public void execute() {
        follower.drivetrain.runDrive(p);
    }

    @Override
    public void end(boolean cancel) {
        double[] zeros = new double[4];
        zeros[0] = 0;
        zeros[1] = 0;
        zeros[2] = 0;
        zeros[3] = 0;
        follower.drivetrain.runDrive(zeros);
    }
}
