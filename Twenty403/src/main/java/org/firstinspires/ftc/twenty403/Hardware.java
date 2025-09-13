package org.firstinspires.ftc.twenty403;

import com.qualcomm.hardware.digitalchickenlabs.OctoQuad;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.technototes.library.hardware.motor.CRServo;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.hardware.motor.Motor;
import com.technototes.library.hardware.sensor.AdafruitIMU;
import com.technototes.library.hardware.sensor.IGyro;
import com.technototes.library.hardware.sensor.IMU;
import com.technototes.library.hardware.servo.Servo;
import com.technototes.library.logger.Loggable;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;
import org.firstinspires.ftc.twenty403.helpers.IEncoder;
import org.firstinspires.ftc.twenty403.helpers.OctoquadEncoder;

public class Hardware implements Loggable {

    public List<LynxModule> hubs;

    public IGyro imu;
    public EncodedMotor<DcMotorEx> fl, fr, rl, rr;
    public IEncoder odoF, odoR;
    private OctoQuad octoquad;

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
        if (Setup.Connected.OCTOQUAD) {
            octoquad = hwmap.get(OctoQuad.class, Setup.HardwareNames.OCTOQUAD);
            octoquad.resetAllPositions();
            if (Setup.Connected.ODOSUBSYSTEM) {
                odoR = new OctoquadEncoder(octoquad, Setup.OctoQuadPorts.ODO_STRAFE);
                odoF = new OctoquadEncoder(octoquad, Setup.OctoQuadPorts.ODO_FWD_BK);
            }
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
