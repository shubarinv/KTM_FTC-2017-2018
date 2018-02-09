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

package org.firstinspires.ftc.teamcode.AutoOPs;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.Objects;


@Autonomous(name = "Blue_RIGHT", group = "WIP")
//@Disabled
public class Auto_Blue_Left extends LinearOpMode {
    /* ADAFRUIT */
    // we assume that the LED pin of the RGB sensor is connected to
    // digital port 5 (zero indexed).
    private static final int LED_CHANNEL = 5;
    ColorSensor sensorRGB;
    DeviceInterfaceModule cdim;
    // hsvValues is an array that will hold the hue, saturation, and value information.
    float hsvValues[] = {0F, 0F, 0F};
    // values is a reference to the hsvValues array.
    final float values[] = hsvValues;
    // bPrevState and bCurrState represent the previous and current state of the button.
    boolean bPrevState = false;
    boolean bCurrState = false;
    // bLedOn represents the state of the LED.
    boolean bLedOn = false;
    OpticalDistanceSensor odsSensor;  // Hardware Device Object
    private CRServo s1_Relic_ext_ret = null;
    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;
    private boolean wasExecuted = false;
    /* Declare OpMode members. */
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotor m1_Drive = null;
    private DcMotor m2_Drive = null;
    private DcMotor m3_Drive = null;
    private DcMotor m4_Drive = null;
    private DcMotor m5_Lift = null;
    private CRServo s1_top_Claw = null;
    private CRServo s2_bottom_Claw = null;
    private Servo s4_kicker = null;
    private Servo s3_rotation = null;
    private Servo s5_shovel = null;
    private DcMotor m6_intake = null;
  /*
  * Functions
  */

    void grab_box(boolean top_clamp, boolean top_release, boolean bottom_clamp, boolean bottom_release) {
        // DEPERECATED
        assert true;
    }

    // Lift claw
    void lift_claw(double lift_power, long ms) {
        // DEPERCATED
        assert true;
    }

