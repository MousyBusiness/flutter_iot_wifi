import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter_iot_wifi/flutter_iot_wifi.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('FlutterIotWifi Example'),
        ),
        body: const Center(
          child: AccessPointWidget(),
        ),
      ),
    );
  }
}

class AccessPointWidget extends StatelessWidget {
  const AccessPointWidget({Key? key}) : super(key: key);

  final String ssid = "Example"; // TODO replace with your ssid
  final String password = "12345678"; // TODO replace with your password

  Future<bool> _checkPermissions() async {
    if (Platform.isIOS || await Permission.location.request().isGranted) {
      return true;
    }
    return false;
  }

  void _connect() async {
    if (await _checkPermissions()) {
      FlutterIotWifi.connect(ssid, password, prefix: true).then((value) => print("connect initiated: $value"));
    } else {
      print("don't have permission");
    }
  }

  void _disconnect() async {
    if (await _checkPermissions()) {
      FlutterIotWifi.disconnect().then((value) => print("disconnect initiated: $value"));
    } else {
      print("don't have permission");
    }
  }

  void _scan() async {
    if (await _checkPermissions()) {
      FlutterIotWifi.scan().then((value) => print("scan started: $value"));
    } else {
      print("don't have permission");
    }
  }

  void _list() async {
    if (await _checkPermissions()) {
      FlutterIotWifi.list().then((value) => print("ssids: $value"));
    } else {
      print("don't have permission");
    }
  }

  void _current() async {
    if (await _checkPermissions()) {
      FlutterIotWifi.current().then((value) => print("current ssid: $value"));
    } else {
      print("don't have permission");
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      mainAxisAlignment: MainAxisAlignment.spaceEvenly,
      children: [
        Center(child: Text("SSID: $ssid, PASSWORD: $password")),
        _CustomButton(onPressed: _connect, child: const Text("Connect")),
        _CustomButton(onPressed: _disconnect, child: const Text("Disconnect")),
        _CustomButton(onPressed: _scan, child: const Text("Scan (Android only)")),
        _CustomButton(onPressed: _list, child: const Text("List (Android only)")),
        _CustomButton(onPressed: _current, child: const Text("Current")),
      ],
    );
  }
}

class _CustomButton extends StatelessWidget {
  const _CustomButton({Key? key, required this.onPressed, required this.child}) : super(key: key);

  final VoidCallback onPressed;
  final Widget child;

  @override
  Widget build(BuildContext context) {
    return SizedBox(height: 40, child: ElevatedButton(onPressed: onPressed, child: child));
  }
}
