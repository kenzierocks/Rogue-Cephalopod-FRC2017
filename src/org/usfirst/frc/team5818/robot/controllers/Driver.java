package org.usfirst.frc.team5818.robot.controllers;

import java.util.Vector;
import java.util.function.DoubleSupplier;

import org.usfirst.frc.team5818.robot.TestingTalon;
import org.usfirst.frc.team5818.robot.commands.ChangeMini;
import org.usfirst.frc.team5818.robot.commands.ControlMotor;
import org.usfirst.frc.team5818.robot.commands.DriveControlCommand;
import org.usfirst.frc.team5818.robot.commands.FullExtention;
import org.usfirst.frc.team5818.robot.commands.GearMode;
import org.usfirst.frc.team5818.robot.commands.OverrideControlCommand;
import org.usfirst.frc.team5818.robot.commands.SetCollectorPower;
import org.usfirst.frc.team5818.robot.commands.SetTurretAngle;
import org.usfirst.frc.team5818.robot.commands.ShiftGears;
import org.usfirst.frc.team5818.robot.commands.SwitchDriveMode;
import org.usfirst.frc.team5818.robot.commands.TapeMode;
import org.usfirst.frc.team5818.robot.commands.TurretReZero;
import org.usfirst.frc.team5818.robot.commands.placewithlimit.PlaceWithLimit;
import org.usfirst.frc.team5818.robot.constants.BotConstants;
import org.usfirst.frc.team5818.robot.constants.DriveMode;
import org.usfirst.frc.team5818.robot.constants.Side;
import org.usfirst.frc.team5818.robot.utils.ArcadeDriveCalculator;
import org.usfirst.frc.team5818.robot.utils.Buttons;
import org.usfirst.frc.team5818.robot.utils.DriveCalculator;
import org.usfirst.frc.team5818.robot.utils.RatioDriveCalculator;
import org.usfirst.frc.team5818.robot.utils.SchedulerAccess;
import org.usfirst.frc.team5818.robot.utils.Vector2d;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.Trigger.ButtonScheduler;
import edu.wpi.first.wpilibj.command.Scheduler;

public class Driver {

    public static double JOYSTICK_DEADBAND = .05;
    public static Vector2d DEADBAND_VEC = new Vector2d(JOYSTICK_DEADBAND, JOYSTICK_DEADBAND);

    public static final double TWIST_DEADBAND = .4;
    
    public Joystick JS_FW_BACK;
    public Joystick JS_TURN;
    public Joystick JS_TURRET;
    public Joystick JS_COLLECTOR;

    public DriveMode dMode;
    public DriveCalculator driveCalc;

    public Driver() {
        JS_FW_BACK = new Joystick(BotConstants.JS_FW_BACK);
        JS_TURN = new Joystick(BotConstants.JS_TURN);
        JS_TURRET = new Joystick(BotConstants.JS_TURRET);
        JS_COLLECTOR = new Joystick(BotConstants.JS_COLLECTOR);

        dMode = DriveMode.POWER;
        driveCalc = RatioDriveCalculator.INSTANCE;//RadiusDriveCalculator.INSTANCE;
    }

    public void setupTeleopButtons() {
        clearButtons();

        Button driverControl = Buttons.FW_BACK.get(1);
        driverControl.whenPressed(new DriveControlCommand(JS_FW_BACK, JS_TURN));
        
        Button switchDriveMode = Buttons.FW_BACK.get(7);
        switchDriveMode.whenPressed(new SwitchDriveMode(ArcadeDriveCalculator.INSTANCE));
        switchDriveMode.whenReleased(new SwitchDriveMode(RatioDriveCalculator.INSTANCE));
        
        Button shiftLow = Buttons.TURN.get(8);
        shiftLow.whenPressed(new ShiftGears(BotConstants.LOW_GEAR_VALUE));

        Button shiftHigh = Buttons.TURN.get(5);
        shiftHigh.whenPressed(new ShiftGears(BotConstants.HIGH_GEAR_VALUE));
        
        Button spitGear = Buttons.TURN.get(7);
        spitGear.whileHeld(new SetCollectorPower(false));
        
        Button zero = Buttons.TURN.get(6);
        zero.whenPressed(new TurretReZero());

        Button gear = Buttons.TURRET.get(7);
        gear.whenPressed(new GearMode());
        gear.whenReleased(new TapeMode());
        
        Button rightMini = Buttons.TURRET.get(8);
        rightMini.whenPressed(new ChangeMini(Side.RIGHT));
        rightMini.whenReleased(new ChangeMini(Side.LEFT));

        Button codriverControl = Buttons.TURRET.get(1);
        codriverControl.whenPressed(new OverrideControlCommand(JS_COLLECTOR));

        Button turretMinus90 = Buttons.COLLECTOR.get(5);
        turretMinus90.whenPressed(new SetTurretAngle(-90.0));

        Button turretZero = Buttons.COLLECTOR.get(4);
        turretZero.whenPressed(new SetTurretAngle(-0.0));

        Button turret90 = Buttons.COLLECTOR.get(3);
        turret90.whenPressed(new SetTurretAngle(90.0));

        Button deploy = Buttons.COLLECTOR.get(8);
        deploy.whenPressed(new PlaceWithLimit());
        
        Button fullExtend = Buttons.COLLECTOR.get(7);
        fullExtend.whenPressed(new FullExtention(true));
        fullExtend.whenReleased(new FullExtention(false));

    }

    public void setupTestButtons() {
        clearButtons();

        // DriveTrain Talons 1-6
        for (int i = 0; i < 6; i++) {
            Buttons.FW_BACK.get(i + 1)
                    .whenPressed(new ControlMotor(inverted(JS_FW_BACK::getY), TestingTalon.DRIVE[i].talon));
        }
        // Turret Talon 7
        Buttons.TURRET.get(1).whenPressed(new ControlMotor(JS_TURRET::getX, TestingTalon.TURRET.talon));
        // Arm Talons 8 & 9
        final CANTalon left = TestingTalon.LEFT_ARM.talon;
        final CANTalon right = TestingTalon.RIGHT_ARM.talon;
        DoubleSupplier collectorY = inverted(JS_COLLECTOR::getY);
        Buttons.COLLECTOR.get(1).whenPressed(new ControlMotor(collectorY, i -> {
            left.pidWrite(i);
            right.pidWrite(i);
        }));
        // Rollers Talons 10 & 11
        Buttons.COLLECTOR.get(2).whenPressed(new ControlMotor(collectorY, TestingTalon.TOP_ROLLER.talon));
        Buttons.COLLECTOR.get(3).whenPressed(new ControlMotor(collectorY, TestingTalon.BOT_ROLLER.talon));
        // Climber Talons 12-15
        for (int i = 0; i < 4; i++) {
            // add 3 for arm/roll motors and one for the correct button offset
            int jsButton = 3 + i + 1;
            Buttons.COLLECTOR.get(jsButton).whenPressed(new ControlMotor(collectorY, TestingTalon.CLIMB[i].talon));
        }
    }

    private DoubleSupplier inverted(DoubleSupplier input) {
        return () -> -input.getAsDouble();
    }

    public void teleopPeriodic() {
    }

    private void clearButtons() {
        Buttons.setButtonMapMode();
        Vector<ButtonScheduler> buttons = SchedulerAccess.getButtons(Scheduler.getInstance());
        if (buttons == null) {
            return;
        }
        buttons.clear();
    }

}
