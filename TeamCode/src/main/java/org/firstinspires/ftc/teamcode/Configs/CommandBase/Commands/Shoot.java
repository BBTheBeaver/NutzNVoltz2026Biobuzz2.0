package org.firstinspires.ftc.teamcode.Configs.CommandBase.Commands;

import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.commands.Commands.waitUntil;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.*;

import com.pedropathing.ivy.Command;

import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Gate;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Transfer;
import org.firstinspires.ftc.teamcode.Configs.Globals.Robot;

import java.util.function.BooleanSupplier;

public class Shoot {

    // Auto — fixed duration, no outside input
    public static Command auto(Gate gate, Intake intake, Transfer transfer) {
        return sequential(
                instant(() -> {
                    gate.open();
                    transfer.on();
                    intake.on();

                }),

                waitMs(1500),

                instant(() -> {
                    gate.close();
                    transfer.off();
                    intake.off();
                })
        );
    }

    // Teleop — runs for as long as "held" keeps returning true
    public static Command whileHeld(Gate gate, Intake intake, Transfer transfer, BooleanSupplier held) {
        return sequential(
                instant(() -> {
                    gate.open();
                    intake.on();
                    transfer.on();
                }),

                waitUntil(() -> !held.getAsBoolean()),

                instant(() -> {
                    gate.close();
                    intake.off();
                    transfer.off();
                })
        );
    }
}