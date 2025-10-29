package org.firstinspires.ftc.sixteen750;

import com.pedropathing.Drivetrain;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.paths.PathConstraints;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;
import org.firstinspires.ftc.sixteen750.subsystems.AimingSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.BrakeSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.DrivebaseSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.LauncherSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.RROTOSLocalizer;
import org.firstinspires.ftc.sixteen750.subsystems.SafetySubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.TestSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.TwoDeadWheelLocalizer;

public class Robot implements Loggable {

    public StartingPosition position;
    public Alliance alliance;

    public double initialVoltage;

    public DrivebaseSubsystem drivebase;
    public TwoDeadWheelLocalizer localizer;
    public SafetySubsystem safetySubsystem;
    public LauncherSubsystem launcherSubsystem;
    public IntakeSubsystem intakeSubsystem;
    public BrakeSubsystem brakeSubsystem;
    public AimingSubsystem aimingSubsystem;
    public TestSubsystem testSubsystem;
    public Follower follower;
    public FollowerConstants followerConstants;
    public FollowerBuilder followerBuilder;
    public PathConstraints pathConstraints;
    public Drivetrain drivetrain;

    public Robot(Hardware hw, Alliance team, StartingPosition pos) {
        this.position = pos;
        this.alliance = team;
        this.initialVoltage = hw.voltage();
        // this.follower = new Follower(followerConstants, localizer, drivetrain, pathConstraints);

        if (Setup.Connected.ODOSUBSYSTEM) {
            this.localizer = new TwoDeadWheelLocalizer(hw.odoF, hw.odoR, hw.imu);
        }
        if (Setup.Connected.DRIVEBASE) {
            drivebase = new DrivebaseSubsystem(hw.fl, hw.fr, hw.rl, hw.rr, hw.imu, this.localizer);
        }
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
    }

    public Follower getFollower() {
        return follower;
    }

    public void prepForStart() {}
}
