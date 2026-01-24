package org.firstinspires.ftc.blackbird.opmodes;

import static org.firstinspires.ftc.blackbird.Setup.HardwareNames.AprilTag_Pipeline;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.technototes.library.command.Command;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import java.util.Arrays;
import java.util.List;
import org.firstinspires.ftc.blackbird.Hardware;
import org.firstinspires.ftc.blackbird.Robot;
import org.firstinspires.ftc.blackbird.Setup;
import org.firstinspires.ftc.blackbird.commands.TeleCommands;
import org.firstinspires.ftc.blackbird.commands.auto.Paths;
import org.firstinspires.ftc.blackbird.commands.driving.DrivingCommands;
import org.firstinspires.ftc.blackbird.controls.DriverController;
import org.firstinspires.ftc.blackbird.controls.OperatorController;
import org.firstinspires.ftc.blackbird.helpers.HeadingHelper;
import org.firstinspires.ftc.blackbird.helpers.StartingPosition;
import org.firstinspires.ftc.blackbird.subsystems.LauncherSubsystem;
import org.firstinspires.ftc.blackbird.subsystems.LimelightSubsystem;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

@TeleOp(name = "Dual Control")
@SuppressWarnings("unused")
public class DualTeleOp extends CommandOpMode {

    public Robot robot;
    public OperatorController controlsOperator;
    public DriverController controlsDriver;
    public Hardware hardware;
    private Limelight3A limelight;
    private PanelsTelemetry panelsTelemetry;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.NONE, StartingPosition.Unspecified);
        controlsOperator = new OperatorController(codriverGamepad, robot);
        panelsTelemetry = PanelsTelemetry.INSTANCE;
        robot.follower.setStartingPose(Paths.getRSegmentedCurveStart());
        // limelight = hardwareMap.get(Limelight3A.class, Setup.HardwareNames.LIMELIGHT);
        if (Setup.Connected.DRIVEBASE) {
            controlsDriver = new DriverController(driverGamepad, robot);
            robot.intakeSubsystem.setGamepad(gamepad1);
            // Just pick a starting point
            CommandScheduler.scheduleForState(
                new SequentialCommandGroup(
                    HeadingHelper.RestorePreviousPosition(robot.follower),
                    DrivingCommands.ResetGyro(controlsDriver.pedroDriver),
                    TeleCommands.SetRegressionCTeleop(robot),
                    TeleCommands.SetRegressionDTeleop(robot)
                ),
                OpModeState.INIT
            );
            // CommandScheduler.scheduleForState(
            //         TeleCommand aas.Intake(robot.intakeSubsystem),
            //         OpModeState.RUN
            // );
        }
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

    @Override
    public void uponStart() {
        robot.prepForStart();
    }

    /*
    @Override
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
    */

    /*
    @Override
    public void runLoop() {
        LLStatus status = null;
        if (Setup.Connected.LIMELIGHTSUBSYSTEM) {
            status = limelight.getStatus();
            limelight.updateRobotOrientation(hardware.imu.getHeadingInDegrees());
        }

        if (Setup.Connected.LIMELIGHTSUBSYSTEM) {
            // here
            telemetry.addData("Name", "%s", status.getName());

            telemetry.addData(
                "Motif:",
                Setup.HardwareNames.Motif[0] +
                    " " +
                    Setup.HardwareNames.Motif[1] +
                    " " +
                    Setup.HardwareNames.Motif[2]
            );
            telemetry.addData(
                "Pipeline",
                "Index: %d, Type: %s",
                status.getPipelineIndex(),
                status.getPipelineType()
            );

            LLResult result = limelight.getLatestResult();

            if (result != null) {
                if (result.isValid()) {
                    if (result.getPipelineIndex() == Setup.HardwareNames.AprilTag_Pipeline) {
                        // Access fiducial results
                        List<LLResultTypes.FiducialResult> fiducialResults =
                            result.getFiducialResults();
                        for (LLResultTypes.FiducialResult fr : fiducialResults) {
                            int tag_id = fr.getFiducialId();
                            telemetry.addData("Tag Id", tag_id);
                            if (
                                fr.getFiducialId() == 23 &&
                                Arrays.equals(
                                    Setup.HardwareNames.Motif,
                                    new String[] { "1", "2", "3" }
                                )
                            ) {
                                Setup.HardwareNames.Motif[0] = "\uD83D\uDFE3";
                                Setup.HardwareNames.Motif[1] = "\uD83D\uDFE3";
                                Setup.HardwareNames.Motif[2] = "\uD83D\uDFE2";
                            } else if (
                                fr.getFiducialId() == 22 &&
                                Arrays.equals(
                                    Setup.HardwareNames.Motif,
                                    new String[] { "1", "2", "3" }
                                )
                            ) {
                                Setup.HardwareNames.Motif[0] = "\uD83D\uDFE3";
                                Setup.HardwareNames.Motif[1] = "\uD83D\uDFE2";
                                Setup.HardwareNames.Motif[2] = "\uD83D\uDFE3";
                            } else if (
                                fr.getFiducialId() == 21 &&
                                Arrays.equals(
                                    Setup.HardwareNames.Motif,
                                    new String[] { "1", "2", "3" }
                                )
                            ) {
                                Setup.HardwareNames.Motif[0] = "\uD83D\uDFE2";
                                Setup.HardwareNames.Motif[1] = "\uD83D\uDFE3";
                                Setup.HardwareNames.Motif[2] = "\uD83D\uDFE3";
                            }
                            Pose3D targetPose = fr.getCameraPoseTargetSpace();
                            double tx = targetPose.getPosition().x;
                            double ty = targetPose.getPosition().y;
                            double tz = targetPose.getPosition().z;
                            // supposedly distance to apriltag
                            double distance = Math.sqrt(tx * tx + ty * ty + tz * tz);
                            if (tag_id == 21 || tag_id == 22 || tag_id == 23) {
                                telemetry.addData(
                                    "Distance to AprilTag (Obelisk)",
                                    String.valueOf(distance)
                                );
                            } else if (tag_id == 20) {
                                telemetry.addData(
                                    "Distance to AprilTag (Blue)",
                                    String.valueOf(distance)
                                );
                            } else if (tag_id == 24) {
                                telemetry.addData(
                                    "Distance to AprilTag (Red)",
                                    String.valueOf(distance)
                                );
                            }
                        }
                    }
                }
            } else {
                telemetry.addData("Limelight", "No data available");
            }
        }

        telemetry.update();
    }
    */

    @Override
    public void end() {
        if (Setup.Connected.LIMELIGHTSUBSYSTEM) {
            limelight.stop();
        }
    }
}
