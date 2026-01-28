package org.firstinspires.ftc.sixteen750.swerveutil;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

/**
 * Constants class for the Coaxial Swerve Drivetrain
 *
 * This class contains all the configuration parameters needed for the swerve drivetrain.
 * Modify these values to match your robot's physical configuration.
 */
public class CoaxialSwerveConstants {



    // ==================== HARDWARE NAMES ====================
    // Change these to match your robot's configuration file

    public String frontLeftDriveMotorName = "frontLeftDrive";
    public String frontRightDriveMotorName = "frontRightDrive";
    public String rearLeftDriveMotorName = "rearLeftDrive";
    public String rearRightDriveMotorName = "rearRightDrive";

    public String frontLeftSteeringServoName = "frontLeftSteer";
    public String frontRightSteeringServoName = "frontRightSteer";
    public String rearLeftSteeringServoName = "rearLeftSteer";
    public String rearRightSteeringServoName = "rearRightSteer";

    // Absolute encoder names for steering feedback
    public String frontLeftEncoderName = "frontLeftEncoder";
    public String frontRightEncoderName = "frontRightEncoder";
    public String rearLeftEncoderName = "rearLeftEncoder";
    public String rearRightEncoderName = "rearRightEncoder";

    // Encoder zero offsets (in radians)
    // These should be calibrated by pointing each module forward and recording the encoder value
    public double frontLeftEncoderOffset = 0.0;
    public double frontRightEncoderOffset = 0.0;
    public double rearLeftEncoderOffset = 0.0;
    public double rearRightEncoderOffset = 0.0;
    public double frontLeftSmoothing = 0.3;
    public double frontRightSmoothing = 0.3;
    public double rearLeftSmoothing = 0.3;
    public double rearRightSmoothing = 0.3;

    // encoder inverted status
    public boolean isFrontLeftEncoderInverted = false;
    public boolean isFrontRightEncoderInverted = false;
    public boolean isRearLeftEncoderInverted = false;
    public boolean isRearRightEncoderInverted = false;

    // ==================== MOTOR DIRECTIONS ====================
    // Set these based on how your motors are mounted

    public DcMotorSimple.Direction frontLeftDriveMotorDirection = DcMotorSimple.Direction.FORWARD;
    public DcMotorSimple.Direction frontRightDriveMotorDirection = DcMotorSimple.Direction.REVERSE;
    public DcMotorSimple.Direction rearLeftDriveMotorDirection = DcMotorSimple.Direction.FORWARD;
    public DcMotorSimple.Direction rearRightDriveMotorDirection = DcMotorSimple.Direction.REVERSE;

    // ==================== SERVO DIRECTIONS ====================
    // Set these based on how your servos are mounted

    public CRServo.Direction frontLeftSteeringServoDirection = CRServo.Direction.FORWARD;
    public CRServo.Direction frontRightSteeringServoDirection = CRServo.Direction.FORWARD;
    public CRServo.Direction rearLeftSteeringServoDirection = CRServo.Direction.FORWARD;
    public CRServo.Direction rearRightSteeringServoDirection = CRServo.Direction.FORWARD;

    // ==================== PHYSICAL DIMENSIONS ====================
    // All measurements should be in the same unit (inches or meters)

    /**
     * Track width: distance between left and right wheels (side to side)
     * Measure from the center of one wheel to the center of the opposite wheel
     */
    public double trackWidth = 12.0; // inches

    /**
     * Wheel base: distance between front and back wheels (front to back)
     * Measure from the center of one wheel to the center of the opposite wheel
     */
    public double wheelBase = 12.0; // inches

    // ==================== STEERING CONTROL ====================

    /**
     * Proportional gain for steering control
     * Higher values make steering more aggressive but can cause oscillation
     * Start with a low value (0.5-1.0) and increase if steering is too slow
     */
    PIDFCoefficients steeringPIDF = new PIDFCoefficients(0, 0, 0,0);

    /**
     * Maximum steering speed (radians per second)
     * This limits how fast the modules can rotate
     */
    public double steeringSpeed = Math.PI; // 180 degrees per second

