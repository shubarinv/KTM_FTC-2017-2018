package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DeviceInterfaceModule;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import org.firstinspires.ftc.robotcore.external.ClassFactory;
import java.util.Objects;

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
    static String get_color(){
      /* AdaFruit */
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
      boolean bLedOn = true;


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

      // check the status of the x button on gamepad.
      bCurrState = true;

      // check for button-press state transitions.
      if (bCurrState != bPrevState) {

        // button is transitioning to a pressed state. Toggle the LED.
        cdim.setDigitalChannelState(LED_CHANNEL, bLedOn);
      }

      // update previous state variable.
      bPrevState = bCurrState;


      double[] hue_arr= new double[5];;
      double[] blue= new double[5];;
      double[] red= new double[5];;
      //для точности 4 измерения
      for(int j = 0;j<4;j++){
        // convert the RGB values to HSV values.
        telemetry.addData("Blue", sensorRGB.blue());
        telemetry.addData("Red", sensorRGB.red());
        telemetry.update();
        sleep(500);
        Color.RGBToHSV((sensorRGB.red() * 255) / 800, (sensorRGB.green() * 255) / 800, (sensorRGB.blue() * 255) / 800, hsvValues);
        red[j]=sensorRGB.red() * 255 / 800;
        blue[j]=sensorRGB.blue() * 255 / 800;
        double hue = hsvValues[0];
        hue_arr[j]=hue;
      }
      //Находим среднее арифметическое
      double red_sr = 0;
      double blue_sr = 0;
      double hue_sr = 0;
      for(int j = 0;j<4;j++){
        red_sr=red_sr+red[j];
        blue_sr=blue_sr+blue[j];
        hue_sr=hue_sr+hue_arr[j];
      }
      red_sr=red_sr/4;
      blue_sr=blue_sr/4;
      hue_sr=hue_sr/4;
      //
      if (hue_sr > 110 && hue_sr < 290) {
        return "Blue";
      } else if (hue_sr < 110 || hue_sr > 290 && hue_sr < 360) {
        return "Red";
      }
      else if(blue_sr>red_sr){
        return "Blue";
      }
      else{
        return "Red";
      }
    }

}
