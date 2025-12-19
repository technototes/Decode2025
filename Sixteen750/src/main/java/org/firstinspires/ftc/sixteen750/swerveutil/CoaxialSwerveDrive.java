package org.firstinspires.ftc.sixteen750.swerveutil;
import android.annotation.SuppressLint;

import com.pedropathing.Drivetrain;
import com.pedropathing.math.Vector;
import com.pedropathing.math.MathFunctions;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.AnalogInput;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import org.firstinspires.ftc.sixteen750.swerveutil.AbsoluteAnalogEncoder;
import org.firstinspires.ftc.sixteen750.swerveutil.Angle;

/**
 * Coaxial Swerve Drivetrain Implementation
 *
 * This drivetrain uses 4 modules, each with:
 * - A drive motor (moves the wheel forward/backward)
 * - A steering servo (rotates the module to point in any direction)
 *
 * The "coaxial" design means the drive and steering mechanisms share the same axis.
 */
public class CoaxialSwerveDrive extends Drivetrain {

    // Hardware components
    private DcMotorEx[] driveMotors;      // 4 motors for driving each wheel
    private CRServo[] steeringServos;     // 4 servos for steering each module
    private AbsoluteAnalogEncoder[] steeringEncoders; // 4 absolute encoders for module angles

    // Module positions relative to robot center (for calculating rotation)
    private Vector[] modulePositions;

    // Current target angles for each module (in radians)
    private double[] targetAngles;

    // Current angles of each module (in radians)
    private double[] currentAngles;

    // Constants
    private CoaxialSwerveConstants constants;

    // Voltage sensor for battery voltage monitoring
    private VoltageSensor voltageSensor;

    // Slew rate limiters for smooth acceleration
    private SlewRateLimiter[] driveRateLimiters = new SlewRateLimiter[4];

    // Voltage tracking
    private double currentVoltage = 12.0;
    double[] lastAngleError = new double[4];

