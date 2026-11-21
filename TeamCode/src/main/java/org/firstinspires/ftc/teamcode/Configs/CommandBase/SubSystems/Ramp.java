
package org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems;

import com.qualcomm.robotcore.hardware.Servo;

import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.RAMP_DOWN;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.RAMP_UP;

public class Ramp {

    // ═════════════════════════ SUBSYSTEM VARIABLES ═════════════════════════

    private final Servo servo;
    private final Servo servo2;


    // ═════════════════════════ CONSTRUCTOR ═════════════════════════

    public Ramp(Servo servo, Servo servo2) {
        this.servo = servo;
        this.servo2 = servo2;
    }


    // ═════════════════════════ RAMP CONTROL METHODS ═════════════════════════

    // Lowers the ramp
    public void down() {
        servo.setPosition(RAMP_DOWN);
        servo2.setPosition(RAMP_DOWN);
    }

    // Raises the ramp
    public void up() {
        servo.setPosition(RAMP_UP);
        servo2.setPosition(RAMP_UP);
    }
}
