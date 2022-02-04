
import 'dart:async';

import 'package:flutter/services.dart';

class FlutterIotWifi {
  static const MethodChannel _channel = MethodChannel('flutter_iot_wifi');

  static Future<bool?> connect(String ssid, String password) async {
    final connected = await _channel.invokeMethod('connect', {"ssid": ssid, "password": password});
    return connected;
  }

  static Future<bool?> disconnect() async {
    final disconnected = await _channel.invokeMethod('disconnect');
    return disconnected;
  }
}
