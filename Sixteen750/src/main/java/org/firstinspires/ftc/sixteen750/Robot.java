package org.firstinspires.ftc.sixteen750;

import com.pedropathing.follower.Follower;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;
import org.firstinspires.ftc.sixteen750.subsystems.AimingSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.BrakeSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.LauncherSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.LimelightSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.SafetySubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.TestSubsystem;

public class Robot implements Loggable {

    public StartingPosition position;
    public Alliance alliance;

    public double initialVoltage;

    public SafetySubsystem safetySubsystem;
    public LauncherSubsystem launcherSubsystem;
    public IntakeSubsystem intakeSubsystem;
    public BrakeSubsystem brakeSubsystem;
    public AimingSubsystem aimingSubsystem;
    public LimelightSubsystem limelightSubsystem;
    public TestSubsystem testSubsystem;
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
        if (Setup.Connected.BRAKESUBSYSTEM) {
            this.brakeSubsystem = new BrakeSubsystem(hw);
        }
        if (Setup.Connected.AIMINGSUBSYSTEM) {
            this.aimingSubsystem = new AimingSubsystem(hw);
        }
        if (Setup.Connected.TESTSUBSYSTEM) {
            this.testSubsystem = new TestSubsystem(hw);
        }
            if (Setup.Connected.LIMELIGHTSUBSYSTEM){
            this.limelightSubsystem = new LimelightSubsystem(hw);
        }
        if (Setup.Connected.DRIVEBASE) {
        follower = AutoConstants.createFollower(hw.map);
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
