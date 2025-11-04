package org.firstinspires.ftc.twenty403.opmodes.autos;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.twenty403.AutoConstants;
import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.Setup;
import org.firstinspires.ftc.twenty403.helpers.StartingPosition;

@Configurable
@Autonomous(name = "RedSmallTriMove", preselectTeleOp = "Two Controller Drive \uD83D\uDDFF")
@SuppressWarnings("unused")
public class RedSmallTriMove extends CommandOpMode {

    public Robot robot;
    public Hardware hardware;
    public int pathState = 0;
    public Timer pathTimer, actionTimer, opmodeTimer;
    public PathChain bluesmalltobluegoal;

    public double MAX_POWER = .75;

    @Override
    public void uponInit() {
        telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.BLUE, StartingPosition.Unspecified);
        pathTimer = new Timer();
        opmodeTimer = new Timer();
        opmodeTimer.resetTimer();
        SparkFunOTOS otos = hardwareMap.get(SparkFunOTOS.class, Setup.HardwareNames.OTOS);
        otos.calibrateImu();
        robot.follower = AutoConstants.createFollower(hardwareMap);
        robot.follower.setPose(new Pose(81.422, 9.422, 90));
        bluesmalltobluegoal = robot.follower
            .pathBuilder()
            .addPath(
                new BezierCurve(
                    new Pose(81.422, 9.422),
                    new Pose(96.000, 21.689),
                    new Pose(108.622, 11.733)
                )
            )
            .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(0))
            .build();
        //        robot.follower.setStartingPose(robot.follower.getPose());
        //        bluesmalltobluegoal = robot.follower
        //                .pathBuilder()
        //                .addPath(
        //                        new BezierCurve(
        //                                new Pose(9.067, 56.889),
        //                                new Pose(116.444, 72),
        //                                new Pose(125.689, 22.044)
        //                        )
        //                )
        //                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(145))
        //                .build();
        telemetry.addData("Pose:", robot.follower.getPose());
        robot.follower.setMaxPower(MAX_POWER);
        robot.follower.update();
    }

    @Override
    public void initLoop() {
        telemetry.addData("Pose:", robot.follower.getPose());
        robot.follower.update();
    }

    @Override
    public void uponStart() {
        //        EZCmd.Drive.ResetGyro(robot.follower);
        robot.atStart();
        opmodeTimer.resetTimer();
    }

    //    public void end() {
    //        HeadingHelper.savePose(robot.drivebaseSubsystem.getPoseEstimate());
    //    }
    @Override
    public void runLoop() {
        telemetry.addData("Pose:", robot.follower.getPose());
        //        if (robot.follower.getHeading() != robot.follower.getCurrentPath().getPose()) {}
        autonomousPathRun();
        robot.follower.update();
    }

    public void autonomousPathRun() {
        if (pathState == 0) {
            robot.follower.followPath(bluesmalltobluegoal);
            pathState++;
        }
    }
}
