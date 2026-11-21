
package org.firstinspires.ftc.teamcode.Configs.Autos;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.ivy.commands.Commands.instant;
import static com.pedropathing.ivy.commands.Commands.waitMs;
import static com.pedropathing.ivy.groups.Groups.deadline;
import static com.pedropathing.ivy.groups.Groups.parallel;
import static com.pedropathing.ivy.groups.Groups.sequential;
import static com.pedropathing.ivy.pedro.PedroCommands.follow;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.ivy.Command;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;

import org.firstinspires.ftc.teamcode.Configs.CommandBase.Commands.Shoot;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.Commands.SpinFlywheel;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Flywheel;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Gate;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Intake;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Ramp;
import org.firstinspires.ftc.teamcode.Configs.CommandBase.SubSystems.Transfer;
import org.firstinspires.ftc.teamcode.Configs.Globals.AutoConfig;
import org.firstinspires.ftc.teamcode.Configs.Globals.Robot;
import org.firstinspires.ftc.teamcode.OpMode.Auto;

public class FarAuto implements AutoRoutine {

    // ═════════════════════════ POSE FACTORIES ═════════════════════════

    private final PoseFactory mirroredFactory = PoseFactory.radians().mirrorX(70.75).mirrorY(70.75);
    private final PoseFactory poseFactory = PoseFactory.degrees();


    // ═════════════════════════ PATH POSES ═════════════════════════

    private final Pose start = poseFactory.of(63.4796, 133.1919, 90);

    private final Pose path1 = poseFactory.of(47.1716, 122, 90);
    private final Pose point2 = poseFactory.of(47.0401, 130.017, 90);

    private final Pose point3 = poseFactory.of(58.8784, 130, 90);
    private final Pose point3Control1 = poseFactory.of(55.7371, 109.9929, 0);

    private final Pose point4Start = poseFactory.of(58.8784, 132.2829, 90);
    private final Pose point4Top = poseFactory.of(11.6707, 121.5026, 0);
    private final Pose point4Bottom = poseFactory.of(11.6707, 90, 0);
    private final Pose point4Control1 = poseFactory.of(61.0535, 104.6039, 0);


    // ═════════════════════════ AUTO SETTINGS ═════════════════════════

    private final boolean blue;

    public FarAuto(boolean blue) {
        this.blue = blue;
    }


    // ═════════════════════════ POSE HELPERS ═════════════════════════

    private Pose m(Pose pose) {
        if (blue) {
            return mirroredFactory.of(pose.x(), pose.y(), pose.heading());
        }
        return pose;
    }

    @Override
    public Pose getStartPose() {
        return m(start);
    }


    // ═════════════════════════ AUTO BUILD ═════════════════════════

    public Command build(Robot robot) {
        Flywheel flywheel = new Flywheel(robot.flywheel);
        Intake intake = new Intake(robot.intake);
        Transfer transfer = new Transfer(robot.transfer);
        Ramp ramp = new Ramp(robot.left_ramp_servo, robot.right_ramp_servo);
        Gate gate = new Gate(robot.gate);

        Command rampDown = instant(ramp::down);
        Command rampUp = instant(ramp::up);
        Command intakeOn = instant(intake::on);
        Command intakeOff = instant(intake::off);
        Command shoot = Shoot.auto(gate, intake, transfer);

        return deadline(
                sequential(
                        shoot,

                        follow(robot.follower, path1()),
                        parallel(
                                rampDown,
                                follow(robot.follower, path2()),
                                intakeOn
                        ),
                        waitMs(1000),
                        intakeOff,

                        parallel(
                                follow(robot.follower, path3()),
                                sequential(
                                        waitMs(100),
                                        rampUp
                                )
                        ),

                        shoot,

                        follow(robot.follower, path4())

                ),
                SpinFlywheel.on(flywheel)
        );
    }


    // ═════════════════════════ PATHS ═════════════════════════

    public Path path1() {
        return Paths.line(start, path1).constant(path1);
    }

    public Path path2() {
        return Paths.line(path1, point2).constant(point2);
    }

    public Path path3() {
        return Paths.curve(point2, point3Control1, point3).constant(point3);
    }

    public Path path4() {
        if (AutoConfig.parkTop) {
            return curve(m(point4Start), m(point4Control1), m(point4Top)).linear(m(point4Start), m(point4Top));
        }
        return curve(m(point4Start), m(point4Control1), m(point4Bottom)).linear(m(point4Start), m(point4Bottom));
    }
}
