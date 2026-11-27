
package org.firstinspires.ftc.teamcode.OpMode;

import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.*;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.RESET_GOAL;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.RESET_GARDEN;
import static org.firstinspires.ftc.teamcode.Configs.Globals.Constants.RESET_TOP;

import com.pedropathing.api.PoseFactory;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.ivy.Scheduler;
import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Aim;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.ColorSensor;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Flywheel;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Gate;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Light;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Ramp;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Transfer;
import org.firstinspires.ftc.teamcode.Configs.Globals.PoseStorage;
import org.firstinspires.ftc.teamcode.Configs.Globals.Robot;
import org.firstinspires.ftc.teamcode.Configs.Globals.ShooterConstants;
import org.firstinspires.ftc.teamcode.Configs.Globals.Zone;

import java.util.Objects;

@TeleOp(name = "Teleop", group = "StarterBot")
//@Disabled
public class Teleop extends OpMode {

    // ═════════════════════════ ROBOT & SUBSYSTEMS ═════════════════════════
    Robot robot;
    Flywheel flywheel;
    Intake intake;
    Transfer transfer;
    Ramp ramp;
    Gate gate;
    ColorSensor colorSensor;
    Light intakeLight;
    Light shooterLight;
    Aim aim;


    // ═════════════════════════ ALLIANCE SELECT ═════════════════════════
    final String[] options = {"Blue", "Red"};
    int selectedOption = 0;
    boolean upLast = false;
    boolean downLast = false;
    boolean isBlue;


    // ═════════════════════════ DRIVE / AIMING ═════════════════════════
    boolean headingLock = false;
    double[] goalPose = GOAL_POSE_BLUE;
    double turnPower;
    double flip;
    int angleOffset;
    int autoAngleOffset;
    int totalOffset;


    // ═════════════════════════ INTAKE / RAMP STATE ═════════════════════════
    boolean intakeOn = false;
    boolean reverseOn = false;
    boolean ramp_on = false;
    boolean lastFull = false; // field, outside loop()


    // ═════════════════════════ ZONES ═════════════════════════
    boolean zone1;
    boolean zone2;


    // ═════════════════════════ TIMER ═════════════════════════
    ElapsedTime timer = new ElapsedTime();
    double secs;
    double waitTime = 1.5; //Second


    // ═════════════════════════ POSE MIRRORING ═════════════════════════
    private final PoseFactory mirroredFactory = PoseFactory.radians().mirrorX(70.75).mirrorY(70.75);


    // ═════════════════════════ INIT ═════════════════════════
    @Override
    public void init() {
        robot = new Robot(hardwareMap);
        Scheduler.reset();
        telemetry.addData("Status", "Initialized");

        flywheel = new Flywheel(robot.flywheel);
        intake = new Intake(robot.intake);
        transfer = new Transfer(robot.transfer);
        ramp = new Ramp(robot.left_ramp_servo, robot.right_ramp_servo);
        gate = new Gate(robot.gate);
        colorSensor = new ColorSensor(robot.colorSensor);
        intakeLight = new Light(robot.intakeLight);
        shooterLight = new Light(robot.shooterLight);
        aim = new Aim();
    }


    // ═════════════════════════ INIT LOOP ═════════════════════════
    @Override
    public void init_loop() {
        if (gamepad1.dpad_down && !downLast) selectedOption = (selectedOption + 1) % options.length;
        if (gamepad1.dpad_up && !upLast) selectedOption = (selectedOption - 1 + options.length) % options.length;

        upLast = gamepad1.dpad_up;
        downLast = gamepad1.dpad_down;

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
        isBlue = Objects.equals(options[selectedOption], "Blue");

        if (isBlue) {
            goalPose = GOAL_POSE_BLUE;
        } else {
            goalPose = GOAL_POSE_RED;
        }

        if (PoseStorage.lastAutoPose != null) {
            robot.follower.setPose(PoseStorage.lastAutoPose);
        }
    }


