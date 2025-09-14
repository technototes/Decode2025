package org.firstinspires.ftc.twenty403;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.follower.Follower;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.twenty403.helpers.StartingPosition;
import org.firstinspires.ftc.twenty403.subsystems.DrivebaseSubsystem;
import org.firstinspires.ftc.twenty403.subsystems.LauncherSubsystem;
import org.firstinspires.ftc.twenty403.subsystems.OTOSLocalizer;
import org.firstinspires.ftc.twenty403.subsystems.SafetySubsystem;
import org.firstinspires.ftc.twenty403.subsystems.TwoDeadWheelLocalizer;

@Configurable
public class Robot implements Loggable {

    public StartingPosition position;
    public Alliance alliance;
    public double initialVoltage;

    public DrivebaseSubsystem drivebaseSubsystem;
    public TwoDeadWheelLocalizer localizer;
    public SafetySubsystem safetySubsystem;
    public LauncherSubsystem launcherSubsystem;
    public OTOSLocalizer otosLocalizer;
    public Follower follower;

    public Robot(Hardware hw, Alliance team, StartingPosition pos) {
        this.position = pos;
        this.alliance = team;
        this.initialVoltage = hw.voltage();
        if (Setup.Connected.OTOS) {
            this.otosLocalizer = new OTOSLocalizer(hw.odo);
        } else {
            this.otosLocalizer = null;
        }

        if (Setup.Connected.ODOSUBSYSTEM && Setup.Connected.OCTOQUAD) {
            this.localizer = new TwoDeadWheelLocalizer(hw.odoF, hw.odoR, hw.imu);
        } else {
            this.localizer = null;
        }
        if (Setup.Connected.DRIVEBASE) {
            this.drivebaseSubsystem = new DrivebaseSubsystem(
                hw.fl,
                hw.fr,
                hw.rl,
                hw.rr,
                hw.imu,
                localizer
            );
        }
        if (Setup.Connected.LAUNCHER) {
            this.launcherSubsystem = new LauncherSubsystem(hw);
        }
        if (Setup.Connected.SAFETYSUBSYSTEM) {
            this.safetySubsystem = new SafetySubsystem(hw);
        }
    }

    public void atStart() {}

    public Follower getFollower() {
        return follower;
    }
}
