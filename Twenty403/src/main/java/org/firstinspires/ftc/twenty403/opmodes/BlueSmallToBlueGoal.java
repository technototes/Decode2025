package org.firstinspires.ftc.twenty403.opmodes;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.pedropathing.util.Timer;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.twenty403.AutoConstants;
import org.firstinspires.ftc.twenty403.Hardware;
import org.firstinspires.ftc.twenty403.Robot;
import org.firstinspires.ftc.twenty403.Setup;
import org.firstinspires.ftc.twenty403.commands.auto.Paths;
import org.firstinspires.ftc.twenty403.helpers.StartingPosition;

@Autonomous(name = "BlueSmallToBlueGoal", preselectTeleOp = "Driving w/Turbo!")
@SuppressWarnings("unused")
public class BlueSmallToBlueGoal extends CommandOpMode {

    public Robot robot;
    public Hardware hardware;
    public int pathState = 0;
    public Timer pathTimer, actionTimer, opmodeTimer;
    public PathChain bluesmalltobluegoal;

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
       robot.follower.setStartingPose(new Pose(56.889, 9.067));
        bluesmalltobluegoal = robot.follower
                .pathBuilder()
                .addPath(
                        new BezierCurve(
                                new Pose(56.889, 9.067),
                                new Pose(72.000, 116.444),
                                new Pose(22.044, 125.689)
                        )
                )
                .setLinearHeadingInterpolation(Math.toRadians(90), Math.toRadians(145))
                .build();
       robot.follower.update();
       telemetry.addData("Pose:", robot.follower.getPose());

    }
    @Override
    public void initLoop(){
        telemetry.addData("Pose:", robot.follower.getPose());
        robot.follower.update();
    }
    @Override
    public void uponStart() {
        robot.atStart(); opmodeTimer.resetTimer();}
    //    public void end() {
    //        HeadingHelper.savePose(robot.drivebaseSubsystem.getPoseEstimate());
    //    }
    @Override
    public void runLoop(){
        autonomousPathRun();
    }

    public void autonomousPathRun() {
        if (pathState == 0) {
            robot.follower.followPath(bluesmalltobluegoal);
            pathState++;
        }

    }
}
