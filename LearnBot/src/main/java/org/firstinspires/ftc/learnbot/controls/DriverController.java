package org.firstinspires.ftc.learnbot.controls;

import com.technototes.library.command.CommandScheduler;
import com.technototes.library.control.CommandAxis;
import com.technototes.library.control.CommandButton;
import com.technototes.library.control.CommandGamepad;
import com.technototes.library.control.Stick;
import org.firstinspires.ftc.learnbot.Hardware;
import org.firstinspires.ftc.learnbot.Robot;
import org.firstinspires.ftc.learnbot.Setup;
import org.firstinspires.ftc.learnbot.commands.DriveCmds;
import org.firstinspires.ftc.learnbot.commands.LLPipelineChangeCommand;
import org.firstinspires.ftc.learnbot.commands.driving.JoystickDriveCommand;

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
    public CommandButton barcodePipeline;
    public CommandButton GreencolorPipeline;
    public CommandButton classifierPipeline;
    public CommandButton objectPipeline;
    public CommandButton apriltagPipeline;
    public CommandButton PurplecolorPipeline;
    public CommandButton AutoAim;
    public JoystickDriveCommand stickDriver;
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
        pipelineMode = gamepad.dpadUp;
        launchSlower = gamepad.ps_cross;
        launchFaster = gamepad.ps_circle;
        apriltagPipeline = gamepad.dpadRight;
        AutoAim = gamepad.dpadDown;
        moveballslow = gamepad.dpadLeft;
    }

    public void bindDriveControls() {
        stickDriver = new JoystickDriveCommand(robot.follower, driveLeftStick, driveRightStick);
        CommandScheduler.scheduleJoystick(stickDriver);

        turboButton.whenPressed(DriveCmds.TurboMode(stickDriver));
        turboButton.whenReleased(DriveCmds.NormalMode(stickDriver));

        snailButton.whenPressed(DriveCmds.SnailMode(stickDriver));
        snailButton.whenReleased(DriveCmds.NormalMode(stickDriver));
        if (Setup.Connected.LIMELIGHT) {
            AutoAim.whenPressed(DriveCmds.AutoAim(stickDriver));
        }

        resetGyroButton.whenPressed(DriveCmds.ResetGyro(stickDriver));
    }

    public void bindPipelineControls() {
        pipelineMode.whenPressed(this::togglePipelineMode);
        if (pipelineToggle) {
            //            barcodePipeline.whenPressed(new LLPipelineChangeCommand(hardware.limelight, Setup.HardwareNames.Barcode_Pipeline));
            GreencolorPipeline.whenPressed(
                new LLPipelineChangeCommand(
                    hardware.limelight,
                    Setup.HardwareNames.Green_Color_Pipeline
                )
            );
            PurplecolorPipeline.whenPressed(
                new LLPipelineChangeCommand(
                    hardware.limelight,
                    Setup.HardwareNames.Purple_Color_Pipeline
                )
            );
            //            classifierPipeline.whenPressed(new LLPipelineChangeCommand(hardware.limelight, Setup.HardwareNames.Classifier_Pipeline));
            //            objectPipeline.whenPressed(new LLPipelineChangeCommand(hardware.limelight, Setup.HardwareNames.Object_Detection_Pipeline));
            apriltagPipeline.whenPressed(
                new LLPipelineChangeCommand(
                    hardware.limelight,
                    Setup.HardwareNames.AprilTag_Pipeline
                )
            );
        }
    }
}
