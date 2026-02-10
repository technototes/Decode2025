package org.firstinspires.ftc.swervebot.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import org.firstinspires.ftc.swervebot.swerveutil.AbsoluteAnalogEncoder;

/**
 * Calibration OpMode for Swerve Drive Absolute Encoders
 *
 * This OpMode helps you find the zero offsets for your swerve module encoders.
 *
 * INSTRUCTIONS:
 * 1. Manually align all 4 swerve modules to point straight forward
 * 2. Run this OpMode
 * 3. Read the encoder values from telemetry
 * 4. Copy these values into your CoaxialSwerveConstants as encoder offsets
 *
 * The offsets ensure that when your code thinks the modules are at 0°,
 * they're actually pointing forward.
 */
@TeleOp(name = "Swerve Encoder Calibration", group = "Calibration")
public class SwerveCalibrationOpMode extends LinearOpMode {

    // Change these to match your robot configuration
    private static final String FL_ENCODER = "servoenc";
//    private static final String FR_ENCODER = "frontRightEncoder";
//    private static final String BL_ENCODER = "backLeftEncoder";
//    private static final String BR_ENCODER = "backRightEncoder";

    @Override
    public void runOpMode() {
        // Initialize encoders
        AbsoluteAnalogEncoder flEncoder = new AbsoluteAnalogEncoder(
            hardwareMap.get(AnalogInput.class, FL_ENCODER)
        );
//        AbsoluteAnalogEncoder frEncoder = new AbsoluteAnalogEncoder(
//            hardwareMap.get(AnalogInput.class, FR_ENCODER)
//        );
//        AbsoluteAnalogEncoder blEncoder = new AbsoluteAnalogEncoder(
//            hardwareMap.get(AnalogInput.class, BL_ENCODER)
//        );
//        AbsoluteAnalogEncoder brEncoder = new AbsoluteAnalogEncoder(
//            hardwareMap.get(AnalogInput.class, BR_ENCODER)
//        );

        telemetry.addLine("========================================");
        telemetry.addLine("SWERVE ENCODER CALIBRATION");
        telemetry.addLine("========================================");
        telemetry.addLine();
        telemetry.addLine("INSTRUCTIONS:");
        telemetry.addLine("1. Manually rotate all modules to point");
        telemetry.addLine("   straight FORWARD on your robot");
        telemetry.addLine("2. Press START when aligned");
        telemetry.addLine("3. Copy the offset values to your");
        telemetry.addLine("   CoaxialSwerveConstants class");
        telemetry.update();

        waitForStart();

        while (opModeIsActive()) {
            // Read current positions
            double flPos = flEncoder.getCurrentPosition() ;
//            double frPos = frEncoder.getRawAngle();
//            double blPos = blEncoder.getRawAngle();
//            double brPos = brEncoder.getRawAngle();

            // Read raw voltages for debugging
            double flVolt = flEncoder.getVoltage();
//            double frVolt = frEncoder.getVoltage();
//            double blVolt = blEncoder.getVoltage();
//            double brVolt = brEncoder.getVoltage();

            // Display information
            telemetry.addLine("========================================");
            telemetry.addLine("ENCODER POSITIONS (radians)");
            telemetry.addLine("========================================");
            telemetry.addData("Front Left", "%.4f rad (%.1f°)", flPos, Math.toDegrees(flPos));
//            telemetry.addData("Front Right", "%.4f rad (%.1f°)", frPos, Math.toDegrees(frPos));
//            telemetry.addData("Back Left", "%.4f rad (%.1f°)", blPos, Math.toDegrees(blPos));
//            telemetry.addData("Back Right", "%.4f rad (%.1f°)", brPos, Math.toDegrees(brPos));

            telemetry.addLine();
            telemetry.addLine("========================================");
            telemetry.addLine("COPY THESE VALUES TO YOUR CONSTANTS");
            telemetry.addLine("========================================");
            telemetry.addLine(
                String.format(
                    ".withEncoderOffsets(%.4f)",
                    flPos
//                    frPos,
//                    blPos,
//                    brPos
                )
            );

            telemetry.addLine();
            telemetry.addLine("========================================");
            telemetry.addLine("RAW VOLTAGES (for debugging)");
            telemetry.addLine("========================================");
            telemetry.addData("Front Left", "%.3fV", flVolt);
//            telemetry.addData("Front Right", "%.3fV", frVolt);
//            telemetry.addData("Back Left", "%.3fV", blVolt);
//            telemetry.addData("Back Right", "%.3fV", brVolt);

            telemetry.addLine();
            telemetry.addLine("========================================");
            telemetry.addLine("Make sure all modules point FORWARD");
            telemetry.addLine("before copying the offset values!");
            telemetry.addLine("========================================");

            telemetry.update();
        }
    }
}
