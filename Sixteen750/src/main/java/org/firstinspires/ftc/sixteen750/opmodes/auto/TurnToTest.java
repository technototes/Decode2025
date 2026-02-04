package org.firstinspires.ftc.sixteen750.opmodes.auto;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import com.technototes.library.util.HeadingHelper;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.AltAutoOrient;
import org.firstinspires.ftc.sixteen750.commands.PedroPathCommand;
import org.firstinspires.ftc.sixteen750.commands.auto.Paths;
import org.firstinspires.ftc.sixteen750.controls.DriverController;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;

@Autonomous(name = "TurnToTest", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class TurnToTest extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        Paths p = new Paths(robot.follower);
        robot.follower.setStartingPose(p.getBSegmentedCurveStart());
        CommandScheduler.register(robot.limelightSubsystem);
        CommandScheduler.scheduleForState(
            new SequentialCommandGroup(
                new PedroPathCommand(robot.follower, p.StartToTestPose),
                new WaitCommand(2),
                new AltAutoOrient(robot),
                new WaitCommand(2),
                //                new PedroPathCommand(robot.follower, p.RLaunchtoIntake1),
                //                new PedroPathCommand(robot.follower, p.RIntake1toIntake1end),
                //                new PedroPathCommand(robot.follower, p.RIntake1endtoLaunch),
                //                new WaitCommand(2),
                //                new AltAutoOrient(robot),
                //                new WaitCommand(2),
                //                new PedroPathCommand(robot.follower, p.RLaunchtoIntake2),
                //                new PedroPathCommand(robot.follower, p.RIntake2toIntake2end),
                //                new PedroPathCommand(robot.follower, p.RIntake2endtoLaunch),
                //                new WaitCommand(2),
                //                new AltAutoOrient(robot),
                //                new WaitCommand(2),
                //                new PedroPathCommand(robot.follower, p.RLaunchtoIntake3),
                //                new PedroPathCommand(robot.follower, p.RIntake3toIntake3end),
                //                new PedroPathCommand(robot.follower, p.RIntake3endtoLaunch),
                //                new WaitCommand(2),
                //                new AltAutoOrient(robot),
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
