# flutter_iot_wifi

Connect to an IoT access point using Android or iOS

## Android Permissions

```
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
```

## iOS Entitlements

```
<key>com.apple.developer.networking.HotspotConfiguration</key>
<true/>
<key>com.apple.developer.networking.wifi-info</key>
<true/>
```

