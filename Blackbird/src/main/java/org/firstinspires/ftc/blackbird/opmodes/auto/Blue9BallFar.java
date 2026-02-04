package org.firstinspires.ftc.blackbird.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.HeadingHelper;
import org.firstinspires.ftc.blackbird.Hardware;
import org.firstinspires.ftc.blackbird.Robot;
import org.firstinspires.ftc.blackbird.commands.PedroPathCommand;
import org.firstinspires.ftc.blackbird.commands.TeleCommands;
import org.firstinspires.ftc.blackbird.commands.auto.Paths;
import org.firstinspires.ftc.blackbird.controls.DriverController;
import org.firstinspires.ftc.blackbird.helpers.StartingPosition;

@Autonomous(name = "Blue9BallFar", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class Blue9BallFar extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        Paths p = new Paths(robot.follower);
        robot.follower.setStartingPose(p.getFar9BallStart());
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                TeleCommands.GateUp(robot),
                TeleCommands.SetFarShoot(robot),
                TeleCommands.Launch(robot),
                TeleCommands.Intake(robot),
                TeleCommands.HoodUp(robot),
                new PedroPathCommand(robot.follower, p.StartFartolaunchfar),
                new WaitCommand(1),
                Paths.AutoLaunching3Balls(robot),
                // new WaitCommand(0.5),
                //                new PedroPathCommand(robot.follower, p.launchfartointake4),
                //                // new WaitCommand(1),
                //                // new WaitCommand(2),
                //                new PedroPathCommand(robot.follower, p.intake4tolaunchfar),
                //                Paths.AutoLaunching3Balls(robot),
                new PedroPathCommand(robot.follower, p.launchfartointakeCorner),
                new WaitCommand(0.5),
                new PedroPathCommand(robot.follower, p.intakeCornertolaunchfar),
                Paths.AutoLaunching3Balls(robot),
                new PedroPathCommand(robot.follower, p.launchfartogateintake),
                new WaitCommand(3.5),
                new PedroPathCommand(robot.follower, p.gateintaketolaunchfar),
                Paths.AutoLaunching3Balls(robot),
                new PedroPathCommand(robot.follower, p.launchfartopark),
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
    }
}
