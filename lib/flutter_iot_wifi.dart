import 'dart:async';
import 'dart:io';

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

  static Future<bool?> scan() async {
    if (Platform.isIOS) {
      print("NOT IMPLEMENTED");
      return false;
    }
    final scan = await _channel.invokeMethod('scan');
    return scan;
  }

  static Future<List<String>> list() async {
    if (Platform.isIOS) {
      print("NOT IMPLEMENTED");
      return [];
    }

    final ssids= <String>[];
    final list = await _channel.invokeMethod('list');
    if(list != null){
      for(final l in list){
        if(l != null){
          ssids.add(l as String);
        }
      }
    }
    return ssids;
  }

  static Future<String?> current() async {
    final current = await _channel.invokeMethod('current');
    print("result in flutter: $current");
    return current;
  }
}
