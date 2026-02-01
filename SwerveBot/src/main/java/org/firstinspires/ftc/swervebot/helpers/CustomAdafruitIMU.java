package org.firstinspires.ftc.swervebot.helpers;

import com.bylazar.configurables.annotations.Configurable;
import com.pedropathing.ftc.localization.CustomIMU;
import com.qualcomm.hardware.adafruit.AdafruitBNO055IMU;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.technototes.library.hardware.sensor.AdafruitIMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.swervebot.Setup;

@Configurable
public class CustomAdafruitIMU implements CustomIMU {

    public static double initialZeroDegrees = 0;
    public double curZero;
    AdafruitIMU imu;

    @Override
    public void initialize(
        HardwareMap hardwareMap,
        String hardwareMapName,
        RevHubOrientationOnRobot hubOrientation
    ) {
        AdafruitBNO055IMU _imu = hardwareMap.get(
            AdafruitBNO055IMU.class,
            Setup.HardwareNames.EXTERNAL_IMU
        );
        imu = new AdafruitIMU(
            _imu,
            Setup.HardwareNames.EXTERNAL_IMU,
            AdafruitIMU.Orientation.Pitch
        );
        curZero = Math.toRadians(initialZeroDegrees);
    }

    @Override
    public double getHeading() {
        return imu.getHeading(AngleUnit.RADIANS) - curZero;
    }

    @Override
    public void resetYaw() {
        curZero = imu.getHeading(AngleUnit.RADIANS);
    }
}
