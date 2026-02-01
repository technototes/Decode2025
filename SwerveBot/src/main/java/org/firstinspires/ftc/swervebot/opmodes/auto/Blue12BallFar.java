package org.firstinspires.ftc.swervebot.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.swervebot.Hardware;
import org.firstinspires.ftc.swervebot.Robot;
import org.firstinspires.ftc.swervebot.commands.LLSetup;
import org.firstinspires.ftc.swervebot.commands.PedroPathCommand;
import org.firstinspires.ftc.swervebot.commands.TeleCommands;
import org.firstinspires.ftc.swervebot.commands.auto.Paths;
import org.firstinspires.ftc.swervebot.controls.DriverController;
import org.firstinspires.ftc.swervebot.helpers.HeadingHelper;
import org.firstinspires.ftc.swervebot.helpers.StartingPosition;

@Autonomous(name = "Blue12BallFar", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class Blue12BallFar extends CommandOpMode {

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
                TeleCommands.SetRegressionCAuto(robot),
                TeleCommands.SetRegressionDAuto(robot),
                new LLSetup(robot),
                TeleCommands.GateUp(robot),
                TeleCommands.SetFarShoot(robot),
                TeleCommands.FarAutoLaunch(robot),
                TeleCommands.HoldIntake(robot),
                TeleCommands.HoodUp(robot),
                new PedroPathCommand(robot.follower, p.StartFartolaunchfar),
                new WaitCommand(0.5),
                Paths.AutoLaunching3BallsSlowIntake(robot),
                TeleCommands.HoldIntake(robot),
                // new WaitCommand(0.5),
                new PedroPathCommand(
                    robot.follower,
                    p.launchfartointake4,
                    p.powerforintake4
                ).alongWith(TeleCommands.Intake(robot)),
                new WaitCommand(0.3),
                // new WaitCommand(2),
                new PedroPathCommand(robot.follower, p.intake4tolaunchfar),
                Paths.AutoLaunching3BallsSlowIntake(robot),
                new PedroPathCommand(robot.follower, p.launchfartointakeVertical, 0.6).alongWith(
                    TeleCommands.Intake(robot)
                ),
                new PedroPathCommand(robot.follower, p.intakeVerticaltolaunchfar),
                Paths.AutoLaunching3BallsSlowIntakeFar(robot),
                new PedroPathCommand(robot.follower, p.launchfartointakeHorizontal, 0.6).alongWith(
                    TeleCommands.Intake(robot)
                ),
                new PedroPathCommand(robot.follower, p.intakeHorizontaltolaunchfar, 0.9),
                Paths.AutoLaunching3BallsSlowIntakeFar(robot),
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
