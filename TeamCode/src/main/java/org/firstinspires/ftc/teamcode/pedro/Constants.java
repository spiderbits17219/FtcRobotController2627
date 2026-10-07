//package org.firstinspires.ftc.teamcode.pedro;
//
//import com.pedropathing.follower.Follower;
//import com.pedropathing.revhub.drivetrains.Mecanum;
//import com.pedropathing.revhub.drivetrains.MecanumConfig;
//import com.pedropathing.revhub.localizers.Encoder;
//import com.pedropathing.revhub.localizers.PinpointLocalizer;
//import com.pedropathing.revhub.localizers.RevHubIMU;
//import com.pedropathing.revhub.localizers.ThreeWheelIMUConfig;
//import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
//import com.qualcomm.robotcore.hardware.DcMotorSimple;
//import com.qualcomm.robotcore.hardware.HardwareMap;
//
//public class Constants {
//    public static Follower create(HardwareMap h) {
//        // return new Follower(Drivetrain, Localizer, Foresight);
//    return null; }
//         public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
//            c.frontLeftName.set("lf");
//            c.frontRightName.set("rf");
//            c.backLeftName.set("lr");
//            c.backRightName.set("rr");
//            c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
//            c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
//            c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
//            c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
//        });
//    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
//        c.frontLeftName.set("lf");
//        c.frontRightName.set("rf");
//        c.backLeftName.set("lr");
//        c.backRightName.set("rr");
//        c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
//        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
//        c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
//        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);
//    });
//         public static ThreeWheelIMUConfig localizerConfig = new ThreeWheelIMUConfig(c -> {
//            c.leftEncoderName.set("lf");
//            c.rightEncoderName.set("rr");
//            c.strafeEncoderName.set("lr");
//            c.imuName.set("imu");
//            c.imu.set(new RevHubIMU(new RevHubOrientationOnRobot(
//                    RevHubOrientationOnRobot.LogoFacingDirection.UP,
//                    RevHubOrientationOnRobot.UsbFacingDirection.LEFT
//            )));
//            c.leftPodY.set(138.38984380703656);
//            c.rightPodY.set(104.22397288165594);
//            c.strafePodX.set(0.0);
//            c.forwardTicksToInches.set(0.06306418866197067);
//            c.strafeTicksToInches.set(0.41630499030324897);
//            c.turnTicksToRadians.set(0.03345861879759126);
//            c.leftEncoderDirection.set(Encoder.REVERSE);
//            c.rightEncoderDirection.set(Encoder.FORWARD);
//            c.strafeEncoderDirection.set(Encoder.FORWARD);
//        });
//    public static ThreeWheelIMUConfig localizerConfig = new ThreeWheelIMUConfig(c -> {
//        c.leftEncoderName.set("lf");
//        c.rightEncoderName.set("rr");
//        c.strafeEncoderName.set("lr");
//        c.imuName.set("imu");
//        c.imu.set(new RevHubIMU(new RevHubOrientationOnRobot(
//                RevHubOrientationOnRobot.LogoFacingDirection.UP,
//                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
//        )));
//        c.leftPodY.set(-7519.237487506844);
//        c.rightPodY.set(4142.457077369497);
//        c.strafePodX.set(0.0);
//        c.forwardTicksToInches.set(3.064260118218732);
//        c.strafeTicksToInches.set(0.39718492086455987);
//        c.turnTicksToRadians.set(2.889411567760764);
//        c.leftEncoderDirection.set(Encoder.REVERSE);
//        c.rightEncoderDirection.set(Encoder.FORWARD);
//        c.strafeEncoderDirection.set(Encoder.REVERSE);
//    });
//    public static Follower create(HardwareMap h) {
//        return new Follower(
//                new PinpointLocalizer(h, localizerConfig),
//                new Mecanum(h, drivetrainConfig),
//                new Foresight(foresightConfig)
//        );
//    }
//
//    }

package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.controllers.Controller;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Matrix;
import com.pedropathing.math.Vector2D;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.Encoder;
import com.pedropathing.revhub.localizers.PinpointConfig;
import com.pedropathing.revhub.localizers.PinpointLocalizer;
import com.pedropathing.revhub.localizers.RevHubIMU;
import com.pedropathing.revhub.localizers.ThreeWheelIMUConfig;
import com.pedropathing.revhub.localizers.ThreeWheelIMULocalizer;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Constants {
    public static MecanumConfig drivetrainConfig = new MecanumConfig(
            c -> {
                c.frontLeftName.set("lf");
                c.backLeftName.set("lr");
                c.frontRightName.set("rf");
                c.backRightName.set("rr");

                c.frontLeftDirection.set(DcMotorSimple.Direction.REVERSE);
                c.backLeftDirection.set(DcMotorSimple.Direction.REVERSE);
                c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);
                c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);

                c.manualBrakeMode.set(true);
            }
    );

    public static ThreeWheelIMUConfig localizerConfig = new ThreeWheelIMUConfig(c -> {
        c.leftEncoderName.set("lf");
        c.rightEncoderName.set("rr");
        c.strafeEncoderName.set("lr");
        c.imuName.set("imu");
        c.imu.set(new RevHubIMU(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.LEFT
        )));
        c.leftPodY.set(-7519.237487506844);
        c.rightPodY.set(4142.457077369497);
        c.strafePodX.set(0.0);
        c.forwardTicksToInches.set(3.064260118218732);
        c.strafeTicksToInches.set(0.39718492086455987);
        c.turnTicksToRadians.set(2.889411567760764);
        c.leftEncoderDirection.set(Encoder.REVERSE);
        c.rightEncoderDirection.set(Encoder.FORWARD);
        c.strafeEncoderDirection.set(Encoder.REVERSE);
    });

    public static ForesightConfig foresightConfig = new ForesightConfig(
            c -> {
                Controller primaryTranslationalForward = Controller.proportional(0.3);
                Controller secondaryTranslationalForward = Controller.proportional(0.1);
                Controller primaryTranslationalLateral = Controller.proportional(0.3);
                Controller secondaryTranslationalLateral = Controller.proportional(0.1);

                c.forwardTranslational.set(Controller.piecewise(secondaryTranslationalForward).put(2.5, primaryTranslationalForward));
                c.strafeTranslational.set(Controller.piecewise(secondaryTranslationalLateral).put(2.5, primaryTranslationalLateral));

                c.coast.set(Controller.proportionalFeedforward(0.010978350889324107));
                c.brake.set(Controller.proportionalFeedforward(0.008731598255925491));

                c.headingFeedback.set(Controller.proportional(5.258721785960744));
                c.headingBrakeCoefficients.set(Vector2D.cartesian(0.05642143125655298, 0.0063829525363003695));

                c.linearBrakeCoefficients.set(Matrix.diag(0.10605894992901523, 0.08719146175596092));
                c.quadraticBrakeCoefficients.set(Matrix.diag(0.0014663966976606565, 0.0013837064502458813));

                c.maxAchievableForwardVelocity.set(72.72923108818539);
                c.maxAchievableStrafeVelocity.set(52.34323936525474);
                c.naturalForwardDeceleration.set(85.01144677379789);
                c.naturalStrafeDeceleration.set(104.49787535782846);
            }
    );

    public static Follower create(HardwareMap h) {
        return new Follower(
                new ThreeWheelIMULocalizer(h, localizerConfig),
                new Mecanum(h, drivetrainConfig),
                new Foresight(foresightConfig)
        );
    }
}