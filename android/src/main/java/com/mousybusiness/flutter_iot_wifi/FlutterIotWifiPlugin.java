package com.mousybusiness.flutter_iot_wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.MacAddress;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.NetworkSpecifier;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSpecifier;
import android.os.Build;
import android.os.Looper;
import android.os.PatternMatcher;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/**
 * FlutterIotWifiPlugin
 */
public class FlutterIotWifiPlugin implements FlutterPlugin, MethodCallHandler {
    private MethodChannel channel;

    private WeakReference<Context> context = null;
    WifiManager wifiManager;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        context = new WeakReference<Context>(flutterPluginBinding.getApplicationContext());
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "flutter_iot_wifi");
        channel.setMethodCallHandler(this);

        wifiManager = (WifiManager)
                context.get().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
        if (call.method.equals("connect")) {
            connect(call, result);
        } else if (call.method.equals("disconnect")) {
            disconnect(result);
        } else if (call.method.equals("scan")) {
            scan(result);
        } else if (call.method.equals("list")) {
            result.success(lastScanResults);
        } else if (call.method.equals("current")) {
            current(result);
        } else {
            result.notImplemented();
        }
    }

    private void debug(String msg) {
        Log.e("FlutterIot", msg);
    }

    private void error(String msg) {
        Log.e("FlutterIot", msg);
    }

    private WeakReference<ConnectivityManager.NetworkCallback> networkWeakReference;

    private void connect(@NonNull MethodCall call, @NonNull Result result) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            debug("connecting, Android version >= Q");
            WifiManager wifiManager = (WifiManager) context.get().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            final ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.get().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (networkWeakReference != null && networkWeakReference.get() != null) {
                debug("removing current network first");
                ConnectivityManager.NetworkCallback nwk = networkWeakReference.get();
                connectivityManager.unregisterNetworkCallback(nwk);
                networkWeakReference.clear();
                networkWeakReference = null;
            }


            Map<String, Object> argMap = (Map<String, Object>) call.arguments;// as Map<String, Any>
            String ssid = (String) argMap.get("ssid");
            String password = (String) argMap.get("password");
            Boolean prefix = (Boolean) argMap.get("prefix");


            final NetworkSpecifier specifier =
                    new WifiNetworkSpecifier.Builder()
                            .setSsidPattern(new PatternMatcher(ssid, prefix != null ? PatternMatcher.PATTERN_PREFIX : PatternMatcher.PATTERN_LITERAL))
                            .setWpa2Passphrase(password)
                            .build();
            final NetworkRequest request =
                    new NetworkRequest.Builder()
                            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                            .removeCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            .setNetworkSpecifier(specifier)
                            .build();

            final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onLosing(@NonNull Network network, int maxMsToLive) {
                    debug("onLosing");
                    super.onLosing(network, maxMsToLive);
                }

                @Override
                public void onLost(@NonNull Network network) {
                    debug("onLost");
                    super.onLost(network);
                    connectivityManager.unregisterNetworkCallback(this);
                    networkWeakReference.clear();
                    networkWeakReference = null;
                }

                @Override
                public void onUnavailable() {
                    debug("onUnavailable");
                    super.onUnavailable();
                    connectivityManager.unregisterNetworkCallback(this);
                    networkWeakReference.clear();
                    networkWeakReference = null;
                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    debug("onCapabilitiesChanged");
                    super.onCapabilitiesChanged(network, networkCapabilities);
                }

                @Override
                public void onLinkPropertiesChanged(@NonNull Network network, @NonNull LinkProperties linkProperties) {
                    debug("onLinkPropertiesChanged");
                    super.onLinkPropertiesChanged(network, linkProperties);
                }

                @Override
                public void onBlockedStatusChanged(@NonNull Network network, boolean blocked) {
                    debug("onBlockedStatusChanged");
                    super.onBlockedStatusChanged(network, blocked);
                }

                @Override
                public void onAvailable(@NonNull Network network) {
                    debug("onAvailable");
                    super.onAvailable(network);

                    boolean bind= connectivityManager.bindProcessToNetwork(network);
debug("Bind result: "+bind);
//                    success = manager.bindProcessToNetwork(null); to clear
                }
            };
            networkWeakReference = new WeakReference<>(networkCallback);
            connectivityManager.requestNetwork(request, networkCallback);

            result.success(true);
        } else {
            error("unsupported Android version, please use >= Q");
            result.success(false);
        }
    }

    private void disconnect(@NonNull Result result) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {

            if (networkWeakReference != null && networkWeakReference.get() != null) {
                debug("disconnecting");
                final ConnectivityManager connectivityManager =
                        (ConnectivityManager) context.get().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                ConnectivityManager.NetworkCallback nwk = networkWeakReference.get();
                connectivityManager.unregisterNetworkCallback(nwk);
                networkWeakReference.clear();
                networkWeakReference = null;
                result.success(true);

            } else {
                error("not connected");
                result.success(false);
            }

        } else {
            error("unsupported Android version, please use >= Q");
            result.success(false);
        }
    }

    private void scan(@NonNull Result result) {
        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }

                context.get().getApplicationContext().unregisterReceiver(this);

            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        context.get().getApplicationContext().registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            result.success(false);
        } else {
            result.success(true);
        }
    }

    ArrayList<String> lastScanResults = new ArrayList<>();

    private void scanSuccess() {
        debug("scan successful");
        List<ScanResult> results = wifiManager.getScanResults();
        lastScanResults.clear();
        for (ScanResult res : results) {
            if (!lastScanResults.contains(res.SSID) && !res.SSID.isEmpty()) {
                lastScanResults.add(res.SSID);
            }
        }
    }

    private void scanFailure() {
        debug("scan failure");
    }

    private void current(@NonNull Result result) {
        WifiInfo info = wifiManager.getConnectionInfo();
        String ssid = info.getSSID();
        result.success(ssid.replace("\"", ""));
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }
}
