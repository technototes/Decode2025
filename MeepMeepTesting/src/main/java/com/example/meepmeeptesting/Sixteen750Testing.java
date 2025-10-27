package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.acmerobotics.roadrunner.trajectory.constraints.AngularVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.ProfileAccelerationConstraint;
import com.robotcode.shared.DO_NOT_EDIT_16750;
import com.robotcode.shared.DO_NOT_EDIT_16750.PathConstants;
import java.io.*;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.roadrunner.*;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;
import org.rowlandhall.meepmeep.roadrunner.trajectorysequence.TrajectorySequence;

public class Sixteen750Testing {

    //Wing Red
    PathConstants vals;

    public static void main(String[] args) {
        // Make this as large as possible while still fitting on our laptop screens:
        MeepMeep meepMeep = new MeepMeep(600);
        // TODO: Pull this data from the drivebase code, thereby eliminating the need for the
        // "func = (Pose2d pose) -> ..." line of code

        // Constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, trackWidth
        // maxVel: The fastest dist/sec we'll travel (velocity)
        // maxAcc: The fastest rate (dist/sec/sec) we'll change our velocity (acceleration)
        // maxAngVel: the fastest degrees/sec we'll rotate (angular velocity)
        // maxAngAcc: the fastest rate (deg/sec/sec) we'll change our rotation (angular acceleration) @MaxAngleAccel
        // trackWidth: The width of our wheelbase (not clear what this really affects...) @TrackWidth
        MinVelocityConstraint min_vel = new MinVelocityConstraint(
            Arrays.asList(
                new AngularVelocityConstraint(60 /* @MaxAngleVelo */),
                new MecanumVelocityConstraint(60 /* @MaxVelo */, 14 /* @TrackWidth */)
            )
        );
        ProfileAccelerationConstraint prof_accel = new ProfileAccelerationConstraint(
            30
            /* @MaxAccel */
        );
        PathConstants.fwdFunc = (Pose2d pose) -> new TrajectoryBuilder(pose, min_vel, prof_accel);
        PathConstants.revFunc = (Pose2d pose) ->
            new TrajectoryBuilder(pose, Math.PI + pose.getHeading(), min_vel, prof_accel);
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
            .setDimensions(14, 17)
            .followTrajectorySequence(Sixteen750Testing::getRedTrajectory);
        try {
            // Try to load the field image from the repo:
            meepMeep.setBackground(ImageIO.read(new File("Field.jpg")));
        } catch (IOException io) {
            // If we can't find the field image, fall back to the gray grid
            meepMeep.setBackground(MeepMeep.Background.GRID_GRAY);
        }
        meepMeep.setBackgroundAlpha(0.75f).addEntity(myBot).start();
    } //Wing Red

    private static TrajectorySequence getRedTrajectory(DriveShim drive) {
        return drive
            .trajectorySequenceBuilder(PathConstants.RSTART_FAR_LAUNCHZONE)
            .addTrajectory(PathConstants.RFARSTART_TO_LAUNCH.get())
                .addTrajectory(PathConstants.RLAUNCH_TO_PICKUP1.get())
                .addTrajectory(PathConstants.RPICKUP1_TO_PICKUP1END.get())
                .addTrajectory(PathConstants.RPICKUP1END_TO_LAUNCH.get())
                .addTrajectory(PathConstants.RLAUNCH_TO_PICKUP2.get())
                .addTrajectory(PathConstants.RPICKUP2_TO_PICKUP2END.get())
                .addTrajectory(PathConstants.RPICKUP2END_TO_LAUNCH.get())
                .addTrajectory(PathConstants.RLAUNCH_TO_PICKUP3.get())
                .addTrajectory(PathConstants.RPICKUP3_TO_PICKUP3END.get())
                .addTrajectory(PathConstants.RPICKUP3END_TO_LAUNCH.get())
                .build();
    }

