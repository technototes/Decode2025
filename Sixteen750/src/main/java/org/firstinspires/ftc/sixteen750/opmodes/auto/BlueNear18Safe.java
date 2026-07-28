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

@Autonomous(name = "BlueNear18Safe", preselectTeleOp = "BlueTele")
@SuppressWarnings("unused")
public class BlueNear18Safe extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;
    public PedroDriver pedroDriver;
    private PanelsTelemetry panelsTelemetry;
    private Limelight3A limelight;

    private static Command BlueGateCycle1(Robot r) {
        return new SequentialCommandGroup(
            TeleCommands.Intake(r),
            new PedroPathCommand(r.follower, BPaths.SBLaunchToBGateInt1)
                .alongWith(AutoCommands.PostLaunchRoutine(r))
                .withTimeout(2.5),
            new WaitForArtifacts(r.intakeSubsystem).withTimeout(1.25),
            new PedroPathCommand(r.follower, BPaths.SBGateInt1ToBLaunch).withTimeout(2.5),
            AutoCommands.AutoLaunching3Balls(r)
        );
    }

    private static Command BlueGateCycle2(Robot r) {
        return new SequentialCommandGroup(
            TeleCommands.Intake(r),
            new PedroPathCommand(r.follower, BPaths.SBLaunchToBGateInt2)
                .alongWith(AutoCommands.PostLaunchRoutine(r))
                .withTimeout(2.5),
            new WaitForArtifacts(r.intakeSubsystem).withTimeout(1.25),
            new PedroPathCommand(r.follower, BPaths.SBGateInt2ToBLaunch).withTimeout(2.5),
            AutoCommands.AutoLaunching3Balls(r)
        );
    }

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
                    a.AutoStartRoutine(robot),
                    new PedroPathCommand(robot.follower, p.SBStartToBLaunch).withTimeout(2.5),
                    a.AutoLaunching3Balls(robot),
                    new PedroPathCommand(robot.follower, p.SBLaunchToBInt1, p.power095)
                        .alongWith(a.PostLaunchRoutine(robot))
                        .withTimeout(2.5),
                    new PedroPathCommand(robot.follower, p.SBInt1ToBLaunch).withTimeout(2.5),
                    a.AutoLaunching3Balls(robot),
                    BlueGateCycle1(robot),
                    new PedroPathCommand(robot.follower, p.SBLaunchToBInt2, p.power095)
                        .alongWith(a.PostLaunchRoutine(robot))
                        .withTimeout(2.5),
                    new PedroPathCommand(robot.follower, p.SBInt2ToBLaunch).withTimeout(2.5),
                    a.AutoLaunching3Balls(robot),
                    BlueGateCycle2(robot),
                    new PedroPathCommand(robot.follower, p.SBLaunchToBInt3, p.power095)
                        .alongWith(a.PostLaunchRoutine(robot))
                        .withTimeout(2.5),
                    new PedroPathCommand(robot.follower, p.SBInt3ToBLaunch).withTimeout(2.5),
                    a.AutoLaunching3Balls(robot),
                    new PedroPathCommand(robot.follower, p.SBLaunchToBEnd)
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
