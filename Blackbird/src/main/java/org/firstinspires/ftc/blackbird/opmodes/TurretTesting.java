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
import org.firstinspires.ftc.blackbird.commands.auto.Paths;
import org.firstinspires.ftc.blackbird.commands.driving.DrivingCommands;
import org.firstinspires.ftc.blackbird.controls.DriverController;
import org.firstinspires.ftc.blackbird.controls.OperatorController;
import org.firstinspires.ftc.blackbird.controls.TestingController;
import org.firstinspires.ftc.blackbird.helpers.HeadingHelper;
import org.firstinspires.ftc.blackbird.helpers.StartingPosition;
import org.firstinspires.ftc.blackbird.subsystems.LauncherSubsystem;
import org.firstinspires.ftc.blackbird.subsystems.LimelightSubsystem;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

@TeleOp(name = "TurretTesting")
@SuppressWarnings("unused")
public class TurretTesting extends CommandOpMode {

    public Robot robot;
    public TestingController controlsDriver;
    public Hardware hardware;
    private PanelsTelemetry panelsTelemetry;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.NONE, StartingPosition.Unspecified);
        controlsDriver = new TestingController(driverGamepad, robot);
        CommandScheduler.register(robot.turretSubsystem);
        panelsTelemetry = PanelsTelemetry.INSTANCE;
    }

    @Override
    public void uponStart() {
        robot.prepForStart();
    }

    @Override
    public void runLoop() {
        // robot.turretSubsystem.setTurretPos(robot.turretSubsystem.turretPIDF.update(robot.turretSubsystem.getTurretPos()));
        // robot.turretSubsystem.turretAngle = robot.turretSubsystem.getEncoderAngleInDegrees();
        // robot.turretSubsystem.turretPow = robot.turretSubsystem.getTurretPow();
        // robot.turretSubsystem.turretTicks = robot.turretSubsystem.getTurretPos();
        telemetry.addData("Turret", robot.turretSubsystem.TurretSubsytemInfoToDS);
        panelsTelemetry.getTelemetry().addData("TurretPos", robot.turretSubsystem.turretTicks);
        panelsTelemetry.getTelemetry().update(telemetry);
        telemetry.update();
    }

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
    public void end() {}
}
