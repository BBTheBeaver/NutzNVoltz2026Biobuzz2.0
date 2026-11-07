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
    private final Pose path1 = poseFactory.of(6.9346, 14.542, 270);
    private final Pose point2 = poseFactory.of(6.6893, 9.3658, 270);
    private final Pose point3Start = poseFactory.of(6.6893, 9.3658, 270);
    private final Pose point3 = poseFactory.of(61.5704, 132.2545, 90);
    private final Pose point3Control1 = poseFactory.of(22.9268, 91.2164, 0);
    private final Pose point4 = poseFactory.of(46.78, 117, 90);
    private final Pose point5 = poseFactory.of(46.783, 129.2663, 90);
    private final Pose point6 = poseFactory.of(61.57, 132.25, 90);
    private final Pose point6Control1 = poseFactory.of(56.6956, 121.205, 0);
    private final Pose point7Start = poseFactory.of(61.57, 132.25, 90);
    private final Pose point7Top = poseFactory.of(11.1302, 121.4698, 0);
    private final Pose point7Bottom = poseFactory.of(11.1302, 90, 0);
    private final Pose point7Control1 = poseFactory.of(64.5148, 117.5485, 0);


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

                        follow(robot.follower, path1()),
                        parallel(
                                follow(robot.follower, path2()),
                                intakeOn
                        ),
                        waitMs(500),
                        intakeOff,

                        follow(robot.follower, path3()),
                        shoot,

                        follow(robot.follower, path4()),
                        parallel(
                                follow(robot.follower, path5()),
                                intakeOn,
                                rampDown
                        ),
                        waitMs(1000),
                        intakeOff,

                        rampUp,
                        follow(robot.follower, path6()),
                        shoot,

                        follow(robot.follower, path7())
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

    public Path path7(){
        if (AutoConfig.parkTop) {
            return curve(m(point7Start), m(point7Control1), m(point7Top)).linear(m(point7Start), m(point7Top));
        }
        return curve(m(point7Start), m(point7Control1), m(point7Bottom)).linear(m(point7Start), m(point7Bottom));
    }
}
