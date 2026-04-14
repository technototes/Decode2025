package org.firstinspires.ftc.swervebot.swerveutil;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.technototes.library.command.Command;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.command.ParallelCommandGroup;
import com.technototes.library.hardware.motor.CRServo;
import com.technototes.library.hardware.sensor.IGyro;
import com.technototes.library.logger.Log;
import com.technototes.library.logger.Loggable;
import com.technototes.library.subsystem.Subsystem;
import com.technototes.library.util.MathUtils;
import com.technototes.library.util.PIDFController;

import org.firstinspires.ftc.swervebot.Hardware;
import org.firstinspires.ftc.swervebot.Setup;

import java.util.Arrays;
import java.util.OptionalDouble;

@Configurable
public class SimpleCoaxSwerveDriveSubsystem implements Loggable, Subsystem {
    //fr,fl,rl,rr
    DcMotorEx[] drive = new DcMotorEx[4];
    //fr,fl,rl,rr
    CRServo[] swervo = new CRServo[4];
    //fr,fl,rl,rr
    AbsoluteAnalogEncoder[] swervoencs = new AbsoluteAnalogEncoder[4];
    public static PIDFCoefficients swervoPID = new PIDFCoefficients(0.3,0,0.003,0);
    PIDFController[] swervoPIDF = new PIDFController[4];

    boolean hasHardware;
    double[] swervopower = new double[4];
    double[] swervopos = new double[4];

    // module to module needs to be measured
    double trackWidth = 10.85;
    double driveLength = 10.69;
    double R = Math.sqrt((trackWidth * trackWidth) + (driveLength * driveLength));
    @Log.Number(name = "X: ")
    public static double forwardInput = 0;
    @Log.Number(name = "Y: ")
    public static double strafeInput = 0;
    @Log.Number(name = "R: ")
    public static double rotationInput = 0;
    @Log.Number(name = "rNeg: ")
    public static double rNeg = 0;
    @Log.Number(name = "A: ")
    public static double A = 0;
    @Log.Number(name = "B: ")
    public static double B = 0;
    @Log.Number(name = "C: ")
    public static double C = 0;
    @Log.Number(name = "D: ")
    public static double D = 0;
    @Log.Number(name = "E: ")
    public static double E = 0;
    @Log.Number(name = "F: ")
    public static double F = 0;
    @Log.Number(name = "G: ")
    public static double G = 0;
    @Log.Number(name = "H: ")
    public static double H = 0;
    @Log.Number(name = "Enc0 (deg): ")
    public static double enc0Deg = 0;
    @Log.Number(name = "Enc1 (deg): ")
    public static double enc1Deg = 0;
    @Log.Number(name = "Enc2 (deg): ")
    public static double enc2Deg = 0;
    @Log.Number(name = "Enc3 (deg): ")
    public static double enc3Deg = 0;

    @Log.Number(name = "Err0 (deg): ")
    public static double err0Deg = 0;
    @Log.Number(name = "Err1 (deg): ")
    public static double err1Deg = 0;
    @Log.Number(name = "Err2 (deg): ")
    public static double err2Deg = 0;
    @Log.Number(name = "Err3 (deg): ")
    public static double err3Deg = 0;
    @Log.Number(name = "Target0 (deg): ")
    public static double target0Deg = 0;
    @Log.Number(name = "Target1 (deg): ")
    public static double target1Deg = 0;
    @Log.Number(name = "Target2 (deg): ")
    public static double target2Deg = 0;
    @Log.Number(name = "Target3 (deg): ")
    public static double target3Deg = 0;
    @Log.Number(name = "Power0: ")
    public static double power0 = 0;
    @Log.Number(name = "Power1: ")
    public static double power1 = 0;
    @Log.Number(name = "Power2: ")
    public static double power2 = 0;
    @Log.Number(name = "Power3: ")
    public static double power3 = 0;
    @Log.Number(name = "Raw0 (deg): ")
    public static double raw0 = 0;
    @Log.Number(name = "Raw1 (deg): ")
    public static double raw1 = 0;
    @Log.Number(name = "Raw2 (deg): ")
    public static double raw2 = 0;
    @Log.Number(name = "Raw3 (deg): ")
    public static double raw3 = 0;
    @Log.Number(name = "AngleDiff0: ")
    public static double diff0 = 0;
    @Log.Number(name = "AngleDiff1: ")
    public static double diff1 = 0;
    @Log.Number(name = "AngleDiff2: ")
    public static double diff2 = 0;
    @Log.Number(name = "AngleDiff3: ")
    public static double diff3 = 0;
    double[] unfilteredWheelPowers = new double[4];

