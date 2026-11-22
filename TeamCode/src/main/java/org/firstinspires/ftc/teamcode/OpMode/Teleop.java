
package org.firstinspires.ftc.teamcode.OpMode;

import static com.pedropathing.ivy.commands.Commands.instant;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.*;

import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.ivy.Scheduler;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Gate;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Ramp;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Transfer;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Turret;
import org.firstinspires.ftc.teamcode.Configs.Helper.ButtonBinder;
import org.firstinspires.ftc.teamcode.Configs.Helper.PoseStorage;
import org.firstinspires.ftc.teamcode.Configs.Globals.Robot;
import org.firstinspires.ftc.teamcode.Configs.Helper.ShooterConstants;

@TeleOp(name = "Teleop", group = "StarterBot")
public class Teleop extends OpMode {

    // ───────────────────────── Robot & subsystems ─────────────────────────
    private Robot robot;
    private Intake intake;
    private Transfer transfer;
    private Ramp ramp;
    private Gate gate;
    private Turret turret;

    // ───────────────────────── Button bindings ─────────────────────────
    private final ButtonBinder binder = new ButtonBinder();

    // ───────────────────────── State ─────────────────────────
    private final String[] options = {"Blue", "Red"};
    private int selectedOption = 0;
    private boolean isBlue;
    private double[] goalPose = GOAL_POSE_BLUE;

    private boolean headingLock = false;
    private boolean intakeOn = false;
    private boolean rampDown = false;
    private double turretAngelOffset = 0;


    // ═════════════════════════ INIT ═════════════════════════
    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        Scheduler.reset();

        intake = new Intake(robot.intake);
        transfer = new Transfer(robot.transfer);
        ramp = new Ramp(robot.left_ramp_servo, robot.right_ramp_servo);
        gate = new Gate(robot.gate);
        turret = new Turret(robot.leftTurretServo, robot.rightTurretServo);

        bindControls();

        telemetry.addData("Status", "Initialized");
    }


    // ═════════════════════════ BUTTON BINDINGS ═════════════════════════
    private void bindControls() {

        // Intake toggle
        binder.onPress(() -> gamepad1.left_bumper,
                instant(() -> {
                    intakeOn = !intakeOn;
                    if (intakeOn) intake.on();
                    else intake.off();
                }));

        // Reverse intake while the trigger is held, then restore the toggle state
        binder.whileHeld(() -> gamepad1.left_trigger > 0.5,
                instant(intake::reverse),
                instant(() -> {
                    if (intakeOn) intake.on();
                    else intake.off();
                }));

        // Ramp toggle
        binder.onPress(() -> gamepad1.right_stick_button,
                instant(() -> {
                    rampDown = !rampDown;
                    if (rampDown) ramp.down();
                    else ramp.up();
                }));

        // Shoot while Y is held
        binder.whileHeld(() -> gamepad1.y,
                instant(() -> {
                    gate.open();
                    intake.on();
                    transfer.on();
                }),
                instant(() -> {
                    gate.close();
                    transfer.off();
                    if (intakeOn) intake.on();
                    else intake.off();
                }));

        // Decrease turret angle offset
        binder.onPress(() -> gamepad2.left_bumper,
                instant(() -> {
                    turretAngelOffset -= 1;
                }));

        // Increase turret angle offset
        binder.onPress(() -> gamepad2.right_bumper,
                instant(() -> {
                    turretAngelOffset += 1;
                }));
    }


    // ═════════════════════════ INIT LOOP ═════════════════════════
    @Override
    public void init_loop() {
        if (gamepad1.dpadDownWasPressed())
            selectedOption = (selectedOption + 1) % options.length;
        if (gamepad1.dpadUpWasPressed())
            selectedOption = (selectedOption - 1 + options.length) % options.length;

        telemetry.addLine("=== ALLIANCE SELECT ===");

        for (int i = 0; i < options.length; i++) {
            telemetry.addData(i == selectedOption ? "> " : "  ", options[i]);
        }

        telemetry.addLine("DPAD up/down to switch, Start to begin");
        telemetry.update();
    }


    // ═════════════════════════ START ═════════════════════════
    @Override
    public void start() {
        isBlue = selectedOption == 0;
        goalPose = isBlue ? GOAL_POSE_BLUE : GOAL_POSE_RED;

        if (PoseStorage.lastAutoPose != null) {
            robot.follower.setPose(PoseStorage.lastAutoPose);
        }
    }


    // ═════════════════════════ LOOP ═════════════════════════
    @Override
    public void loop() {
        robot.periodic();

        binder.update();
        Scheduler.execute();

        // Drive
        double flip = isBlue ? 1 : -1;

        turret.setAngleOffset(turretAngelOffset);

        turret.update(robot, goalPose);

        robot.flywheel.setVelocity(
                ShooterConstants.flywheelSpeed(turret.getDistanceToGoal())
        );
        robot.flywheel2.setVelocity(
                ShooterConstants.flywheelSpeed(turret.getDistanceToGoal())
        );

        DrivePowers powers = ManualDrive.fieldCentric(
                flip * gamepad1.left_stick_y,
                flip * gamepad1.left_stick_x,
                gamepad1.right_stick_x,
                robot.follower.pose().heading()
        );

        robot.follower.manual(powers);


        telemetry.addData("Heading Lock", headingLock);
        telemetry.addData("Intake On", intakeOn);
        telemetry.addData("Distance", turret.getDistanceToGoal());
        telemetry.addData("Loop Hz", robot.getLoopTimeHz());
        telemetry.update();
    }
}