    /**
     * Constructor for the Coaxial Swerve Drivetrain
     *
     * @param hardwareMap the FTC hardware map to get motors and servos
     * @param constants the constants object containing configuration parameters
     */
    public CoaxialSwerveDrive(HardwareMap hardwareMap, CoaxialSwerveConstants constants) {
        this.constants = constants;
        this.maxPowerScaling = 1.0;
        this.voltageCompensation = false;
        this.nominalVoltage = 12.0;

        // Get voltage sensor from the Control Hub
        try {
            voltageSensor = hardwareMap.voltageSensor.iterator().next();
        } catch (Exception e) {
            voltageSensor = null;
        }

        // Initialize hardware arrays
        driveMotors = new DcMotorEx[4];
        steeringServos = new CRServo[4];
        steeringEncoders = new AbsoluteAnalogEncoder[4];
        targetAngles = new double[4];
        currentAngles = new double[4];
        for (int i = 0; i < 4; i++) {
            lastAngleError[i] = 0;
        }

        // Initialize slew rate limiters
        for (int i = 0; i < 4; i++) {
            driveRateLimiters[i] = new SlewRateLimiter(constants.driveMaxAcceleration);
        }

        // Get hardware from the hardware map
        driveMotors[0] = hardwareMap.get(DcMotorEx.class, constants.frontLeftDriveMotorName);
        driveMotors[1] = hardwareMap.get(DcMotorEx.class, constants.frontRightDriveMotorName);
        driveMotors[2] = hardwareMap.get(DcMotorEx.class, constants.rearLeftDriveMotorName);
        driveMotors[3] = hardwareMap.get(DcMotorEx.class, constants.rearRightDriveMotorName);

        steeringServos[0] = hardwareMap.get(CRServo.class, constants.frontLeftSteeringServoName);
        steeringServos[1] = hardwareMap.get(CRServo.class, constants.frontRightSteeringServoName);
        steeringServos[2] = hardwareMap.get(CRServo.class, constants.rearLeftSteeringServoName);
        steeringServos[3] = hardwareMap.get(CRServo.class, constants.rearRightSteeringServoName);

        // Initialize absolute encoders for steering feedback

        steeringEncoders[0] = new AbsoluteAnalogEncoder(
                hardwareMap.get(AnalogInput.class, constants.frontLeftEncoderName))
                .zero(constants.frontLeftEncoderOffset)
                .setInverted(constants.isFrontLeftEncoderInverted);

        steeringEncoders[1] = new AbsoluteAnalogEncoder(
                hardwareMap.get(AnalogInput.class, constants.frontRightEncoderName))
                .zero(constants.frontRightEncoderOffset)
                .setInverted(constants.isFrontRightEncoderInverted);
        steeringEncoders[2] = new AbsoluteAnalogEncoder(
                hardwareMap.get(AnalogInput.class, constants.rearLeftEncoderName))
                .zero(constants.rearLeftEncoderOffset)
                .setInverted(constants.isRearLeftEncoderInverted);
        steeringEncoders[3] = new AbsoluteAnalogEncoder(
                hardwareMap.get(AnalogInput.class, constants.rearRightEncoderName))
                .zero(constants.rearRightEncoderOffset)
                .setInverted(constants.isRearRightEncoderInverted);

        // Set motor directions
        driveMotors[0].setDirection(constants.frontLeftDriveMotorDirection);
        driveMotors[1].setDirection(constants.frontRightDriveMotorDirection);
        driveMotors[2].setDirection(constants.rearLeftDriveMotorDirection);
        driveMotors[3].setDirection(constants.rearRightDriveMotorDirection);

        // Set servo directions
        steeringServos[0].setDirection(constants.frontLeftSteeringServoDirection);
        steeringServos[1].setDirection(constants.frontRightSteeringServoDirection);
        steeringServos[2].setDirection(constants.rearLeftSteeringServoDirection);
        steeringServos[3].setDirection(constants.rearRightSteeringServoDirection);

        // Initialize module positions (relative to robot center)
        // These represent the physical location of each module on the robot
        modulePositions = new Vector[4];
        modulePositions[0] = new Vector(constants.trackWidth / 2, constants.wheelBase / 2);   // Front Left
        modulePositions[1] = new Vector(constants.trackWidth / 2, -constants.wheelBase / 2);  // Front Right
        modulePositions[2] = new Vector(-constants.trackWidth / 2, constants.wheelBase / 2);  // Back Left
        modulePositions[3] = new Vector(-constants.trackWidth / 2, -constants.wheelBase / 2); // Back Right

        // Set motor modes
        for (DcMotorEx motor : driveMotors) {
            motor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }
    }

