package org.firstinspires.ftc.swervebot.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.HeadingHelper;
import org.firstinspires.ftc.swervebot.Hardware;
import org.firstinspires.ftc.swervebot.Robot;
import org.firstinspires.ftc.swervebot.commands.LLSetup;
import org.firstinspires.ftc.swervebot.commands.PedroPathCommand;
import org.firstinspires.ftc.swervebot.commands.auto.LinePaths;
import org.firstinspires.ftc.swervebot.controls.DriverController;
import org.firstinspires.ftc.swervebot.helpers.StartingPosition;

@Autonomous(name = "ForwardBackward48", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
@Disabled
public class ForwardBackward48 extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        LinePaths p = new LinePaths(robot.follower);
        robot.follower.setMaxPowerScaling(0.5);
        robot.follower.setStartingPose(p.getForward48Start());
        CommandScheduler.register(robot.limelightSubsystem);
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                new LLSetup(robot),
                new PedroPathCommand(robot.follower, p.Forward48),
                new WaitCommand(5),
                new PedroPathCommand(robot.follower, p.Backward48),
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
