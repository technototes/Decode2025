package org.firstinspires.ftc.blackbird.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.blackbird.Hardware;
import org.firstinspires.ftc.blackbird.Robot;
import org.firstinspires.ftc.blackbird.commands.LLSetup;
import org.firstinspires.ftc.blackbird.commands.PedroPathCommand;
import org.firstinspires.ftc.blackbird.commands.TeleCommands;
import org.firstinspires.ftc.blackbird.commands.auto.Paths;
import org.firstinspires.ftc.blackbird.controls.DriverController;
import org.firstinspires.ftc.blackbird.helpers.HeadingHelper;
import org.firstinspires.ftc.blackbird.helpers.StartingPosition;

@Autonomous(name = "Red9BallFar", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class Red9BallFar extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        Paths p = new Paths(robot.follower);
        robot.follower.setStartingPose(p.getRFar9BallStart());
        CommandScheduler.register(robot.limelightSubsystem);
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                new LLSetup(robot),
                TeleCommands.GateUp(robot),
                TeleCommands.SetFarShoot(robot),
                TeleCommands.Launch(robot),
                TeleCommands.Intake(robot),
                TeleCommands.HoodUp(robot),
                new PedroPathCommand(robot.follower, p.RStartFartolaunchfar),
                new WaitCommand(1),
                Paths.AutoLaunching3Balls(robot),
                // new WaitCommand(0.5),
                //new PedroPathCommand(robot.follower, p.Rlaunchfartointake4),
                // new WaitCommand(1),
                // new WaitCommand(2),
                // new PedroPathCommand(robot.follower, p.Rintake4tolaunchfar),
                // Paths.AutoLaunching3Balls(robot),
                new PedroPathCommand(robot.follower, p.RlaunchfartointakeCorner),
                new PedroPathCommand(robot.follower, p.RintakeCornertolaunchfar),
                Paths.AutoLaunching3Balls(robot),
                new PedroPathCommand(robot.follower, p.Rlaunchfartogateintake),
                new WaitCommand(3.5),
                new PedroPathCommand(robot.follower, p.Rgateintaketolaunchfar),
                Paths.AutoLaunching3Balls(robot),
                new PedroPathCommand(robot.follower, p.Rlaunchfartopark),
                new WaitCommand(1),
                TeleCommands.StopLaunch(robot),
                TeleCommands.IntakeStop(robot),
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
        robot.limelightSubsystem.LimelightTurnOff();
    }
}
