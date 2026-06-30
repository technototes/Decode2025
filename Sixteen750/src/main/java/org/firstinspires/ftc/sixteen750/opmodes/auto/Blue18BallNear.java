package org.firstinspires.ftc.sixteen750.opmodes.auto;

import static org.firstinspires.ftc.sixteen750.Setup.HardwareNames.AprilTag_Pipeline;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.Command;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.ParallelRaceGroup;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.HeadingHelper;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.commands.AltAutoVelocity;
import org.firstinspires.ftc.sixteen750.commands.PedroPathCommand;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.auto.Paths;
import org.firstinspires.ftc.sixteen750.commands.auto.Paths2;
import org.firstinspires.ftc.sixteen750.commands.auto.Poses;
import org.firstinspires.ftc.sixteen750.controls.DriverController;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;
import org.firstinspires.ftc.sixteen750.subsystems.LauncherSubsystem;

@Autonomous(name = "Blue18BallNear", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class Blue18BallNear extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;
    private PanelsTelemetry panelsTelemetry;
    private Limelight3A limelight;

    public static Command BlueGateCycle1(Robot r) {
        return new SequentialCommandGroup(
            TeleCommands.Intake(r),
            new PedroPathCommand(r.follower, Paths2.BLaunchToBGateInt1),
            new WaitCommand(1.1),
            new PedroPathCommand(r.follower, Paths2.BGateInt1ToBLaunch),
            Paths2.AutoLaunching3Balls(r)
        );
    }

    public static Command BlueGateCycle2(Robot r) {
        return new SequentialCommandGroup(
            TeleCommands.Intake(r),
            new PedroPathCommand(r.follower, Paths2.BLaunchToBGateInt2),
            new WaitCommand(1.1),
            new PedroPathCommand(r.follower, Paths2.BGateInt2ToBLaunch),
            Paths2.AutoLaunching3Balls(r)
        );
    }

    public static Command BlueGateCycle3(Robot r) {
        return new SequentialCommandGroup(
            TeleCommands.Intake(r),
            new PedroPathCommand(r.follower, Paths2.BLaunchToBGateInt3),
            new WaitCommand(1.1),
            new PedroPathCommand(r.follower, Paths2.BGateInt3ToBLaunch),
            Paths2.AutoLaunching3Balls(r)
        );
    }

    // POSITION FOR COLIN:
    // X = 132.5 Y = 65.75 H = 41
    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.BLUE, StartingPosition.Net);
        Paths2 p = new Paths2(robot.follower);
        panelsTelemetry = PanelsTelemetry.INSTANCE;
        robot.follower.setStartingPose(Poses.getBStart());
        CommandScheduler.scheduleForState(
            new AltAutoVelocity(robot).alongWith(
                new SequentialCommandGroup(
                    TeleCommands.Launch(robot),
                    //TeleCommands.AutoLaunch1(robot),
                    TeleCommands.GateUp(robot),
                    TeleCommands.Intake(robot),
                    TeleCommands.HoodUp(robot),
                    new PedroPathCommand(robot.follower, p.BStartToBLaunch),
                    Paths2.AutoLaunching3Balls(robot),
                    new PedroPathCommand(
                        robot.follower,
                        p.BLaunchToBInt1,
                        Paths2.power092
                    ).alongWith(TeleCommands.Intake(robot)),
                    new PedroPathCommand(robot.follower, p.BInt1ToBLaunch),
                    Paths2.AutoLaunching3Balls(robot),
                    BlueGateCycle1(robot).alongWith(TeleCommands.Intake(robot)),
                    BlueGateCycle2(robot).alongWith(TeleCommands.Intake(robot)),
                    new PedroPathCommand(
                        robot.follower,
                        p.BLaunchToBInt2,
                        Paths2.power085
                    ).alongWith(TeleCommands.Intake(robot)),
                    new PedroPathCommand(robot.follower, p.BInt2ToBLaunch),
                    Paths2.AutoLaunching3Balls(robot),
                    BlueGateCycle3(robot).alongWith(TeleCommands.Intake(robot)),
                    new PedroPathCommand(robot.follower, p.BLaunchToBEnd),
                    TeleCommands.StopLaunch(robot),
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
