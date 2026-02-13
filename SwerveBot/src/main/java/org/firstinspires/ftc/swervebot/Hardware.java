package org.firstinspires.ftc.swervebot;

import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.hardware.sparkfun.SparkFunOTOS;
import com.qualcomm.robotcore.hardware.AnalogInput;
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
import org.firstinspires.ftc.swervebot.swerveutil.AbsoluteAnalogEncoder;

@Configurable
public class Hardware implements Loggable {

    public List<LynxModule> hubs;

    public IGyro imu;
    public EncodedMotor<DcMotorEx> fl, fr, rl, rr, testMotor;
    public DcMotorEx intake, frswervedrive, flswervedrive, rlswervedrive, rrswervedrive;
    public EncodedMotor launcher1;
    public EncodedMotor launcher2;
    public Servo brake;
    public Servo hood;
    public Servo lever;
    public MotorEncoder odoRL, odoFB;
    public SparkFunOTOS odo;
    public CRServo testCRServo, frswervo, flswervo, rlswervo, rrswervo;
    public Servo testServo;
    public Limelight3A limelight;
    public AbsoluteAnalogEncoder frswervoenc, flswervoenc, rlswervoenc, rrswervoenc;
    public HardwareMap map;

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
        if (Setup.Connected.INTAKESUBSYSTEM) {
            intake = this.map.get(DcMotorEx.class, Setup.HardwareNames.INTAKE_MOTOR);
        }
        if (Setup.Connected.LAUNCHERSUBSYSTEM) {
            launcher1 = new EncodedMotor(Setup.HardwareNames.LAUNCHER_MOTOR1);
            launcher2 = new EncodedMotor(Setup.HardwareNames.LAUNCHER_MOTOR2);
        }
        if (Setup.Connected.LIMELIGHTSUBSYSTEM) {
            limelight = hwmap.get(Limelight3A.class, Setup.HardwareNames.LIMELIGHT);
        }
        if (Setup.Connected.SWERVESUBSYSTEM){
            frswervedrive = this.map.get(DcMotorEx.class, Setup.HardwareNames.FR_SWERVEDRIVE);
            flswervedrive = this.map.get(DcMotorEx.class, Setup.HardwareNames.FL_SWERVEDRIVE);
            rlswervedrive = this.map.get(DcMotorEx.class, Setup.HardwareNames.RL_SWERVEDRIVE);
            rrswervedrive = this.map.get(DcMotorEx.class, Setup.HardwareNames.RR_SWERVEDRIVE);
            frswervo = new CRServo(Setup.HardwareNames.FR_SWERVO);
            flswervo = new CRServo(Setup.HardwareNames.FL_SWERVO);
            rlswervo = new CRServo(Setup.HardwareNames.RL_SWERVO);
            rrswervo = new CRServo(Setup.HardwareNames.RR_SWERVO);
            frswervoenc = new AbsoluteAnalogEncoder(hwmap.get(AnalogInput.class, Setup.HardwareNames.FR_SWERVO_ENCODER));
            flswervoenc = new AbsoluteAnalogEncoder(hwmap.get(AnalogInput.class, Setup.HardwareNames.FL_SWERVO_ENCODER));
            rlswervoenc = new AbsoluteAnalogEncoder(hwmap.get(AnalogInput.class, Setup.HardwareNames.RL_SWERVO_ENCODER));
            rrswervoenc = new AbsoluteAnalogEncoder(hwmap.get(AnalogInput.class, Setup.HardwareNames.RR_SWERVO_ENCODER));
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
