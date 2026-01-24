package org.firstinspires.ftc.sixteen750.commands;

import com.pedropathing.geometry.BezierPoint;
import com.technototes.library.command.Command;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.auto.Paths;
import org.firstinspires.ftc.sixteen750.subsystems.LimelightSubsystem;

public class TeleCommands {

    public static Command TurnTo90(Robot r) {
        return Command.create(() ->
            r.follower.holdPoint(new BezierPoint(90, 90), Math.toRadians(90), false)
        ); //pose might need to be current pose?
    }

    public static Command Rumble(Robot r) {
        return Command.create(r.intakeSubsystem::setRumble);
    }

    public static Command RumbleOff(Robot r) {
        return Command.create(r.intakeSubsystem::setRumbleOff);
    }

    public static Command IncreaseMotor(Robot r) {
        return Command.create(r.launcherSubsystem::IncreaseMotorVelocity);
    }

    public static Command DecreaseMotor(Robot r) {
        return Command.create(r.launcherSubsystem::DecreaseMotorVelocity);
    }

    public static Command Intake(Robot r) {
        return Command.create(r.intakeSubsystem::Intake);
    }

    public static Command IntakeStop(Robot r) {
        return Command.create(r.intakeSubsystem::StopIntake);
    }

    public static Command Spit(Robot r) {
        return Command.create(r.intakeSubsystem::Spit);
    }

    public static Command EngageBrake(Robot r) {
        return Command.create(r.brakeSubsystem::Engage);
    }

    public static Command HoldIntake(Robot r) {
        return Command.create(r.intakeSubsystem::Hold);
    }

    public static Command DisengageBrake(Robot r) {
        return Command.create(r.brakeSubsystem::Disengage);
    }

    public static Command Aim(Robot r) {
        return Command.create(r.aimingSubsystem::Aim);
    }

    public static Command HoodUp(Robot r) {
        return Command.create(r.aimingSubsystem::testHoodUp);
    }

    public static Command HoodUpAutoOnly(Robot r) {
        return Command.create(r.aimingSubsystem::testHoodUpAutoOnly);
    }

    public static Command HoodUpAutoOnly2(Robot r) {
        return Command.create(r.aimingSubsystem::testHoodUpAutoOnly2);
    }

    public static Command HoodDown(Robot r) {
        return Command.create(r.aimingSubsystem::testHoodDown);
    }

    public static Command GateUp(Robot r) {
        return Command.create(r.aimingSubsystem::StopBall);
    }

    public static Command GateDown(Robot r) {
        return Command.create(r.aimingSubsystem::GoBall);
    }

    public static Command MotorPowerTest(Robot r) {
        return Command.create(r.testSubsystem::setMotorPowerTest);
    }

    public static Command MotorVelocityTest(Robot r) {
        return Command.create(r.testSubsystem::setMotorVelocityTest);
    }

    public static Command ReadVelocity(Robot r) {
        return Command.create(r.launcherSubsystem::readVelocity);
    }

    public static Command SetRegressionCAuto(Robot r) {
        return Command.create(r.launcherSubsystem::setRegressionCAuto);
    }

    public static Command SetRegressionDAuto(Robot r) {
        return Command.create(r.launcherSubsystem::setRegressionDAuto);
    }

    public static Command SetRegressionCTeleop(Robot r) {
        return Command.create(r.launcherSubsystem::setRegressionCTeleop);
    }

    public static Command SetRegressionDTeleop(Robot r) {
        return Command.create(r.launcherSubsystem::setRegressionDTeleop);
    }

    public static Command IncreaseRegressionDTeleop(Robot r) {
        return Command.create(r.launcherSubsystem::increaseRegressionDTeleop);
    }
}
