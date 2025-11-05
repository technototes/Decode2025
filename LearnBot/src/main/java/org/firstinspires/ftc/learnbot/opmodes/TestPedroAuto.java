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
import org.firstinspires.ftc.learnbot.TestPaths;
import org.firstinspires.ftc.learnbot.commands.PedroPathCommand;
import org.firstinspires.ftc.learnbot.controls.DriverController;
import org.firstinspires.ftc.learnbot.helpers.HeadingHelper;
import org.firstinspires.ftc.learnbot.helpers.StartingPosition;

@Autonomous(name = "Test Pedro", preselectTeleOp = "Just Drive", group = "--Testing--")
@SuppressWarnings("unused")
public class TestPedroAuto extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        TestPaths p = new TestPaths(robot.follower);
        CommandScheduler.scheduleOnceForState(
            () -> robot.follower.setStartingPose(p.getStart()),
            OpModeState.INIT
        );
        CommandScheduler.scheduleForState(robot::updatePose, OpModeState.INIT, OpModeState.RUN);
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                robot::normal,
                new PedroPathCommand(robot.follower, p.Path1),
                robot::snail,
                new PedroPathCommand(robot.follower, p.Path2),
                robot::turbo,
                new PedroPathCommand(robot.follower, p.Path3),
                robot::auto,
                new PedroPathCommand(robot.follower, p.Path4),
                CommandScheduler::terminateOpMode
            ),
            OpModeState.RUN
        );
    }

    public void uponStart() {
        robot.atStart();
    }

    public void end() {
        HeadingHelper.savePose(robot.follower.getPose());
    }
}
