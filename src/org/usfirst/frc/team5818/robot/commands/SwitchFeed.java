package org.usfirst.frc.team5818.robot.commands;

import org.usfirst.frc.team5818.robot.Robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.command.Command;

public class SwitchFeed extends Command{

	private SerialPort rPi;
	private boolean done;
	private static boolean gear;
	
	public static boolean isGear(){
		return gear;
	}
	
	public SwitchFeed(){
		rPi = Robot.runningrobot.track.getRasPi();
		done = false;
	}
	
	@Override
	protected void initialize(){
		try{
			rPi.writeString("w");
			gear = !gear;
		}
		catch(Exception e){
			DriverStation.reportError("Writing Failed", false);
		}
		done = true;
	}
	
	@Override
	protected boolean isFinished() {
		return done;
	}

}