    // Rotate claw
    void rotate_claw(double rotate) { //if rotate true then rotate to  180 . else to 0
        s3_rotation.setPosition(rotate);
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

    String get_color() {
        // check the status of the x button on gamepad.
        bCurrState = true;

        // check for button-press state transitions.

        // button is transitioning to a pressed state. Toggle the LED.
        cdim.setDigitalChannelState(LED_CHANNEL, true);

        // update previous state variable.
        bPrevState = bCurrState;

        double[] hue_arr = new double[5];
        double[] blue = new double[5];
        double[] red = new double[5];
        //для точности 4 измерения
        for (int j = 0; j < 4; j++) {
            // convert the RGB values to HSV values.
            telemetry.addData("Blue", sensorRGB.blue());
            telemetry.addData("Red", sensorRGB.red());
            telemetry.update();
            sleep(500);
            Color.RGBToHSV((sensorRGB.red() * 255) / 800, (sensorRGB.green() * 255) / 800, (sensorRGB.blue() * 255) / 800, hsvValues);
            red[j] = sensorRGB.red() * 255 / 800;
            blue[j] = sensorRGB.blue() * 255 / 800;
            double hue = hsvValues[0];
            hue_arr[j] = hue;
        }

        //Находим среднее арифметическое
        double red_sr = 0;
        double blue_sr = 0;
        double hue_sr = 0;
        for (int j = 0; j < 4; j++) {
            red_sr = red_sr + red[j];
            blue_sr = blue_sr + blue[j];
            hue_sr = hue_sr + hue_arr[j];
        }
        red_sr = red_sr / 4;
        blue_sr = blue_sr / 4;
        hue_sr = hue_sr / 4;
        //
        if (hue_sr > 110 && hue_sr < 290) {
            return "Blue";
        } else if (hue_sr < 110 || hue_sr > 290 && hue_sr <= 360) {
            return "Red";
        }
        // THIS IS DEPRECATED
        else if (blue_sr > red_sr) {
            return "Blue";
        } else {
            return "Red";
        }
    }


    @Override
    public void runOpMode() {
        s3_rotation.setPosition(0.8);

        s1_Relic_ext_ret = hardwareMap.get(CRServo.class, "s1 top claw");
        s1_Relic_ext_ret.setPower(0);
    /*
    * To start up Vuforia, tell it the view that we wish to use for camera monitor (on the RC phone);
    * If no camera monitor is desired, use the parameterless constructor instead (commented out below).
    */
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        //Vuforia API key
        parameters.vuforiaLicenseKey = "AfQcHkL/////AAAAGd5Auzk+t0CxnAw8xKONnjke+r6gFs0KfKK8LsB35FsX6bnhXZmEN+0f3blTVk7nI4xjKNob63Ps1Jpp/JS25hHc083okOZzcTsBlA5qz2hJK3LFNWyZv59kjCUyqbc3qS7dTXJ4i4/JD9t+IeyvGH9G9xPwV7DNmcuNeT7o+YDn3cI7zgUcVcrdFM8t22/wGkmiCz5TfY5A0BMETyriYX6BzlVuwGtMfXdp9CYDQ+ZhZTRNjPfvKlNyLLxVycIiM1p4nprW2UnySO11fmTkUZR9Ofqr+gbHj0VNm7gUEz77s/cHTl+swX84pxpOhm1QJeO0wuNw4c5siQpizcWHPMhJCDRFqRmTQ3LBpcMJWjTx";
    /*
    * We also indicate which camera on the RC that we wish to use.
    * Here we chose the back (HiRes) camera (for greater range), but
    * for a competition robot, the front camera might be more convenient.
    */
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary
    /*
    * Initialize the drive system variables.
    * The init() method of the hardware class does all the work here
    */
        //Обработка исключений
        // m1_drive
        try {
            m1_Drive = hardwareMap.get(DcMotor.class, "m1 drive");
        } catch (RuntimeException e) {
            m1_Drive = null;
            telemetry.addData("EXCEPTION", "Отвалился m1_Drive");
        }
        // m2_drive
        try {
            m2_Drive = hardwareMap.get(DcMotor.class, "m2 drive");
        } catch (RuntimeException e) {
            m2_Drive = null;
            telemetry.addData("EXCEPTION", "Отвалился m2_Drive");
        }
        // m3_drive
        try {
            m3_Drive = hardwareMap.get(DcMotor.class, "m3 drive");
        } catch (RuntimeException e) {
            m3_Drive = null;
            telemetry.addData("EXCEPTION", "Отвалился m3_Drive");
        }
        // m4_drive
        try {
            m4_Drive = hardwareMap.get(DcMotor.class, "m4 drive");
        } catch (RuntimeException e) {
            m4_Drive = null;
            telemetry.addData("EXCEPTION", "Отвалился m4_Drive");
        }
        // m5_lift
        try {
            m5_Lift = hardwareMap.get(DcMotor.class, "m5 lift");
        } catch (RuntimeException e) {
            m5_Lift = null;
            telemetry.addData("EXCEPTION", "Отвалился m5_lift");
        }
        // s1_top_Claw
        try {
            s1_top_Claw = hardwareMap.get(CRServo.class, "s1 top claw");
        } catch (RuntimeException e) {
            s1_top_Claw = null;
            telemetry.addData("EXCEPTION", "Отвалился s1 top claw");
        }
        // s2_bottom_Claw
        try {
            s2_bottom_Claw = hardwareMap.get(CRServo.class, "s2 bottom claw");
        } catch (RuntimeException e) {
            s2_bottom_Claw = null;
            telemetry.addData("EXCEPTION", "Отвалился s2 bottom claw");
        }
        //s4_kicker
        try {
            s4_kicker = hardwareMap.get(Servo.class, "s4 kick");
        } catch (RuntimeException e) {
            s4_kicker = null;
            telemetry.addData("EXCEPTION", "Отвалился s4 kick(палка)");
        }
        odsSensor = hardwareMap.get(OpticalDistanceSensor.class, "sensor_ods");
        s3_rotation = hardwareMap.get(Servo.class, "s3 rotation");
        s5_shovel = hardwareMap.get(Servo.class, "s5 shovel");
        m6_intake = hardwareMap.get(DcMotor.class, "m6 intake");
        // Конец обработки исключений

        m1_Drive.setDirection(DcMotor.Direction.FORWARD);
        m2_Drive.setDirection(DcMotor.Direction.FORWARD);
        m3_Drive.setDirection(DcMotor.Direction.FORWARD);
        m4_Drive.setDirection(DcMotor.Direction.FORWARD);
    /* AdaFruit */

        // get a reference to our DeviceInterfaceModule object.
        cdim = hardwareMap.deviceInterfaceModule.get("dim");

        // set the digital channel to output mode.
        // remember, the Adafruit sensor is actually two devices.
        // It's an I2C sensor and it's also an LED that can be turned on or off.
        cdim.setDigitalChannelMode(LED_CHANNEL, DigitalChannel.Mode.OUTPUT);

        // get a reference to our ColorSensor object.
        sensorRGB = hardwareMap.colorSensor.get("sensor_color");

        // turn the LED on in the beginning, just so user will know that the sensor is active.
        cdim.setDigitalChannelState(LED_CHANNEL, bLedOn);
        telemetry.addData("AdaFruit", "Ready");
        telemetry.update();

        waitForStart();

        relicTrackables.activate();
        while (opModeIsActive()) {
            s1_Relic_ext_ret.setPower(0);
            if (wasExecuted) {
                telemetry.addData("Autonomous: ", "DONE");
            }

            if (!wasExecuted) {
                RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
        /*
        STEP 1 -Trying to kick jewel
        */
                rotate_claw(0.8);// so that boxes won't fall off
                sleep(800);
                m6_intake.setPower(0.6);
                s4_kicker.setPosition(0.85);
                sleep(500);
                grab_box(true, false, true, false);
                lift_claw(0.1, 250);

                telemetry.addData("Step-1", "Running");
                String jewel_color = get_color();
                telemetry.addData("AdaFruit", jewel_color);
                telemetry.update();
                if (Objects.equals(jewel_color, "Blue")) {
                    set_Motors_Power_timed(-0.1, -0.1, -0.1, -0.1, 300);//поворот по часовой
                    set_Motors_Power_timed(0.1, 0.1, 0.1, 0.1, 300);//поворот против часовой
                } else if (Objects.equals(jewel_color, "Red")) {
                    set_Motors_Power_timed(0.1, 0.1, 0.1, 0.1, 300);//поворот против часовой
                    set_Motors_Power_timed(-0.1, -0.1, -0.1, -0.1, 300);//поворот по часовой
                } else {
                    telemetry.addData("AdaFruit", "ERROR RECOGNISING COLOR");
                    telemetry.addData("Step-1", "FAILED");
                    telemetry.update();
                }
                s4_kicker.setPosition(0.1);
                cdim.setDigitalChannelState(LED_CHANNEL, false);
                //requestOpModeStop(); //WARNING THIS WILL STOP OPMODE


        /*
        STEP 2 -Cryptobox related
        */
                //requestOpModeStop();
                set_Motors_Power_timed(0.2, -0.2, -0.2, 0.2, 2100);//движение вперёд
                /*while (odsSensor.getLightDetected() < 0.8) {
                    set_Motors_Power(0.1, -0.1, -0.1, 0.1);//движение вперёд
                    telemetry.addData("Line", "(X)NOT VISIBLE");
                    telemetry.update();
                }*/
                telemetry.addData("Line", "VISIBLE (OK)");
                telemetry.update();
                set_Motors_Power_timed(0.2, 0.2, 0.2, 0.2, 600);//поворот против часовой
                if (vuMark == RelicRecoveryVuMark.RIGHT) {
                    telemetry.addData("Vumark", " RIGHT");
                    telemetry.update();
                } else if (vuMark == RelicRecoveryVuMark.CENTER) {
                    telemetry.addData("Vumark", " CENTER");
                    telemetry.update();
                } else if (vuMark == RelicRecoveryVuMark.LEFT) {
                    telemetry.addData("Vumark", " LEFT");
                    telemetry.update();
                } else {
                    telemetry.addData("Vumark", " NOT VISIBLE (X)");
                    telemetry.update();
                }
                set_Motors_Power_timed(-0.1, 0.1, 0.1, -0.1, 1000);//движение назад
                sleep(100);
                set_Motors_Power_timed(0.1, -0.1, -0.1, 0.1, 300);//движение вперёд
                rotate_claw(0);
                sleep(1000);
                set_Motors_Power_timed(0.1, -0.1, -0.1, 0.1, 300);//движение вперёд
                set_Motors_Power_timed(-0.1, 0.1, 0.1, -0.1, 300);//движение назад
                set_Motors_Power_timed(0.1, -0.1, -0.1, 0.1, 300);//движение вперёд
                rotate_claw(0.8);

                wasExecuted = true;
            }
            telemetry.update();
        }
    }
}
