package org.firstinspires.ftc.sixteen750.opmodes.auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
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

@Autonomous(name = "RedIntakeandShootPedro", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class RedIntakeandShootPedro extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        Paths p = new Paths(robot.follower);
        robot.follower.setStartingPose(p.getRStart());
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                TeleCommands.GateUp(robot),
                TeleCommands.Intake(robot),
                TeleCommands.HoodUpAutoOnly(robot),
                new PedroPathCommand(robot.follower, p.Rlaunch),
                Paths.Launching3Balls(robot),
                // new WaitCommand(0.5),
                new PedroPathCommand(robot.follower, p.Rlaunchtointake1),
                // new WaitCommand(1),
                new PedroPathCommand(robot.follower, p.Rintake1tolaunch),
                // new WaitCommand(2),
                Paths.AutoLaunching3Balls(robot),
                new PedroPathCommand(robot.follower, p.Rlaunchtointake2),
                // new WaitCommand(4),
                new PedroPathCommand(robot.follower, p.Rintake2tolaunch),
                Paths.AutoLaunching3Balls(robot),
                // new WaitCommand(4),
                new PedroPathCommand(robot.follower, p.Rlaunchtopark),
                // new WaitCommand(4),
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
