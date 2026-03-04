package org.firstinspires.ftc.crossbones.subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.technototes.library.hardware.motor.Motor;
import com.technototes.library.subsystem.drivebase.DrivebaseSubsystem;
import com.technototes.library.subsystem.drivebase.SimpleMecanumDrivebaseSubsystem;
import java.util.function.DoubleSupplier;

public class CrossBase extends SimpleMecanumDrivebaseSubsystem<DcMotorEx> {

    public CrossBase(
        DoubleSupplier gyro,
        Motor<DcMotorEx> flMotor,
        Motor<DcMotorEx> frMotor,
        Motor<DcMotorEx> rlMotor,
        Motor<DcMotorEx> rrMotor
    ) {
        super(gyro, flMotor, frMotor, rlMotor, rrMotor);
    }
}