    /**
     * Calculates the drive powers for each swerve module.
     *
     * This is the core method that determines:
     * 1. What direction each module should point
     * 2. How fast each module should drive
     *
     * @param correctivePower translational correction vector (for path following)
     * @param headingPower rotational power for turning
     * @param pathingPower forward movement vector along the path
     * @param robotHeading current robot heading in radians
     * @return array of 8 values: [drive0, steer0, drive1, steer1, drive2, steer2, drive3, steer3]
     */
    @Override
    public double[] calculateDrive(Vector correctivePower, Vector headingPower, Vector pathingPower, double robotHeading) {
        // Combine all the input vectors into a single desired movement vector
        Vector desiredTranslation = correctivePower.plus(pathingPower);

        // Get the desired rotation (heading power magnitude determines rotation speed)
        double desiredRotation = headingPower.getMagnitude() * Math.signum(headingPower.getXComponent());

        // Convert field-relative movement to robot-relative
        // This rotates the desired movement vector by the robot's current heading
        double cos = Math.cos(-robotHeading);
        double sin = Math.sin(-robotHeading);
        double rotatedX = desiredTranslation.getXComponent() * cos - desiredTranslation.getYComponent() * sin;
        double rotatedY = desiredTranslation.getXComponent() * sin + desiredTranslation.getYComponent() * cos;

        // Array to store module speeds and angles
        double[] moduleSpeeds = new double[4];
        double[] moduleAngles = new double[4];
        if (desiredTranslation.getMagnitude() < 0.02 &&
                Math.abs(desiredRotation) < 0.02) {
            double[] zeroPowers = new double[8];
            for (int i = 0; i < 8; i++) {
                zeroPowers[i] = 0;
            }
            return zeroPowers;
        }
        // Calculate the desired state for each swerve module
        for (int i = 0; i < 4; i++) {

            // Calculate the rotational contribution for this module
            // Rotation creates a tangent vector perpendicular to the module's position
            double rotationX = -modulePositions[i].getYComponent() * desiredRotation;
            double rotationY = modulePositions[i].getXComponent() * desiredRotation;

            // Combine translation and rotation
            double moduleVectorX = rotatedX + rotationX;
            double moduleVectorY = rotatedY + rotationY;

            // Calculate the speed (magnitude) and angle (direction) for this module
            moduleSpeeds[i] = Math.sqrt(moduleVectorX * moduleVectorX + moduleVectorY * moduleVectorY);
            moduleAngles[i] = Math.atan2(moduleVectorY, moduleVectorX);

            // Optimize the module angle to avoid rotating more than 90 degrees
            // If the module needs to rotate >90°, we can reverse the drive direction instead
            double angleDifference = Angle.normDelta(moduleAngles[i] - currentAngles[i]);

            // If we need to turn more than 90 degrees, flip the angle and reverse speed
            if (Math.abs(angleDifference) > Math.PI / 2) {
                moduleAngles[i] += Math.PI;
                moduleSpeeds[i] *= -1;

                // Normalize the angle [-pi, pi]
                moduleAngles[i] = Angle.normDelta(moduleAngles[i]);
            }

            // Store the target angle for this module
            targetAngles[i] = moduleAngles[i];
        }

        // Normalize wheel speeds so none exceed 1.0
        double maxSpeed = 0;
        for (double speed : moduleSpeeds) {
            if (Math.abs(speed) > maxSpeed) {
                maxSpeed = Math.abs(speed);
            }
        }

        if (maxSpeed > 1.0) {
            for (int i = 0; i < 4; i++) {
                moduleSpeeds[i] /= maxSpeed;
            }
        }

        // Apply max power scaling
        for (int i = 0; i < 4; i++) {
            moduleSpeeds[i] *= maxPowerScaling;
        }

        // Calculate steering servo powers based on angle error
        double[] steeringPowers = new double[4];
        for (int i = 0; i < 4; i++) {
            // Get actual current angle from absolute encoder
            currentAngles[i] = steeringEncoders[i].getCurrentPosition();

            // Calculate angle error
            double angleError = Angle.normDelta(targetAngles[i] - currentAngles[i]);

            double derivative = angleError - lastAngleError[i];

            double steeringPower =
                    constants.steeringKp * angleError
                            - constants.steeringKd * derivative;

            // Small minimum power to overcome static friction
            if (Math.abs(steeringPower) < constants.steeringMinPower &&
                    Math.abs(angleError) > constants.steeringDeadband) {
                steeringPower = Math.signum(steeringPower) * constants.steeringMinPower;
            }

            steeringPowers[i] = MathFunctions.clamp(steeringPower, -1, 1);

            lastAngleError[i] = angleError;

        }

        // Apply voltage compensation if enabled
        if (voltageCompensation && voltageSensor != null) {
            currentVoltage = voltageSensor.getVoltage();
            double voltageScale = nominalVoltage / currentVoltage;
            for (int i = 0; i < 4; i++) {
                moduleSpeeds[i] *= voltageScale;
                steeringPowers[i] *= voltageScale;
            }
        }

        // Return interleaved array: [drive0, steer0, drive1, steer1, ...]
        return new double[] {
                moduleSpeeds[0], steeringPowers[0],
                moduleSpeeds[1], steeringPowers[1],
                moduleSpeeds[2], steeringPowers[2],
                moduleSpeeds[3], steeringPowers[3]
        };
    }

