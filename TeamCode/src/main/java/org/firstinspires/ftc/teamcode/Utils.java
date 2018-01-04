package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import java.util.Objects;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
/**
 * Created by vhundef on 01.01.2018.
 */
public class Utils {
    static float[] tmpHsv= {0F, 0F, 0F};

    static float hue(ColorSensor sensorRGB) {
        Color.RGBToHSV((sensorRGB.red() * 255) / 800, (sensorRGB.green() * 255) / 800, (sensorRGB.blue() * 255) / 800,
                tmpHsv);
        return tmpHsv[0];
    }

    /**
     * returns from 0 to 2400
     * @param sensorRGB
     * @return
     */
   }
