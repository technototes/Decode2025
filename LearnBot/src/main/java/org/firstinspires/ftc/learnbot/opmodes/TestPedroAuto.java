package org.firstinspires.ftc.learnbot.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.HeadingHelper;
import org.firstinspires.ftc.learnbot.Hardware;
import org.firstinspires.ftc.learnbot.Robot;
import org.firstinspires.ftc.learnbot.TestPaths;
import org.firstinspires.ftc.learnbot.commands.PedroPathCommand;
import org.firstinspires.ftc.learnbot.controls.DriverController;
import org.firstinspires.ftc.learnbot.helpers.StartingPosition;

@SuppressWarnings("unused")
@Autonomous(name = "Test Pedro", preselectTeleOp = "Just Drive")
public class TestPedroAuto extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        TestPaths p = new TestPaths(robot.drivebase.follower);
        CommandScheduler.scheduleOnceForState(
            () -> robot.drivebase.follower.setStartingPose(p.getStart()),
            OpModeState.INIT
        );
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                robot.drivebase.MakePathCommand(p.Path1),
                robot.drivebase.MakePathCommand(p.Path2),
                robot.drivebase.MakePathCommand(p.Path3),
                robot.drivebase.MakePathCommand(p.Path4),
                new WaitCommand(1),
                HeadingHelper.SaveCurrentPosition(robot.drivebase),
                CommandScheduler::terminateOpMode
            ),
            OpModeState.RUN
        );
    }

    public void uponStart() {
        robot.atStart();
    }

    public void end() {
        HeadingHelper.SaveCurrentPosition(robot.drivebase);
    }
}
