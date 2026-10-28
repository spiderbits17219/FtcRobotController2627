package org.firstinspires.ftc.teamcode.apriltag;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.ExposureControl;
import org.firstinspires.ftc.robotcore.external.hardware.camera.controls.GainControl;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagClusterDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.List;
import java.util.concurrent.TimeUnit;

@TeleOp(name="Omni Drive To AprilTag", group = "Concept")
public class RobotAutoDriveToAprilTagOmni extends LinearOpMode
{
    // Adjust these numbers to suit your robot.
    final double DESIRED_DISTANCE = 30.0; //  this is how close the camera should get to the target (inches)

    //  Set the GAIN constants to control the relationship between the measured position error, and how much power is
    //  applied to the drive motors to correct the error.
    final double SPEED_GAIN  =  0.02  ;   //  Forward Speed Control "Gain"
    final double STRAFE_GAIN =  0.015 ;   //  Strafe Speed Control "Gain"
    final double TURN_GAIN   =  0.01  ;   //  Turn Control "Gain"

    final double MAX_AUTO_SPEED = 0.5;    //  Clip the approach speed to this max value
    final double MAX_AUTO_STRAFE= 0.5;    //  Clip the strafing speed to this max value
    final double MAX_AUTO_TURN  = 0.3;    //  Clip the turn speed to this max value

    private DcMotor lfDrive = null;  //  Left front drive wheel
    private DcMotor rfDrive = null;  //  Right front drive wheel
    private DcMotor lrDrive = null;  //  Left back drive wheel
    private DcMotor rrDrive = null;  //  Right back drive wheel

    private final boolean USE_WEBCAM = true;  // Set true to use a webcam, or false for a phone camera
    private final int DESIRED_TAG_ID = -1;    // The tag you want to approach, or set to -1 for ANY tag.
    private final String DESIRED_CLUSTER_NAME = null; // The cluster name you want to approach

    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;

    private boolean targetFound  = false;
    private String targetName    = "none";
    private int    targetID      = 0;
    private double targetRange   = 0;
    private double targetBearing = 0;
    private double targetYaw     = 0;

