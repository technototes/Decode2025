package org.firstinspires.ftc.blackbird.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.blackbird.Hardware;
import org.firstinspires.ftc.blackbird.Robot;
import org.firstinspires.ftc.blackbird.commands.PedroPathCommand;
import org.firstinspires.ftc.blackbird.commands.auto.TestPaths;
import org.firstinspires.ftc.blackbird.controls.DriverController;
import org.firstinspires.ftc.blackbird.helpers.HeadingHelper;
import org.firstinspires.ftc.blackbird.helpers.StartingPosition;

@Autonomous(name = "TestPedro", preselectTeleOp = "OneDriver")
@SuppressWarnings("unused")
public class TestPedroAuto extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        TestPaths p = new TestPaths(robot.follower);
        robot.follower.setStartingPose(p.getStart());
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                new PedroPathCommand(robot.follower, p.Path1),
                // new WaitCommand(0.5),
                new PedroPathCommand(robot.follower, p.Path2),
                // new WaitCommand(1),
                new PedroPathCommand(robot.follower, p.Path3),
                // new WaitCommand(2),
                new PedroPathCommand(robot.follower, p.Path4),
                // new WaitCommand(4),
                new PedroPathCommand(robot.follower, p.Path5),
                CommandScheduler::terminateOpMode
            ),
            OpModeState.RUN
        );
    }

    public void uponStart() {
        robot.prepForStart();
    }

    public void end() {
        HeadingHelper.savePose(robot.follower.getPose());
    }
}
