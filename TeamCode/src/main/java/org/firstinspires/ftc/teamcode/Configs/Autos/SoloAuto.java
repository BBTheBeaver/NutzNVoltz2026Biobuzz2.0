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

public class SoloAuto implements AutoRoutine {

    private final PoseFactory mirroredFactory = PoseFactory.radians().mirrorX(70.75).mirrorY(70.75);
    private final PoseFactory poseFactory = PoseFactory.degrees();

    private final Pose start = poseFactory.of(53.5477, 8.8583, 90);
    private final Pose path1 = poseFactory.of(6.812, 14.542, 270);
    private final Pose point2 = poseFactory.of(6.6893, 9.3658, 270);
    private final Pose point3Start = poseFactory.of(6.6893, 9.3658, 270);
    private final Pose point3 = poseFactory.of(61.5704, 132.2545, 90);
    private final Pose point3Control1 = poseFactory.of(22.9268, 91.2164, 0);
    private final Pose point4 = poseFactory.of(46.78, 117, 90);
    private final Pose point5 = poseFactory.of(46.783, 129.2663, 90);
    private final Pose point6 = poseFactory.of(61.57, 132.25, 90);
    private final Pose point6Control1 = poseFactory.of(54.2433, 112.0087, 0);
    private final Pose point7Start = poseFactory.of(61.57, 132.25, 90);
    private final Pose point7 = poseFactory.of(22.5336, 46.9187, 180);
    private final Pose point7Control1 = poseFactory.of(64.5148, 117.5485, 0);
    private final Pose point7Control2 = poseFactory.of(5.4902, 99.0999, 0);
    private final Pose point8 = poseFactory.of(12.4396, 46.92, 180);
    private final Pose point9Start = poseFactory.of(12.4396, 46.92, 180);
    private final Pose point9 = poseFactory.of(61.57, 132.25, 90);
    private final Pose point9Control1 = poseFactory.of(32.9974, 38.462, 0);
    private final Pose point9Control2 = poseFactory.of(31.1169, 84.6312, 0);
    private final Pose point9Control3 = poseFactory.of(15.9879, 110.8011, 0);
    private final Pose point9Control4 = poseFactory.of(28.8085, 100.3223, 0);
    private final Pose point10Start = poseFactory.of(61.57, 132.25, 90);
    private final Pose point10Top = poseFactory.of(10.929, 119.679, 0);
    private final Pose point10Bottom = poseFactory.of(10.929, 90, 0);
    private final Pose point10Control1 = poseFactory.of(56.7371, 101.4707, 0);


    private final boolean blue;
    public SoloAuto(boolean blue) {
        this.blue = blue;
    }
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

                        intakeOn,
                        follow(robot.follower, path1()),
                        follow(robot.follower, path2()),
                        waitMs(750),

                        intakeOff,
                        follow(robot.follower, path3()),

                        shoot,

                        follow(robot.follower, path4()),
                        intakeOn,
                        rampDown,

                        follow(robot.follower, path5()),
                        waitMs(2000),
                        parallel(
                                follow(robot.follower, path6()),
                                sequential(
                                        waitMs(100),
                                        rampUp,
                                        waitMs(500),
                                        intakeOff
                                )
                        ),

                        shoot,

                        follow(robot.follower, path7()),
                        rampDown,
                        follow(robot.follower, path8()),
                        parallel(
                                follow(robot.follower, path9()),
                                sequential(
                                        waitMs(100),
                                        rampUp,
                                        waitMs(500),
                                        intakeOff
                                )
                        ),

                        follow(robot.follower, path10())

                ),
                SpinFlywheel.on(flywheel)
        );
    }




    public Path path1() {
        return Paths.line(start, path1).constant(path1);
    }

    public Path path2() {
        return Paths.line(path1, point2).constant(point2);
    }

    public Path path3() {
        return Paths.curve(point3Start, point3Control1, point3).linear(point3Start, point3);
    }

    public Path path4() {
        return Paths.line(point3, point4).constant(point4);
    }

    public Path path5() {
        return Paths.line(point4, point5).constant(point5);
    }

    public Path path6() {
        return Paths.curve(point5, point6Control1, point6).constant(point6);
    }

    public Path path7() {
        return Paths.curve(point7Start, point7Control1, point7Control2, point7).linear(point7Start, point7);
    }

    public Path path8() {
        return Paths.line(point7, point8).constant(point8);
    }

    public Path path9() {
        return Paths.curve(point9Start, point9Control1, point9Control2, point9Control3, point9Control4, point9).linear(point9Start, point9);
    }

    public Path path10(){
        if (AutoConfig.parkTop) {
            return curve(m(point10Start), m(point10Control1), m(point10Top)).linear(m(point10Start), m(point10Top));
        }
        return curve(m(point10Start), m(point10Control1), m(point10Bottom)).linear(m(point10Start), m(point10Bottom));
    }
}
