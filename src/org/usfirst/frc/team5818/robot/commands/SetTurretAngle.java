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
import org.usfirst.frc.team5818.robot.subsystems.Turret;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Rotates the turret to an angle.
 */
public class SetTurretAngle extends Command {

    public static final double DEGREES_TOLERANCE = 2;

    private Turret turr;
    private double targetAng;

    public SetTurretAngle(double ang) {
        setInterruptible(false);
        turr = Robot.runningRobot.turret;
        requires(turr.rotator);
        setTimeout(0.5);
        targetAng = ang;
    }

    @Override
    public void initialize() {
        turr.setAngle(targetAng);
    }

    @Override
    protected boolean isFinished() {
        return (Math.abs(turr.getPositionRaw() - targetAng) < 2 && Math.abs(turr.getVeleocity()) < 1) ||  isTimedOut();
    }

    @Override
    protected void end() {
    }

}
