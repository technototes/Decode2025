package org.firstinspires.ftc.twenty403.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.technototes.library.command.Command;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.ParallelCommandGroup;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;

import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.commands.auto.DriveAutoCommand;
import org.firstinspires.ftc.twenty403.controls.DriverController;
import org.firstinspires.ftc.twenty403.helpers.HeadingHelper;
import org.firstinspires.ftc.twenty403.helpers.StartingPosition;

@Autonomous(name = "DriveBackwardShoot", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class DriveBackwardShoot extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        //robot.drivebase.setPoseEstimate(AutoConstants.BACKWARD.toPose());
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                    //might not need to run the drive command depending on where the bot launches from
                    new ParallelCommandGroup(
                        new DriveAutoCommand(robot.drivebaseSubsystem, 0.5),
                        Command.create(robot.launcherSubsystem::Launch)),
                new DriveAutoCommand(robot.drivebaseSubsystem, 0),
                new WaitCommand(1),
                Command.create(robot.feedingSubsystem::moveball),
                new WaitCommand(1),
                CommandScheduler::terminateOpMode
            ),
            OpModeState.RUN
        );
    }

    public void uponStart() {
        robot.atStart();
    }

    public void end() {
        HeadingHelper.savePose(robot.drivebaseSubsystem.getPoseEstimate());
    }
}