    // ═════════════════════════ LOOP ═════════════════════════
    @Override
    public void loop() {
        robot.periodic();
        Scheduler.execute(); // runs anything scheduled — nothing's scheduled yet, but it's plugged in and ready

        if(robot.follower.pose().x() < 23){
            autoAngleOffset = 2;
        }
        else if(robot.follower.pose().x() < 47){
            autoAngleOffset = 1;
        }
        else if(robot.follower.pose().x() < 72){
            autoAngleOffset = 0;
        }
        else if(robot.follower.pose().x() < 95){
            autoAngleOffset = -1;
        }
        else if(robot.follower.pose().x() < 118){
            autoAngleOffset = -2;
        }
        else if(robot.follower.pose().x() < 141.5){
            autoAngleOffset = -2;
        }

        if (gamepad2.dpad_down) {
            robot.follower.setPose(m(RESET_GARDEN));
        }

        if (gamepad2.dpad_up) {
            robot.follower.setPose(m(RESET_TOP));
        }

        if (gamepad2.dpad_left || gamepad1.dpad_right) {
            robot.follower.setPose(m(RESET_GOAL));
        }

        if (gamepad2.leftBumperWasPressed()) {
            angleOffset -= 1;
        }

        if (gamepad2.rightBumperWasPressed()) {
            angleOffset += 1;
        }

        int sign = isBlue ? -1 : 1;
        aim.setAutoAngleOffset(sign * autoAngleOffset);
        aim.setAngleOffset(sign * angleOffset);

        //region Drive
        if (gamepad1.rightBumperWasPressed()) {
            headingLock = !headingLock;
        }

        turnPower = aim.update(robot, headingLock, goalPose, gamepad1.right_stick_x);

        flip = isBlue ? 1 : -1;

        DrivePowers powers = ManualDrive.fieldCentric(
                flip * gamepad1.left_stick_y,
                flip * gamepad1.left_stick_x,
                turnPower,
                robot.follower.pose().heading()
        );

        robot.follower.manual(powers);
        //endregion


        //region Shoot
        zone1 = Zone.contains(redLaunchZone1, robot.follower.pose().x(), robot.follower.pose().y());
        zone2 = Zone.contains(redLaunchZone2, robot.follower.pose().x(), robot.follower.pose().y());

        if (zone1 || zone2) {
            shooterLight.blue();
        } else {
            shooterLight.off();
        }

        if (gamepad1.y) {
            gate.open();
            transfer.on();
            //schedule(Shoot.whileHeld(gate, intake, transfer, () -> gamepad1.y));
        } else {
            gate.close();
            transfer.reverse();
        }

        boolean full = colorSensor.full();

        if (gamepad1.right_trigger > 0.5) {
            intake.reverse();
        } else if (gamepad1.y) {
            intake.on();
        } else if (gamepad1.left_bumper && !full) {
            intake.on();
        } else {
            intake.off();
        }

        if (full) {
            intakeLight.green();
        } else {
            intakeLight.red();
        }
        //endregion


        //region Ramp
        if (gamepad1.rightStickButtonWasPressed()) {
            ramp_on = !ramp_on;
        }

        if (ramp_on) {
            ramp.down();
        } else if (!ramp_on) {
            ramp.up();
        }
        //endregion

        totalOffset = angleOffset + autoAngleOffset;


        robot.flywheel.setVelocity(ShooterConstants.flywheelSpeed(aim.getDistanceToGoal()));


        //region Telemetry
        telemetry.addData("Shooter RPM: ", robot.flywheel.getVelocity());
        telemetry.addData("Distance from goal: ", aim.getDistanceToGoal());
        telemetry.addData("X Pose: ", robot.follower.pose().x());
        telemetry.addData("Y Pose: ", robot.follower.pose().y());
        telemetry.addData("Angle Offset ", totalOffset);
        telemetry.addData("Manual Angle Offset ", angleOffset);
        telemetry.addData("Auto Angle Offset ", autoAngleOffset);
        telemetry.addData("Heading Error (deg)", Math.toDegrees(aim.getHeadingError()));
        telemetry.addData("Heading", "%.1f°", Math.toDegrees(robot.follower.pose().heading()));
        telemetry.addData("Loop Hz", robot.getLoopTimeHz());
        telemetry.addLine("Dpad_Down for garden reset");
        telemetry.addLine("Dpad_Up for top reset");
        telemetry.addLine("Dpad_left or Dpad_right for flower reset");
        //endregion
    }


    // ═════════════════════════ HELPERS ═════════════════════════
    private static double normalizeAngle(double angle) {
        return Math.atan2(Math.sin(angle), Math.cos(angle));
    }

    private Pose m(Pose pose) {
        if (isBlue) {
            return mirroredFactory.of(pose.x(), pose.y(), pose.heading());
        }

        return pose;
    }

    private static double clamp(double v, double max) {
        return Math.max(-max, Math.min(max, v));
    }
}