    OptionalDouble maxPower = OptionalDouble.of(0.0);
    double[] wheelPowers = new double[4];
    double[] wheelAngles = new double[4];
    double[] angleDifferences = new double[4];
    IGyro imu;
    double robotHeading;


    public SimpleCoaxSwerveDriveSubsystem(Hardware hw) {
        hasHardware = Setup.Connected.SWERVESUBSYSTEM;
        // Do stuff in here
        if (hasHardware) {
            drive[0] = hw.frswervedrive;
            drive[1] = hw.flswervedrive;
            drive[2] = hw.rlswervedrive;
            drive[3] = hw.rrswervedrive;
            swervoencs[0] = hw.frswervoenc;
            swervoencs[1] = hw.flswervoenc;
            swervoencs[2] = hw.rlswervoenc;
            swervoencs[3] = hw.rrswervoenc;
            swervo[0] = hw.frswervo;
            swervo[1] = hw.flswervo;
            swervo[2] = hw.rlswervo;
            swervo[3] = hw.rrswervo;
            CommandScheduler.register(this);
            for (int i = 0; i < 4; i++) {
                swervoPIDF[i] = new PIDFController(swervoPID);
                swervoPIDF[i].setInputBounds(-Math.PI, Math.PI);
                drive[i].setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                drive[i].setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
            }
            imu = hw.imu;

        } else {
            drive = null;
            swervo = null;
            swervoencs = null;
            imu = null;
            hasHardware = false;
        }
    }

    @Override
    public void periodic() {
        if (hasHardware) {

            power0 = wheelPowers[0];
            power1 = wheelPowers[1];
            power2 = wheelPowers[2];
            power3 = wheelPowers[3];

        }
    }

    private void setSwervoPow(CRServo crServo, double power) {
        if (hasHardware) {
            crServo.setPower(power);
        }
    }
    public void setSwervosPow() {
        if (hasHardware) {
            swervo[0].setPower(1);
            swervo[1].setPower(1);
            swervo[2].setPower(1);
            swervo[3].setPower(1);

        }
    }
    public Command setSwervosCmd() {
        if (hasHardware) {
            return () -> new ParallelCommandGroup(() -> swervo[0].setPower(1),
                () -> swervo[1].setPower(1),
                () -> swervo[2].setPower(1),
                () -> swervo[3].setPower(1));

        }
        return null;
    }
    public Command stopSwervosCmd() {
        if (hasHardware) {
            return () -> new ParallelCommandGroup(() -> swervo[0].setPower(0),
                () -> swervo[1].setPower(0),
                () -> swervo[2].setPower(0),
                () -> swervo[3].setPower(0));

        }
        return null;
    }

    public void setSwervosPowZero() {
        if (hasHardware) {
            for (int i = 0; i < 4; i++) {
                swervo[i].setPower(0.5);
            }
        }
    }
    private void setSwervoPos(PIDFController swervoPIDF, double pos) {
        if (hasHardware) {
            swervoPIDF.setTarget(pos);
        }
    }

    private double getSwervoPow(CRServo crServo) {
        if (hasHardware) {
            return crServo.getPower();
        }
        return 0;
    }

