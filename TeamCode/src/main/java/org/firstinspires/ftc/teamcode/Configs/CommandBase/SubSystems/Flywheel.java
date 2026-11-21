
package org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;

import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.LAUNCHER_MIN_VELOCITY;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.LAUNCHER_TARGET_VELOCITY;

public class Flywheel {

    // ═════════════════════════ SUBSYSTEM VARIABLES ═════════════════════════

    private final DcMotorEx motor;


    // ═════════════════════════ CONSTRUCTOR ═════════════════════════

    public Flywheel(DcMotorEx motor) {
        this.motor = motor;
    }


    // ═════════════════════════ FLYWHEEL CONTROL METHODS ═════════════════════════

    // Spins up the flywheel
    public void spinUp() {
        motor.setVelocity(LAUNCHER_TARGET_VELOCITY);
    }

    // Stops the flywheel
    public void stop() {
        motor.setVelocity(0);
    }


    // ═════════════════════════ STATUS METHODS ═════════════════════════

    // Checks whether the flywheel is ready to shoot
    public boolean isAtSpeed() {
        return motor.getVelocity() >= LAUNCHER_MIN_VELOCITY;
    }

    // Returns the current flywheel velocity
    public double getVelocity() {
        return motor.getVelocity();
    }
}
