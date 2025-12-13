package org.firstinspires.ftc.sixteen750.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.PedroPathCommand;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.auto.Paths;
import org.firstinspires.ftc.sixteen750.controls.DriverController;
import org.firstinspires.ftc.sixteen750.helpers.HeadingHelper;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;

@Autonomous(name = "RedSegmentedCurveLever", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class RedSegmentedCurveLever extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        Paths p = new Paths(robot.follower);
        robot.follower.setStartingPose(p.getRSegmentedCurveStart());
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                TeleCommands.GateUp(robot),
                TeleCommands.AutoLaunch(robot),
                TeleCommands.Intake(robot),
                TeleCommands.HoodUp(robot),
                new PedroPathCommand(robot.follower, p.RStarttoLaunch),
                Paths.AutoLaunching3Balls(robot),
                // new WaitCommand(0.5),
                new PedroPathCommand(robot.follower, p.RLaunchtoIntake1),
                // new WaitCommand(1),
                new PedroPathCommand(robot.follower, p.RIntake1toIntake1end),
                // new WaitCommand(2),
                new PedroPathCommand(robot.follower, p.RIntake1endtoLever),
                new WaitCommand(1),
                new PedroPathCommand(robot.follower, p.RLevertoLaunch),
                Paths.AutoLaunching3Balls(robot),
                new PedroPathCommand(robot.follower, p.RLaunchtoIntake2),
                new PedroPathCommand(robot.follower, p.RIntake2toIntake2end, p.power),
                new WaitCommand(0.4),
                new PedroPathCommand(robot.follower, p.RIntake2endtoLaunch),
                Paths.AutoLaunching3Balls(robot),
                new PedroPathCommand(robot.follower, p.RLaunchtoIntake3),
                new PedroPathCommand(robot.follower, p.RIntake3toIntake3end, p.power),
                new WaitCommand(0.4),
                new PedroPathCommand(robot.follower, p.RIntake3endtoLaunch),
                Paths.AutoLaunching3Balls(robot),
                new PedroPathCommand(robot.follower, p.RLaunchtoEnd),
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
