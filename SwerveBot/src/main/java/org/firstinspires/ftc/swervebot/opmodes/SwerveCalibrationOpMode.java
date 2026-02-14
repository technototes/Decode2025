package org.firstinspires.ftc.swervebot.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.technototes.library.hardware.motor.CRServo;

import org.firstinspires.ftc.swervebot.Setup;
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
@TeleOp(name = "Swerve Encoder Calibration")
public class SwerveCalibrationOpMode extends LinearOpMode {
    CRServo flsteer = null;
    CRServo frsteer = null;

    CRServo rlsteer = null;
    CRServo rrsteer =null;


    @Override
    public void runOpMode() {
        // Initialize encoders
        AbsoluteAnalogEncoder flEncoder = new AbsoluteAnalogEncoder(
            hardwareMap.get(AnalogInput.class, Setup.HardwareNames.FL_SWERVO_ENCODER)
        );
        AbsoluteAnalogEncoder frEncoder = new AbsoluteAnalogEncoder(
            hardwareMap.get(AnalogInput.class, Setup.HardwareNames.FR_SWERVO_ENCODER)
        );
        AbsoluteAnalogEncoder blEncoder = new AbsoluteAnalogEncoder(
            hardwareMap.get(AnalogInput.class, Setup.HardwareNames.RL_SWERVO_ENCODER)
        );
        AbsoluteAnalogEncoder brEncoder = new AbsoluteAnalogEncoder(
            hardwareMap.get(AnalogInput.class, Setup.HardwareNames.RR_SWERVO_ENCODER)
        );
        if (hardwareMap.crservo.contains(Setup.HardwareNames.FR_SWERVO)) {
            frsteer = new CRServo(hardwareMap.get(com.qualcomm.robotcore.hardware.CRServo.class, Setup.HardwareNames.FR_SWERVO), Setup.HardwareNames.FR_SWERVO);
        } if (hardwareMap.crservo.contains(Setup.HardwareNames.FL_SWERVO)) {
            flsteer = new CRServo(hardwareMap.get(com.qualcomm.robotcore.hardware.CRServo.class, Setup.HardwareNames.FL_SWERVO), Setup.HardwareNames.FL_SWERVO);
        } if (hardwareMap.crservo.contains(Setup.HardwareNames.RL_SWERVO)) {
            rlsteer = new CRServo(hardwareMap.get(com.qualcomm.robotcore.hardware.CRServo.class, Setup.HardwareNames.RL_SWERVO), Setup.HardwareNames.RL_SWERVO);
        } if (hardwareMap.crservo.contains(Setup.HardwareNames.RR_SWERVO)) {
            rrsteer = new CRServo(hardwareMap.get(com.qualcomm.robotcore.hardware.CRServo.class, Setup.HardwareNames.RR_SWERVO), Setup.HardwareNames.RR_SWERVO);
        }



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
            double frPos = frEncoder.getCurrentPosition();
            double rlPos = blEncoder.getCurrentPosition();
            double rrPos = brEncoder.getCurrentPosition();

            // Read raw voltages for debugging
            double flVolt = flEncoder.getVoltage()  ;
            double frVolt = frEncoder.getVoltage();
            double rlVolt = blEncoder.getVoltage();
            double rrVolt = brEncoder.getVoltage();

            // Display information
            telemetry.addLine("========================================");
            telemetry.addLine("ENCODER POSITIONS (radians)");
            telemetry.addLine("========================================");
            telemetry.addData("Front Left", "%.4f rad (%.1f°)", flPos, Math.toDegrees(flPos));
            telemetry.addData("Front Right", "%.4f rad (%.1f°)", frPos, Math.toDegrees(frPos));
            telemetry.addData("Back Left", "%.4f rad (%.1f°)", rlPos, Math.toDegrees(rlPos));
            telemetry.addData("Back Right", "%.4f rad (%.1f°)", rrPos, Math.toDegrees(rrPos));

            telemetry.addLine();
            telemetry.addLine("========================================");
            telemetry.addLine("COPY THESE VALUES TO YOUR CONSTANTS");
            telemetry.addLine("========================================");
            telemetry.addLine(
                String.format(
                    ".withEncoderOffsets(%.4f)",
                    flPos,
                    frPos,
                    rlPos,
                    rrPos
                )
            );

            telemetry.addLine();
            telemetry.addLine("========================================");
            telemetry.addLine("RAW VOLTAGES (for debugging)");
            telemetry.addLine("========================================");
            telemetry.addData("Front Left", "%.3fV", flVolt);
            telemetry.addData("Front Right", "%.3fV", frVolt);
            telemetry.addData("Back Left", "%.3fV", rlVolt);
            telemetry.addData("Back Right", "%.3fV", rrVolt);
            if (gamepad1.cross && flsteer != null){
                flsteer.setPower(1);
            }
//            if (gamepad1.cross && flsteer != null){
//                flsteer.setPower(0);
//            }
//            if (gamepad1.squareWasPressed() && frsteer != null){
//                frsteer.setPower(1);
//            }
//            if (gamepad1.squareWasReleased() && frsteer != null){
//                flsteer.setPower(0);
//            }
//            if (gamepad1.circleWasPressed() && rlsteer != null){
//                rlsteer.setPower(1);
//            }
//            if (gamepad1.circleWasReleased() && rlsteer != null){
//                flsteer.setPower(0);
//            }
//            if (gamepad1.triangleWasPressed() && rrsteer != null){
//                rrsteer.setPower(1);
//            }
//            if (gamepad1.triangleWasReleased() && rrsteer != null){
//                flsteer.setPower(0);
//            }

            telemetry.addLine();
            telemetry.addLine("========================================");
            telemetry.addLine("Make sure all modules point FORWARD");
            telemetry.addLine("before copying the offset values!");
            telemetry.addLine("========================================");

            telemetry.update();
        }
    }
}
