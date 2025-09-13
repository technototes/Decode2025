package org.firstinspires.ftc.twenty403.commands.auto;

import com.technototes.library.command.Command;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.path.command.TrajectorySequenceCommand;
import org.firstinspires.ftc.twenty403.AutoConstants;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.subsystems.DrivebaseSubsystem;

public class MotorParking implements Command {

    public DrivebaseSubsystem subsystem;
    public double p;

    public MotorParking(DrivebaseSubsystem s, double power) {
        subsystem = s;
        p = power;
    }

    //    public static Command motorPower(Robot r) {
    //        return new SequentialCommandGroup(
    //        Command.create(r.drivebaseSubsystem::setMotorPark(1.0, 1.0, 1.0, 1.0)),
    //                r.drivebaseSubsystem.setMotorPowers(0,0,0,0)
    //        );
    //    }

    public void execute() {
        subsystem.setMotorPowers(-p, p, -p, p);
    }
}
