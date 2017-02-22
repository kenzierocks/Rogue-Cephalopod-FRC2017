package org.usfirst.frc.team5818.robot.commands;

import org.usfirst.frc.team5818.robot.Robot;
import org.usfirst.frc.team5818.robot.controllers.Driver;
import org.usfirst.frc.team5818.robot.subsystems.Collector;
import org.usfirst.frc.team5818.robot.utils.MathUtil;

public class CollectorControlCommand extends ControlCommand {

    private final Collector coll = Robot.runningRobot.collector;

    public CollectorControlCommand() {
        super(js(driver -> driver.JS_COLLECTOR));
        requires(coll);
    }

    @Override
    protected void initialize(){
    	coll.setBrakeMode(true);
    }
    
    @Override
    protected void setPower() {
        coll.setPower(MathUtil.adjustDeadband(driver.JS_COLLECTOR, Driver.DEADBAND_VEC).getY());
    }

    @Override
    protected void setZero() {
        coll.setPower(0);
    }

}
