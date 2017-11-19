package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;

/**
 * Created by dimich on 18.03.2017.
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
    static float level(ColorSensor sensorRGB) {
        return sensorRGB.red() + sensorRGB.blue() + sensorRGB.green();
    }
}
