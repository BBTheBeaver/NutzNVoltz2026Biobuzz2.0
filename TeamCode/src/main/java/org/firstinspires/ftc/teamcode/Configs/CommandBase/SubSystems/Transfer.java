
package org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems;

import com.qualcomm.robotcore.hardware.DcMotor;

public class Transfer {

    // ═════════════════════════ SUBSYSTEM VARIABLES ═════════════════════════

    private final DcMotor motor;


    // ═════════════════════════ CONSTRUCTOR ═════════════════════════

    public Transfer(DcMotor motor) {
        this.motor = motor;
    }


    // ═════════════════════════ CONTROL METHODS ═════════════════════════

    // Turns off the transfer
    public void off() {
        motor.setPower(0);
    }
    public void reverse(){motor.setPower(-1);}

    // Turns on the transfer
    public void on() {
        motor.setPower(1);
    }
}