    public void updateValues(double x, double y, double r) {
        if (hasHardware) {
            robotHeading = imu.getHeadingInRadians() - Math.PI / 2;
            forwardInput = x * Math.cos(robotHeading) + y * Math.sin(robotHeading);
            strafeInput = -x * Math.sin(robotHeading) + y * Math.cos(robotHeading);
            rotationInput = r;
            A = strafeInput - rotationInput * (driveLength / R);
            B = strafeInput + rotationInput * (driveLength / R);
            C = forwardInput - rotationInput * (trackWidth / R);
            D = forwardInput + rotationInput * (trackWidth / R);
            rNeg = -rotationInput;
            E = strafeInput - rNeg * (driveLength / R);
            F = strafeInput + rNeg * (driveLength / R);
            G = forwardInput - rNeg * (trackWidth / R);
            H = forwardInput + rNeg * (trackWidth / R);
            unfilteredWheelPowers[0] = Math.sqrt((F * F) + (G * G)) * (r != 0 ? (-rotationInput / r ) : 1);
            unfilteredWheelPowers[1] = Math.sqrt((B * B) + (D * D));
            unfilteredWheelPowers[2] = Math.sqrt((E * E) + (H * H)) * (r !=0 ? (-rotationInput / r ) : 1);
            unfilteredWheelPowers[3] = Math.sqrt((A * A) + (C * C));
            maxPower = Arrays.stream(unfilteredWheelPowers).max();
            for (int i = 0; i < 4; i++) {
                wheelPowers[i] = maxPower.getAsDouble() > 1
                        ? unfilteredWheelPowers[i] / maxPower.getAsDouble()
                        : unfilteredWheelPowers[i];
            }
            wheelAngles[0] = C == 0 && B == 0 ? 0 : MathUtils.normalizeDeltaRadians(Math.atan2(C, B));
            wheelAngles[1] = D == 0 && B == 0 ? 0 : MathUtils.normalizeDeltaRadians(Math.atan2(D, B));
            wheelAngles[2] = D == 0 && A == 0 ? 0 : MathUtils.normalizeDeltaRadians(Math.atan2(D, A));
            wheelAngles[3] = C == 0 && A == 0 ? 0 : MathUtils.normalizeDeltaRadians(Math.atan2(C, A));
            for (int i = 0; i < 4; i++) {
                swervopos[i] = swervoencs[i].getCurrentPosition();
                angleDifferences[i] = MathUtils.normalizeDeltaRadians(wheelAngles[i] - swervopos[i]);
                if (Math.abs(angleDifferences[i]) > Math.PI / 2) {
                    wheelAngles[i] += Math.PI;
                    wheelPowers[i] *= -1;

                    // Normalize the angle [-pi, pi]
                    wheelAngles[i] = MathUtils.normalizeDeltaRadians(wheelAngles[i]);
                }
            }
            enc0Deg = Math.toDegrees(swervopos[0]);
            enc1Deg = Math.toDegrees(swervopos[1]);
            enc2Deg = Math.toDegrees(swervopos[2]);
            enc3Deg = Math.toDegrees(swervopos[3]);
            err0Deg = Math.toDegrees(swervoPIDF[0].getLastError());
            err1Deg = Math.toDegrees(swervoPIDF[1].getLastError());
            err2Deg = Math.toDegrees(swervoPIDF[2].getLastError());
            err3Deg = Math.toDegrees(swervoPIDF[3].getLastError());
            target0Deg = Math.toDegrees(wheelAngles[0]);
            target1Deg = Math.toDegrees(wheelAngles[1]);
            target2Deg = Math.toDegrees(wheelAngles[2]);
            target3Deg = Math.toDegrees(wheelAngles[3]);
            raw0 = Math.toDegrees((swervoencs[0].getVoltage() / 3.3) * 360);
            raw1 = Math.toDegrees((swervoencs[1].getVoltage() / 3.3) * 360);
            raw2 = Math.toDegrees((swervoencs[2].getVoltage() / 3.3) * 360);
            raw3 = Math.toDegrees((swervoencs[3].getVoltage() / 3.3) * 360);
            diff0 = Math.toDegrees(angleDifferences[0]);
            diff1 = Math.toDegrees(angleDifferences[1]);
            diff2 = Math.toDegrees(angleDifferences[2]);
            diff3 = Math.toDegrees(angleDifferences[3]);
            for (int i = 0; i < 4; i++) {
                swervoPIDF[i].setTarget(wheelAngles[i]);
                setSwervoPow(swervo[i], -swervoPIDF[i].update(swervopos[i]));
                drive[i].setPower(wheelPowers[i]);

            }
        }
    }
    private double DeadZoneScale(double d) {
        // Okay, we want a small dead zone in the middle of the stick, but that also means that
        // you can't have a value any smaller than that value, so instead, we're going to scale
        // the value after compensating for the dead zone
            // If the value is inside the dead zone, just make it zero
            if (Math.abs(d) <= Setup.OtherSettings.STICK_DEAD_ZONE) {
                return 0.0;
            }
            // If the value is outside the dead zone, scale it
            return (
                    (d - Math.copySign(Setup.OtherSettings.STICK_DEAD_ZONE, d)) /
                            (1.0 - Setup.OtherSettings.STICK_DEAD_ZONE)
            );

    }
    public void setAllModuleAngles(double angle) {
        if (hasHardware) {
            for (int i = 0; i < 4; i++) {
                swervoPIDF[i].setTarget(angle);
            }
        }

    }
    public Command setAll180Cmd() {
        return () -> setAllModuleAngles(Math.PI);
    }
    public void setAllModules180() {
        if (hasHardware) {
            for (int i = 0; i < 4; i++) {
                swervoPIDF[i].setTarget(Math.PI);
            }
        }
    }
}
