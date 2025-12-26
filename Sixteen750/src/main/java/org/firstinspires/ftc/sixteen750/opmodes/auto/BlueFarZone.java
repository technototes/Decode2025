package org.firstinspires.ftc.sixteen750.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
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

@Autonomous(name = "BlueFarZone", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
@Disabled
public class BlueFarZone extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        Paths p = new Paths(robot.follower);
        robot.follower.setStartingPose(p.getBFarZoneStart());
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                TeleCommands.GateUp(robot),
                TeleCommands.AutoLaunch1(robot),
                TeleCommands.Intake(robot),
                TeleCommands.HoodUp(robot),
                new PedroPathCommand(robot.follower, p.FarStarttoFarLaunch),
                new WaitCommand(0.2),
                Paths.AutoLaunching3Balls(robot),
                // new WaitCommand(0.5),
                new PedroPathCommand(robot.follower, p.FarLaunchtoIntakeCorner),
                new WaitCommand(2),
                // new WaitCommand(1),
                new PedroPathCommand(robot.follower, p.IntakeCornertoLaunch),
                // new WaitCommand(2),
                Paths.AutoLaunching3Balls(robot),
                new WaitCommand(0.2),
                new PedroPathCommand(robot.follower, p.LaunchtoIntakeTunnel),
                new WaitCommand(4),
                new PedroPathCommand(robot.follower, p.IntakeTunneltoLaunch),
                Paths.AutoLaunching3Balls(robot),
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
