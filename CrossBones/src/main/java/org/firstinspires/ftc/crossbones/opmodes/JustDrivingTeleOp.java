package org.firstinspires.ftc.crossbones.opmodes;

import static org.firstinspires.ftc.crossbones.Setup.HardwareNames.AprilTag_Pipeline;
import static org.firstinspires.ftc.crossbones.Setup.HardwareNames.LIMELIGHT;

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
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import java.util.Arrays;
import java.util.List;
import org.firstinspires.ftc.crossbones.AutoConstants;
import org.firstinspires.ftc.crossbones.Hardware;
import org.firstinspires.ftc.crossbones.Robot;
import org.firstinspires.ftc.crossbones.Setup;
import org.firstinspires.ftc.crossbones.commands.EZCmd;
import org.firstinspires.ftc.crossbones.controls.DriverController;
import org.firstinspires.ftc.crossbones.controls.OperatorController;
import org.firstinspires.ftc.crossbones.helpers.HeadingHelper;
import org.firstinspires.ftc.crossbones.helpers.StartingPosition;
import org.firstinspires.ftc.robotcore.external.Supplier;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose3D;

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

        controlsOperator = new OperatorController(codriverGamepad, robot);
        // SparkFunOTOS otos = hardwareMap.get(SparkFunOTOS.class, Setup.HardwareNames.OTOS);
        // otos.calibrateImu();
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

        if (Setup.Connected.LAUNCHER) {
            CommandScheduler.register(robot.launcherSubsystem);
        }
        if (Setup.Connected.FEED) {
            CommandScheduler.register(robot.feedingSubsystem);
        }
        telemetry.addData(">", "Robot Ready.  Press Play.");
        telemetry.update();
    }

    @Override
    public void uponStart() {
        robot.atStart();
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
