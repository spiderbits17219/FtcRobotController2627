package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "intake continous", group = "Linear OpMode")
public class intakecontinous extends LinearOpMode {
        private DcMotor myMotor;

    @Override
    public void runOpMode() {
        myMotor = hardwareMap.get(DcMotor.class, "intake");

        myMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        waitForStart();

        while (opModeIsActive()) {

            if (gamepad1.a) {
                myMotor.setPower(1.0);
            } else {
                myMotor.setPower(0.0);
            }
        }
    }
}