    private static TrajectorySequence getParkingTrajectory(DriveShim drive) {
        return drive
            .trajectorySequenceBuilder(PathConstants.PICKUP2_START)
            //.turn(Math.toRadians(135))
            // .setReversed(true)
            //.addTrajectory(AutoConstants.NETSCORING_TO_NETSCORING_CLEAR.get())
            //.addTrajectory(AutoConstants.NETSCORING_CLEAR_TO_ASCENT.get())
            .addTrajectory(PathConstants.PICKUP2_TO_PICKUP2END.get())
            .build();
    }

    private static TrajectorySequence getBlueTrajectory(DriveShim drive) {
        return drive
            .trajectorySequenceBuilder(PathConstants.PICKUP3_START)
            .addTrajectory(PathConstants.PICKUP3_TO_PICKUP3END.get())
            //.turn(Math.toRadians(-135))
            //.addTrajectory(AutoConstants.START_TO_MID_CLEAR.get())
            //.addTrajectory(WingRed.MIDSPIKE_TO_RIGHT_SPIKE.get())
            //.addTrajectory(WingRed.RIGHT_SPIKE_TO_MIDSPIKE.get())
            //.addTrajectory(WingRed.MIDSPIKE_TO_BACK.get())
            //.addTrajectory(AutoConstantsRed.Wing.BACK_TO_PARK_CORNER.get())
            //.addTrajectory(AutoConstantsRed.Stage.LEFT_SPIKE_TO_CENTER_SPIKE.get())
            //.addTrajectory(AutoConstantsRed.Stage.CENTER_SPIKE_TO_RIGHT_SPIKE.get())
            .build();
    }

