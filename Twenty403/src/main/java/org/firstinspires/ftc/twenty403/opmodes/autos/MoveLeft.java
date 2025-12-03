package org.firstinspires.ftc.twenty403.opmodes.autos;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.twenty403.AutoConstants;
import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.Setup;
import org.firstinspires.ftc.twenty403.commands.auto.DriveAutoCommand;
import org.firstinspires.ftc.twenty403.controls.DriverController;
import org.firstinspires.ftc.twenty403.helpers.HeadingHelper;
import org.firstinspires.ftc.twenty403.helpers.StartingPosition;

@Configurable
@Autonomous(name = "MoveLeft", preselectTeleOp = "Two Controller Drive \uD83D\uDDFF")
@SuppressWarnings("unused")
public class MoveLeft extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;
    public static double motor_duration = .3;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        SparkFunOTOS otos = hardwareMap.get(SparkFunOTOS.class, Setup.HardwareNames.OTOS);
        otos.calibrateImu();
        robot.follower = AutoConstants.createFollower(hardwareMap);
        //robot.drivebase.setPoseEstimate(AutoConstants.BACKWARD.toPose());
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                new DriveAutoCommand(robot.follower, -0.5, 0.5, 0.5, -0.5),
                new WaitCommand(motor_duration),
                new DriveAutoCommand(robot.follower, 0),
                CommandScheduler::terminateOpMode
            ),
            OpModeState.RUN
        );
        robot.follower.update();
    }

    @Override
    public void uponStart() {
        robot.atStart();
    }

    @Override
    public void runLoop() {
        telemetry.addData("Pose:", robot.follower.getPose());
        //        if (robot.follower.getHeading() != robot.follower.getCurrentPath().getPose()) {}
        robot.follower.update();
    }

    public void end() {
        HeadingHelper.savePose(
            new Pose(
                robot.follower.getPose().getX(),
                robot.follower.getPose().getY(),
                hardware.imu.getHeading()
            )
        );
    }
}
