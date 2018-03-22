package org.firstinspires.ftc.teamcode.AutoOPs;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.OpticalDistanceSensor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.RelicRecoveryVuMark;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

import java.util.Objects;

/**
 * Created by root on 22.03.18.
 */

public class AutoOPBase extends LinearOpMode {
    private VuforiaLocalizer vuforia;
    float hsvValues[] = {0F, 0F, 0F};
    // bLedOn represents the state of the LED.
    boolean bLedOn = false;
    OpticalDistanceSensor odsSensor;  // Hardware Device Object
    private static final int LED_CHANNEL = 5;
    TouchSensor touchSensor;  // Hardware Device Object
    ColorSensor sensorRGB;
    DeviceInterfaceModule cdim;
    // Declare OpMode members.
    protected ElapsedTime runtime = new ElapsedTime();
    //Chassis
    protected DcMotor m1_Drive = null;
    protected DcMotor m2_Drive = null;
    protected DcMotor m3_Drive = null;
    protected DcMotor m4_Drive = null;
    protected DcMotor m5_Lift = null;
    protected DcMotor m6_intake = null;
    protected CRServo s1_top_Claw = null;
    protected CRServo s1_Relic_ext_ret = null;
    protected Servo s3_rotation = null;
    protected Servo s4_kicker = null;
    protected Servo s5_shovel = null;
    protected Servo s6_relic_claw = null;
    protected Servo s7_relic_arm = null;
    VuforiaTrackables relicTrackables = null;
    VuforiaTrackable relicTemplate = relicTrackables.get(0);
    boolean wasExecuted = false;
    boolean isPositioned = false;

    void vuforiaInit() throws RuntimeException {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters(cameraMonitorViewId);
        //Vuforia API key
        parameters.vuforiaLicenseKey = "AfQcHkL/////AAAAGd5Auzk+t0CxnAw8xKONnjke+r6gFs0KfKK8LsB35FsX6bnhXZmEN+0f3blTVk7nI4xjKNob63Ps1Jpp/JS25hHc083okOZzcTsBlA5qz2hJK3LFNWyZv59kjCUyqbc3qS7dTXJ4i4/JD9t+IeyvGH9G9xPwV7DNmcuNeT7o+YDn3cI7zgUcVcrdFM8t22/wGkmiCz5TfY5A0BMETyriYX6BzlVuwGtMfXdp9CYDQ+ZhZTRNjPfvKlNyLLxVycIiM1p4nprW2UnySO11fmTkUZR9Ofqr+gbHj0VNm7gUEz77s/cHTl+swX84pxpOhm1QJeO0wuNw4c5siQpizcWHPMhJCDRFqRmTQ3LBpcMJWjTx";
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        this.vuforia = ClassFactory.createVuforiaLocalizer(parameters);

        VuforiaTrackables relicTrackables = this.vuforia.loadTrackablesFromAsset("RelicVuMark");
        VuforiaTrackable relicTemplate = relicTrackables.get(0);
        relicTemplate.setName("relicVuMarkTemplate"); // can help in debugging; otherwise not necessary
    }

    void initialise() throws RuntimeException {
        //Chassis
        m1_Drive = hardwareMap.get(DcMotor.class, "m1 drive");
        m2_Drive = hardwareMap.get(DcMotor.class, "m2 drive");
        m3_Drive = hardwareMap.get(DcMotor.class, "m3 drive");
        m4_Drive = hardwareMap.get(DcMotor.class, "m4 drive");
        m5_Lift = hardwareMap.get(DcMotor.class, "m5 lift");
        s1_top_Claw = hardwareMap.get(CRServo.class, "s1 top claw");
        s4_kicker = hardwareMap.get(Servo.class, "s4 kick");
        odsSensor = hardwareMap.get(OpticalDistanceSensor.class, "sensor_ods");
        s3_rotation = hardwareMap.get(Servo.class, "s3 rotation");
        s5_shovel = hardwareMap.get(Servo.class, "s5 shovel");
        m6_intake = hardwareMap.get(DcMotor.class, "m6 intake");
        s1_Relic_ext_ret = hardwareMap.get(CRServo.class, "s1 top claw");
    }

    void preRunVarUpdates() {
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
    }

    String getJewelColor() {
        // button is transitioning to a pressed state. Toggle the LED.
        cdim.setDigitalChannelState(LED_CHANNEL, true);

        double[] hue_arr = new double[5];
        //для точности 4 измерения
        for (int j = 0; j < 4; j++) {
            // convert the RGB values to HSV values.
            telemetry.addData("Blue", sensorRGB.blue());
            telemetry.addData("Red", sensorRGB.red());
            telemetry.update();
            sleep(500);
            Color.RGBToHSV((sensorRGB.red() * 255) / 800, (sensorRGB.green() * 255) / 800, (sensorRGB.blue() * 255) / 800, hsvValues);
            double hue = hsvValues[0];
            hue_arr[j] = hue;
        }

        //Находим среднее арифметическое

        double hue_sr = 0;
        for (int j = 0; j < 4; j++) {
            hue_sr = hue_sr + hue_arr[j];
        }
        hue_sr = hue_sr / 4;
        //
        if (hue_sr > 110 && hue_sr < 290) {
            return "Blue";
        } else {
            return "Red";
        }
    }

