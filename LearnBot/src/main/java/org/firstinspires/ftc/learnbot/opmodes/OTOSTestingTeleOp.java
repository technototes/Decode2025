package org.firstinspires.ftc.learnbot.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.technototes.library.hardware.HardwareDevice;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.learnbot.AutoConstants;
import org.firstinspires.ftc.learnbot.Hardware;
import org.firstinspires.ftc.learnbot.Robot;
import org.firstinspires.ftc.learnbot.controls.DriverController;
import org.firstinspires.ftc.learnbot.controls.OperatorController;
import org.firstinspires.ftc.learnbot.helpers.StartingPosition;
import org.firstinspires.ftc.learnbot.subsystems.DrivebaseSubsystem;

@TeleOp(name = "OTOS Testing TeleOp")
@SuppressWarnings("unused")
public class OTOSTestingTeleOp extends OpMode implements Loggable {

    public Robot robot;
    public DriverController controlsDriver;
    private boolean slowMode;
    public OperatorController controlsOperator;
    public Hardware hardware;
    private boolean automatedDrive;

    private TelemetryManager telemetryM;
    private Follower follower;
    public static Pose startingPose;
    public SparkFunOTOS otos;

    @Override
    public void init() {
        HardwareDevice.initMap(hardwareMap);
        otos = hardwareMap.get(SparkFunOTOS.class, "sparky");
        follower = AutoConstants.createFollower(hardwareMap);
        follower.setStartingPose(startingPose == null ? new Pose() : startingPose);
        follower.update();

        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        hardware = new Hardware(hardwareMap);
        follower = AutoConstants.createFollower(hardwareMap);
        robot = new Robot(hardware, Alliance.BLUE, StartingPosition.Unspecified);
        // controlsOperator = new OperatorController(codriverGamepad, robot);
        //    for pedro:    robot.getF().setStartingPose(whatever it is);
        //if (Setup.Connected.DRIVEBASE) {
        //            controlsDriver = new DriverController(driverGamepad, robot);
        //
        //            CommandScheduler.scheduleForState(
        //                EZCmd.Drive.ResetGyro(robot.drivebaseSubsystem),
        //                OpModeState.INIT
        //            );
        //        }
        follower = AutoConstants.createFollower(hardwareMap);
        follower.setStartingPose(startingPose == null ? new Pose() : startingPose);
        follower.update();

        //        telemetry.setMsTransmissionInterval(11);

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
}
