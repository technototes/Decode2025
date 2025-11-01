package org.firstinspires.ftc.sixteen750.helpers;

import com.pedropathing.ftc.localization.CustomIMU;
import com.qualcomm.hardware.adafruit.AdafruitBNO055IMU;
import com.qualcomm.hardware.adafruit.AdafruitBNO055IMUNew;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.technototes.library.hardware.sensor.AdafruitIMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.sixteen750.Setup;

public class CustomAdafruitIMU implements CustomIMU {

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
        curZero = 0;
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
