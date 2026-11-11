package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
//import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
//import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
//import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
//import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.List;
import java.util.concurrent.TimeUnit;

@TeleOp(name = "Teleop2627V1", group = "Teleop")
public class Teleop2627V1 extends LinearOpMode {

    final double DESIRED_DISTANCE = 30.0;

    final double SPEED_GAIN  = 0.02;
    final double STRAFE_GAIN = 0.015;
    final double TURN_GAIN   = 0.01;
    final double MAX_AUTO_SPEED  = 0.5;
    final double MAX_AUTO_STRAFE = 0.5;
    final double MAX_AUTO_TURN   = 0.3;

    private DcMotorEx lfDrive = null ;
    private DcMotorEx rfDrive = null;
    private DcMotorEx lrDrive = null;
    private DcMotorEx rrDrive = null;

    private DcMotorEx intakeMotor = null;
    private DcMotorEx shooterMotor = null;
    private DcMotorEx transferMotor = null;

//    private final boolean USE_WEBCAM = true;
    private final int DESIRED_TAG_ID = -1;

//    private VisionPortal visionPortal;
//    private AprilTagProcessor aprilTag;

    private boolean targetFound = false;
    private String targetName   = "none";
    private int    targetID     = 0;
    private double targetRange  = 0;
    private double targetBearing= 0;
    private double targetYaw    = 0;

    @Override
    public void runOpMode() {
        double drive  = 0;
        double strafe = 0;
        double turn   = 0;

//        initAprilTag();

        lfDrive = hardwareMap.get(DcMotorEx.class, "lf");
        rfDrive = hardwareMap.get(DcMotorEx.class, "rf");
        lrDrive = hardwareMap.get(DcMotorEx.class, "lr");
        rrDrive = hardwareMap.get(DcMotorEx.class, "rr");

        intakeMotor  = hardwareMap.get(DcMotorEx.class, "intake");
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shooter");
        transferMotor = hardwareMap.get(DcMotorEx.class, "transfer");

        lfDrive.setDirection(DcMotorEx.Direction.REVERSE);
        lrDrive.setDirection(DcMotorEx.Direction.REVERSE);
        rfDrive.setDirection(DcMotorEx.Direction.FORWARD);
        rrDrive.setDirection(DcMotorEx.Direction.FORWARD);

//        if (USE_WEBCAM) {
//            setManualExposure(6, 250); // Low exposure reduces motion blur for vision
//        }

        telemetryInitStatus();

        waitForStart();

        while (opModeIsActive()) {
            targetFound = false;

//            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
//            for (AprilTagDetection detection : currentDetections) {
//                if (detection instanceof AprilTagSingleDetection) {
//                    AprilTagSingleDetection singleDetection = (AprilTagSingleDetection) detection;
//
//                    if (singleDetection.metadata != null) {
//                        if ((DESIRED_TAG_ID < 0) || (singleDetection.id == DESIRED_TAG_ID)) {
//                            targetName    = singleDetection.metadata.name;
//                            targetID      = singleDetection.id;
//                            targetRange   = singleDetection.ftcPose.range;
//                            targetBearing = singleDetection.ftcPose.bearing;
//                            targetYaw     = singleDetection.ftcPose.yaw;
//                            targetFound   = true;
//                            break;
//                        }
//                    }
//                }
//            }

            double rangeError   = targetFound ? (targetRange - DESIRED_DISTANCE) : 0;
            double headingError = targetFound ? targetBearing : 0;
            double yawError     = targetFound ? targetYaw : 0;

            if (targetFound) {
                telemetry.addData("Status", "Tag Found: ID %d (%s)", targetID, targetName);
                telemetry.addData("Range / Bearing", "%.1f in | %.0f deg", targetRange, targetBearing);
            } else {
                telemetry.addData("Status", "Searching for AprilTag...");
            }


            if (gamepad1.left_bumper && targetFound) {
                drive  = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                turn   = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN);
                strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);
                telemetry.addData("Drive Mode", "AUTO ALIGN ACTIVE");
            }
            else {
                drive  = -gamepad1.right_stick_y;
                strafe = gamepad1.right_stick_x;
                turn   = gamepad1.left_stick_x;
                telemetry.addData("Drive Mode", "Manual Control");
            }

            moveRobot(drive, strafe, turn);

            if (gamepad2.a) {
                intakeMotor.setPower(1.0);
            } else if (gamepad2.b) {
                intakeMotor.setPower(-1.0);
            } else {
                intakeMotor.setPower(0.0);
            }

            if (gamepad2.right_bumper) {
                shooterMotor.setPower(-1.0);
            } else {
                shooterMotor.setPower(0.0);
            }

            if (gamepad2.x) {
                transferMotor.setPower(1.0);
            } else if (gamepad2.y) {
                transferMotor.setPower(-1.0);
            } else {
                transferMotor.setPower(0.0);
            }

            telemetry.addData("Intake Power", intakeMotor.getPower());
            telemetry.addData("Shooter Power", shooterMotor.getPower());
            telemetry.addData("Transfer Power", transferMotor.getPower());
            telemetry.update();

            sleep(10);
        }
    }

    public void moveRobot(double x, double y, double yaw) {
        double lfPower = x + y - yaw;
        double rfPower = x - y + yaw;
        double lrPower = x - y - yaw;
        double rrPower = x - y + yaw;

        double max = Math.max(Math.abs(lfPower), Math.abs(rfPower));
        max = Math.max(max, Math.abs(lrPower));
        max = Math.max(max, Math.abs(rrPower));

        if (max > 1.0) {
            lfPower /= max;
            rfPower /= max;
            lrPower /= max;
            rrPower /= max;
        }

        lfDrive.setPower(lfPower);
        rfDrive.setPower(rfPower);
        lrDrive.setPower(lrPower);
        rrDrive.setPower(rrPower);
    }

//    private void initAprilTag() {
//        aprilTag = new AprilTagProcessor.Builder().build();
//        aprilTag.setDecimation(2);
//
//        if (USE_WEBCAM) {
//            visionPortal = new VisionPortal.Builder()
//                    .setCamera(hardwareMap.get(WebcamName.class, "webcam"))
//                    .addProcessor(aprilTag)
//                    .build();
//        } else {
//            visionPortal = new VisionPortal.Builder()
//                    .setCamera(BuiltinCameraDirection.BACK)
//                    .addProcessor(aprilTag)
//                    .build();
//        }
//    }

//    private void setManualExposure(int exposureMS, int gain) {
//        if (visionPortal == null) {
//            return;
//        }
//
//        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
//            while (!isStopRequested() && (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)) {
//                sleep(20);
//            }
//        }
//
//        if (!isStopRequested()) {
//            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
//            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
//                exposureControl.setMode(ExposureControl.Mode.Manual);
//                sleep(50);
//            }
//            exposureControl.setExposure((long) exposureMS, TimeUnit.MILLISECONDS);
//            sleep(20);
//            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
//            gainControl.setGain(gain);
//            sleep(20);
//        }
//    }

    private void telemetryInitStatus() {
        telemetry.addData("Camera preview", "Check Driver Station stream");
        telemetry.addData(">", "Press START to begin OpMode");
        telemetry.update();
    }
}