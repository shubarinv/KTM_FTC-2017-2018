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

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuMarkInstanceId;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

@Autonomous(name = "Blue_RIGHT", group = "WIP")
//@Disabled
public class Auto_Blue_Right extends LinearOpMode {
    OpenGLMatrix lastLocation = null;
    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    VuforiaLocalizer vuforia;
    boolean wasExecuted = false;
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor m1_Drive = null;
    private DcMotor m2_Drive = null;
    private DcMotor m3_Drive = null;
    private DcMotor m4_Drive = null;
    private DcMotor m5_Lift = null;
    private Servo s1_top_Claw = null;
    private Servo s2_bottom_Claw = null;
    private Servo s3_rotation = null;


    /*
     * Functions
     */

    void grab_box(boolean top_clamp, boolean top_release, boolean bottom_clamp, boolean bottom_release) {
        if (top_clamp) {
            s1_top_Claw.setPosition(0);
        }
        if (top_release) {
            s1_top_Claw.setPosition(0.50);
        }
        if (bottom_clamp) {
            s2_bottom_Claw.setPosition(0);
        }
        if (bottom_release) {
            s2_bottom_Claw.setPosition(0.50);
        }
    }

    // Lift claw
    void lift_claw(double lift_power, long ms) {
        m5_Lift.setPower(lift_power);
        sleep(ms);
        m5_Lift.setPower(0);
    }

    // Rotate claw
    void rotate_claw(boolean rotate) { //if rotate true then rotate to  180 . else to 0
        if (rotate) {
            s3_rotation.setPosition(1);
        } else {
            s3_rotation.setPosition(0);
        }
    }

    void set_Motors_Power(double D1_power, double D2_power, double D3_power, double D4_power) { //Warning: Эта функция включит моторы но, выключить их надо будет после выполнения какого либо условия
        // Send power to wheels
        m1_Drive.setPower(D1_power);
        m2_Drive.setPower(D2_power);
        m3_Drive.setPower(D3_power);
        m4_Drive.setPower(D4_power);
    }

    void set_Motors_Power_timed(double m1_power, double m2_power, double m3_power, double m4_power, long ms) {
        m1_Drive.setPower(m1_power);
        m2_Drive.setPower(m2_power);
        m3_Drive.setPower(m3_power);
        m4_Drive.setPower(m4_power);
        sleep(ms);
        chassis_stop_movement();
    }

    void chassis_stop_movement() {
        m1_Drive.setPower(0);
        m2_Drive.setPower(0);
        m3_Drive.setPower(0);
        m4_Drive.setPower(0);
    }


