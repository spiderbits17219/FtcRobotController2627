
package org.firstinspires.ftc.teamcode.apriltag;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import org.firstinspires.ftc.robotcore.external.hardware.camera.BuiltinCameraDirection;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamServer;
import org.firstinspires.ftc.vision.VisionPortal;
import org.firstinspires.ftc.vision.apriltag.AprilTagDetection;
import org.firstinspires.ftc.vision.apriltag.AprilTagProcessor;
import org.firstinspires.ftc.vision.apriltag.AprilTagSingleDetection;

import java.util.List;

@TeleOp(name = "Concept: AprilTag Stream", group = "Concept")
public class ConceptAprilTagStream extends LinearOpMode {

    private static final boolean USE_WEBCAM = true;
    private VisionPortal visionPortal;
    private AprilTagProcessor aprilTag;

    @Override
    public void runOpMode() {
        initAprilTag();

        telemetry.addData("Camera preview on/off", "3 dots, Camera Stream");
        telemetry.addData(">", "Touch START to start OpMode");
        telemetry.update();
        waitForStart();

        while (opModeIsActive()) {
            telemetryAprilTag();
            telemetry.update();

            if (gamepad1.dpad_down) {
                visionPortal.stopStreaming();
            } else if (gamepad1.dpad_up) {
                visionPortal.resumeStreaming();
            }

            sleep(20);
        }

        visionPortal.close();
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

        // Project the webcam feed to the Driver Hub Camera Stream
        CameraStreamServer.getInstance().setSource(visionPortal);
    }

    private void telemetryAprilTag() {
        List<AprilTagDetection> currentDetections = aprilTag.getDetections();
        telemetry.addData("# AprilTags Detected", currentDetections.size());

        for (AprilTagDetection detection : currentDetections) {
            if (detection instanceof AprilTagSingleDetection) {
                AprilTagSingleDetection singleDet = (AprilTagSingleDetection) detection;

                if (singleDet.metadata != null) {
                    telemetry.addLine(String.format("--- Tag ID: %d (%s) ---", singleDet.id, singleDet.metadata.name));
                } else {
                    telemetry.addLine(String.format("--- Tag ID: %d (Unknown/Custom) ---", singleDet.id));
                }

                // Display distance and angles whenever ftcPose is available, or show error if null
                if (singleDet.ftcPose != null) {
                    telemetry.addData("  Range (Distance)", "%5.1f inches", singleDet.ftcPose.range);
                    telemetry.addData("  Bearing (Angle)", "%3.0f degrees", singleDet.ftcPose.bearing);
                    telemetry.addData("  Yaw (Rotation)", "%3.0f degrees", singleDet.ftcPose.yaw);
                } else {
                    telemetry.addData("  Pose Error", "Tag found, but ftcPose is NULL (Check Tag Library!)");
                }
            }
        }
    }
}