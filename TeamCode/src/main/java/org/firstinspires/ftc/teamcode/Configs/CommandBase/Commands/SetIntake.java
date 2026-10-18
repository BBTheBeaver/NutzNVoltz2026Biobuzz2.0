package org.firstinspires.ftc.teamcode.Configs.CommandBase.Commands;

import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.sequential;

import com.pedropathing.ivy.Command;

import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Configs.Globals.Robot;
import org.firstinspires.ftc.teamcode.Configs.Globals.Constants.*;

public class SetIntake {

    public static Command forward(Intake intake) {
        return sequential(
                instant(intake::on)
        );
    }

    public static Command reverse(Intake intake) {
        return sequential(
                instant(intake::reverse)
        );
    }

    public static Command stop(Intake intake) {
        return sequential(
                instant(intake::off)
        );
    }
}