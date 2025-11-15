package org.firstinspires.ftc.twenty403.opmodes;

import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.AprilTag_Pipeline;
import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.LIMELIGHT;

import android.app.appsearch.SearchResult;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.LLStatus;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import java.util.Arrays;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.Supplier;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.twenty403.AutoConstants;
import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.Setup;
import org.firstinspires.ftc.twenty403.commands.EZCmd;
import org.firstinspires.ftc.twenty403.controls.DriverController;
import org.firstinspires.ftc.twenty403.controls.OperatorController;
import org.firstinspires.ftc.twenty403.helpers.StartingPosition;

// unicode is moai emoji
@Configurable
@TeleOp(name = "Two Controller Drive \uD83D\uDDFF")
@SuppressWarnings("unused")
public class JustDrivingTeleOp extends CommandOpMode {

    public Robot robot;
    public DriverController controlsDriver;
    public OperatorController controlsOperator;
    public Hardware hardware;
    // For Panels controller widget the three lines below
    private final GamepadManager driverManager = PanelsGamepad.INSTANCE.getFirstManager();
    private final GamepadManager operatorManager = PanelsGamepad.INSTANCE.getSecondManager();
    private final TelemetryManager panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

    private Limelight3A limelight;
    public static Pose startingPose; //See ExampleAuto to understand how to use this
    private Supplier<PathChain> pathChain;
    private TelemetryManager telemetryM;
    private double slowModeMultiplier = 0.5;

    /*
     * Barcode pipeline: 0
     * Color Pipeline: 1
     * Classifier Pipeline: 2
     * Object Detection Pipeline: 3
     * AprilTag Pipeline: 4
     * */

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.BLUE, StartingPosition.Unspecified);
        robot.follower = AutoConstants.createFollower(hardwareMap);
        robot.follower.setStartingPose(robot.follower.getPose());
        robot.follower.update();
        controlsOperator = new OperatorController(codriverGamepad, robot);
        SparkFunOTOS otos = hardwareMap.get(SparkFunOTOS.class, Setup.HardwareNames.OTOS);
        otos.calibrateImu();
        controlsDriver = new DriverController(driverGamepad, robot);
        if (Setup.Connected.LIMELIGHT) {
            limelight = hardwareMap.get(Limelight3A.class, LIMELIGHT);
            limelight.setPollRateHz(100);

            telemetry.setMsTransmissionInterval(11);

            limelight.pipelineSwitch(AprilTag_Pipeline);
        }

        /*
         * Starts polling for data.  If you neglect to call start(), getLatestResult() will return null.
         */
        if (Setup.Connected.LIMELIGHT) {
            limelight.start();
        }
        // use only if useing Move foward auto that relys on just setting motor powers
        robot.follower.setPose(
            new Pose(
                robot.follower.getPose().getX(),
                robot.follower.getPose().getY(),
                robot.follower.getHeading() - 90
            )
        );
        if (Setup.Connected.LAUNCHER){
            CommandScheduler.register(robot.launcherSubsystem);
        }
        telemetry.addData(">", "Robot Ready.  Press Play.");
        telemetry.update();
    }

    @Override
    public void uponStart() {
        robot.follower.startTeleopDrive();
        robot.atStart();
    }

    @Override
    public void runLoop() {
        robot.follower.update();

        robot.follower.setTeleOpDrive(
            -gamepad1.left_stick_y,
            -gamepad1.left_stick_x,
            -gamepad1.right_stick_x,
            false // Robot Centric
        );
        LLStatus status = null;
        if (Setup.Connected.LIMELIGHT) {
            status = limelight.getStatus();
            limelight.updateRobotOrientation(hardware.imu.getHeadingInDegrees());
            controlsDriver.bindPipelineControls();
        }

        if (Setup.Connected.LAUNCHER) {
            //robot.launcherSubsystem.RunLoop(telemetry);
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

                            // distance to apriltag
                            double distance = calculateDistanceFromPose(fiducialResults.get(0));
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
        panelsTelemetry.update(telemetry);
        telemetry.update();
    }

    @Override
    public void end() {
        if (Setup.Connected.LIMELIGHT) {
            limelight.stop();
        }
    }

    /**
     * Calculate distance using the target's 3D pose data (most accurate method)
     * Uses the robot pose relative to the AprilTag coordinate system
     */
    private double calculateDistanceFromPose(LLResultTypes.FiducialResult target) {
        try {
            // Get robot pose relative to the AprilTag coordinate system
            Pose3D robotPoseTargetSpace = target.getRobotPoseTargetSpace();

            if (robotPoseTargetSpace != null) {
                // Calculate 3D distance from robot to AprilTag
                double x = robotPoseTargetSpace.getPosition().x;
                double y = robotPoseTargetSpace.getPosition().y;
                double z = robotPoseTargetSpace.getPosition().z;

                // 3D distance calculation
                double distance = Math.sqrt(x * x + y * y + z * z);

                // Convert from meters to inches (Limelight uses meters)
                distance = distance * 39.3701;
                // cover for error
                distance += 14;

                // Sanity check - reject unreasonable values
                if (distance > 0 && distance < 500) {
                    // Reasonable range in inches
                    return distance;
                }
            }
        } catch (Exception e) {
            telemetry.addData("Could not get distance", e.getMessage());
        }

        return -1; // Invalid result
    }
}
