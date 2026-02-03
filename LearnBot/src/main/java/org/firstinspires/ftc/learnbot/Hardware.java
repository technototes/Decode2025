package org.firstinspires.ftc.learnbot;

import com.pedropathing.follower.Follower;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.technototes.library.hardware.motor.EncodedMotor;
import com.technototes.library.hardware.sensor.IGyro;
import com.technototes.library.hardware.sensor.IMU;
import com.technototes.library.hardware.servo.Servo;
import com.technototes.library.logger.Loggable;
import java.util.List;
import org.firstinspires.ftc.learnbot.Setup.HardwareNames;
import org.firstinspires.ftc.learnbot.components.Gimbal;
import org.firstinspires.ftc.learnbot.components.Pedro;
import org.firstinspires.ftc.learnbot.subsystems.AllianceDetection;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;

public class Hardware implements Loggable {

    public List<LynxModule> hubs;
    public HardwareMap map;
    public IGyro imu;
    public EncodedMotor<DcMotorEx> fl, fr, rl, rr, testMotor;
    public AllianceDetection allianceDetection;
    public Limelight3A limelight;
    public SparkFunOTOS odo;
    public Follower follower;
    public Servo yawServo, pitchServo;

    /* Put other hardware here! */

    public Hardware(HardwareMap hwmap) {
        map = hwmap;
        hubs = hwmap.getAll(LynxModule.class);
        // imu = new AdafruitIMU(HardwareNames.EXTERNALIMU, AdafruitIMU.Orientation.Pitch);
        imu = new IMU(
            Setup.HardwareNames.IMU,
            RevHubOrientationOnRobot.LogoFacingDirection.UP,
            RevHubOrientationOnRobot.UsbFacingDirection.BACKWARD
        );
        if (Setup.Connected.DRIVEBASE) {
            fl = new EncodedMotor<>(Pedro.Config.FLMOTOR);
            fr = new EncodedMotor<>(Pedro.Config.FRMOTOR);
            rl = new EncodedMotor<>(Pedro.Config.RLMOTOR);
            rr = new EncodedMotor<>(Pedro.Config.RRMOTOR);
            follower = Pedro.createFollower(hwmap);
        }
        if (Setup.Connected.OTOS) {
            odo = hwmap.get(SparkFunOTOS.class, Pedro.Config.Localizer.OTOSConfig.hardwareName);
        }
        if (Setup.Connected.LIMELIGHT) {
            limelight = hwmap.get(Limelight3A.class, HardwareNames.LIMELIGHT);
        }
        if (Setup.Connected.GIMBAL) {
            yawServo = new Servo(Gimbal.Config.YAW_SERVO);
            pitchServo = new Servo(Gimbal.Config.PITCH_SERVO);
        }
        allianceDetection = new AllianceDetection(
            hwmap,
            HardwareNames.ALLIANCE_SWITCH_RED,
            HardwareNames.ALLIANCE_SWITCH_BLUE
        );
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
