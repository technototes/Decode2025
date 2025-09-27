package org.firstinspires.ftc.twenty403.controls;

import com.technototes.library.command.CommandScheduler;
import com.technototes.library.control.CommandAxis;
import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import com.technototes.library.control.Stick;

import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.Setup;
import org.firstinspires.ftc.twenty403.commands.EZCmd;
import org.firstinspires.ftc.twenty403.commands.LLPipelineChangeCommand;
import org.firstinspires.ftc.twenty403.commands.driving.JoystickDriveCommand;

import java.util.Set;

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
    public CommandButton moveballup;
    public CommandButton moveballupandlaunch;
    public CommandButton pipelineMode;
    public CommandButton barcodePipeline;
    public CommandButton GreencolorPipeline;
    public CommandButton classifierPipeline;
    public CommandButton objectPipeline;
    public CommandButton apriltagPipeline;
    public CommandButton PurplecolorPipeline;
    public CommandButton AutoAim;
    public static boolean pipelineToggle = false;
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
        if (Setup.Connected.LIMELIGHT){
            bindPipelineControls();
        }
        if (Setup.Connected.FEED){
            bindFeedControls();
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
        moveballupandlaunch = gamepad.ps_circle;
        pipelineMode = gamepad.dpadUp;
        barcodePipeline = gamepad.ps_square;
        GreencolorPipeline = gamepad.ps_cross;
        classifierPipeline = gamepad.ps_circle;
        objectPipeline = gamepad.ps_triangle;
        apriltagPipeline = gamepad.dpadRight;
        AutoAim = gamepad.dpadDown;

    }

    public void bindDriveControls() {
        CommandScheduler.scheduleJoystick(
            new JoystickDriveCommand(
                robot,
                driveLeftStick,
                driveRightStick,
                straightTrigger,
                angleTrigger
            )
        );

        turboButton.whenPressed(EZCmd.Drive.TurboMode(robot.drivebaseSubsystem));
        turboButton.whenReleased(EZCmd.Drive.NormalMode(robot.drivebaseSubsystem));

        snailButton.whenPressed(EZCmd.Drive.SnailMode(robot.drivebaseSubsystem));
        snailButton.whenReleased(EZCmd.Drive.NormalMode(robot.drivebaseSubsystem));
        if (Setup.Connected.LIMELIGHT){
            AutoAim.whenPressed(EZCmd.Drive.AutoAim(robot.drivebaseSubsystem));
        }

        // resetGyroButton.whenPressed(EZCmd.Drive.ResetGyro(robot.drivebaseSubsystem));
    }

    public void bindLaunchControls() {
        if (!pipelineToggle) {
            launch.whilePressed(robot.launcherSubsystem::Launch);
            launch.whenReleased(robot.launcherSubsystem::Stop);



        }
    }
    public void bindFeedControls() {
        moveballup.whilePressed(robot.feedingSubsystem::moveball);
        moveballup.whenReleased(robot.feedingSubsystem::stop);
    }
    public void bindPipelineControls() {
        pipelineMode.whenPressed(this::togglePipelineMode);
        if (pipelineToggle) {
//            barcodePipeline.whenPressed(new LLPipelineChangeCommand(hardware.limelight, Setup.HardwareNames.Barcode_Pipeline));
            GreencolorPipeline.whenPressed(new LLPipelineChangeCommand(hardware.limelight, Setup.HardwareNames.Green_Color_Pipeline));
            PurplecolorPipeline.whenPressed(new LLPipelineChangeCommand(hardware.limelight, Setup.HardwareNames.Purple_Color_Pipeline));
//            classifierPipeline.whenPressed(new LLPipelineChangeCommand(hardware.limelight, Setup.HardwareNames.Classifier_Pipeline));
//            objectPipeline.whenPressed(new LLPipelineChangeCommand(hardware.limelight, Setup.HardwareNames.Object_Detection_Pipeline));
            apriltagPipeline.whenPressed(new LLPipelineChangeCommand(hardware.limelight, Setup.HardwareNames.AprilTag_Pipeline));
        }
    }

}
