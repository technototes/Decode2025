package org.firstinspires.ftc.twenty403.opmodes;

//import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.LIMELIGHT;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.control.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
//import com.qualcomm.hardware.limelightvision.LLResultTypes;
//import com.qualcomm.hardware.limelightvision.LLStatus;
//import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.hardware.HardwareDevice;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.logger.Logger;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;
import org.firstinspires.ftc.twenty403.AutoConstants;
import org.firstinspires.ftc.twenty403.Constants;
import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.Setup;
import org.firstinspires.ftc.twenty403.commands.EZCmd;
import org.firstinspires.ftc.twenty403.controls.DriverController;
import org.firstinspires.ftc.twenty403.controls.OperatorController;
import org.firstinspires.ftc.twenty403.helpers.StartingPosition;
import org.firstinspires.ftc.twenty403.subsystems.DrivebaseSubsystem;

@TeleOp(name = "Driving w/Turbo!")
@SuppressWarnings("unused")
public class JustDrivingTeleOp extends OpMode implements Loggable {

    public Robot robot;
    public DriverController controlsDriver;
    private boolean slowMode;
    public OperatorController controlsOperator;
    public Hardware hardware;
    // private Limelight3A limelight;
    private boolean automatedDrive;

    private TelemetryManager telemetryM;
    private Follower follower;
    public static Pose startingPose;
    public SparkFunOTOS otos;

    @Override
    public void init() {
        HardwareDevice.initMap(hardwareMap);
        otos = hardwareMap.get(SparkFunOTOS.class, "sparky");
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startingPose == null ? new Pose() : startingPose);
        follower.update();

        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        hardware = new Hardware(hardwareMap);
        follower = Constants.createFollower(hardwareMap);
        robot = new Robot(hardware, Alliance.BLUE, StartingPosition.Unspecified);
        // controlsOperator = new OperatorController(codriverGamepad, robot);
        //    for pedro:    robot.getF().setStartingPose(whatever it is);
        //if (Setup.Connected.DRIVEBASE) {
        //            controlsDriver = new DriverController(driverGamepad, robot);
        //
        //            // CommandScheduler.scheduleForState(
        //            //     EZCmd.Drive.ResetGyro(robot.drivebaseSubsystem),
        //            //     OpModeState.INIT
        //            // );
        //        }
        follower = Constants.createFollower(hardwareMap);
        follower.setStartingPose(startingPose == null ? new Pose() : startingPose);
        follower.update();
        //limelight = hardwareMap.get(Limelight3A.class, LIMELIGHT);

        //        telemetry.setMsTransmissionInterval(11);

        //limelight.pipelineSwitch(0);

        /*
         * Starts polling for data.  If you neglect to call start(), getLatestResult() will return null.
         */
        // limelight.start();

