package org.firstinspires.ftc.learnbot.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.learnbot.Hardware;
import org.firstinspires.ftc.learnbot.Robot;
import org.firstinspires.ftc.learnbot.commands.auto.DriveAutoCommand;
import org.firstinspires.ftc.learnbot.controls.DriverController;
import org.firstinspires.ftc.learnbot.helpers.HeadingHelper;
import org.firstinspires.ftc.learnbot.helpers.StartingPosition;

@Autonomous(name = "DriveForward", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class DriveForward extends CommandOpMode {

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
                new DriveAutoCommand(robot.drivebaseSubsystem, 0.5),
                new WaitCommand(0.3),
                new DriveAutoCommand(robot.drivebaseSubsystem, 0),
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
