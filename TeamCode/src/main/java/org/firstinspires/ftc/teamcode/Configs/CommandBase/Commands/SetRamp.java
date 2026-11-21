
package org.firstinspires.ftc.teamcode.Configs.CommandBase.Commands;

import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.groups.Groups.sequential;

import com.pedropathing.ivy.Command;

import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Ramp;

public class SetRamp {

    // ═════════════════════════ COMMAND METHODS ═════════════════════════

    public static Command down(Ramp ramp) {
        return sequential(
                instant(ramp::down)
        );
    }

    public static Command up(Ramp ramp) {
        return sequential(
                instant(ramp::up)
        );
    }
}
