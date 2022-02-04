import 'package:flutter/material.dart';
import 'package:flutter_iot_wifi/flutter_iot_wifi.dart';

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

  final String ssid = "Example";
  final String password = "12345678";

  void _connect() {
    FlutterIotWifi.connect(ssid, password).then((value) => print("connected $value"));
  }

  void _disconnect() {
    FlutterIotWifi.disconnect().then((value) => print("disconnected $value"));
  }

  @override
  Widget build(BuildContext context) {
    return  Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          Center(child: Text("SSID: $ssid, PASSWORD: $password")),
          _CustomButton(onPressed: _connect, child: const Text("Connect")),
          _CustomButton(onPressed: _disconnect, child: const Text("Disconnect")),
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

