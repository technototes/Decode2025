package org.firstinspires.ftc.twenty403;

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.technototes.library.hardware.motor.CRServo;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.hardware.sensor.AdafruitIMU;
import com.technototes.library.hardware.sensor.IGyro;
import com.technototes.library.hardware.sensor.IMU;
import com.technototes.library.hardware.servo.Servo;
import com.technototes.library.logger.Loggable;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.firstinspires.ftc.twenty403.helpers.IEncoder;

public class Hardware implements Loggable {

    public List<LynxModule> hubs;

    public IGyro imu;
    public EncodedMotor<DcMotorEx> fl, fr, rl, rr, top, testMotor;
    public IEncoder odoF, odoR;
    private OctoQuad octoquad;
    public CRServo bottomLeft, bottomRight;
    public Limelight3A limelight;
    public CRServo testCRServo;
    public Servo testServo;

    /* Put other hardware here! */

    public Hardware(HardwareMap hwmap) {
        hubs = hwmap.getAll(LynxModule.class);
        if (Setup.Connected.EXTERNALIMU) {
            imu = new AdafruitIMU(Setup.HardwareNames.EXTERNALIMU, AdafruitIMU.Orientation.Pitch);
        } else {
            imu = new IMU(
                Setup.HardwareNames.IMU,
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
            );
        }
        if (Setup.Connected.DRIVEBASE) {
            fl = new EncodedMotor<>(Setup.HardwareNames.FLMOTOR);
            fr = new EncodedMotor<>(Setup.HardwareNames.FRMOTOR);
            rl = new EncodedMotor<>(Setup.HardwareNames.RLMOTOR);
            rr = new EncodedMotor<>(Setup.HardwareNames.RRMOTOR);
        }
        //        if (Setup.Connected.OCTOQUAD) {
        //            octoquad = hwmap.get(OctoQuad.class, Setup.HardwareNames.OCTOQUAD);
        //            octoquad.resetAllPositions();
        //            if (Setup.Connected.ODOSUBSYSTEM) {
        //                odoR = new OctoquadEncoder(octoquad, Setup.OctoQuadPorts.ODO_STRAFE);
        //                odoF = new OctoquadEncoder(octoquad, Setup.OctoQuadPorts.ODO_FWD_BK);
        //            }
        //        }
        if (Setup.Connected.LAUNCHER) {
            top = new EncodedMotor<>(Setup.HardwareNames.TOP);
        }
        if (Setup.Connected.FEED) {
            bottomLeft = new CRServo(Setup.HardwareNames.BOTTOML);
            bottomRight = new CRServo(Setup.HardwareNames.BOTTOMR);
        }
        if (Setup.Connected.LIMELIGHT) {
            limelight = hwmap.get(Limelight3A.class, Setup.HardwareNames.LIMELIGHT);
        }
        if (Setup.Connected.TESTSUBSYSTEM) {
            testMotor = new EncodedMotor<>(Setup.HardwareNames.TESTMOTOR);
            testCRServo = new CRServo(Setup.HardwareNames.TESTCRSERVO);
            testServo = new Servo(Setup.HardwareNames.TESTSERVO);
        }
    }

    // We can read the voltage from the different hubs for fun...
    public double voltage() {
        double volt = 0;
        double count = 0;
        for (LynxModule lm : hubs) {
            count += 1;
            volt += lm.getInputVoltage(VoltageUnit.VOLTS);
        }
        return volt / count;
    }
}
