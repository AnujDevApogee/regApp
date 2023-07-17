package com.example.tcp_ble.utility;

import java.util.HashMap;

/**
* Created by admin on 11/19/2018.
*/

public class SampleGattAttributes {
      private static HashMap<String, String> attributes = new HashMap();
      public static String HEART_RATE_MEASUREMENT = "00001101-0000-1000-8000-00805F9B34FB";
      public static String CLIENT_CHARACTERISTIC_CONFIG = "00002902-0000-1000-8000-00805f9b34fb";
  public static String Write_UUI = "495343-1e4d-4bd9-ba61-23c647249616";
      static {
          // Sample Services.
          attributes.put("0000180d-0000-1000-8000-00805f9b34fb", "Heart Rate Service");
          attributes.put("0000180a-0000-1000-8000-00805f9b34fb", "Device Information Service");
          // Sample Characteristics.
          attributes.put(HEART_RATE_MEASUREMENT, "Heart Rate Measurement");
          attributes.put("00002a29-0000-1000-8000-00805f9b34fb", "Manufacturer Name String");
      }
      public static String lookup(String uuid, String defaultName) {
          String name = attributes.get(uuid);
          return name == null ? defaultName : name;
      }
}
