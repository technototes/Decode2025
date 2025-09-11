package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.TrajectoryBuilder;
import com.acmerobotics.roadrunner.trajectory.constraints.AngularVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MecanumVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.ProfileAccelerationConstraint;
import com.robotcode.shared.DO_NOT_EDIT_20403.AutoConstants;
import java.io.*;
import java.util.Arrays;
import javax.imageio.ImageIO;
import org.rowlandhall.meepmeep.MeepMeep;
import org.rowlandhall.meepmeep.roadrunner.*;
import org.rowlandhall.meepmeep.roadrunner.entity.RoadRunnerBotEntity;
import org.rowlandhall.meepmeep.roadrunner.trajectorysequence.TrajectorySequence;

//public class Twenty403Testing {
//
//    //Wing Red
//    AutoConstants vals;
//
//    public static void main(String[] args) {
//        // Make this as large as possible while still fitting on our laptop screens:
//        MeepMeep meepMeep = new MeepMeep(600);
//        // TODO: Pull this data from the drivebase code, thereby eliminating the need for the
//        // "func = (Pose2d pose) -> ..." line of code

//        // Constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, trackWidth
//        // maxVel: The fastest dist/sec we'll travel (velocity)
//        // maxAcc: The fastest rate (dist/sec/sec) we'll change our velocity (acceleration)
//        // maxAngVel: the fastest degrees/sec we'll rotate (angular velocity)
//        // maxAngAcc: the fastest rate (deg/sec/sec) we'll change our rotation (angular acceleration) @MaxAngleAccel
//        // trackWidth: The width of our wheelbase (not clear what this really affects...) @TrackWidth
//        MinVelocityConstraint min_vel = new MinVelocityConstraint(
//            Arrays.asList(
//                new AngularVelocityConstraint(60 /* @MaxAngleVelo */),
//                new MecanumVelocityConstraint(60 /* @MaxVelo */, 14 /* @TrackWidth */)
//            )
//        );
//        ProfileAccelerationConstraint prof_accel = new ProfileAccelerationConstraint(
//            30
//            /* @MaxAccel */
//        );
//        AutoConstants.fwdFunc = (Pose2d pose) -> new TrajectoryBuilder(pose, min_vel, prof_accel);
//        AutoConstants.revFunc = (Pose2d pose) ->
//            new TrajectoryBuilder(pose, Math.PI + pose.getHeading(), min_vel, prof_accel);
//        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
//            .setDimensions(18, 17.5)
//            .followTrajectorySequence(drive -> getTestTrajectory(drive));
//        try {
//            // Try to load the field image from the repo:
//            meepMeep.setBackground(ImageIO.read(new File("Field.jpg")));
//        } catch (IOException io) {
//            // If we can't find the field image, fall back to the gray grid
//            meepMeep.setBackground(MeepMeep.Background.GRID_GRAY);
//        }
//        meepMeep.setBackgroundAlpha(0.75f).addEntity(myBot).start();
//    }
//
//    private static TrajectorySequence getTestTrajectory(DriveShim drive) {
//        return drive
//            .build();
//    }
//}