    void kickJewel(String jewelColor) {
        telemetry.addData("AdaFruit", jewelColor);
        telemetry.update();
        if (Objects.equals(jewelColor, "Blue")) {
            set_Motors_Power_timed(-0.1, -0.1, -0.1, -0.1, 300);//поворот против часовой
            set_Motors_Power_timed(0.1, 0.1, 0.1, 0.1, 300);//поворот по часовой
        } else {
            set_Motors_Power_timed(0.1, 0.1, 0.1, 0.1, 300);//поворот по часовой
            set_Motors_Power_timed(-0.1, -0.1, -0.1, -0.1, 300);//поворот против часовой
        }
        s4_kicker.setPosition(0.1);
        cdim.setDigitalChannelState(LED_CHANNEL, false);
    }

    void moveToCryptoBox() {
        set_Motors_Power_timed(0.2, -0.2, -0.2, 0.2, 1400);//движение вперёд
        set_Motors_Power_timed(0.2, -0.2, 0.2, -0.2, 600);// Slide right
        double fieldColor;
        double fieldColorSR = odsSensor.getLightDetected();
        int tick;

        for (tick = 5; tick < 2000; tick += 1) {
            telemetry.addData("Centring loop", "iteration: " + tick);
            telemetry.addData(" ", odsSensor.getLightDetected());
            telemetry.update();
            cdim.setDigitalChannelState(LED_CHANNEL, false);
            fieldColor = odsSensor.getLightDetected();
            fieldColorSR = (fieldColorSR + fieldColor) / (tick / 5);
            set_Motors_Power(0.1, -0.1, -0.1, 0.1);
            if (tick > 4) {
                if (fieldColor - fieldColorSR > 0.1) {

                    telemetry.addData("Centring loop", "line Found 1");
                    telemetry.update();
                    set_Motors_Power_timed(0.2, -0.2, -0.2, 0.2, 200);//движение вперёд
                    sleep(200);
                    if (!isPositioned) {
                        set_Motors_Power_timed(-0.15, -0.15, -0.15, -0.15, 400);//поворот против часовой
                        isPositioned = true;
                    }
                    sleep(200);
                    int drivetime = 0;
                    while (odsSensor.getLightDetected() - fieldColorSR <= 0.1) {
                        cdim.setDigitalChannelState(LED_CHANNEL, true);
                        if (isStopRequested()) {
                            break;
                        }
                        telemetry.addData("Centring loop", "coasting");
                        telemetry.update();
                        sleep(5);
                        set_Motors_Power(0.2, -0.2, -0.2, 0.2);
                        drivetime += 5;

                    }
                    if (odsSensor.getLightDetected() - fieldColorSR > fieldColorSR) {
                        telemetry.addData("Centring loop", "line Found 2 (break)");
                        telemetry.update();
                        sleep(400);
                        cdim.setDigitalChannelState(LED_CHANNEL, false);
                        set_Motors_Power_timed(-0.2, 0.2, 0.2, -0.2, (drivetime / 2));
                        sleep(500);
                        break;
                    }
                }
            }
        }
    }

    void goToCorrectShelf(RelicRecoveryVuMark vuMark) {
        set_Motors_Power_timed(-0.2, -0.2, -0.2, -0.2, 800);//поворот против часовой
        if (vuMark == RelicRecoveryVuMark.RIGHT) {
            telemetry.addData("Vumark", " RIGHT");
            telemetry.update();
            set_Motors_Power_timed(-0.1, -0.1, 0.1, 0.1, 300);// Slide left
        } else if (vuMark == RelicRecoveryVuMark.CENTER) {
            telemetry.addData("Vumark", " CENTER");
            telemetry.update();

        } else if (vuMark == RelicRecoveryVuMark.LEFT) {
            telemetry.addData("Vumark", " LEFT");
            telemetry.update();
            set_Motors_Power_timed(0.1, 0.1, -0.1, -0.1, 300);// Slide right
        } else {
            telemetry.addData("Line", "(X)NOT VISIBLE");
            telemetry.update();

        }
        putBox();
        wasExecuted = true;
    }


    @Override
    public void runOpMode() throws InterruptedException {
        //Init and Stuff
        initialise();
        vuforiaInit();
        preRunVarUpdates();
        relicTrackables.activate();
        waitForStart();

        //Post Init Activities
        while (opModeIsActive()) {
            if (wasExecuted) {
                telemetry.addData("Autonomous: ", "DONE");
            }
            if (!wasExecuted) {
                RelicRecoveryVuMark vuMark = RelicRecoveryVuMark.from(relicTemplate);
                rotate_claw(0.8);// so that boxes won't fall off
                sleep(800);
                s4_kicker.setPosition(0.75);
                sleep(500);
                lift_claw(0.1, 250);
                telemetry.addData("Step-1", "Running");
                kickJewel(getJewelColor());
                moveToCryptoBox();
                goToCorrectShelf(vuMark);
            }
        }


    }

    void putBox() {
        set_Motors_Power_timed(-0.2, 0.2, 0.2, -0.2, 1200);//движение назад
        sleep(100);
        set_Motors_Power_timed(0.2, -0.2, -0.2, 0.2, 300);//движение вперёд
        rotate_claw(0);
        set_Motors_Power_timed(0.2, -0.2, -0.2, 0.2, 300);//движение вперёд
        set_Motors_Power_timed(-0.2, 0.2, 0.2, -0.2, 600);//движение назад
        set_Motors_Power_timed(0.1, -0.1, -0.1, 0.1, 500);//движение вперёд
        rotate_claw(0.8);
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
}
