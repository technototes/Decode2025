package org.firstinspires.ftc.sixteen750.opmodes.auto;

import com.bylazar.telemetry.PanelsTelemetry;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.SequentialCommandGroup;
import com.technototes.library.command.WaitCommand;
import com.technototes.library.structure.CommandOpMode;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.AltAutoOrient;
import org.firstinspires.ftc.sixteen750.commands.AltAutoVelocity;
import org.firstinspires.ftc.sixteen750.commands.LLSetup;
import org.firstinspires.ftc.sixteen750.commands.PedroPathCommand;
import org.firstinspires.ftc.sixteen750.commands.TeleCommands;
import org.firstinspires.ftc.sixteen750.commands.auto.Paths;
import org.firstinspires.ftc.sixteen750.controls.DriverController;
import org.firstinspires.ftc.sixteen750.helpers.HeadingHelper;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;

@Autonomous(name = "Red12BallFar", preselectTeleOp = "Dual Control")
@SuppressWarnings("unused")
public class Red12BallFar extends CommandOpMode {

    public Robot robot;
    public DriverController controls;
    public Hardware hardware;
    private PanelsTelemetry panelsTelemetry;

    @Override
    public void uponInit() {
        hardware = new Hardware(hardwareMap);
        robot = new Robot(hardware, Alliance.RED, StartingPosition.Net);
        Paths p = new Paths(robot.follower);
        panelsTelemetry = PanelsTelemetry.INSTANCE;
        robot.follower.setStartingPose(p.getRFar9BallStart());
        CommandScheduler.register(robot.limelightSubsystem);
        CommandScheduler.scheduleForState(
            //new AltAutoVelocity(robot).alongWith(
            new SequentialCommandGroup(
                new LLSetup(robot),
                TeleCommands.GateUp(robot),
                TeleCommands.Intake(robot),
                TeleCommands.SetFarShoot(robot),
                TeleCommands.HoldIntake(robot),
                TeleCommands.FarAutoLaunch(robot),
                TeleCommands.HoodUp(robot),
                new PedroPathCommand(robot.follower, p.RStartFartolaunchfar),
                new WaitCommand(0.5),
                Paths.AutoLaunching3BallsSlowIntake(robot),
                //                TeleCommands.HoldIntake(robot),
                // new WaitCommand(0.5),
                new PedroPathCommand(
                    robot.follower,
                    p.Rlaunchfartointake4,
                    p.powerforintake4
                ).alongWith(TeleCommands.Intake(robot)),
                new WaitCommand(0.3),
                // new WaitCommand(2),
                new PedroPathCommand(robot.follower, p.Rintake4tolaunchfar),
                Paths.AutoLaunching3BallsSlowIntake(robot),
                //                new PedroPathCommand(robot.follower, p.RlaunchfartointakeCorner, 0.7).alongWith(TeleCommands.Intake(robot)),
                //                new PedroPathCommand(robot.follower, p.RintakeCornertolaunchfar),

                new PedroPathCommand(robot.follower, p.RlaunchfartointakeEdgeNew, 0.6).alongWith(
                    TeleCommands.Intake(robot)
                ),
                new PedroPathCommand(robot.follower, p.RintakeEdgeNewtolaunchfar),
                //the two paths above is a new way to intake the corner balls
                //the two commented paths above these new ones is the old way to intake the corner balls

                Paths.AutoLaunching3BallsSlowIntake(robot),
                new PedroPathCommand(robot.follower, p.RlaunchfartointakeSweep, 0.6).alongWith(
                    TeleCommands.Intake(robot)
                ),
                new WaitCommand(0.4),
                new PedroPathCommand(robot.follower, p.RintakeSweeptolaunchfar),
                //the two paths above is a new way to intake the corner balls
                //the two commented paths above these new ones is the old way to intake the corner balls

                Paths.AutoLaunching3BallsSlowIntake(robot),
                //                    new PedroPathCommand(robot.follower, p.RlaunchfartointakeCornerNew,
                //                        0.5
                //                    ).alongWith(TeleCommands.Intake(robot)),
                //                    new PedroPathCommand(robot.follower, p.RintakeCornerNewtolaunchfar),
                //                    //the two paths above is a new way to intake the corner balls
                //                    //the two commented paths above these new ones is the old way to intake the corner balls
                //
                //                    Paths.AutoLaunching3BallsSlowIntake(robot),
                //                    new PedroPathCommand(robot.follower, p.Rlaunchfartogateintake).alongWith(
                //                        TeleCommands.Intake(robot)
                //                    ),
                //                    new WaitCommand(2.5),
                //                    new PedroPathCommand(robot.follower, p.Rgateintaketolaunchfar),
                //                    Paths.AutoLaunching3BallsSlowIntake(robot),
                new PedroPathCommand(robot.follower, p.Rlaunchfartopark).alongWith(
                    TeleCommands.Intake(robot)
                ),
                //new WaitCommand(1),
                //                TeleCommands.StopLaunch(robot),
                TeleCommands.IntakeStop(robot),
                CommandScheduler::terminateOpMode
                //)
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
