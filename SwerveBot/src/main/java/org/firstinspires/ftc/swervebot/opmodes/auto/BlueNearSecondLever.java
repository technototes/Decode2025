package org.firstinspires.ftc.swervebot.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.swervebot.Hardware;
import org.firstinspires.ftc.swervebot.Robot;
import org.firstinspires.ftc.swervebot.commands.AltAutoVelocity;
import org.firstinspires.ftc.swervebot.commands.PedroPathCommand;
import org.firstinspires.ftc.swervebot.commands.TeleCommands;
import org.firstinspires.ftc.swervebot.commands.auto.Paths;
import org.firstinspires.ftc.swervebot.controls.DriverController;
import org.firstinspires.ftc.swervebot.helpers.HeadingHelper;
import org.firstinspires.ftc.swervebot.helpers.StartingPosition;

@Autonomous(name = "BlueNearLever2️⃣", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class BlueNearSecondLever extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.BLUE, StartingPosition.Net);
        Paths p = new Paths(robot.follower);
        robot.follower.setStartingPose(p.getBSegmentedCurveStart());
        CommandScheduler.scheduleForState(
            new AltAutoVelocity(robot).alongWith(
                new SequentialCommandGroup(
                    //TeleCommands.AutoLaunch1(robot),
                    TeleCommands.GateUp(robot),
                    TeleCommands.Intake(robot),
                    TeleCommands.HoodUp(robot),
                    new PedroPathCommand(robot.follower, p.StarttoLaunch, p.power2),
                    Paths.AutoLaunching3Balls(robot),
                    // new WaitCommand(0.5),
                    new PedroPathCommand(robot.follower, p.LaunchtoIntake1),
                    // new WaitCommand(1),
                    new PedroPathCommand(robot.follower, p.Intake1toIntake1end, p.power).alongWith(
                        TeleCommands.Intake(robot)
                    ),
                    new WaitCommand(0.05),
                    //   TeleCommands.AutoLaunch2(robot),
                    new PedroPathCommand(robot.follower, p.Intake1endtoLaunch),
                    Paths.AutoLaunching3Balls(robot),
                    new PedroPathCommand(robot.follower, p.LaunchtoIntake2),
                    new PedroPathCommand(robot.follower, p.Intake2toIntake2end, p.power).alongWith(
                        TeleCommands.Intake(robot)
                    ),
                    new WaitCommand(0.05),
                    //TeleCommands.AutoLaunch2(robot),

                    new PedroPathCommand(robot.follower, p.Intake2endtoLever2, p.power2),
                    new PedroPathCommand(robot.follower, p.Lever2toLaunch),
                    new WaitCommand(0.05),
                    Paths.AutoLaunching3Balls(robot),
                    new PedroPathCommand(robot.follower, p.LaunchtoIntake3),
                    new WaitCommand(0.05),
                    new PedroPathCommand(robot.follower, p.Intake3toIntake3end, p.power).alongWith(
                        TeleCommands.Intake(robot)
                    ),
                    new WaitCommand(0.1),
                    new PedroPathCommand(robot.follower, p.Intake3endtoLaunch).alongWith(
                        TeleCommands.IntakeStop(robot)
                    ),
                    Paths.AutoLaunching3Balls(robot),
                    new PedroPathCommand(robot.follower, p.LaunchtoEnd, p.power2),
                    TeleCommands.IntakeStop(robot),
                    CommandScheduler::terminateOpMode

                    //        CommandScheduler.scheduleForState(
                    //            new SequentialCommandGroup(
                    //                TeleCommands.GateUp(robot),
                    //                TeleCommands.AutoLaunch1(robot),
                    //                TeleCommands.Intake(robot),
                    //                TeleCommands.HoodUp(robot),
                    //                new PedroPathCommand(robot.follower, p.StarttoLaunch),
                    //                Paths.AutoLaunching3Balls(robot),
                    //                // new WaitCommand(0.5),
                    //                new PedroPathCommand(robot.follower, p.LaunchtoIntake1),
                    //                // new WaitCommand(1),
                    //                new PedroPathCommand(robot.follower, p.Intake1toIntake1end, 0.8),
                    //                new PedroPathCommand(robot.follower, p.Intake1endtoLever),
                    //                new WaitCommand(1),
                    //                // new WaitCommand(2),
                    //                new PedroPathCommand(robot.follower, p.LevertoLaunch),
                    //                Paths.AutoLaunching3Balls(robot),
                    //                new PedroPathCommand(robot.follower, p.LaunchtoIntake2),
                    //                new PedroPathCommand(robot.follower, p.Intake2toIntake2end, p.power),
                    //                new PedroPathCommand(robot.follower, p.Intake2endtoLaunch),
                    //                Paths.AutoLaunching3Balls(robot),
                    //                new PedroPathCommand(robot.follower, p.LaunchtoIntake3),
                    //                new WaitCommand(2),
                    //                new PedroPathCommand(robot.follower, p.Intake3toIntake3end, p.power),
                    //                new PedroPathCommand(robot.follower, p.Intake3endtoLaunch),
                    //                Paths.AutoLaunching3Balls(robot),
                    //                new PedroPathCommand(robot.follower, p.LaunchtoEnd, p.power),
                    //                TeleCommands.StopLaunch(robot),
                    //                TeleCommands.IntakeStop(robot),
                    //                CommandScheduler::terminateOpMode
                )
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
