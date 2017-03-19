package org.usfirst.frc.team5818.robot.commands.driveatratio;

import org.usfirst.frc.team5818.robot.constants.Camera;
import org.usfirst.frc.team5818.robot.constants.Spin;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class SanicOpts implements DriveAtRatioOptions {

    public static Builder builder() {
        return new AutoValue_SanicOpts.Builder();
    }

    @AutoValue.Builder
    public interface Builder {

        Builder inches(double value);

        Builder maxPower(double value);

        Builder targetRatio(double value);

        SanicOpts build();

    }

    @Override
    public abstract double getInches();

    @Override
    public abstract double getMaxPower();

    @Override
    public abstract double getTargetRatio();

    @Override
    public final Camera getCamera() {
        return Camera.ULTRASANIC;
    }

    @Override
    public final Spin getRotation() {
        return null;
    }

    @Override
    public final double getMaxRatio() {
        return 1.0;
    }

    @Override
    public final double getAccel() {
        return 0.0;
    }

    @Override
    public final double getMinPower() {
        return 0.0;
    }

    @Override
    public double getVisionOffset() {
        return 0.0;
    }

    @Override
    public final boolean isStoppingAtEnd() {
        return true;
    }

}
