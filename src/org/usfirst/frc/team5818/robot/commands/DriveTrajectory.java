/*
 * This file is part of Rogue-Cephalopod, licensed under the GNU General Public License (GPLv3).
 *
 * Copyright (c) Riviera Robotics <https://github.com/Team5818>
 * Copyright (c) contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.usfirst.frc.team5818.robot.commands;

import org.usfirst.frc.team5818.robot.Robot;
import org.usfirst.frc.team5818.robot.constants.Constants;
import org.usfirst.frc.team5818.robot.constants.Direction;
import org.usfirst.frc.team5818.robot.constants.Side;
import org.usfirst.frc.team5818.robot.subsystems.DriveTrain;
import org.usfirst.frc.team5818.robot.utils.MathUtil;
import org.usfirst.frc.team5818.robot.utils.Trajectory;
import org.usfirst.frc.team5818.robot.utils.TrajectoryFollower;
import org.usfirst.frc.team5818.robot.utils.TrajectoryGenerator;

import edu.wpi.first.wpilibj.command.Command;

/**
 * This controller drives the robot along a specified trajectory.
 */
public class DriveTrajectory extends Command {

	private static final double minDeltaHeading = 2;

	private DriveTrain driveTrain;
	private TrajectoryFollower followerLeft;
	private TrajectoryFollower followerRight;
	boolean stoppingAtEnd;
	double directionMultiplier;
	Direction direction;
	double distance;
	double goalVel;
	double goalHeading;
	double initialVel;
	double kTurn = .2 / Math.PI;
	int loopCount;

	public DriveTrajectory(double dist, double head, double startVel, double endVel, Direction dir, boolean stop) {
		distance = dist;
		driveTrain = Robot.runningRobot.driveTrain;
		requires(driveTrain);
		direction = dir;
		if (dir == Direction.FORWARD) {
			directionMultiplier = -1.0;
		} else {
			directionMultiplier = 1.0;
		}
		goalHeading = head;
		initialVel = startVel;
		stoppingAtEnd = stop;
	}

	public DriveTrajectory(double dist, double startVel, double endVel, Direction dir, boolean stop) {
		this(dist, Integer.MAX_VALUE, startVel, endVel, dir, stop);
	}

	protected void initialize() {
		loopCount = 0;
		double curHeading = driveTrain.getGyroHeading();
		if (goalHeading == Integer.MAX_VALUE) {
			goalHeading = curHeading;
		}
		double deltaHeading = goalHeading - curHeading;
		double radius = Math.abs(Math.abs(distance) / (deltaHeading));

		Trajectory leftProfile = TrajectoryGenerator.generate(.8 * Constants.Constant.maxVelocityIPS(),
				.8 * Constants.Constant.maxAccelIPS2(), .02, initialVel, curHeading, Math.abs(distance), goalVel,
				goalHeading);
		Trajectory rightProfile = leftProfile.copy();

		double faster = (radius + (Constants.Constant.wheelToWheelWidth() / 2.0)) / radius;
		double slower = (radius - (Constants.Constant.wheelToWheelWidth() / 2.0)) / radius;

		if (Math.abs(deltaHeading) > Math.toRadians(minDeltaHeading)) {
			if (deltaHeading > 0) {
				if (direction == Direction.FORWARD) {
					leftProfile.scale(faster);
					rightProfile.scale(slower);
				} else {
					leftProfile.scale(slower);
					rightProfile.scale(faster);
				}
			} else if (deltaHeading <= 0) {
				if (direction == Direction.FORWARD) {
					leftProfile.scale(slower);
					rightProfile.scale(faster);
				} else {
					leftProfile.scale(faster);
					rightProfile.scale(slower);
				}
			}
		}

		followerLeft = new TrajectoryFollower(.06, 1.0 / Constants.Constant.maxVelocityIPS(),
				0.3 / Constants.Constant.maxAccelIPS2(), leftProfile, Side.LEFT);
		followerRight = new TrajectoryFollower(.06, 1.0 / Constants.Constant.maxVelocityIPS(),
				0.3 / Constants.Constant.maxAccelIPS2(), rightProfile, Side.RIGHT);

		reset();
	}

	public void reset() {
		followerLeft.reset();
		followerRight.reset();
		driveTrain.resetEncs();
	}

	public int getFollowerCurrentSegment() {
		return followerLeft.getCurrentSegment();
	}

	public int getNumSegments() {
		return followerLeft.getNumSegments();
	}

	protected void execute() {
		loopCount++;
		double distanceL = Math.abs(driveTrain.getLeftSide().getSidePosition());
		double distanceR = Math.abs(driveTrain.getRightSide().getSidePosition());

		double speedLeft = directionMultiplier * followerLeft.calculate(distanceL);
		double speedRight = directionMultiplier * followerRight.calculate(distanceR);

		double goalHeading = followerLeft.getHeading();
		double observedHeading = driveTrain.getGyroHeading();

		double angleDiffRads = MathUtil.wrapAngleRad(goalHeading - observedHeading);

		double turn = kTurn * angleDiffRads;
		driveTrain.setPowerLeftRight(speedLeft + turn, speedRight - turn);
	}

	@Override
	protected boolean isFinished() {
		return followerLeft.isFinishedTrajectory();
	}

	@Override
	protected void end() {
		if (stoppingAtEnd) {
			driveTrain.stop();
		}
	}

}