    /**
     * Runs the drivetrain hardware with the calculated powers.
     *
     * @param drivePowers array containing [drive0, steer0, drive1, steer1, drive2, steer2, drive3, steer3]
     */
    @Override
    public void runDrive(double[] drivePowers) {
        if (drivePowers.length != 8) {
            throw new IllegalArgumentException("Drive powers array must have length 8 for coaxial swerve");
        }

        double[] limitedPowers = new double[8];
        for (int i = 0; i < 4; i++) {
            // Drive motor slew rate limiting
            if (constants.useDriveSlewRateLimiting) {
                limitedPowers[i * 2] = driveRateLimiters[i].calculate(drivePowers[i * 2]);
            } else {
                limitedPowers[i * 2] = drivePowers[i * 2];
            }

            limitedPowers[i * 2 + 1] = drivePowers[i * 2 + 1];


        }
        // Set drive motor powers
        driveMotors[0].setPower(limitedPowers[0]);
        driveMotors[1].setPower(limitedPowers[2]);
        driveMotors[2].setPower(limitedPowers[4]);
        driveMotors[3].setPower(limitedPowers[6]);

        // Set steering servo powers
        steeringServos[0].setPower(limitedPowers[1]);
        steeringServos[1].setPower(limitedPowers[3]);
        steeringServos[2].setPower(limitedPowers[5]);
        steeringServos[3].setPower(limitedPowers[7]);

    }

    @Override
    public void updateConstants() {
        // Update module positions if constants changed
        modulePositions[0] = new Vector(constants.trackWidth / 2, constants.wheelBase / 2);
        modulePositions[1] = new Vector(constants.trackWidth / 2, -constants.wheelBase / 2);
        modulePositions[2] = new Vector(-constants.trackWidth / 2, constants.wheelBase / 2);
        modulePositions[3] = new Vector(-constants.trackWidth / 2, -constants.wheelBase / 2);
    }

    @Override
    public void breakFollowing() {
        // Stop all motors and servos
        for (DcMotorEx motor : driveMotors) {
            motor.setPower(0);
        }
        for (CRServo servo : steeringServos) {
            servo.setPower(0);
        }

        // Reset slew rate limiters to zero
        for (int i = 0; i < 4; i++) {
            driveRateLimiters[i].reset(0);
        }
    }

    @Override
    public void startTeleopDrive() {
        startTeleopDrive(true);
    }

    @Override
    public void startTeleopDrive(boolean brakeMode) {
        DcMotor.ZeroPowerBehavior behavior = brakeMode ?
                DcMotor.ZeroPowerBehavior.BRAKE : DcMotor.ZeroPowerBehavior.FLOAT;

        for (DcMotorEx motor : driveMotors) {
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setZeroPowerBehavior(behavior);
        }
    }

    @Override
    public double xVelocity() {
        // Calculate average velocity in X direction from all modules
        // This would typically use encoder velocities
        double sum = 0;
        for (int i = 0; i < 4; i++) {
            sum += driveMotors[i].getVelocity() * Math.cos(currentAngles[i]);
        }
        return sum / 4.0;
    }

    @Override
    public double yVelocity() {
        // Calculate average velocity in Y direction from all modules
        double sum = 0;
        for (int i = 0; i < 4; i++) {
            sum += driveMotors[i].getVelocity() * Math.sin(currentAngles[i]);
        }
        return sum / 4.0;
    }

    @Override
    public void setXVelocity(double xMovement) {
        // Not typically used in swerve drive (use calculateDrive instead)
        // This is a simplified implementation
    }

    @Override
    public void setYVelocity(double yMovement) {
        // Not typically used in swerve drive (use calculateDrive instead)
        // This is a simplified implementation
    }

    @Override
    public double getVoltage() {
        // Read current battery voltage from the voltage sensor
        if (voltageSensor != null) {
            currentVoltage = voltageSensor.getVoltage();
            return currentVoltage;
        }
        // Return nominal voltage if sensor unavailable
        return nominalVoltage;
    }
    @Override
    public String debugString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Coaxial Swerve Drivetrain Debug:\n");
        for (int i = 0; i < 4; i++) {
            sb.append(String.format("Module %d: Speed=%.2f, Angle=%.2f°, Target=%.2f°\n",
                    i, driveMotors[i].getPower(),
                    Math.toDegrees(currentAngles[i]),
                    Math.toDegrees(targetAngles[i])));
        }
        sb.append(String.format("Voltage: %.2fV\n", currentVoltage));
        return sb.toString();
    }
}



