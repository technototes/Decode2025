package org.firstinspires.ftc.learnbot.opmodes;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.gamepad.GamepadManager;
import com.bylazar.gamepad.PanelsGamepad;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.pedropathing.geometry.Pose;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.logger.Loggable;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.learnbot.Hardware;
import org.firstinspires.ftc.learnbot.Robot;
import org.firstinspires.ftc.learnbot.controls.DriverController;
import org.firstinspires.ftc.learnbot.helpers.HeadingHelper;
import org.firstinspires.ftc.learnbot.helpers.StartingPosition;

@Configurable
@SuppressWarnings("unused")
@TeleOp(name = "JustDrive")
public class JustDriveTele extends CommandOpMode implements Loggable {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;
    // For Panels controller widget the two lines below
    private final GamepadManager driverManager = PanelsGamepad.INSTANCE.getFirstManager();
    private final TelemetryManager panelsTelemetry = PanelsTelemetry.INSTANCE.getTelemetry();

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        hardware.follower.setPose(new Pose(0, 0, 0));
        hardware.follower.update();
        robot = new Robot(hardware, Alliance.NONE, StartingPosition.Unspecified);
        controls = new DriverController(driverGamepad, robot);
        CommandScheduler.scheduleInit(HeadingHelper.RestorePreviousPosition(robot.drivebase));
        CommandScheduler.scheduleJoystick(controls.stickDriver);
    }

    @Override
    public void uponStart() {
        robot.atStart();
    }

    //    @Override
    //    public void runLoop() {
    //        telemetry.update();
    //    }
}
