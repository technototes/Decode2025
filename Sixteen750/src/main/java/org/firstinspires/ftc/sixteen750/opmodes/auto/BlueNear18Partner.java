package org.firstinspires.ftc.sixteen750.opmodes.auto;

import static org.firstinspires.ftc.sixteen750.Setup.HardwareNames.AprilTag_Pipeline;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.Command;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.HeadingHelper;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.commands.AltAutoVelocity;
import org.firstinspires.ftc.sixteen750.commands.PedroDriver;
import org.firstinspires.ftc.sixteen750.commands.PedroPathCommand;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.auto.AutoCommands;
import org.firstinspires.ftc.sixteen750.commands.auto.BPaths;
import org.firstinspires.ftc.sixteen750.commands.auto.Poses;
import org.firstinspires.ftc.sixteen750.commands.auto.WaitForArtifacts;
import org.firstinspires.ftc.sixteen750.controls.DriverController;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;
import org.firstinspires.ftc.sixteen750.subsystems.LauncherSubsystem;

@Autonomous(name = "BlueNear18Partner", preselectTeleOp = "BlueTele")
@SuppressWarnings("unused")
public class BlueNear18Partner extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;
    public PedroDriver pedroDriver;
    private PanelsTelemetry panelsTelemetry;
    private Limelight3A limelight;

    private static Command BlueGateCycle1(Robot r) {
        return new SequentialCommandGroup(
            new PedroPathCommand(r.follower, BPaths.PBLaunchToBGateInt1)
                .alongWith(AutoCommands.PostLaunchRoutine(r))
                .withTimeout(2.5),
            new WaitForArtifacts(r.intakeSubsystem).withTimeout(1.25),
            new PedroPathCommand(r.follower, BPaths.PBGateInt1ToBLaunch).withTimeout(2.5),
            AutoCommands.AutoLaunching3Balls(r)
        );
    }

    private static Command BlueGateCycle2(Robot r) {
        return new SequentialCommandGroup(
            new PedroPathCommand(r.follower, BPaths.PBLaunchToBGateInt2)
                .alongWith(AutoCommands.PostLaunchRoutine(r))
                .withTimeout(2.5),
            new WaitForArtifacts(r.intakeSubsystem).withTimeout(1.25),
            new PedroPathCommand(r.follower, BPaths.PBGateInt2ToBLaunch).withTimeout(2.5),
            AutoCommands.AutoLaunching3Balls(r)
        );
    }

    private static Command BlueGateCycle3(Robot r) {
        return new SequentialCommandGroup(
            new PedroPathCommand(r.follower, BPaths.PBLaunchToBGateInt3)
                .alongWith(AutoCommands.PostLaunchRoutine(r))
                .withTimeout(2.5),
            new WaitForArtifacts(r.intakeSubsystem).withTimeout(1.25),
            new PedroPathCommand(r.follower, BPaths.PBGateInt3ToBLaunch).withTimeout(2.5),
            AutoCommands.AutoLaunching3Balls(r)
        );
    }

    // POSITION FOR COLIN:
    // X = 132.5 Y = 65.75 H = 41
    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.BLUE, StartingPosition.Net);
        BPaths p = new BPaths(robot.follower);
        TeleCommands t = new TeleCommands();
        AutoCommands a = new AutoCommands();
        panelsTelemetry = PanelsTelemetry.INSTANCE;
        robot.follower.setStartingPose(Poses.StartPoses.getBStart());
        CommandScheduler.scheduleForState(
            new AltAutoVelocity(robot).alongWith(
                new SequentialCommandGroup(
                    new PedroPathCommand(robot.follower, p.PBStartToBLaunch)
                        .alongWith(a.AutoStartRoutine(robot))
                        .withTimeout(2.5),
                    a.AutoLaunching3Balls(robot),
                    new PedroPathCommand(robot.follower, p.PBLaunchToBInt1, p.power095)
                        .alongWith(a.PostLaunchRoutine(robot))
                        .withTimeout(2.5),
                    new PedroPathCommand(robot.follower, p.PBInt1ToBLaunch).withTimeout(2.5),
                    a.AutoLaunching3Balls(robot),
                    BlueGateCycle1(robot),
                    BlueGateCycle2(robot),
                    new PedroPathCommand(robot.follower, p.PBLaunchToBInt2, p.power095)
                        .alongWith(a.PostLaunchRoutine(robot))
                        .withTimeout(2.5),
                    new PedroPathCommand(robot.follower, p.PBInt2ToBLaunch).withTimeout(2.5),
                    a.AutoLaunching3Balls(robot),
                    BlueGateCycle3(robot),
                    new PedroPathCommand(robot.follower, p.PBLaunchToBEnd)
                        .alongWith(a.PostLaunchRoutine(robot))
                        .withTimeout(2.5),
                    CommandScheduler::terminateOpMode
                )
            ),
            OpModeState.RUN
        );
        if (Setup.Connected.LIMELIGHTSUBSYSTEM) {
            limelight = hardware.limelight;
            limelight.setPollRateHz(100);

            telemetry.setMsTransmissionInterval(11);

            limelight.pipelineSwitch(AprilTag_Pipeline);
            CommandScheduler.register(robot.limelightSubsystem);

            /*
             * Starts polling for data.  If you neglect to call start(), getLatestResult() will return null.
             */
            limelight.start();
        }
        if (Setup.Connected.LAUNCHERSUBSYSTEM) {
            CommandScheduler.register(robot.launcherSubsystem);
        }
    }

    public void uponStart() {
        robot.prepForStart();
    }

    public void runLoop() {
        panelsTelemetry
            .getTelemetry()
            .addData(
                "currentLaunchVelocity",
                String.valueOf(LauncherSubsystem.currentLaunchVelocity)
            );
        panelsTelemetry
            .getTelemetry()
            .addData("launcherError", String.valueOf(LauncherSubsystem.err));
        panelsTelemetry
            .getTelemetry()
            .addData("launcherTargetVelocity", String.valueOf(LauncherSubsystem.targetSpeed));
        panelsTelemetry
            .getTelemetry()
            .addData("launcher1Current", String.valueOf(LauncherSubsystem.launcher1Current));
        panelsTelemetry
            .getTelemetry()
            .addData("launcher2Current", String.valueOf(LauncherSubsystem.launcher2Current));
        panelsTelemetry.getTelemetry().update(telemetry);
    }

    public void end() {
        HeadingHelper.savePose(robot.follower.getPose());
        if (Setup.Connected.LIMELIGHTSUBSYSTEM) {
            limelight.stop();
        }
    }
}
