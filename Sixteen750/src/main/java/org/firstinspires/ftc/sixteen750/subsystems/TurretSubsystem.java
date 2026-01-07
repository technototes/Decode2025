package org.firstinspires.ftc.sixteen750.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.technototes.library.hardware.motor.EncodedMotor;
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
    public static double TICKS_PER_REV = 1440;
    double GEAR_RATIO = 1.0;
    PIDFCoefficients turretPID = new PIDFCoefficients(0, 0,0,0);
    PIDFController turretPIDF = new PIDFController(turretPID);
    boolean hasHardware;
    public TurretSubsystem(Hardware h) {
        turretMotor = h.turretMotor;
        hasHardware = true;
    }

    private double getEncoderAngle() {
        return (getTurretPos() / (TICKS_PER_REV * GEAR_RATIO)) * 360.0;
    }
    public double angleToPosition(double angle) {
        return (angle / 360.0) * TICKS_PER_REV * GEAR_RATIO;
    }

    private double getTurretPos() {
        return turretMotor.getSensorValue();
    }
    private double getTurretPow() {
        return turretMotor.getPower();
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
        turretAngle = getEncoderAngle();
        turretPow = getTurretPow();
        turretTicks = getTurretPos();


    }
}
