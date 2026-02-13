package org.firstinspires.ftc.swervebot.swerveutil;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.technototes.library.command.CommandScheduler;
import com.technototes.library.hardware.motor.CRServo;
import com.technototes.library.hardware.sensor.IGyro;
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
    PIDFCoefficients swervoPID = new PIDFCoefficients(1,0,0,0);
    PIDFController[] swervoPIDF = new PIDFController[4];

    boolean hasHardware;
    double[] swervopower = new double[4];
    double[] swervopos = new double[4];

    // module to module needs to be measured
    double trackWidth = 17;
    double driveLength = 16;
    double R = Math.sqrt((trackWidth * trackWidth) + (driveLength * driveLength));
    double forwardInput = 0;
    double strafeInput = 0;
    double rotationInput = 0;
    double A = 0;
    double B = 0;
    double C = 0;
    double D = 0;
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
            for (int i = 0; i < 4; i++) {
                if (0 <= Math.abs(swervoPIDF[i].getLastError()) && Math.abs(swervoPIDF[i].getLastError()) < Math.toRadians(1)) {
                    //commenting out is helpful for just tuning swervo pid
//                drive[i].setPower(wheelPowers[i]);
                }
            }
        }
    }

    private void setSwervoPow(CRServo crServo, double power) {
        if (hasHardware) {
            crServo.setPower(power);
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
            robotHeading = -imu.getHeadingInRadians();
            forwardInput = x * Math.cos(robotHeading) + y * Math.sin(robotHeading);
            strafeInput = -x * Math.sin(robotHeading) + y * Math.cos(robotHeading);
            rotationInput = r;
            A = strafeInput - rotationInput * (driveLength / R);
            B = strafeInput + rotationInput * (driveLength / R);
            C = forwardInput - rotationInput * (trackWidth / R);
            D = forwardInput + rotationInput * (trackWidth / R);
            unfilteredWheelPowers[0] = Math.sqrt((B * B) + (C * C));
            unfilteredWheelPowers[1] = Math.sqrt((B * B) + (D * D));
            unfilteredWheelPowers[2] = Math.sqrt((A * A) + (D * D));
            unfilteredWheelPowers[3] = Math.sqrt((A * A) + (C * C));
            maxPower = Arrays.stream(unfilteredWheelPowers).max();
            if (maxPower.getAsDouble() > 1) {
                for (int i = 0; i < 4; i++) {
                    wheelPowers[i] = unfilteredWheelPowers[i] / maxPower.getAsDouble();
                }
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
            for (int i = 0; i < 4; i++) {
                swervoPIDF[i].setTarget(wheelAngles[i]);
                setSwervoPow(swervo[i], swervoPIDF[i].update(swervopos[i]));
            }
        }
    }

}
