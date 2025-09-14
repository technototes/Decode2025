package org.firstinspires.ftc.twenty403.opmodes;

import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.LIMELIGHT;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.logger.Loggable;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;

import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.twenty403.AutoConstants;
import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.Setup;
import org.firstinspires.ftc.twenty403.commands.EZCmd;
import org.firstinspires.ftc.twenty403.controls.DriverController;
import org.firstinspires.ftc.twenty403.controls.SingleController;
import org.firstinspires.ftc.twenty403.helpers.StartingPosition;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
@TeleOp(name = "OneDriverTeleOp")
public class SingleDriverTeleOp extends CommandOpMode implements Loggable {

    public Robot robot;
    public Setup setup;
    public DriverController controls;
    public Hardware hardware;
    private Limelight3A limelight;

    /*
     * Barcode pipeline: 0
     * Color Pipeline: 1
     * Classifier Pipeline: 2
     * Object Detection Pipeline: 3
     * AprilTag Pipeline: 4
     * */

    @Override
    public void uponInit() {

        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.NONE, StartingPosition.Unspecified);
        controls = new DriverController(driverGamepad, robot);
        // CommandScheduler.scheduleForState(
        //     EZCmd.Drive.NormalMode(robot.drivebaseSubsystem), //was ResetGyro cmd
        //     OpModeState.INIT
        // );
        limelight = hardware.limelight;

        telemetry.setMsTransmissionInterval(11);

        limelight.pipelineSwitch(0);

        /*
         * Starts polling for data.  If you neglect to call start(), getLatestResult() will return null.
         */
        limelight.start();

        telemetry.addData(">", "Robot Ready.  Press Play.");
        telemetry.update();
    }

    @Override
    public void uponStart() {
        robot.atStart();
    }

    @Override
    public void runLoop() {
        controls.bindLaunchControls();
        controls.bindPipelineControls();
        LLStatus status = limelight.getStatus();
        telemetry.addData("Name", "%s", status.getName());
        telemetry.addData(
                "LL",
                "Temp: %.1fC, CPU: %.1f%%, FPS: %d",
                status.getTemp(),
                status.getCpu(),
                (int) status.getFps()
        );
        telemetry.addData(
                "Pipeline",
                "Index: %d, Type: %s",
                status.getPipelineIndex(),
                status.getPipelineType()
        );

        LLResult result = limelight.getLatestResult();
        if (result != null) {
            // Access general information
            Pose3D botpose = result.getBotpose();
            double captureLatency = result.getCaptureLatency();
            double targetingLatency = result.getTargetingLatency();
            double parseLatency = result.getParseLatency();
            telemetry.addData("LL Latency", captureLatency + targetingLatency);
            telemetry.addData("Parse Latency", parseLatency);
            telemetry.addData("PythonOutput", java.util.Arrays.toString(result.getPythonOutput()));

            if (result.isValid()) {
                telemetry.addData("tx", result.getTx());
                telemetry.addData("txnc", result.getTxNC());
                telemetry.addData("ty", result.getTy());
                telemetry.addData("tync", result.getTyNC());

                telemetry.addData("Botpose", botpose.toString());

                if (result.getPipelineIndex() == Setup.HardwareNames.Barcode_Pipeline) {
                    // Access barcode results
                    List<LLResultTypes.BarcodeResult> barcodeResults = result.getBarcodeResults();
                    for (LLResultTypes.BarcodeResult br : barcodeResults) {
                        telemetry.addData("Barcode", "Data: %s", br.getData());
                    }
                } else if (result.getPipelineIndex() == Setup.HardwareNames.Classifier_Pipeline) {
                    // Access classifier results
                    List<LLResultTypes.ClassifierResult> classifierResults =
                            result.getClassifierResults();
                    for (LLResultTypes.ClassifierResult cr : classifierResults) {
                        telemetry.addData(
                                "Classifier",
                                "Class: %s, Confidence: %.2f",
                                cr.getClassName(),
                                cr.getConfidence()
                        );
                    }
                } else if (result.getPipelineIndex() == Setup.HardwareNames.Object_Detection_Pipeline) {
                    // Access detector results
                    List<LLResultTypes.DetectorResult> detectorResults = result.getDetectorResults();
                    for (LLResultTypes.DetectorResult dr : detectorResults) {
                        telemetry.addData(
                                "Detector",
                                "Class: %s, Area: %.2f",
                                dr.getClassName(),
                                dr.getTargetArea()
                        );
                    }
                } else if (result.getPipelineIndex() == Setup.HardwareNames.AprilTag_Pipeline) {
                    // Access fiducial results
                    List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
                    for (LLResultTypes.FiducialResult fr : fiducialResults) {
                        if (fr.getFiducialId() == 23 && Arrays.equals(Setup.HardwareNames.Motif, new String[]{"1", "2", "3"})) {
                            Setup.HardwareNames.Motif[0] = "P";
                            Setup.HardwareNames.Motif[1] = "P";
                            Setup.HardwareNames.Motif[2] = "G";
                        } else if (fr.getFiducialId() == 22 && Arrays.equals(Setup.HardwareNames.Motif, new String[]{"1", "2", "3"})) {
                            Setup.HardwareNames.Motif[0] = "P";
                            Setup.HardwareNames.Motif[1] = "G";
                            Setup.HardwareNames.Motif[2] = "P";
                        } else if (fr.getFiducialId() == 21 && Arrays.equals(Setup.HardwareNames.Motif, new String[]{"1", "2", "3"})) {
                            Setup.HardwareNames.Motif[0] = "G";
                            Setup.HardwareNames.Motif[1] = "P";
                            Setup.HardwareNames.Motif[2] = "P";

                        }
                        telemetry.addData(
                                "Fiducial",
                                "ID: %d, Family: %s, X: %.2f, Y: %.2f",
                                fr.getFiducialId(),
                                fr.getFamily(),
                                fr.getTargetXDegrees(),
                                fr.getTargetYDegrees()
                        );
                        telemetry.addData("Motif:", Setup.HardwareNames.Motif);
                    }
                } else if (result.getPipelineIndex() == Setup.HardwareNames.Color_Pipeline) {
                    // Access color results
                    List<LLResultTypes.ColorResult> colorResults = result.getColorResults();
                    for (LLResultTypes.ColorResult cr : colorResults) {
                        telemetry.addData(
                                "Color",
                                "X: %.2f, Y: %.2f",
                                cr.getTargetXDegrees(),
                                cr.getTargetYDegrees()
                        );
                    }
                }
            }
        } else {
            telemetry.addData("Limelight", "No data available");
        }

        telemetry.update();
    }

    @Override
    public void end() {
        limelight.stop();
    }
}
