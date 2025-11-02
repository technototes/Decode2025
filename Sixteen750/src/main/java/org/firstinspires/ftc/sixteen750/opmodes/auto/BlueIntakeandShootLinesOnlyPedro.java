package org.firstinspires.ftc.sixteen750.opmodes.auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.ParallelCommandGroup;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.PedroPathCommand;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.auto.LinePaths;
import org.firstinspires.ftc.sixteen750.commands.auto.Paths;
import org.firstinspires.ftc.sixteen750.controls.DriverController;
import org.firstinspires.ftc.sixteen750.helpers.HeadingHelper;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;

@Autonomous(name = "BlueIntakeandShootLinesOnlyPedro", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class BlueIntakeandShootLinesOnlyPedro extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        LinePaths p = new LinePaths(robot.follower);
        robot.follower.setStartingPose(p.getStart());
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                new ParallelCommandGroup(
                    new PedroPathCommand(robot.follower, p.Start_to_Launch),
                    Paths.Launching3Balls(robot),
                    TeleCommands.HoodUp(robot)
                ),
                // new WaitCommand(0.5),
                new PedroPathCommand(robot.follower, p.Launch_to_Intake1),
                // new WaitCommand(1),
                new PedroPathCommand(robot.follower, p.Intake1_to_Intake1end),
                // new WaitCommand(2),
                new PedroPathCommand(robot.follower, p.Intake1end_to_Launch),
                Paths.AutoLaunching3Balls(robot),
                new PedroPathCommand(robot.follower, p.Launch_to_Move),
//                new PedroPathCommand(robot.follower, p.Launch_to_Intake2),
//                // new WaitCommand(1),
//                new PedroPathCommand(robot.follower, p.Intake2_to_Intake2end),
//                // new WaitCommand(2),
//                new PedroPathCommand(robot.follower, p.Intake2end_to_Launch),
//                Paths.AutoLaunching3Balls(robot),
//                new PedroPathCommand(robot.follower, p.Launch_to_Intake3),
//                // new WaitCommand(1),
//                new PedroPathCommand(robot.follower, p.Intake3_to_Intake3end),
//                // new WaitCommand(2),
//                new PedroPathCommand(robot.follower, p.Intake3end_to_Launch),
//                Paths.AutoLaunching3Balls(robot),
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
