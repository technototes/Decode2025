package org.firstinspires.ftc.sixteen750.opmodes.auto;

import static org.firstinspires.ftc.sixteen750.Setup.HardwareNames.AprilTag_Pipeline;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.HeadingHelper;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.commands.PedroPathCommand;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.auto.Paths;
import org.firstinspires.ftc.sixteen750.controls.DriverController;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;
import org.firstinspires.ftc.sixteen750.subsystems.LauncherSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.LauncherSubsystem.LauncherCommand;

@Autonomous(name = "RedNearLever1️⃣2️⃣", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class RedNearFirstandSecondLever extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;
    private PanelsTelemetry panelsTelemetry;
    private Limelight3A limelight;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        Paths p = new Paths(robot.follower);
        panelsTelemetry = PanelsTelemetry.INSTANCE;
        robot.follower.setStartingPose(p.getRSegmentedCurveStart());
        CommandScheduler.scheduleForState(
            LauncherCommand.AutoVelocity().alongWith(
                new SequentialCommandGroup(
                    //TeleCommands.AutoLaunch1(robot),
                    TeleCommands.GateUp(robot),
                    TeleCommands.Intake(robot),
                    TeleCommands.HoodUp(robot),
                    new PedroPathCommand(robot.follower, p.RStarttoLaunch, p.power2),
                    Paths.AutoLaunching3Balls(robot),
                    // new WaitCommand(0.5),
                    new PedroPathCommand(robot.follower, p.RLaunchtoIntake1),
                    // new WaitCommand(1),
                    new PedroPathCommand(robot.follower, p.RIntake1toIntake1end, p.power).alongWith(
                        TeleCommands.Intake(robot)
                    ),
                    new WaitCommand(0.05),
                    new PedroPathCommand(robot.follower, p.RIntake1endtoLever).alongWith(
                        TeleCommands.IntakeStop(robot)
                    ),
                    new WaitCommand(0.5),
                    new PedroPathCommand(robot.follower, p.RLevertoLaunch, p.power2),
                    new WaitCommand(0.05),
                    Paths.AutoLaunching3Balls(robot),
                    new PedroPathCommand(robot.follower, p.RLaunchtoIntake2),
                    new PedroPathCommand(robot.follower, p.RIntake2toIntake2end, p.power).alongWith(
                        TeleCommands.Intake(robot)
                    ),
                    new WaitCommand(0.05),
                    //TeleCommands.AutoLaunch2(robot),

                    new PedroPathCommand(robot.follower, p.RIntake2endtoLever2).alongWith(
                        TeleCommands.IntakeStop(robot)
                    ),
                    new WaitCommand(0.5),
                    new PedroPathCommand(robot.follower, p.RLever2toLaunch),
                    new WaitCommand(0.05),
                    Paths.AutoLaunching3Balls(robot),
                    new PedroPathCommand(robot.follower, p.RLaunchtoIntake3),
                    new PedroPathCommand(robot.follower, p.RIntake3toIntake3end, p.power).alongWith(
                        TeleCommands.Intake(robot)
                    ),
                    new WaitCommand(0.1),
                    new PedroPathCommand(robot.follower, p.RIntake3endtoLaunch).alongWith(
                        TeleCommands.IntakeStop(robot)
                    ),
                    Paths.AutoLaunching3Balls(robot),
                    new PedroPathCommand(robot.follower, p.RLaunchtoEnd, p.power2),
                    TeleCommands.IntakeStop(robot),
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
