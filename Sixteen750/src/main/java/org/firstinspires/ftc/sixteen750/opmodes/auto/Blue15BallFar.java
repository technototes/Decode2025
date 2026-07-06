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
import org.firstinspires.ftc.sixteen750.commands.AltAutoVelocity;
import org.firstinspires.ftc.sixteen750.commands.AutoCommands;
import org.firstinspires.ftc.sixteen750.commands.PedroPathCommand;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.auto.BPaths;
import org.firstinspires.ftc.sixteen750.commands.auto.Poses;
import org.firstinspires.ftc.sixteen750.commands.auto.RPaths;
import org.firstinspires.ftc.sixteen750.controls.DriverController;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;

@Autonomous(name = "Blue15BallFar", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class Blue15BallFar extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    private PanelsTelemetry panelsTelemetry;
    private Limelight3A limelight;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.BLUE, StartingPosition.Net);
        BPaths p = new BPaths(robot.follower);
        TeleCommands t = new TeleCommands();
        AutoCommands a = new AutoCommands();
        robot.follower.setStartingPose(Poses.StartPoses.getBFStart());
        CommandScheduler.scheduleForState(
            new AltAutoVelocity(robot).alongWith(
                new SequentialCommandGroup(
                    t.GateUp(robot),
                    t.HoodUp(robot),
                    new PedroPathCommand(robot.follower, p.BFStartToBFLaunch),
                    new WaitCommand(0.7),
                    a.AutoLaunching3BallsFar(robot),
                    new PedroPathCommand(robot.follower, p.BFLaunchToBFInt1),
                    new PedroPathCommand(robot.follower, p.BFInt1ToBFLaunch),
                    a.AutoLaunching3BallsFar(robot),
                    new PedroPathCommand(robot.follower, p.BFLaunchToBFInt2),
                    new PedroPathCommand(robot.follower, p.BFInt2ToBFLaunch),
                    a.AutoLaunching3BallsFar(robot),
                    new PedroPathCommand(robot.follower, p.BFLaunchToBFInt3),
                    new PedroPathCommand(robot.follower, p.BFInt3ToBFLaunch),
                    a.AutoLaunching3BallsFar(robot),
                    new PedroPathCommand(robot.follower, p.BFLaunchToBFInt3),
                    new PedroPathCommand(robot.follower, p.BFInt3ToBFLaunch),
                    a.AutoLaunching3BallsFar(robot),
                    new PedroPathCommand(robot.follower, p.BFLaunchToBFEnd),
                    t.StopLaunch(robot),
                    t.IntakeStop(robot),
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

    public void runLoop() {}

    public void end() {
        HeadingHelper.savePose(robot.follower.getPose());
        if (Setup.Connected.LIMELIGHTSUBSYSTEM) {
            limelight.stop();
        }
    }
}
