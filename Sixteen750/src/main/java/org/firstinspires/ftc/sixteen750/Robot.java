package org.firstinspires.ftc.sixteen750;

import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;
import org.firstinspires.ftc.sixteen750.subsystems.DrivebaseSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.OTOSLocalizer;
import org.firstinspires.ftc.sixteen750.subsystems.SafetySubsystem;

public class Robot implements Loggable {

    public StartingPosition position;
    public Alliance alliance;

    public double initialVoltage;

    public DrivebaseSubsystem drivebase;
    public OTOSLocalizer localizer;
    public SafetySubsystem safetySubsystem;

    public Robot(Hardware hw, Alliance team, StartingPosition pos) {
        this.position = pos;
        this.alliance = team;
        this.initialVoltage = hw.voltage();

        if (Setup.Connected.ODOSUBSYSTEM) {
            this.localizer = new OTOSLocalizer(hw.odo);
        } else {
            this.localizer = null;
        }

        if (Setup.Connected.DRIVEBASE) {
            drivebase = new DrivebaseSubsystem(hw.fl, hw.fr, hw.rl, hw.rr, hw.imu, localizer);
            //            if (localizer != null) {
            //                // YOU MUST CALL THIS IMMEDIATELY AFTER CREATING THE DRIVEBASE!
            //                localizer.setDrivebase(this.drivebase);
            //            }
        }

        if (Setup.Connected.SAFETYSUBSYSTEM) {
            this.safetySubsystem = new SafetySubsystem(hw);
        }
    }

    public void prepForStart() {}
}