    @Override
    public void runOpMode() {
        /*
         * To start up Vuforia, tell it the view that we wish to use for camera monitor (on the RC phone);
         * If no camera monitor is desired, use the parameterless constructor instead (commented out below).
         */
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();
        //Vuforia API key
        parameters.vuforiaLicenseKey = "AfQcHkL/////AAAAGd5Auzk+t0CxnAw8xKONnjke+r6gFs0KfKK8LsB35FsX6bnhXZmEN+0f3blTVk7nI4xjKNob63Ps1Jpp/JS25hHc083okOZzcTsBlA5qz2hJK3LFNWyZv59kjCUyqbc3qS7dTXJ4i4/JD9t+IeyvGH9G9xPwV7DNmcuNeT7o+YDn3cI7zgUcVcrdFM8t22/wGkmiCz5TfY5A0BMETyriYX6BzlVuwGtMfXdp9CYDQ+ZhZTRNjPfvKlNyLLxVycIiM1p4nprW2UnySO11fmTkUZR9Ofqr+gbHj0VNm7gUEz77s/cHTl+swX84pxpOhm1QJeO0wuNw4c5siQpizcWHPMhJCDRFqRmTQ3LBpcMJWjTx";
        /*
         * We also indicate which camera on the RC that we wish to use.
         * Here we chose the back (HiRes) camera (for greater range), but
         * for a competition robot, the front camera might be more convenient.
         */
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.FRONT;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        /**
         * Load the data set containing the VuMarks for Relic Recovery. There's only one trackable
         * in this data set: all three of the VuMarks in the game were created from this one template,
         * but differ in their instance id information.
         * @see VuMarkInstanceId
         */
        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary
        /*
         * Initialize the drive system variables.
         * The init() method of the hardware class does all the work here
         */
        m1_Drive = hardwareMap.get(DcMotor.class, "m1 drive");
        m2_Drive = hardwareMap.get(DcMotor.class, "m2 drive");
        m3_Drive = hardwareMap.get(DcMotor.class, "m3 drive");
        m4_Drive = hardwareMap.get(DcMotor.class, "m4 drive");
        m5_Lift = hardwareMap.get(DcMotor.class, "m5 lift");
        s1_top_Claw = hardwareMap.get(Servo.class, "s1 top claw");
        s2_bottom_Claw = hardwareMap.get(Servo.class, "s2 bottom claw");
        s3_rotation = hardwareMap.get(Servo.class, "s3 rotation");

        m1_Drive.setDirection(DcMotor.Direction.FORWARD);
        m2_Drive.setDirection(DcMotor.Direction.FORWARD);
        m3_Drive.setDirection(DcMotor.Direction.FORWARD);
        m4_Drive.setDirection(DcMotor.Direction.FORWARD);
/*
        telemetry.addData(">", "Press Play to start");
        telemetry.update();*/
        s1_top_Claw.setPosition(0);
        s2_bottom_Claw.setPosition(0);
        waitForStart();

        relicTrackables.activate();
        //wasExecuted=false;
        while (opModeIsActive()) {
            if (wasExecuted) {
                break;
            }
            /**
             * See if any of the instances of {@link relicTemplate} are currently visible.
             * {@link RelicRecoveryVuMark} is an enum which can have the following values:
             * UNKNOWN, LEFT, CENTER, and RIGHT. When a VuMark is visible, something other than
             * UNKNOWN will be returned by {@link RelicRecoveryVuMark#from(VuforiaTrackable)}.
             */
            RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
            if (vuMark != RelicRecoveryVuMark.UNKNOWN) {

                /* Found an instance of the template. In the actual game, you will probably
                 * loop until this condition occurs, then move on to act accordingly depending
                 * on which VuMark was visible. */
                telemetry.addData("VuMark", "%s visible", vuMark);
                OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) relicTemplate.getListener()).getPose();
                telemetry.addData("Pose", format(pose));
                /* We further illustrate how to decompose the pose into useful rotational and
                 * translational components */
                if (pose != null) {
                    VectorF trans = pose.getTranslation();
                    Orientation rot = Orientation.getOrientation(pose, AxesReference.EXTRINSIC, AxesOrder.XYZ, AngleUnit.DEGREES);

                    // Extract the X, Y, and Z components of the offset of the target relative to the robot
                    double tX = trans.get(0);
                    double tY = trans.get(1);
                    double tZ = trans.get(2);
                    // Extract the rotational components of the target relative to the robot
                    double rX = rot.firstAngle;
                    double rY = rot.secondAngle;
                    double rZ = rot.thirdAngle;
                    telemetry.addData("Vumark Rotation :", "rX:", rX, "rY:", rY, "rZ:", rZ);
                    telemetry.update();

                    if (tX < -100) {
                        set_Motors_Power(-0.1, 0.1, 0.1, -0.1);
                    } else if (-100 < tX && tX < 100) {
                        if (!wasExecuted) {
                            if (vuMark == RelicRecoveryVuMark.RIGHT) {
                                telemetry.addData("Vumark", " RIGHT");
                                telemetry.update();
                                grab_box(true, false, false, true);
                                sleep(100);
                                lift_claw(0.3, 1250);
                                sleep(100);
                                set_Motors_Power_timed(-0.2, 0.2, 0.2, -0.2, 3500);//движение вперед
                                set_Motors_Power_timed(0.2, 0.2, 0.2, 0.2, 1350);//поворот по против часовой
                                lift_claw(-0.3, 1250);
                                sleep(100);
                                grab_box(false, true, false, false);
                                set_Motors_Power_timed(-0.2, 0.2, 0.2, -0.2, 1000);
                                sleep(100);
                                set_Motors_Power_timed(0.2, -0.2, -0.2, 0.2, 500);//движение назад
                                wasExecuted = true;
                            }
                            if (vuMark == RelicRecoveryVuMark.CENTER) {
                                telemetry.addData("Vumark", " CENTER");
                                telemetry.update();

                                grab_box(true, false, false, true);
                                sleep(500);
                                lift_claw(0.3, 1250);
                                sleep(100);
                                set_Motors_Power_timed(-0.2, 0.2, 0.2, -0.2, 2750);//движение вперед
                                set_Motors_Power_timed(0.2, 0.2, 0.2, 0.2, 1350);//поворот по против часовой
                                lift_claw(-0.3, 1250);
                                sleep(100);
                                grab_box(false, true, false, false);
                                set_Motors_Power_timed(-0.2, 0.2, 0.2, -0.2, 1000);
                                sleep(100);
                                set_Motors_Power_timed(0.2, -0.2, -0.2, 0.2, 500);//движение назад
                                wasExecuted = true;
                            }
                            if (vuMark == RelicRecoveryVuMark.LEFT) {
                                telemetry.addData("Vumark", " LEFT");
                                telemetry.update();
                                grab_box(true, false, false, true);
                                sleep(500);
                                lift_claw(0.3, 1250);
                                sleep(100);

                                set_Motors_Power_timed(-0.2, 0.2, 0.2, -0.2, 2100);//движение вперед
                                set_Motors_Power_timed(0.2, 0.2, 0.2, 0.2, 1350);//поворот по против часовой
                                lift_claw(-0.3, 1250);
                                sleep(100);
                                grab_box(false, true, false, false);
                                set_Motors_Power_timed(-0.2, 0.2, 0.2, -0.2, 1000);
                                sleep(100);
                                set_Motors_Power_timed(0.2, -0.2, -0.2, 0.2, 500);//движение назад
                                wasExecuted = true;
                            }
                        }
                    } else {
                        set_Motors_Power(0.1, -0.1, -0.1, 0.1);
                    }


                } else {
                    telemetry.addData("VuMark", "not visible");
                }
                telemetry.update();
            }
        }
    }

    String format(OpenGLMatrix transformationMatrix) {
        return (transformationMatrix != null) ? transformationMatrix.formatAsTransform() : "null";
    }
}
