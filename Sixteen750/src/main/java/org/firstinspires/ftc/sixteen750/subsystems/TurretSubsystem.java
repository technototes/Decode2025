package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import com.technototes.library.util.PIDFController;
import org.firstinspires.ftc.sixteen750.Hardware;
import org.firstinspires.ftc.sixteen750.Setup;

@Configurable
public class TurretSubsystem implements Subsystem, Loggable {

    public EncodedMotor<DcMotorEx> turretMotor;
    public static double turretAngle = 0;
    public static double turretTicks = 0;
    public static double turretPow = 0;
    public static double turretOffsetDegrees = 0;
    public static double TICKS_PER_REV = 384.5;
    public static double GEAR_RATIO = 4.0;

    @Log(name = "Turret")
    public String TurretSubsytemInfoToDS;

    PIDFCoefficients turretPID = new PIDFCoefficients(0, 0, 0, 0);
    PIDFController turretPIDF = new PIDFController(turretPID);
    boolean hasHardware;

    public TurretSubsystem(Hardware h) {
        hasHardware = Setup.Connected.TURRETSUBSYSTEM;
        if (hasHardware) {
            turretMotor = h.turretMotor;
        }
    }

    public TurretSubsystem() {
        hasHardware = false;
    }

    private double getEncoderAngleInDegrees() {
        return (getTurretPos() / (TICKS_PER_REV * GEAR_RATIO)) * 360.0;
    }

    public double degreesToPosition(double angle) {
        return (angle / 360.0) * TICKS_PER_REV * GEAR_RATIO;
    }

    private double getTurretPos() {
        if (hasHardware) {
            return turretMotor.getSensorValue();
        }
        return 0;
    }

    public void setTurretPos(double pos) {
        if (hasHardware) {
            turretPIDF.setTarget(pos + degreesToPosition(turretOffsetDegrees));
        }
    }

    private double getTurretPow() {
        if (hasHardware) {
            return turretMotor.getPower();
        }
        return 0;
    }

    private void setTurretPower(double power) {
        if (hasHardware) {
            power = Math.clamp(power, -1, 1);
            turretMotor.setPower(power);
        }
    }

    @Override
    public void periodic() {
        setTurretPower(turretPIDF.update(getTurretPos()));
        turretAngle = getEncoderAngleInDegrees();
        turretPow = getTurretPow();
        turretTicks = getTurretPos();
        TurretSubsytemInfoToDS = String.format(
            "Angle: %.1f Power: %.2f Pos: %i",
            getEncoderAngleInDegrees(),
            getTurretPow(),
            getTurretPos()
        );
    }
}
