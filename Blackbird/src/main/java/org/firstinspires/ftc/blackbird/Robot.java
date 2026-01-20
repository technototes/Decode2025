package org.firstinspires.ftc.blackbird;

import com.pedropathing.follower.Follower;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.blackbird.helpers.StartingPosition;
import org.firstinspires.ftc.blackbird.subsystems.AimingSubsystem;
import org.firstinspires.ftc.blackbird.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.blackbird.subsystems.LauncherSubsystem;
import org.firstinspires.ftc.blackbird.subsystems.LimelightSubsystem;
import org.firstinspires.ftc.blackbird.subsystems.SafetySubsystem;
import org.firstinspires.ftc.blackbird.subsystems.TestSubsystem;
import org.firstinspires.ftc.blackbird.subsystems.TurretSubsystem;

public class Robot implements Loggable {

    public StartingPosition position;
    public Alliance alliance;

    public double initialVoltage;

    public SafetySubsystem safetySubsystem;
    public LauncherSubsystem launcherSubsystem;
    public IntakeSubsystem intakeSubsystem;
    public AimingSubsystem aimingSubsystem;
    public LimelightSubsystem limelightSubsystem;
    public TestSubsystem testSubsystem;
    public TurretSubsystem turretSubsystem;
    public Follower follower;
    private Hardware hardware;

    public Robot(Hardware hw, Alliance team, StartingPosition pos) {
        this.position = pos;
        this.alliance = team;
        this.hardware = hw;
        this.initialVoltage = hw.voltage();

        if (Setup.Connected.SAFETYSUBSYSTEM) {
            this.safetySubsystem = new SafetySubsystem(hw);
        }
        if (Setup.Connected.INTAKESUBSYSTEM) {
            this.intakeSubsystem = new IntakeSubsystem(hw);
        }
        if (Setup.Connected.LAUNCHERSUBSYSTEM) {
            this.launcherSubsystem = new LauncherSubsystem(hw);
        }
        if (Setup.Connected.LIMELIGHTSUBSYSTEM) {
            this.limelightSubsystem = new LimelightSubsystem(hw);
        }
        if (Setup.Connected.AIMINGSUBSYSTEM) {
            this.aimingSubsystem = new AimingSubsystem(hw, limelightSubsystem);
        }
        if (Setup.Connected.TESTSUBSYSTEM) {
            this.testSubsystem = new TestSubsystem(hw);
        }
        if (Setup.Connected.DRIVEBASE) {
            follower = AutoConstants.createFollower(hw.map);
        }
        if (Setup.Connected.TURRETSUBSYSTEM) {
            this.turretSubsystem = new TurretSubsystem(hw);
        } else {
            this.turretSubsystem = new TurretSubsystem();
        }
    }

    public Hardware getHardware() {
        return hardware;
    }

    public Follower getFollower() {
        return follower;
    }

    public void prepForStart() {}
}
