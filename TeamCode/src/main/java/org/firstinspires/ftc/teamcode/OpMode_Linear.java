/* Copyright (c) 2017 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;


/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 * <p>
 * This particular OpMode just executes a basic Tank Drive Teleop for a four wheeled robot
 * It includes all the skeletal structure that all linear OpModes contain.
 * <p>
 * Use Android Studios to Copy this Class, and Paste it into your team's code folder with a new name.
 * Remove or comment out the @Disabled line to add this opmode to the Driver Station OpMode list
 */

@TeleOp(name = "Basic: Linear OpMode", group = "Linear Opmode")
@Disabled
public class OpMode_Linear extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor m1_Drive = null;
    private DcMotor m2_Drive = null;
    private DcMotor m3_Drive = null;
    private DcMotor m4_Drive = null;

    @Override
    public void runOpMode() {
        telemetry.addData("Status", "Initialized");
        telemetry.update();

        // Initialize the hardware variables. Note that the strings used here as parameters
        // to 'get' must correspond to the names assigned during the robot configuration
        // step (using the FTC Robot Controller app on the phone).
        // m2
        m1_Drive = hardwareMap.get(DcMotor.class, "m1_drive");
        m2_Drive = hardwareMap.get(DcMotor.class, "m2_drive");
        m3_Drive = hardwareMap.get(DcMotor.class, "m3_drive");
        m4_Drive = hardwareMap.get(DcMotor.class, "m4_drive");

        // Most robots need the motor on one side to be reversed to drive forward
        // Reverse the motor that runs backwards when connected directly to the battery
        m1_Drive.setDirection(DcMotor.Direction.FORWARD);
        m2_Drive.setDirection(DcMotor.Direction.FORWARD);
        m3_Drive.setDirection(DcMotor.Direction.FORWARD);
        m4_Drive.setDirection(DcMotor.Direction.FORWARD);

        // Wait for the game to start (driver presses PLAY)
        waitForStart();
        runtime.reset();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {

            // Setup a variable for each drive wheel to save power level for telemetry
            double m1_Drive_Power;
            double m2_Drive_Power;
            double m3_Drive_Power;
            double m4_Drive_Power;

            // POV Mode uses right stick to go forward and right to turn.
            // - This uses basic math to combine motions and is easier to drive straight.
            double drive = gamepad1.right_stick_y;
            double turn = gamepad1.right_stick_x;
            double rotation_left_stick = gamepad1.left_stick_x;
            double A = rotation_left_stick + drive + turn;
            if (A <= 1) {
                m1_Drive_Power = Range.clip(rotation_left_stick - drive - turn, -1.0, 1.0);
                m2_Drive_Power = Range.clip(rotation_left_stick + drive - turn, -1.0, 1.0);
                m3_Drive_Power = Range.clip(rotation_left_stick + drive + turn, -1.0, 1.0);
                m4_Drive_Power = Range.clip(rotation_left_stick - drive + turn, -1.0, 1.0);
            } else if (A > 1) {
                rotation_left_stick = rotation_left_stick / A;
                drive = drive / A;
                turn = turn / A;
                m1_Drive_Power = Range.clip(rotation_left_stick - drive - turn, -1.0, 1.0);
                m2_Drive_Power = Range.clip(rotation_left_stick - drive - turn, -1.0, 1.0);
                m3_Drive_Power = Range.clip(rotation_left_stick - drive - turn, -1.0, 1.0);
                m4_Drive_Power = Range.clip(rotation_left_stick - drive - turn, -1.0, 1.0);
            } else {
                m1_Drive_Power = Range.clip(0, -1.0, 1.0);
                m2_Drive_Power = Range.clip(0, -1.0, 1.0);
                m3_Drive_Power = Range.clip(0, -1.0, 1.0);
                m4_Drive_Power = Range.clip(0, -1.0, 1.0);
            }
            // Send calculated power to wheels
            m1_Drive.setPower(m1_Drive_Power);
            m2_Drive.setPower(m2_Drive_Power);
            m2_Drive.setPower(m3_Drive_Power);
            m2_Drive.setPower(m4_Drive_Power);

            // Show the elapsed game time and wheel power.
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Motors", "m1_Drive (%.2f), m2_Drive (%.2f), m3_Drive (%.2f), m4_Drive (%.2f)", m1_Drive_Power, m2_Drive_Power, m3_Drive_Power, m4_Drive_Power);
            telemetry.update();
        }
    }
}
