package org.firstinspires.ftc.sixteen750.commands;

import com.pedropathing.geometry.BezierPoint;
import com.technototes.library.command.Command;
import org.firstinspires.ftc.sixteen750.Robot;
import org.firstinspires.ftc.sixteen750.commands.auto.Paths;
import org.firstinspires.ftc.sixteen750.subsystems.LimelightSubsystem;

public class TeleCommands {

    public static Command AltAutoOrient(Robot r) {
        return Command.create(() -> r.follower.turnTo(Math.toRadians(LimelightSubsystem.Xangle))); //r.limelightSubsystem.getTX()
    }

    public static Command TurnTo90(Robot r) {
        return Command.create(() ->
            r.follower.holdPoint(new BezierPoint(90, 90), Math.toRadians(90), false)
        ); //pose might need to be current pose?
    }

    public static Command Launch(Robot r) {
        return Command.create(r.launcherSubsystem::Launch);
    }

    public static Command SetFarShoot(Robot r) {
        return Command.create(r.launcherSubsystem::FarShoot);
    }

    public static Command SetCloseShoot(Robot r) {
        return Command.create(r.launcherSubsystem::CloseShoot);
    }

    public static Command AutoLaunch(Robot r) {
        return Command.create(r.launcherSubsystem::AutoLaunch);
    }

    public static Command StopLaunch(Robot r) {
        return Command.create(r.launcherSubsystem::Stop);
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
}