    @Override public void runOpMode()
    {
        double  drive           = 0;
        double  strafe          = 0;
        double  turn            = 0;

        // Initialize the Apriltag Detection process
        initAprilTag();

        // Initialize hardware variables
        lfDrive = hardwareMap.get(DcMotor.class, "lf");
        rfDrive = hardwareMap.get(DcMotor.class, "rf");
        lrDrive = hardwareMap.get(DcMotor.class, "lr");
        rrDrive = hardwareMap.get(DcMotor.class, "rr");

        // Motor directions
        lfDrive.setDirection(DcMotor.Direction.REVERSE);
        lrDrive.setDirection(DcMotor.Direction.REVERSE);
        rfDrive.setDirection(DcMotor.Direction.FORWARD);
        rrDrive.setDirection(DcMotor.Direction.FORWARD);

        if (USE_WEBCAM)
            setManualExposure(6, 250);  // Use low exposure time to reduce motion blur

        // Wait for driver to press start
        telemetry.addData("Camera preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch START to start OpMode");
        telemetry.update();
        waitForStart();

        while (opModeIsActive())
        {
            targetFound = false;

            // Step through the list of detected tags and look for a matching tag
            List<AprilTagDetection> currentDetections = aprilTag.getDetections();
            telemetry.addData("current_detection", currentDetections.size());
            for (AprilTagDetection detection : currentDetections) {

                if (detection instanceof AprilTagSingleDetection) {
                    AprilTagSingleDetection singleDetection = (AprilTagSingleDetection) detection;

                    if (singleDetection.metadata != null) {
                        if ((DESIRED_TAG_ID < 0) || (singleDetection.id == DESIRED_TAG_ID)) {
                            targetName    = singleDetection.metadata.name;
                            targetID      = singleDetection.id;
                            targetRange   = singleDetection.ftcPose.range;
                            targetBearing = singleDetection.ftcPose.bearing;
                            targetYaw     = singleDetection.ftcPose.yaw;
                            targetFound = true;
                            break;
                        } else {
                            telemetry.addData("Skipping", "Tag ID %d is not desired", singleDetection.id);
                        }
                    } else {
                        telemetry.addData("Unknown", "Tag ID %d is not in TagLibrary", singleDetection.id);
                    }
                } else {
                    AprilTagClusterDetection clusterDet = (AprilTagClusterDetection) detection;

                    if (DESIRED_CLUSTER_NAME == null || clusterDet.metadata.shortName.equals(DESIRED_CLUSTER_NAME) ) {
                        targetName    = clusterDet.metadata.shortName;
                        targetID      = -1;
                        targetRange   = clusterDet.ftcPose.range;
                        targetBearing = clusterDet.ftcPose.bearing;
                        targetYaw     = clusterDet.ftcPose.yaw;
                        targetFound = true;
                        break;
                    }
                }
            }

            // Tell the driver what we see
            if (targetFound) {
                telemetry.addData("\n>","HOLD Left-Bumper to Drive to Target\n");
                telemetry.addData("Found", "ID %d (%s)", targetID, targetName);
                telemetry.addData("Range",  "%5.1f inches", targetRange);
                telemetry.addData("Bearing","%3.0f degrees", targetBearing);
                telemetry.addData("Yaw","%3.0f degrees", targetYaw);
            } else {
                telemetry.addData("\n>","Drive using joysticks to find valid target\n");
            }

            // If Left Bumper is pressed AND we found a target, Drive Automatically
            if (gamepad1.left_bumper && targetFound) {

                // FIXED: rangeError calculation is now properly on a single line with subtraction
                double  rangeError      = targetRange - DESIRED_DISTANCE;
                double  headingError    = targetBearing;
                double  yawError        = targetYaw;

                // Calculate movement powers using gains
                drive  = Range.clip(rangeError * SPEED_GAIN, -MAX_AUTO_SPEED, MAX_AUTO_SPEED);
                turn   = Range.clip(headingError * TURN_GAIN, -MAX_AUTO_TURN, MAX_AUTO_TURN) ;
                strafe = Range.clip(-yawError * STRAFE_GAIN, -MAX_AUTO_STRAFE, MAX_AUTO_STRAFE);

                telemetry.addData("Auto","Drive %5.2f, Strafe %5.2f, Turn %5.2f ", drive, strafe, turn);
            } else {
                // Manual POV Joystick mode
                drive  = -gamepad1.left_stick_y  / 2.0;
                strafe = -gamepad1.left_stick_x  / 2.0;
                turn   = -gamepad1.right_stick_x / 3.0;
                telemetry.addData("Manual","Drive %5.2f, Strafe %5.2f, Turn %5.2f ", drive, strafe, turn);
            }
            telemetry.update();

            // Apply motions to the drivetrain
            moveRobot(drive, strafe, turn);
            sleep(10);
        }
    }

    public void moveRobot(double x, double y, double yaw) {
        double lfPower = x - y - yaw;
        double rfPower = x + y + yaw;
        double lrPower = x + y - yaw;
        double rrPower = x - y + yaw;

        // Normalize wheel powers
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

    private void initAprilTag() {
        aprilTag = new AprilTagProcessor.Builder().build();
        aprilTag.setDecimation(2);

        if (USE_WEBCAM) {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(hardwareMap.get(WebcamName.class, "webcam"))
                    .addProcessor(aprilTag)
                    .build();
        } else {
            visionPortal = new VisionPortal.Builder()
                    .setCamera(BuiltinCameraDirection.BACK)
                    .addProcessor(aprilTag)
                    .build();
        }
    }

    private void setManualExposure(int exposureMS, int gain) {
        if (visionPortal == null) {
            return;
        }

        if (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING) {
            telemetry.addData("Camera", "Waiting");
            telemetry.update();
            while (!isStopRequested() && (visionPortal.getCameraState() != VisionPortal.CameraState.STREAMING)) {
                sleep(20);
            }
            telemetry.addData("Camera", "Ready");
            telemetry.update();
        }

        if (!isStopRequested())
        {
            ExposureControl exposureControl = visionPortal.getCameraControl(ExposureControl.class);
            if (exposureControl.getMode() != ExposureControl.Mode.Manual) {
                exposureControl.setMode(ExposureControl.Mode.Manual);
                sleep(50);
            }
            exposureControl.setExposure((long)exposureMS, TimeUnit.MILLISECONDS);
            sleep(20);
            GainControl gainControl = visionPortal.getCameraControl(GainControl.class);
            gainControl.setGain(gain);
            sleep(20);
        }
    }
}