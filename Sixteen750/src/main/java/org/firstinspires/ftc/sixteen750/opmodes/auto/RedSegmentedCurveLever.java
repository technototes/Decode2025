package org.firstinspires.ftc.sixteen750.opmodes.auto;

import com.bylazar.telemetry.PanelsTelemetry;
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
import org.firstinspires.ftc.sixteen750.subsystems.LauncherSubsystem;

@Autonomous(name = "RedSegmentedCurveLever", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class RedSegmentedCurveLever extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;
    private PanelsTelemetry panelsTelemetry;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        Paths p = new Paths(robot.follower);
        panelsTelemetry = PanelsTelemetry.INSTANCE;
        robot.follower.setStartingPose(p.getRSegmentedCurveStart());
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                TeleCommands.GateUp(robot),
                TeleCommands.AutoLaunch(robot),
                TeleCommands.HoodUp(robot),
                TeleCommands.Intake(robot),
                new PedroPathCommand(robot.follower, p.RStarttoLaunch, p.power2),
                Paths.AutoLaunching3Balls(robot),
                // new WaitCommand(0.5),
                new PedroPathCommand(robot.follower, p.RLaunchtoIntake1),
                // new WaitCommand(1),
                new PedroPathCommand(robot.follower, p.RIntake1toIntake1end, p.power),
                new WaitCommand(0.4),
                // new WaitCommand(2),
                new PedroPathCommand(robot.follower, p.RIntake1endtoLever),
                new WaitCommand(0.4),
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
                new PedroPathCommand(robot.follower, p.RLaunchtoEnd, p.power2),
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
    public void runLoop() {
        panelsTelemetry.getTelemetry().addData("currentLaunchVelocity", String.valueOf(LauncherSubsystem.currentLaunchVelocity));
        panelsTelemetry.getTelemetry().addData("launcherError", String.valueOf(LauncherSubsystem.err));
        panelsTelemetry.getTelemetry().addData("launcherTargetVelocity", String.valueOf(LauncherSubsystem.targetSpeed));
        panelsTelemetry.getTelemetry().addData("launcher1Current", String.valueOf(LauncherSubsystem.launcher1Current));
        panelsTelemetry.getTelemetry().addData("launcher2Current", String.valueOf(LauncherSubsystem.launcher2Current));
        panelsTelemetry.getTelemetry().update(telemetry);
    }

    public void end() {
        HeadingHelper.savePose(robot.follower.getPose());
    }
}