    // ==================== SLEW RATE LIMITING ====================

    /**
     * Enable slew rate limiting for drive motors
     * Prevents sudden acceleration/deceleration
     */
    public boolean useDriveSlewRateLimiting = true;

    /**
     * Maximum drive power change per second
     * Example: 2.0 means power can change from 0 to 1.0 in 0.5 seconds
     * Higher values = more responsive but jerkier
     * Lower values = smoother but less responsive
     */
    public double driveMaxAcceleration = 4.0; // 0 to 1.0 in 0.25 seconds

    public double steeringDeadband = Math.toRadians(2);

    public double steeringMinPower = 0.05;
    /**
     * Constructor with default values
     * You can create instances with different configurations
     */
    public CoaxialSwerveConstants() {
        // Uses default values defined above
    }

    /**
     * Constructor with custom track width and wheel base
     * Useful for quickly creating configurations for different robot sizes
     *
     * @param trackWidth distance between left and right wheels
     * @param wheelBase distance between front and back wheels
     */
    public CoaxialSwerveConstants(double trackWidth, double wheelBase) {
        this.trackWidth = trackWidth;
        this.wheelBase = wheelBase;
    }

    /**
     * Builder-style method to set hardware names
     * This allows for cleaner configuration code
     */
    public CoaxialSwerveConstants withMotorNames(String fl, String fr, String rl, String rr) {
        this.frontLeftDriveMotorName = fl;
        this.frontRightDriveMotorName = fr;
        this.rearLeftDriveMotorName = rl;
        this.rearRightDriveMotorName = rr;
        return this;
    }

    /**
     * Builder-style method to set servo names
     */
    public CoaxialSwerveConstants withServoNames(String fl, String fr, String rl, String rr) {
        this.frontLeftSteeringServoName = fl;
        this.frontRightSteeringServoName = fr;
        this.rearLeftSteeringServoName = rl;
        this.rearRightSteeringServoName = rr;
        return this;
    }

    /**
     * Builder-style method to set encoder names
     */
    public CoaxialSwerveConstants withEncoderNames(String fl, String fr, String rl, String rr) {
        this.frontLeftEncoderName = fl;
        this.frontRightEncoderName = fr;
        this.rearLeftEncoderName = rl;
        this.rearRightEncoderName = rr;
        return this;
    }

    /**
     * Builder-style method to set encoder offsets
     * Use this after calibrating your modules
     */
    public CoaxialSwerveConstants withEncoderOffsets(double fl, double fr, double rl, double rr) {
        this.frontLeftEncoderOffset = fl;
        this.frontRightEncoderOffset = fr;
        this.rearLeftEncoderOffset = rl;
        this.rearRightEncoderOffset = rr;
        return this;
    }
    public CoaxialSwerveConstants withEncoderInvertation(boolean fl, boolean fr, boolean rl, boolean rr) {
        this.isFrontLeftEncoderInverted = fl;
        this.isFrontRightEncoderInverted = fr;
        this.isRearLeftEncoderInverted = rl;
        this.isRearRightEncoderInverted = rr;
        return this;
    }

    /**
     * Builder-style method to set physical dimensions
     */
    public CoaxialSwerveConstants withDimensions(double trackWidth, double wheelBase, double wheelDiameter) {
        this.trackWidth = trackWidth;
        this.wheelBase = wheelBase;
        return this;
    }

    public CoaxialSwerveConstants withEncoderSmoothing(double fl, double fr, double rl, double rr) {
        this.frontLeftSmoothing = fl;
        this.frontRightSmoothing = fr;
        this.rearLeftSmoothing = rl;
        this.rearRightSmoothing = rr;
        return this;
    }

    /**
     * Builder-style method to set steering parameters
     */
    public CoaxialSwerveConstants withSteeringParams(PIDFCoefficients steeringPIDF, double maxSpeed, double db, double minPower) {
        this.steeringPIDF = steeringPIDF;
        this.steeringSpeed = maxSpeed;
        this.steeringDeadband = db;
        this.steeringMinPower = minPower;
        return this;
    }
}