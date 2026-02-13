package org.firstinspires.ftc.swervebot.controls;

import com.technototes.library.command.Command;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.control.CommandGamepad;
import com.technototes.library.control.Stick;
import org.firstinspires.ftc.swervebot.Hardware;
import org.firstinspires.ftc.swervebot.Robot;
import org.firstinspires.ftc.swervebot.Setup;
import org.firstinspires.ftc.swervebot.commands.SwerveDriveCmd;

public class DriverController {

    public Robot robot;
    public Hardware hardware;
    public CommandGamepad gamepad;

    public Stick driveLeftStick, driveRightStick;

    public static double triggerThreshold = 0.1;
    public Command SwerveDriveCmd;

    public DriverController(CommandGamepad g, Robot r) {
        this.robot = r;
        gamepad = g;

        AssignNamedControllerButton();
        if (Setup.Connected.SWERVESUBSYSTEM) {
            bindDriveControls();
        }
    }

    private void AssignNamedControllerButton() {
        driveLeftStick = gamepad.leftStick;
        driveRightStick = gamepad.rightStick;
    }

    public void bindDriveControls() {
       SwerveDriveCmd = new SwerveDriveCmd(robot.swerveDriveSubsystem, driveLeftStick, driveRightStick);
        CommandScheduler.scheduleJoystick(SwerveDriveCmd);
    }


}
