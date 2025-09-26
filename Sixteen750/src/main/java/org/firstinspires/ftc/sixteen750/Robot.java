package org.firstinspires.ftc.sixteen750;

import com.technototes.library.logger.Loggable;
import com.technototes.library.util.Alliance;
import org.firstinspires.ftc.sixteen750.helpers.StartingPosition;
import org.firstinspires.ftc.sixteen750.subsystems.AimingSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.BrakeSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.DrivebaseSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.LauncherSubsystem;
import org.firstinspires.ftc.sixteen750.subsystems.OTOSLocalizer;
import org.firstinspires.ftc.sixteen750.subsystems.SafetySubsystem;

public class Robot implements Loggable {

    public StartingPosition position;
    public Alliance alliance;

    public double initialVoltage;

    public DrivebaseSubsystem drivebase;
    public OTOSLocalizer localizer;
    public SafetySubsystem safetySubsystem;
    public LauncherSubsystem launcherSubsystem;
    public IntakeSubsystem intakeSubsystem;
    public BrakeSubsystem brakeSubsystem;
    public AimingSubsystem aimingSubsystem;

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
    }


    public void prepForStart() {}
}
