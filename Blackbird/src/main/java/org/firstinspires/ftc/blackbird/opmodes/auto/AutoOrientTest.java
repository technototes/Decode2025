package org.firstinspires.ftc.blackbird.opmodes.auto;

import static org.firstinspires.ftc.blackbird.Setup.HardwareNames.AprilTag_Pipeline;

import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.blackbird.Hardware;
import org.firstinspires.ftc.blackbird.Robot;
import org.firstinspires.ftc.blackbird.Setup;
import org.firstinspires.ftc.blackbird.commands.AltAutoOrient;
import org.firstinspires.ftc.blackbird.commands.LLSetup;
import org.firstinspires.ftc.blackbird.commands.PedroPathCommand;
import org.firstinspires.ftc.blackbird.commands.TeleCommands;
import org.firstinspires.ftc.blackbird.commands.auto.Paths;
import org.firstinspires.ftc.blackbird.controls.DriverController;
import org.firstinspires.ftc.blackbird.helpers.HeadingHelper;
import org.firstinspires.ftc.blackbird.helpers.StartingPosition;
import org.firstinspires.ftc.blackbird.subsystems.LimelightSubsystem;

@Autonomous(name = "AutoOrientTest", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class AutoOrientTest extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;
    public LimelightSubsystem ls;
    private Limelight3A limelight;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        ls = robot.limelightSubsystem;
        Paths p = new Paths(robot.follower);
        robot.follower.setStartingPose(new Pose(80, 35, 45));
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
            ls.getLatestResult();
        }
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                TeleCommands.Intake(robot),
                new AltAutoOrient(robot),
                new WaitCommand(15),
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
