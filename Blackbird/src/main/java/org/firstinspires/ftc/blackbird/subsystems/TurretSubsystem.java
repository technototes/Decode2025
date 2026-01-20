package org.firstinspires.ftc.blackbird.subsystems;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import com.technototes.library.util.PIDFController;
import org.firstinspires.ftc.blackbird.Hardware;
import org.firstinspires.ftc.blackbird.Robot;
import org.firstinspires.ftc.blackbird.Setup;

@Configurable
public class TurretSubsystem implements Subsystem, Loggable {

    public EncodedMotor<DcMotorEx> turretMotor;
    public Robot robot;
    public double turretAngle = 0;
    public static double degrees90 = 90;
    public static double degrees45 = 45;
    // needs to be -99 to compensate for wire
    public static double degreesNeg90 = -99;
    public static double degrees0 = 0;
    public double turretTicks = 0;
    public double turretPow = 0;
    public static double turretOffsetDegrees = 0;
    public static double turretPos = 0;
    // 435 rpm gobilda yellow jacket
    public static double TICKS_PER_REV = 384.5;
    // 1:4 reduction
    public static double GEAR_RATIO = 4.0;
    // If the PID controller tells the motor to supply anything less than this, we'll
    // set it to zero, which should apply the motor brake.
    public static double BRAKE_THRESHOLD = 0.005;

    @Log(name = "Turret")
    public String TurretSubsytemInfoToDS;

    public static PIDFCoefficients turretPID = new PIDFCoefficients(0.005, 0, 0, 0.0001);
    public static PIDFCoefficients dtopPID = new PIDFCoefficients(
        positionToDegrees(0.005),
        0,
        0,
        positionToDegrees(0.0001)
    );
    public PIDFController turretPIDF = new PIDFController(turretPID);
    public PIDFController degreesToPowerPIDF = new PIDFController(dtopPID);
    boolean hasHardware;

    public TurretSubsystem(Hardware h) {
        hasHardware = Setup.Connected.TURRETSUBSYSTEM;
        if (hasHardware) {
            turretMotor = h.turretMotor;
            turretMotor.brake();
        }
    }

    public TurretSubsystem() {
        hasHardware = false;
    }

    public double getEncoderAngleInDegrees() {
        return positionToDegrees(getTurretPos());
    }

    static double degreesToPosition(double angle) {
        return (angle / 360.0) * (TICKS_PER_REV * GEAR_RATIO);
    }

    static double positionToDegrees(double pos) {
        return (360.0 * pos) / (TICKS_PER_REV * GEAR_RATIO);
    }

    public double getTurretPos() {
        if (hasHardware) {
            return turretMotor.getSensorValue();
        }
        return 0;
    }

    public void setTurretAngle(double deg) {
        if (hasHardware) {
            //            turretOffsetDegrees += robot.follower.getHeading() * (180 / Math.PI);
            turretPIDF.setTarget(degreesToPosition(deg - turretOffsetDegrees));
            degreesToPowerPIDF.setTarget(deg - turretOffsetDegrees);
        }
    }

    public void setTurretPosTX() {
        if (hasHardware) {
            //            turretOffsetDegrees = robot.follower.getHeading() * (180 / Math.PI);
            turretPIDF.setTarget(
                degreesToPosition(
                    positionToDegrees(turretPIDF.getTarget()) +
                        LimelightSubsystem.Xangle +
                        turretOffsetDegrees
                )
            );
            degreesToPowerPIDF.setTarget(
                degreesToPowerPIDF.getTarget() + LimelightSubsystem.Xangle + turretOffsetDegrees
            );
        }
    }

    public void setTurretPosTXWithPos(double pos) {
        if (hasHardware) {
            //            turretOffsetDegrees = robot.follower.getHeading() * (180 / Math.PI);
            turretPIDF.setTarget(
                degreesToPosition(pos + LimelightSubsystem.Xangle + turretOffsetDegrees)
            );
            degreesToPowerPIDF.setTarget(pos + LimelightSubsystem.Xangle + turretOffsetDegrees);
        }
    }

    public double getTurretPow() {
        if (hasHardware) {
            return turretMotor.getPower();
        }
        return 0;
    }

    private void setTurretPower(double power) {
        if (hasHardware) {
            turretPow = power = Math.clamp(power, -1, 1);
            turretMotor.setPower(power);
        }
    }

    public void turretGoToZero() {
        setTurretAngle(degrees0);
    }

    public void turretGoTo90() {
        setTurretAngle(degrees90);
    }

    public void turretGoTo45() {
        setTurretAngle(degrees45);
    }

    public void turretGoToNeg45() {
        setTurretAngle(-degrees45);
    }

    public void turretGoToNeg90() {
        setTurretAngle(degreesNeg90);
    }

    public double getTurretDegrees() {
        return positionToDegrees(getTargetTurretTicks());
    }

    public double getTargetTurretTicks() {
        return turretPIDF.getTarget();
    }

    @Override
    public void periodic() {
        turretPow = turretPIDF.update(getTurretPos());
        double degreesBaseDPower = degreesToPowerPIDF.update(positionToDegrees(getTurretPos()));
        String extra = "";
        if (Math.abs(turretPow) < BRAKE_THRESHOLD && turretPow != 0) {
            turretPow = 0.0;
            extra = "[BRK]";
        }
        if (Math.abs(degreesBaseDPower - turretPow) > 0.0001) {
            extra = "[bug]";
        }
        setTurretPower(turretPow);
        turretAngle = getEncoderAngleInDegrees();
        turretTicks = getTurretPos();
        TurretSubsytemInfoToDS = String.format(
            "Angle: %.1f Power: %.2f Pos: %.2f%s",
            turretAngle,
            turretPow,
            turretTicks,
            extra
        );
    }
}
