package org.firstinspires.ftc.swervebot.opmodes;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.HeadingHelper;

import org.firstinspires.ftc.swervebot.Hardware;
import org.firstinspires.ftc.swervebot.Robot;
import org.firstinspires.ftc.swervebot.Setup;
import org.firstinspires.ftc.swervebot.controls.DriverController;
import org.firstinspires.ftc.swervebot.helpers.StartingPosition;


@TeleOp(name = "SwerveTeleOp")
public class SwerveTeleOp extends CommandOpMode {
    public Robot robot;
    public Setup setup;
    public DriverController controls;
    public Hardware hardware;
    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.NONE, StartingPosition.Unspecified);
        // limelight = hardwareMap.get(Limelight3A.class, Setup.HardwareNames.LIMELIGHT);
        if (Setup.Connected.DRIVEBASE) {
            controls = new DriverController(driverGamepad, robot);

        }
    }

    @Override
    public void uponStart() {
        robot.prepForStart();
    }
}