        telemetry.addData(">", "Robot Ready.  Press Play.");
        telemetry.update();
        follower.startTeleopDrive();
        robot.atStart();
    }

    @Override
    public void loop() {
        telemetryM.addData("LaserPos", otos.getPosition());

        follower.update();
        telemetryM.update();
        follower.update();

        if (!automatedDrive) {
            //Make the last parameter false for field-centric
            //In case the drivers want to use a "slowMode" you can scale the vectors
            //This is the normal version to use in the TeleOp
            if (!slowMode) follower.setTeleOpDrive(
                -gamepad1.left_stick_y,
                -gamepad1.left_stick_x,
                -gamepad1.right_stick_x,
                false
            );
            //This is how it looks with slowMode on
            else follower.setTeleOpDrive(
                -gamepad1.left_stick_y * DrivebaseSubsystem.DriveConstants.SLOW_MOTOR_SPEED,
                -gamepad1.left_stick_x * DrivebaseSubsystem.DriveConstants.SLOW_MOTOR_SPEED,
                -gamepad1.right_stick_x * DrivebaseSubsystem.DriveConstants.SLOW_MOTOR_SPEED,
                false
            );
        }
        //Automated PathFollowing
        //        if (gamepad1.aWasPressed()) {
        //            follower.followPath(pathChain.get());
        //            automatedDrive = true;
        //        }
        //Stop automated following if the follower is done
        if (automatedDrive && (gamepad1.bWasPressed() || !follower.isBusy())) {
            follower.startTeleopDrive();
            automatedDrive = false;
        }
        //Slow Mode
        if (gamepad1.rightBumperWasPressed()) {
            slowMode = !slowMode;
        }
    }
    //  LLStatus status = limelight.getStatus();
    //        telemetry.addData("Name", "%s", status.getName());
    //        telemetry.addData(
    //                "LL",
    //                "Temp: %.1fC, CPU: %.1f%%, FPS: %d",
    //                status.getTemp(),
    //                status.getCpu(),
    //                (int) status.getFps()
    //        );
    //        telemetry.addData(
    //                "Pipeline",
    //                "Index: %d, Type: %s",
    //                status.getPipelineIndex(),
    //                status.getPipelineType()
    //        );

    //LLResult result = limelight.getLatestResult();
    //        if (result != null) {
    //            // Access general information
    //            Pose3D botpose = result.getBotpose();
    //            double captureLatency = result.getCaptureLatency();
    //            double targetingLatency = result.getTargetingLatency();
    //            double parseLatency = result.getParseLatency();
    //            telemetry.addData("LL Latency", captureLatency + targetingLatency);
    //            telemetry.addData("Parse Latency", parseLatency);
    //            telemetry.addData("PythonOutput", java.util.Arrays.toString(result.getPythonOutput()));
    //
    //            if (result.isValid()) {
    //                telemetry.addData("tx", result.getTx());
    //                telemetry.addData("txnc", result.getTxNC());
    //                telemetry.addData("ty", result.getTy());
    //                telemetry.addData("tync", result.getTyNC());
    //
    //                telemetry.addData("Botpose", botpose.toString());
    //
    //                // Access barcode results
    //                List<LLResultTypes.BarcodeResult> barcodeResults = result.getBarcodeResults();
    //                for (LLResultTypes.BarcodeResult br : barcodeResults) {
    //                    telemetry.addData("Barcode", "Data: %s", br.getData());
    //                }
    //
    //                // Access classifier results
    //                List<LLResultTypes.ClassifierResult> classifierResults =
    //                        result.getClassifierResults();
    //                for (LLResultTypes.ClassifierResult cr : classifierResults) {
    //                    telemetry.addData(
    //                            "Classifier",
    //                            "Class: %s, Confidence: %.2f",
    //                            cr.getClassName(),
    //                            cr.getConfidence()
    //                    );
    //                }
    //
    //                // Access detector results
    //                List<LLResultTypes.DetectorResult> detectorResults = result.getDetectorResults();
    //                for (LLResultTypes.DetectorResult dr : detectorResults) {
    //                    telemetry.addData(
    //                            "Detector",
    //                            "Class: %s, Area: %.2f",
    //                            dr.getClassName(),
    //                            dr.getTargetArea()
    //                    );
    //                }
    //
    //                // Access fiducial results
    //                List<LLResultTypes.FiducialResult> fiducialResults = result.getFiducialResults();
    //                for (LLResultTypes.FiducialResult fr : fiducialResults) {
    //                    telemetry.addData(
    //                            "Fiducial",
    //                            "ID: %d, Family: %s, X: %.2f, Y: %.2f",
    //                            fr.getFiducialId(),
    //                            fr.getFamily(),
    //                            fr.getTargetXDegrees(),
    //                            fr.getTargetYDegrees()
    //                    );
    //                }
    //
    //                // Access color results
    //                List<LLResultTypes.ColorResult> colorResults = result.getColorResults();
    //                for (LLResultTypes.ColorResult cr : colorResults) {
    //                    telemetry.addData(
    //                            "Color",
    //                            "X: %.2f, Y: %.2f",
    //                            cr.getTargetXDegrees(),
    //                            cr.getTargetYDegrees()
    //                    );
    //                }
    //            }
    //        } else {
    //            telemetry.addData("Limelight", "No data available");
    //        }
    //
    //        telemetry.update();
}

//
// @Override//
//    @Override
//    public void end() {
//        limelight.stop();
//    }
//
//    @Override
//    public Logger getLogger() {
//        return super.getLogger();
//    }
//    public void stop() {
//        limelight.stop();
//    }
//
//    @Override
//    public Logger getLogger() {
//        return super.getLogger();
//    }
