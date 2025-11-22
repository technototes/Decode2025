package org.firstinspires.ftc.learnbot;

import com.bylazar.configurables.annotations.Configurable;
import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.learnbot.helpers.StartingPosition;
import org.firstinspires.ftc.learnbot.subsystems.PedroDrivebaseSubsystem;
import org.firstinspires.ftc.learnbot.subsystems.SpinningSubsystem;
import org.firstinspires.ftc.learnbot.subsystems.VisionSubsystem;

@Configurable
public class SpinningBot implements Loggable {

    public Alliance alliance;
    public StartingPosition position;

    public double initialVoltage;
    // Subsystems:
    // (Currently, Mouse only has a single subsystem: The drivebase)
    public SpinningSubsystem spin;

    public SpinningBot(Hardware hw, Alliance team, StartingPosition pos) {
        this.position = pos;
        this.alliance = team;
        this.initialVoltage = hw.voltage();
        this.spin = new SpinningSubsystem(hw.fl, hw);
    }

    public void atStart() {}
}
