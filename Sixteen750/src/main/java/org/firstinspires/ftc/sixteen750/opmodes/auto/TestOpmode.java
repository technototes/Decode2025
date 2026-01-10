package org.firstinspires.ftc.sixteen750.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.ParallelCommandGroup;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.AltAutoOrient;
import org.firstinspires.ftc.sixteen750.commands.AltAutoVelocity;
import org.firstinspires.ftc.sixteen750.commands.LLSetup;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.auto.Paths;
import org.firstinspires.ftc.sixteen750.controls.DriverController;
import org.firstinspires.ftc.sixteen750.helpers.HeadingHelper;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;

@Autonomous(name = "TestOpmode", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class TestOpmode extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        //robot.drivebase.setPoseEstimate(AutoConstants.BACKWARD.toPose());
        //        hardware.rl.setDirection(DcMotorSimple.Direction.FORWARD);
        //        hardware.rr.setDirection(DcMotorSimple.Direction.REVERSE);
        //        hardware.fl.setDirection(DcMotorSimple.Direction.FORWARD);
        //        hardware.fr.setDirection(DcMotorSimple.Direction.REVERSE);
        CommandScheduler.register(robot.limelightSubsystem);
        Paths p = new Paths(robot.follower);
        robot.follower.setStartingPose(p.getRSegmentedCurveStart());
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                new LLSetup(robot),
                new ParallelCommandGroup(
                    new AltAutoVelocity(robot),
                    //new AltAutoOrient(robot),
                    //new DriveAutoCommand(robot.follower, 0.5),
                    //                    TeleCommands.HoldIntake(robot),
                    //                    TeleCommands.GateUp(robot),
                    //                    TeleCommands.HoodUp(robot)
                    Paths.AutoLaunching3BallsSlowIntake(robot)
                ),
                //                new WaitCommand(0.5),
                //                new ParallelCommandGroup(
                //                    //new DriveAutoCommand(robot.follower, 0),
                //                ),
                new WaitCommand(2),
                TeleCommands.GateDown(robot),
                new WaitCommand(6),
                //p.AutoLaunching3Balls(robot),
                //                new WaitCommand(4),
                //                TeleCommands.GateDown(robot),
                //                new WaitCommand(0.1),
                //                TeleCommands.GateUp(robot),
                //                new WaitCommand(0.5),
                //                TeleCommands.GateDown(robot),
                //                new WaitCommand(0.1),
                //                TeleCommands.GateUp(robot),
                //                new WaitCommand(0.5),
                //                TeleCommands.GateDown(robot),
                //                new WaitCommand(2),
                TeleCommands.StopLaunch(robot),
                TeleCommands.IntakeStop(robot),
                CommandScheduler::terminateOpMode
            ),
            OpModeState.RUN
        );
    }

    public void uponStart() {
        robot.prepForStart();
    }

    public void end() {
        HeadingHelper.savePose(robot.follower.getPose());
        robot.limelightSubsystem.LimelightTurnOff();
    }
}
