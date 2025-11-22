package org.firstinspires.ftc.twenty403.controls;

import static org.firstinspires.ftc.twenty403.Setup.HardwareNames.LIMELIGHT;

import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.CycleCommandGroup;
import com.technototes.library.control.CommandAxis;
import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import com.technototes.library.control.Stick;
import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.Setup;
import org.firstinspires.ftc.twenty403.commands.EZCmd;
import org.firstinspires.ftc.twenty403.commands.FeedCMD;
import org.firstinspires.ftc.twenty403.commands.LLPipelineChangeCommand;
import org.firstinspires.ftc.twenty403.commands.auto.DriveAutoCommand;
import org.firstinspires.ftc.twenty403.commands.driving.JoystickDriveCommand;
import org.firstinspires.ftc.twenty403.subsystems.LauncherSubsystem;

public class DriverController {

    public Robot robot;
    public CommandGamepad gamepad;
    public Hardware hardware;

    public Stick driveLeftStick, driveRightStick;
    public CommandButton resetGyroButton;
    public CommandButton turboButton;
    public CommandButton snailButton;
    public CommandAxis straightTrigger;
    public CommandAxis angleTrigger;
    public CommandButton launch;
    public CommandButton launchFaster;
    public CommandButton launchSlower;
    public CommandButton moveballup;
    public CommandButton moveballslow;
    public CommandButton pipelineMode;
    public CommandButton compactScore;
    public CommandButton barcodePipeline;
    public CommandButton GreencolorPipeline;
    public CommandButton moveballanyways;
    public CommandButton classifierPipeline;
    public CommandButton objectPipeline;
    public CommandButton apriltagPipeline;
    public CommandButton PurplecolorPipeline;
    public CommandButton AutoAim;
    public static boolean pipelineToggle = false;
    public static boolean launchOn = false;
    private boolean faceTagMode = false;

    public void togglePipelineMode() {
        pipelineToggle = !pipelineToggle;
    }

    public DriverController(CommandGamepad g, Robot r) {
        this.robot = r;
        gamepad = g;

        AssignNamedControllerButton();
        if (Setup.Connected.DRIVEBASE) {
            bindDriveControls();
        }
        if (Setup.Connected.LAUNCHER) {
            bindLaunchControls();
        }
        if (Setup.Connected.FEED) {
            bindFeedControls();
        }
        if (Setup.Connected.LIMELIGHT) {
            bindPipelineControls();
        }
    }

    private void AssignNamedControllerButton() {
        resetGyroButton = gamepad.ps_options;
        driveLeftStick = gamepad.leftStick;
        driveRightStick = gamepad.rightStick;
        turboButton = gamepad.rightBumper;
        snailButton = gamepad.leftBumper;
        straightTrigger = gamepad.rightTrigger;
        angleTrigger = gamepad.leftTrigger;
        moveballup = gamepad.ps_square;
        launch = gamepad.ps_triangle;
//        pipelineMode = gamepad.dpadUp;
        launchSlower = gamepad.ps_cross;
        launchFaster = gamepad.ps_circle;
//        apriltagPipeline = gamepad.dpadRight;
//        AutoAim = gamepad.dpadDown;
        moveballanyways = gamepad.dpadDown;
        compactScore = gamepad.dpadLeft;

    }

    public void bindDriveControls() {
        CommandScheduler.scheduleJoystick(
            new JoystickDriveCommand(
                robot.follower,
                driveLeftStick,
                driveRightStick,
                straightTrigger,
                angleTrigger
            )
        );

        turboButton.whenPressed(EZCmd.Drive.TurboMode(robot.follower));
        turboButton.whenReleased(EZCmd.Drive.NormalMode(robot.follower));

        snailButton.whenPressed(EZCmd.Drive.SnailMode(robot.follower));
        snailButton.whenReleased(EZCmd.Drive.NormalMode(robot.follower));
        if (Setup.Connected.LIMELIGHT) {
            AutoAim.whenPressed(EZCmd.Drive.AutoAim());
        }

        resetGyroButton.whenPressed(EZCmd.Drive.ResetGyro(robot.follower));
    }

    public void bindLaunchControls() {
        launch.toggle(robot.launcherSubsystem::Launch, robot.launcherSubsystem::Stop);
        launchSlower.whenPressed(this::launchSlower);
        launchFaster.whenPressed(this::launchFaster);
    }

    public void bindFeedControls() {
        moveballup.whenPressed(robot.feedingSubsystem::moveball);
        moveballup.whenReleased(robot.feedingSubsystem::stop);
        compactScore.whenPressed(FeedCMD.Feed(robot));
        moveballanyways.whenPressed(robot.feedingSubsystem::moveballanyways);
        moveballanyways.whenReleased(robot.feedingSubsystem::stop);
    }

    public void bindPipelineControls() {
//        pipelineMode.whenPressed(this::togglePipelineMode);
//        if (pipelineToggle) {
//            //            barcodePipeline.whenPressed(new LLPipelineChangeCommand(hardware.limelight, Setup.HardwareNames.Barcode_Pipeline));
//            GreencolorPipeline.whenPressed(
//                new LLPipelineChangeCommand(
//                    hardware.limelight,
//                    Setup.HardwareNames.Green_Color_Pipeline
//                )
//            );
//            PurplecolorPipeline.whenPressed(
//                new LLPipelineChangeCommand(
//                    hardware.limelight,
//                    Setup.HardwareNames.Purple_Color_Pipeline
//                )
//            );
//            //            classifierPipeline.whenPressed(new LLPipelineChangeCommand(hardware.limelight, Setup.HardwareNames.Classifier_Pipeline));
//            //            objectPipeline.whenPressed(new LLPipelineChangeCommand(hardware.limelight, Setup.HardwareNames.Object_Detection_Pipeline));
//            apriltagPipeline.whenPressed(
//                new LLPipelineChangeCommand(
//                    hardware.limelight,
//                    Setup.HardwareNames.AprilTag_Pipeline
//                )
//            );
//        }
    }

    // public void setLaunch() {
    //     launchOn = !launchOn;
    //     Launch();
    // }

    public void launchFaster() {
        robot.launcherSubsystem.IncreaseVelocity();
    }

    public void launchSlower() {
        robot.launcherSubsystem.DecreaseVelocity();
    }

    // public void Launch() {
    //     if (launchOn) {
    //         robot.launcherSubsystem.Launch();
    //     } else {
    //         robot.launcherSubsystem.Stop();
    //     }
    // }
}
