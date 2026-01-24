package org.firstinspires.ftc.sixteen750.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.HeadingHelper;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.LLSetup;
import org.firstinspires.ftc.sixteen750.commands.PedroPathCommand;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.auto.Paths;
import org.firstinspires.ftc.sixteen750.controls.DriverController;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;
import org.firstinspires.ftc.sixteen750.subsystems.LauncherSubsystem.LauncherCommand;

@Autonomous(name = "Blue9BallFarNew", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class Blue9BallFarNew extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.BLUE, StartingPosition.Net);
        Paths p = new Paths(robot.follower);
        robot.follower.setStartingPose(p.getFar9BallStart());
        CommandScheduler.register(robot.limelightSubsystem);
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                LauncherCommand.SetRegressionAuto(),
                new LLSetup(robot),
                TeleCommands.GateUp(robot),
                LauncherCommand.SetFarShoot(),
                LauncherCommand.FarAutoLaunch(),
                TeleCommands.HoldIntake(robot),
                TeleCommands.HoodUp(robot),
                new PedroPathCommand(robot.follower, p.StartFartolaunchfar),
                new WaitCommand(0.5),
                Paths.AutoLaunching3BallsSlowIntake(robot),
                TeleCommands.HoldIntake(robot),
                // new WaitCommand(0.5),
                //                new PedroPathCommand(robot.follower, p.launchfartointakeCorner, 0.7),
                //                new PedroPathCommand(robot.follower, p.intakeCornertolaunchfar),
                //                Paths.AutoLaunching3Balls(robot),
                //                new PedroPathCommand(robot.follower, p.launchfartogateintake),
                //                new WaitCommand(3.5),
                //                new PedroPathCommand(robot.follower, p.gateintaketolaunchfar),
                //                Paths.AutoLaunching3Balls(robot),
                new PedroPathCommand(robot.follower, p.launchfartopark).alongWith(
                    TeleCommands.Intake(robot)
                ),
                new WaitCommand(1),
                new PedroPathCommand(robot.follower, p.parktolaunchfar),
                new WaitCommand(0.5),
                Paths.AutoLaunching3BallsSlowIntake(robot),
                new WaitCommand(0.3),
                new PedroPathCommand(robot.follower, p.launchfartopark).alongWith(
                    TeleCommands.Intake(robot)
                ),
                new WaitCommand(1),
                new PedroPathCommand(robot.follower, p.parktolaunchfar),
                new WaitCommand(0.5),
                Paths.AutoLaunching3BallsSlowIntake(robot),
                new WaitCommand(1),
                new PedroPathCommand(robot.follower, p.launchfartopark).alongWith(
                    TeleCommands.Intake(robot)
                ),
                new WaitCommand(1),
                LauncherCommand.StopLaunch(),
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
