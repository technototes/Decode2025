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
import com.technototes.library.util.HeadingHelper;
import org.firstinspires.ftc.learnbot.Hardware;
import org.firstinspires.ftc.learnbot.Robot;
import org.firstinspires.ftc.learnbot.commands.SetVisionPipeline;
import org.firstinspires.ftc.learnbot.components.Pedro;
import org.firstinspires.ftc.learnbot.controls.DriverController;
import org.firstinspires.ftc.learnbot.helpers.StartingPosition;
import org.firstinspires.ftc.learnbot.subsystems.TargetSubsystem;

@Configurable
@SuppressWarnings("unused")
@TeleOp(name = "Vision Driving")
public class VisionDrivingTele extends CommandOpMode implements Loggable {

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
        CommandScheduler.register(robot.vision);
        CommandScheduler.scheduleInit(HeadingHelper.RestorePreviousPosition(Pedro.getFollower()));
        CommandScheduler.scheduleInit(
            new SetVisionPipeline(robot.vision, TargetSubsystem.Pipeline.APRIL_TAG)
        );
        CommandScheduler.scheduleJoystick(controls.stickDriver);
    }

    @Override
    public void uponStart() {
        robot.atStart();
    }
}
