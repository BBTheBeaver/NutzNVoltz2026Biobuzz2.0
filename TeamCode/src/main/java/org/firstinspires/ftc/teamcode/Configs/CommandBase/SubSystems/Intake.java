
package org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems;

import com.qualcomm.robotcore.hardware.DcMotor;

import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.INTAKE_FORWARD;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.INTAKE_REVERSE;

public class Intake {

    // ═════════════════════════ SUBSYSTEM VARIABLES ═════════════════════════

    private final DcMotor motor;


    // ═════════════════════════ CONSTRUCTOR ═════════════════════════

    public Intake(DcMotor motor) {
        this.motor = motor;
    }


    // ═════════════════════════ INTAKE CONTROL METHODS ═════════════════════════

    // Turns on the intake
    public void on() {
        motor.setPower(INTAKE_FORWARD);
    }

    // Reverses the intake
    public void reverse() {
        motor.setPower(INTAKE_REVERSE);
    }

    // Stops the intake
    public void off() {
        motor.setPower(0);
    }
}
