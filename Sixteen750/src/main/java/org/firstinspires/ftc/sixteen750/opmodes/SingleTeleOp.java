package org.firstinspires.ftc.sixteen750.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.sixteen750.AutoConstants;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.Setup;
import org.firstinspires.ftc.sixteen750.commands.driving.DrivingCommands;
import org.firstinspires.ftc.sixteen750.controls.SingleController;
import org.firstinspires.ftc.sixteen750.helpers.HeadingHelper;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;

@TeleOp(name = "Single Control")
public class SingleTeleOp extends CommandOpMode {

    public Robot robot;
    public Setup setup;
    public SingleController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.NONE, StartingPosition.Unspecified);
        controls = new SingleController(driverGamepad, robot, setup);
        robot.drivebase.setPoseEstimate(HeadingHelper.getSavedPose());
        //CommandScheduler.scheduleInit(HorizontalSlidesCommands.transferring(robot));
        CommandScheduler.scheduleForState(
            DrivingCommands.ResetGyro(robot.drivebase),
            OpModeState.INIT
        );
    }
}
