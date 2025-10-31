package org.firstinspires.ftc.learnbot.opmodes;

import static org.firstinspires.ftc.learnbot.Setup.HardwareNames.AprilTag_Pipeline;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.technototes.library.logger.Loggable;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import java.util.Arrays;
import java.util.List;
import org.firstinspires.ftc.learnbot.Hardware;
import org.firstinspires.ftc.learnbot.Robot;
import org.firstinspires.ftc.learnbot.Setup;
import org.firstinspires.ftc.learnbot.controls.DriverController;
import org.firstinspires.ftc.learnbot.helpers.StartingPosition;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

@Configurable
@SuppressWarnings("unused")
@TeleOp(name = "OneDriverTeleOp")
public class SingleDriverTeleOp extends CommandOpMode implements Loggable {

    public Robot robot;
    public Setup setup;
    public DriverController controls;
    public Hardware hardware;
    private Limelight3A limelight;
    // For Panels controller widget the two lines below
    private final GamepadManager driverManager = PanelsGamepad.INSTANCE.getFirstManager();
    private final TelemetryManager panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

    @Override
    public void uponInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.NONE, StartingPosition.Unspecified);
        controls = new DriverController(driverGamepad, robot);
        SparkFunOTOS otos = hardwareMap.get(SparkFunOTOS.class, Setup.HardwareNames.OTOS);
        otos.calibrateImu();
        if (Setup.Connected.LIMELIGHT) {
            limelight = hardware.limelight;
            limelight.setPollRateHz(100);

            telemetry.setMsTransmissionInterval(11);

            limelight.pipelineSwitch(AprilTag_Pipeline);

            /*
             * Starts polling for data.  If you neglect to call start(), getLatestResult() will return null.
             */
            limelight.start();
        }

        telemetry.addData(">", "Robot Ready.  Press Play.");
        telemetry.update();
    }

    @Override
    public void uponStart() {
        robot.atStart();
    }

    @Override
    public void runLoop() {
        LLStatus status = null;
        if (Setup.Connected.LIMELIGHT) {
            status = limelight.getStatus();
            limelight.updateRobotOrientation(hardware.imu.getHeadingInDegrees());
            controls.bindPipelineControls();
        }

        if (Setup.Connected.LIMELIGHT) {
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
                long staleness = result.getStaleness();
                if (staleness < 100) {
                    // Less than 100 milliseconds old
                    telemetry.addData("Data", "Good");
                } else {
                    telemetry.addData("Data", "Old (" + staleness + " ms)");
                }
                // Access general information
                Pose3D botpose = result.getBotpose_MT2();
                double captureLatency = result.getCaptureLatency();
                double targetingLatency = result.getTargetingLatency();
                double parseLatency = result.getParseLatency();
                telemetry.addData("LL Latency", captureLatency + targetingLatency);
                //                telemetry.addData("Parse Latency", parseLatency);

                if (result.isValid()) {
                    //                    telemetry.addData("tx", result.getTx());
                    //                    telemetry.addData("txnc", result.getTxNC());
                    //                    telemetry.addData("ty", result.getTy());
                    //                    telemetry.addData("tync", result.getTyNC());
                    //
                    //                    telemetry.addData("Botpose", botpose.toString());

                    if (result.getPipelineIndex() == Setup.HardwareNames.AprilTag_Pipeline) {
                        // Access fiducial results
                        List<LLResultTypes.FiducialResult> fiducialResults =
                            result.getFiducialResults();
                        for (LLResultTypes.FiducialResult fr : fiducialResults) {
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
                            telemetry.addData("Distance to AprilTag", String.valueOf(distance));
                        }
                    } else if (
                        result.getPipelineIndex() == Setup.HardwareNames.Green_Color_Pipeline
                    ) {
                        // Access color results
                        List<LLResultTypes.ColorResult> colorResults = result.getColorResults();
                        //                        for (LLResultTypes.ColorResult cr : colorResults) {
                        //
                        //                        }
                    } else if (
                        result.getPipelineIndex() == Setup.HardwareNames.Purple_Color_Pipeline
                    ) {
                        // Access color results
                        List<LLResultTypes.ColorResult> colorResults = result.getColorResults();
                        //                        for (LLResultTypes.ColorResult cr : colorResults) {
                        //
                        //                        }
                    }
                }
            } else {
                telemetry.addData("Limelight", "No data available");
            }
        }

        telemetry.update();
    }

    @Override
    public void end() {
        if (Setup.Connected.LIMELIGHT) {
            limelight.stop();
        }
    }
}
