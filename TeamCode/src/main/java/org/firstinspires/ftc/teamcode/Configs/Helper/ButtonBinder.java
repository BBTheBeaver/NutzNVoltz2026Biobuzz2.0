
package org.firstinspires.ftc.teamcode.Configs.Helper;

import com.pedropathing.ivy.Command;
import com.pedropathing.ivy.Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

/**
 * Watches buttons and schedules Ivy commands when they change.
 * Register bindings once, then call update() every loop.
 */
public class ButtonBinder {

    // ───────────────────────── Binding Class ─────────────────────────
    private static class Binding {

        final BooleanSupplier button;
        final Command onPress;
        final Command onRelease;
        boolean last = false;

        Binding(BooleanSupplier button, Command onPress, Command onRelease) {
            this.button = button;
            this.onPress = onPress;
            this.onRelease = onRelease;
        }

        void update() {
            boolean now = button.getAsBoolean();

            if (now && !last && onPress != null) Scheduler.schedule(onPress);
            if (!now && last && onRelease != null) Scheduler.schedule(onRelease);

            last = now;
        }
    }


    // ───────────────────────── Variables ─────────────────────────
    private final List<Binding> bindings = new ArrayList<>();


    // ───────────────────────── Button Registration ─────────────────────────
    /** Runs the command once, the moment the button is pressed. */
    public void onPress(BooleanSupplier button, Command command) {
        bindings.add(new Binding(button, command, null));
    }

    /** Runs the command once, the moment the button is released. */
    public void onRelease(BooleanSupplier button, Command command) {
        bindings.add(new Binding(button, null, command));
    }

    /** Runs start when pressed and stop when released. */
    public void whileHeld(BooleanSupplier button, Command start, Command stop) {
        bindings.add(new Binding(button, start, stop));
    }


    // ───────────────────────── Update ─────────────────────────
    /** Call every loop, before Scheduler.execute(). */
    public void update() {
        for (Binding b : bindings) b.update();
    }
}
