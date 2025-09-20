package org.firstinspires.ftc.twenty403.opmodes;

import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.AprilTag_Pipeline;
import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.LIMELIGHT;
import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.Motif;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;
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
    // For Panels controller widget the two lines below
    private final GamepadManager driverManager = PanelsGamepad.INSTANCE.getFirstManager();
    private final TelemetryManager panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();


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
        limelight.setPollRateHz(100);

        telemetry.setMsTransmissionInterval(11);

        limelight.pipelineSwitch(AprilTag_Pipeline);

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
        LLStatus status = null;
        if (Setup.Connected.LIMELIGHT) {
            status = limelight.getStatus();
            limelight.updateRobotOrientation(hardware.imu.getHeadingInDegrees());
            if (Setup.Connected.LAUNCHER) {
                controls.bindLaunchControls();
            }
            controls.bindPipelineControls();
        }
        // For Panels controller widget until
        Gamepad Driver = driverManager.asCombinedFTCGamepad(gamepad1);
        panelsTelemetry.debug("==== Buttons ====");
        panelsTelemetry.debug("A: " + Driver.a);
        panelsTelemetry.debug("B: " + Driver.b);
        panelsTelemetry.debug("X: " + Driver.x);
        panelsTelemetry.debug("Y: " + Driver.y);
        panelsTelemetry.debug("DPad Up: " + Driver.dpad_up);
        panelsTelemetry.debug("DPad Down: " + Driver.dpad_down);
        panelsTelemetry.debug("DPad Left: " + Driver.dpad_left);
        panelsTelemetry.debug("DPad Right: " + Driver.dpad_right);
        panelsTelemetry.debug("Left Bumper: " + Driver.left_bumper);
        panelsTelemetry.debug("Right Bumper: " + Driver.right_bumper);
        panelsTelemetry.debug("Left Trigger: " + Driver.left_trigger);
        panelsTelemetry.debug("Right Trigger: " + Driver.right_trigger);
        panelsTelemetry.debug("Start / Options: " + Driver.options);
        panelsTelemetry.debug("Back / Share: " + Driver.back);
        panelsTelemetry.debug("Guide / PS: " + Driver.guide);
        panelsTelemetry.debug("Touchpad: " + Driver.touchpad);
        panelsTelemetry.debug("Left Stick Button: " + Driver.left_stick_button);
        panelsTelemetry.debug("Right Stick Button: " + Driver.right_stick_button);
        panelsTelemetry.debug("==== Sticks ====");
        panelsTelemetry.debug("Left Stick X: " + Driver.left_stick_x);
        panelsTelemetry.debug("Left Stick Y: " + Driver.left_stick_y);
        panelsTelemetry.debug("Right Stick X: " + Driver.right_stick_x);
        panelsTelemetry.debug("Right Stick Y: " + Driver.right_stick_y);
        // here

        if (Setup.Connected.LIMELIGHT) {
            // here
            telemetry.addData("Name", "%s", status.getName());
            telemetry.addData("Motif:", Setup.HardwareNames.Motif[0] + " " + Setup.HardwareNames.Motif[1] + " " + Setup.HardwareNames.Motif[2]);
            telemetry.addData(
                    "Pipeline",
                    "Index: %d, Type: %s",
                    status.getPipelineIndex(),
                    status.getPipelineType()
            );

            LLResult result = limelight.getLatestResult();

            if (result != null) {
                long staleness = result.getStaleness();
                if (staleness < 100) { // Less than 100 milliseconds old
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
                        List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
                        for (LLResultTypes.FiducialResult fr : fiducialResults) {
                            if (fr.getFiducialId() == 23 && Arrays.equals(Setup.HardwareNames.Motif, new String[]{"1", "2", "3"})) {
                                Setup.HardwareNames.Motif[0] = "\uD83D\uDFE3";
                                Setup.HardwareNames.Motif[1] = "\uD83D\uDFE3";
                                Setup.HardwareNames.Motif[2] = "\uD83D\uDFE2";
                            } else if (fr.getFiducialId() == 22 && Arrays.equals(Setup.HardwareNames.Motif, new String[]{"1", "2", "3"})) {
                                Setup.HardwareNames.Motif[0] = "\uD83D\uDFE3";
                                Setup.HardwareNames.Motif[1] = "\uD83D\uDFE2";
                                Setup.HardwareNames.Motif[2] = "\uD83D\uDFE3";
                            } else if (fr.getFiducialId() == 21 && Arrays.equals(Setup.HardwareNames.Motif, new String[]{"1", "2", "3"})) {
                                Setup.HardwareNames.Motif[0] = "\uD83D\uDFE2";
                                Setup.HardwareNames.Motif[1] = "\uD83D\uDFE3";
                                Setup.HardwareNames.Motif[2] = "\uD83D\uDFE3";

                            }
                            Pose3D targetPose = fr.getCameraPoseTargetSpace();
                            double tx = targetPose.getPosition().x;
                            double ty = targetPose.getPosition().y;
                            double tz = targetPose.getPosition().z;
                            // supposedly distance to apriltag
                            double distance = Math.sqrt(tx*tx + ty*ty + tz*tz);
                            telemetry.addData("Distance to AprilTag", String.valueOf(distance));

                        }
                    } else if (result.getPipelineIndex() == Setup.HardwareNames.Green_Color_Pipeline) {
                        // Access color results
                        List<LLResultTypes.ColorResult> colorResults = result.getColorResults();
//                        for (LLResultTypes.ColorResult cr : colorResults) {
//
//                        }

                    } else if (result.getPipelineIndex() == Setup.HardwareNames.Purple_Color_Pipeline) {
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
