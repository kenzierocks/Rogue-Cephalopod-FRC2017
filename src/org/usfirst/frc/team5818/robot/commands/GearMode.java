package org.usfirst.frc.team5818.robot.commands;

import org.usfirst.frc.team5818.robot.Robot;
import org.usfirst.frc.team5818.robot.subsystems.CameraController;
import org.usfirst.frc.team5818.robot.subsystems.VisionTracker;

import edu.wpi.first.wpilibj.command.Command;

public class GearMode extends Command {

    private CameraController cont;
    private VisionTracker track;

    public GearMode() {
        cont = Robot.runningRobot.camCont;
        track = Robot.runningRobot.track;
        requires(cont);
        requires(track);
    }

    @Override
    protected void initialize() {
        cont.enterGearMode();
        track.setLightsOn(false);
    }

    @Override
    protected boolean isFinished() {
        return true;
    }

}
