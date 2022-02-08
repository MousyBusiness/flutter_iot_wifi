# flutter_iot_wifi

Connect to an IoT access point using Android or iOS

## Android Support

Currently requires Version >= Android Q

## Android Permissions

```
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" /> !!! Android < 29 Required to scan !!!
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" /> !!! Android >= 29 Required to scan !!!
```

> NOTE: Explicit requesting of permissions (e.g. with permission_handler) is required with newer versions.

## iOS Support

Minimum iOS >= 11.0, iOS >= 13.0 required for prefix connect

## iOS Entitlements

```
<key>com.apple.developer.networking.HotspotConfiguration</key>
<true/>
<key>com.apple.developer.networking.wifi-info</key>
<true/>
```

## iOS Info.plist

```
<key>NSLocationWhenInUseUsageDescription</key>
<string>XXXXXX</string>
```