    /*

    public static void main(String[] args) {
        // Make this as large as possible while still fitting on our laptop screens:
        MeepMeep meepMeep = new MeepMeep(600);
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
            .setDimensions(14, 14)
            // Constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, trackWidth
            // maxVel: The fastest dist/sec we'll travel (velocity)
            // maxAcc: The fastest rate (dist/sec/sec) we'll change our velocity (acceleration)
            // maxAngVel: the fastest degrees/sec we'll rotate (angular velocity)
            // maxAngAcc: the fastest rate (deg/sec/sec) we'll change our rotation (angular acceleration)
            // trackWidth: The width of our wheelbase (not clear what this really affects...)
            .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 9.5)
            .followTrajectorySequence(drive ->
                drive
                    .trajectorySequenceBuilder(AutoConstantsRed.Wing.START)
                    //.addTrajectory(AutoConstantsRed.Stage.START_TO_LEFT_LOW.get())
                    .addTrajectory(AutoConstantsBlue.Wing.START_TO_CENTER_SPIKE.get())
                        .addTrajectory(AutoConstantsBlue.Wing.CENTER_SPIKE_TO_BACK.get())
                        .addTrajectory(AutoConstantsBlue.Wing.BACK_TO_PARK_CORNER.get())
                    //.addTrajectory(AutoConstantsRed.Stage.LEFT_SPIKE_TO_CENTER_SPIKE.get())
                    //.addTrajectory(AutoConstantsRed.Stage.CENTER_SPIKE_TO_RIGHT_SPIKE.get())
                    .build()
            );
        try {
            // Try to load the field image from the repo:
            meepMeep.setBackground(ImageIO.read(new File("Field.jpg")));
        } catch (IOException io) {
            // If we can't find the field image, fall back to the gray grid
            meepMeep.setBackground(MeepMeep.Background.GRID_GRAY);
        }
        meepMeep.setBackgroundAlpha(0.75f).addEntity(myBot).start();
    } //Wing Blue

/*
    public static void main(String[] args) {
        // Make this as large as possible while still fitting on our laptop screens:
        MeepMeep meepMeep = new MeepMeep(600);
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                .setDimensions(14, 14)
                // Constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, trackWidth
                // maxVel: The fastest dist/sec we'll travel (velocity)
                // maxAcc: The fastest rate (dist/sec/sec) we'll change our velocity (acceleration)
                // maxAngVel: the fastest degrees/sec we'll rotate (angular velocity)
                // maxAngAcc: the fastest rate (deg/sec/sec) we'll change our rotation (angular acceleration)
                // trackWidth: The width of our wheelbase (not clear what this really affects...)
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 9.5)
                .followTrajectorySequence(drive ->
                        drive
                                .trajectorySequenceBuilder(AutoConstantsRed.Stage.START)
                                //.addTrajectory(AutoConstantsRed.Stage.START_TO_LEFT_LOW.get())
                                .addTrajectory(AutoConstantsRed.Stage.START_TO_CENTER_SPIKE.get())
                                .addTrajectory(AutoConstantsRed.Stage.CENTER_SPIKE_TO_START.get())
                                .addTrajectory(AutoConstantsRed.Stage.START_TO_PARK_CENTER.get())
                                //.addTrajectory(AutoConstantsRed.Stage.LEFT_SPIKE_TO_CENTER_SPIKE.get())
                                //.addTrajectory(AutoConstantsRed.Stage.CENTER_SPIKE_TO_RIGHT_SPIKE.get())
                                .build()
                );
        try {
            // Try to load the field image from the repo:
            meepMeep.setBackground(ImageIO.read(new File("Field.jpg")));
        } catch (IOException io) {
            // If we can't find the field image, fall back to the gray grid
            meepMeep.setBackground(MeepMeep.Background.GRID_GRAY);
        }
        meepMeep.setBackgroundAlpha(0.75f).addEntity(myBot).start();
    } //Stage Red Center -> done 10/10/23
    public static void main(String[] args) {
        // Make this as large as possible while still fitting on our laptop screens:
        MeepMeep meepMeep = new MeepMeep(600);
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
            .setDimensions(14, 14)
            // Constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, trackWidth
            // maxVel: The fastest dist/sec we'll travel (velocity)
            // maxAcc: The fastest rate (dist/sec/sec) we'll change our velocity (acceleration)
            // maxAngVel: the fastest degrees/sec we'll rotate (angular velocity)
            // maxAngAcc: the fastest rate (deg/sec/sec) we'll change our rotation (angular acceleration)
            // trackWidth: The width of our wheelbase (not clear what this really affects...)
            .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 9.5)
            .followTrajectorySequence(drive ->
                drive
                    .trajectorySequenceBuilder(AutoConstantsRed.Stage.START)
                    //.addTrajectory(AutoConstantsRed.Stage.START_TO_LEFT_LOW.get())
                    .addTrajectory(AutoConstantsRed.Stage.START_TO_RIGHT_SPIKE.get())
                    .addTrajectory(AutoConstantsRed.Stage.RIGHT_SPIKE_TO_START.get())
                    .addTrajectory(AutoConstantsRed.Stage.START_TO_PARK_CENTER.get())
                    //.addTrajectory(AutoConstantsRed.Stage.LEFT_SPIKE_TO_CENTER_SPIKE.get())
                    //.addTrajectory(AutoConstantsRed.Stage.CENTER_SPIKE_TO_RIGHT_SPIKE.get())
                    .build()
            );
        try {
            // Try to load the field image from the repo:
            meepMeep.setBackground(ImageIO.read(new File("Field.jpg")));
        } catch (IOException io) {
            // If we can't find the field image, fall back to the gray grid
            meepMeep.setBackground(MeepMeep.Background.GRID_GRAY);
        }
        meepMeep.setBackgroundAlpha(0.75f).addEntity(myBot).start();
    } //Stage Red Right -> done 10/10/23
    public static void main(String[] args) {
        // Make this as large as possible while still fitting on our laptop screens:
        MeepMeep meepMeep = new MeepMeep(600);
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                .setDimensions(14, 14)
                // Constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, trackWidth
                // maxVel: The fastest dist/sec we'll travel (velocity)
                // maxAcc: The fastest rate (dist/sec/sec) we'll change our velocity (acceleration)
                // maxAngVel: the fastest degrees/sec we'll rotate (angular velocity)
                // maxAngAcc: the fastest rate (deg/sec/sec) we'll change our rotation (angular acceleration)
                // trackWidth: The width of our wheelbase (not clear what this really affects...)
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 9.5)
                .followTrajectorySequence(drive ->
                        drive
                                .trajectorySequenceBuilder(AutoConstantsRed.Stage.START)
                                //.addTrajectory(AutoConstantsRed.Stage.START_TO_LEFT_LOW.get())
                                .addTrajectory(AutoConstantsRed.Stage.START_TO_LEFT_SPIKE.get())
                                .addTrajectory(AutoConstantsRed.Stage.LEFT_SPIKE_TO_START.get())
                                .addTrajectory(AutoConstantsRed.Stage.START_TO_PARK_CENTER.get())
                                //.addTrajectory(AutoConstantsRed.Stage.LEFT_SPIKE_TO_CENTER_SPIKE.get())
                                //.addTrajectory(AutoConstantsRed.Stage.CENTER_SPIKE_TO_RIGHT_SPIKE.get())
                                .build()
                );
        try {
            // Try to load the field image from the repo:
            meepMeep.setBackground(ImageIO.read(new File("Field.jpg")));
        } catch (IOException io) {
            // If we can't find the field image, fall back to the gray grid
            meepMeep.setBackground(MeepMeep.Background.GRID_GRAY);
        }
        meepMeep.setBackgroundAlpha(0.75f).addEntity(myBot).start();
    }//Stage Red Left -> done 10/10/23


    public static void main(String[] args) {
        // Make this as large as possible while still fitting on our laptop screens:
        MeepMeep meepMeep = new MeepMeep(600);
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                .setDimensions(14, 14)
                // Constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, trackWidth
                // maxVel: The fastest dist/sec we'll travel (velocity)
                // maxAcc: The fastest rate (dist/sec/sec) we'll change our velocity (acceleration)
                // maxAngVel: the fastest degrees/sec we'll rotate (angular velocity)
                // maxAngAcc: the fastest rate (deg/sec/sec) we'll change our rotation (angular acceleration)
                // trackWidth: The width of our wheelbase (not clear what this really affects...)
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 9.5)
                .followTrajectorySequence(drive ->
                        drive
                                .trajectorySequenceBuilder(AutoConstantsBlue.Stage.START)
                                //.addTrajectory(AutoConstantsRed.Stage.START_TO_LEFT_LOW.get())
                                .addTrajectory(AutoConstantsBlue.Stage.START_TO_CENTER_SPIKE.get())
                                .addTrajectory(AutoConstantsBlue.Stage.CENTER_SPIKE_TO_BACK.get())
                                .addTrajectory(AutoConstantsBlue.Stage.START_TO_PARK_CENTER.get())
                                //.addTrajectory(AutoConstantsRed.Stage.LEFT_SPIKE_TO_CENTER_SPIKE.get())
                                //.addTrajectory(AutoConstantsRed.Stage.CENTER_SPIKE_TO_RIGHT_SPIKE.get())
                                .build()
                );
        try {
            // Try to load the field image from the repo:
            meepMeep.setBackground(ImageIO.read(new File("Field.jpg")));
        } catch (IOException io) {
            // If we can't find the field image, fall back to the gray grid
            meepMeep.setBackground(MeepMeep.Background.GRID_GRAY);
        }
        meepMeep.setBackgroundAlpha(0.75f).addEntity(myBot).start();
    }
 //Stage Blue Center -> done 10/10/23
    public static void main(String[] args) {
        // Make this as large as possible while still fitting on our laptop screens:
        MeepMeep meepMeep = new MeepMeep(600);
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                .setDimensions(14, 14)
                // Constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, trackWidth
                // maxVel: The fastest dist/sec we'll travel (velocity)
                // maxAcc: The fastest rate (dist/sec/sec) we'll change our velocity (acceleration)
                // maxAngVel: the fastest degrees/sec we'll rotate (angular velocity)
                // maxAngAcc: the fastest rate (deg/sec/sec) we'll change our rotation (angular acceleration)
                // trackWidth: The width of our wheelbase (not clear what this really affects...)
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 9.5)
                .followTrajectorySequence(drive ->
                        drive
                                .trajectorySequenceBuilder(AutoConstantsBlue.Stage.START)
                                //.addTrajectory(AutoConstantsRed.Stage.START_TO_LEFT_LOW.get())
                                .addTrajectory(AutoConstantsBlue.Stage.START_TO_RIGHT_SPIKE.get())
                                .addTrajectory(AutoConstantsBlue.Stage.RIGHT_SPIKE_TO_START.get())
                                .addTrajectory(AutoConstantsBlue.Stage.START_TO_PARK_CENTER.get())
                                //.addTrajectory(AutoConstantsRed.Stage.LEFT_SPIKE_TO_CENTER_SPIKE.get())
                                //.addTrajectory(AutoConstantsRed.Stage.CENTER_SPIKE_TO_RIGHT_SPIKE.get())
                                .build()
                );
        try {
            // Try to load the field image from the repo:
            meepMeep.setBackground(ImageIO.read(new File("Field.jpg")));
        } catch (IOException io) {
            // If we can't find the field image, fall back to the gray grid
            meepMeep.setBackground(MeepMeep.Background.GRID_GRAY);
        }
        meepMeep.setBackgroundAlpha(0.75f).addEntity(myBot).start();
    }//Stage Blue Right -> done 10/10/23
    public static void main(String[] args) {
        // Make this as large as possible while still fitting on our laptop screens:
        MeepMeep meepMeep = new MeepMeep(600);
        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                .setDimensions(14, 14)
                // Constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, trackWidth
                // maxVel: The fastest dist/sec we'll travel (velocity)
                // maxAcc: The fastest rate (dist/sec/sec) we'll change our velocity (acceleration)
                // maxAngVel: the fastest degrees/sec we'll rotate (angular velocity)
                // maxAngAcc: the fastest rate (deg/sec/sec) we'll change our rotation (angular acceleration)
                // trackWidth: The width of our wheelbase (not clear what this really affects...)
                .setConstraints(60, 60, Math.toRadians(180), Math.toRadians(180), 9.5)
                .followTrajectorySequence(drive ->
                        drive
                                .trajectorySequenceBuilder(AutoConstantsBlue.Stage.START)
                                //.addTrajectory(AutoConstantsRed.Stage.START_TO_LEFT_LOW.get())
                                .addTrajectory(AutoConstantsBlue.Stage.START_TO_LEFT_SPIKE.get())
                                .addTrajectory(AutoConstantsBlue.Stage.LEFT_SPIKE_TO_START.get())
                                .addTrajectory(AutoConstantsBlue.Stage.START_TO_PARK_CENTER.get())
                                //.addTrajectory(AutoConstantsRed.Stage.LEFT_SPIKE_TO_CENTER_SPIKE.get())
                                //.addTrajectory(AutoConstantsRed.Stage.CENTER_SPIKE_TO_RIGHT_SPIKE.get())
                                .build()
                );
        try {
            // Try to load the field image from the repo:
            meepMeep.setBackground(ImageIO.read(new File("Field.jpg")));
        } catch (IOException io) {
            // If we can't find the field image, fall back to the gray grid
            meepMeep.setBackground(MeepMeep.Background.GRID_GRAY);
        }
        meepMeep.setBackgroundAlpha(0.75f).addEntity(myBot).start();
    }//Stage Blue Left -> done 10/10/23

*/
}
