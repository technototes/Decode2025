package org.firstinspires.ftc.sixteen750.swerveutil;
import com.bylazar.configurables.annotations.Configurable;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.CRServo;

/**
 * Constants class for the Coaxial Swerve Drivetrain
 *
 * This class contains all the configuration parameters needed for the swerve drivetrain.
 * Modify these values to match your robot's physical configuration.
 */
@Configurable
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

    // ==================== MOTOR DIRECTIONS ====================
    // Set these based on how your motors are mounted

    public DcMotorSimple.Direction frontLeftDriveMotorDirection = DcMotorSimple.Direction.FORWARD;
    public DcMotorSimple.Direction frontRightDriveMotorDirection = DcMotorSimple.Direction.REVERSE;
    public DcMotorSimple.Direction backLeftDriveMotorDirection = DcMotorSimple.Direction.FORWARD;
    public DcMotorSimple.Direction backRightDriveMotorDirection = DcMotorSimple.Direction.REVERSE;

    // ==================== SERVO DIRECTIONS ====================
    // Set these based on how your servos are mounted

    public CRServo.Direction frontLeftSteeringServoDirection = CRServo.Direction.FORWARD;
    public CRServo.Direction frontRightSteeringServoDirection = CRServo.Direction.FORWARD;
    public CRServo.Direction backLeftSteeringServoDirection = CRServo.Direction.FORWARD;
    public CRServo.Direction backRightSteeringServoDirection = CRServo.Direction.FORWARD;

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

    /**
     * Wheel diameter in inches
     * Used for calculating distances and velocities
     */
    public double wheelDiameter = 4.0; // inches

    // ==================== STEERING CONTROL ====================

    /**
     * Proportional gain for steering control
     * Higher values make steering more aggressive but can cause oscillation
     * Start with a low value (0.5-1.0) and increase if steering is too slow
     */
    public double steeringKp = 1.5;

    /**
     * Maximum steering speed (radians per second)
     * This limits how fast the modules can rotate
     */
    public double steeringSpeed = Math.PI; // 180 degrees per second

    // ==================== MOTOR SPECIFICATIONS ====================

    /**
     * Maximum RPM of your drive motors
     * This is used for velocity calculations
     * Common FTC motors:
     * - HD Hex Motor: 6000 RPM
     * - Core Hex Motor: 125 RPM
     * - goBILDA 5202/5203: 435 RPM
     * - goBILDA 5204: 312 RPM
     */
    public double motorMaxRPM = 435.0;

    /**
     * Gear ratio from motor to wheel
     * If your motor drives the wheel directly, this is 1.0
     * If you have a gear reduction, calculate: motor_teeth / wheel_teeth
     * Example: 12 tooth motor gear to 60 tooth wheel gear = 12/60 = 0.2
     */
    public double gearRatio = 1.0;

    // ==================== ENCODER CONFIGURATION ====================

    /**
     * Ticks per revolution for your drive motors
     * Common FTC motor encoders:
     * - HD Hex Motor: 28 ticks/rev
     * - Core Hex Motor: 288 ticks/rev
     * - goBILDA motors: 28 ticks/rev (at the motor)
     */
    public double motorTicksPerRev = 28.0;

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
    public CoaxialSwerveConstants withEncoderNames(String fl, String fr, String bl, String br) {
        this.frontLeftEncoderName = fl;
        this.frontRightEncoderName = fr;
        this.rearLeftEncoderName = bl;
        this.rearRightEncoderName = br;
        return this;
    }

    /**
     * Builder-style method to set encoder offsets
     * Use this after calibrating your modules
     */
    public CoaxialSwerveConstants withEncoderOffsets(double fl, double fr, double bl, double br) {
        this.frontLeftEncoderOffset = fl;
        this.frontRightEncoderOffset = fr;
        this.rearRightEncoderOffset = bl;
        this.rearRightEncoderOffset = br;
        return this;
    }

    /**
     * Builder-style method to set physical dimensions
     */
    public CoaxialSwerveConstants withDimensions(double trackWidth, double wheelBase, double wheelDiameter) {
        this.trackWidth = trackWidth;
        this.wheelBase = wheelBase;
        this.wheelDiameter = wheelDiameter;
        return this;
    }

    /**
     * Builder-style method to set steering parameters
     */
    public CoaxialSwerveConstants withSteeringParams(double kp, double maxSpeed) {
        this.steeringKp = kp;
        this.steeringSpeed = maxSpeed;
        return this;
    }
}