package org.firstinspires.ftc.sixteen750;

import com.bylazar.configurables.annotations.Configurable;
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
import com.technototes.library.hardware.sensor.encoder.MotorEncoder;
import com.technototes.library.hardware.servo.Servo;
import com.technototes.library.logger.Loggable;
import java.util.List;
import org.firstinspires.ftc.robotcore.external.navigation.VoltageUnit;

@Configurable
public class Hardware implements Loggable {

    public List<LynxModule> hubs;

    public IGyro imu;
    public EncodedMotor<DcMotorEx> fl, fr, rl, rr, testMotor;
    public DcMotorEx intake;
    public EncodedMotor launcher1;
    public EncodedMotor launcher2;
    public Servo brake;
    public Servo hood;
    public Servo lever;
    public MotorEncoder odoRL, odoFB;
    public SparkFunOTOS odo;
    public CRServo testCRServo;
    public Servo testServo;
    public Limelight3A limelight;
    public HardwareMap map;
    public EncodedMotor<DcMotorEx> turretMotor;

    /* Put other hardware here! */

    public Hardware(HardwareMap hwmap) {
        map = hwmap;
        hubs = hwmap.getAll(LynxModule.class);
        if (Setup.Connected.EXTERNAL_IMU) {
            imu = new AdafruitIMU(Setup.HardwareNames.EXTERNAL_IMU, AdafruitIMU.Orientation.Pitch);
        } else {
            imu = new IMU(
                Setup.HardwareNames.IMU,
                RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP
            );
        }
        if (Setup.Connected.DRIVEBASE) {
            fl = new EncodedMotor<DcMotorEx>(Setup.HardwareNames.FL_DRIVE_MOTOR);
            fr = new EncodedMotor<DcMotorEx>(Setup.HardwareNames.FR_DRIVE_MOTOR);
            rl = new EncodedMotor<DcMotorEx>(Setup.HardwareNames.RL_DRIVE_MOTOR);
            rr = new EncodedMotor<DcMotorEx>(Setup.HardwareNames.RR_DRIVE_MOTOR);
        }
        if (Setup.Connected.ODOSUBSYSTEM) {
            odoFB = new MotorEncoder(Setup.HardwareNames.ODOFB);
            odoRL = new MotorEncoder(Setup.HardwareNames.ODORL);
        }
        if (Setup.Connected.OTOS) {
            odo = hwmap.get(SparkFunOTOS.class, Setup.HardwareNames.OTOS);
        }
        if (Setup.Connected.INTAKESUBSYSTEM) {
            intake = this.map.get(DcMotorEx.class, Setup.HardwareNames.INTAKE_MOTOR);
        }
        if (Setup.Connected.LAUNCHERSUBSYSTEM) {
            launcher1 = new EncodedMotor(Setup.HardwareNames.LAUNCHER_MOTOR1);
            launcher2 = new EncodedMotor(Setup.HardwareNames.LAUNCHER_MOTOR2);
        }
        if (Setup.Connected.AIMINGSUBSYSTEM) {
            hood = new Servo(Setup.HardwareNames.HOOD_SERVO);
            lever = new Servo(Setup.HardwareNames.LEVER_SERVO);
        }
        if (Setup.Connected.BRAKESUBSYSTEM) {
            brake = new Servo(Setup.HardwareNames.BRAKE_SERVO);
        }
        if (Setup.Connected.TESTSUBSYSTEM) {
            testMotor = new EncodedMotor<>(Setup.HardwareNames.TESTMOTOR);
            testCRServo = new CRServo(Setup.HardwareNames.TESTCRSERVO);
            testServo = new Servo(Setup.HardwareNames.TESTSERVO);
        }
        if (Setup.Connected.LIMELIGHTSUBSYSTEM) {
            limelight = hwmap.get(Limelight3A.class, Setup.HardwareNames.LIMELIGHT);
        }
        if (Setup.Connected.TURRETSUBSYSTEM) {
            turretMotor = new EncodedMotor<>(Setup.HardwareNames.TURRET);
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
