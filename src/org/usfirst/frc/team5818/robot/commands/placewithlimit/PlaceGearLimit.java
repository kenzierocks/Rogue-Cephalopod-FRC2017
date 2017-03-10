package org.usfirst.frc.team5818.robot.commands.placewithlimit;

import org.usfirst.frc.team5818.robot.Robot;
import org.usfirst.frc.team5818.robot.commands.SetExtendTurret;
import org.usfirst.frc.team5818.robot.commands.SetPunchTurret;
import org.usfirst.frc.team5818.robot.commands.TurretSmallAdjustment;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.command.TimedCommand;

public class PlaceGearLimit extends CommandGroup {

    public PlaceGearLimit() {
        this.addSequential(new SetExtendTurret(true));
        this.addSequential(new SetPunchTurret(true));
        this.addSequential(new TimedCommand(0.6));
        this.addSequential(new SetExtendTurret(false));
        this.addSequential(new SetPunchTurret(false));
        this.addSequential(new ConditionalCommand(new TurretSmallAdjustment(0)) {

            @Override
            protected boolean condition() {
                return Math.abs(Robot.runningRobot.turret.getAngle()) < 10;
            }
        });
    }

}
