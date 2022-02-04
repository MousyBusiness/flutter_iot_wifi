//    @RequiresApi(Build.VERSION_CODES.M)
private fun connectToWifi(call: MethodCall, result: Result) {
        val argMap = call.arguments as Map<String, Any>
        val ssid = argMap["ssid"] as String
        val password = argMap["password"] as String?

        // 비밀번호가 있냐 없냐에 따라 wifi configration을 설정한다.
        val wifiConfiguration =
                if (password == null) {
                        WifiConfiguration().apply {
                                SSID = ssid.wrapWithDoubleQuotes()
                                status = WifiConfiguration.Status.CURRENT
                                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                        }
                } else {
                        WifiConfiguration().apply {
                                SSID = ssid.wrapWithDoubleQuotes()
                                preSharedKey = password.wrapWithDoubleQuotes()
                                status = WifiConfiguration.Status.CURRENT
                                allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                        }
                }

        val wifiManager = activityContext?.applicationContext?.getSystemService(Context.WIFI_SERVICE) as WifiManager

        with(wifiManager) {
                if (!isWifiEnabled) {
                        isWifiEnabled = true
                }

                // 위에서 생성한 configration을 추가하고 해당 네트워크와 연결한다.
                var networkId = addNetwork(wifiConfiguration)
                if (networkId == null) {
                        result.success(false)
                        return;
                }
                disconnect()
                enableNetwork(networkId, true)
                reconnect()

//            var canWriteFlag = false
//
//            if (true) {
//                val manager = activityContext?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//                val builder: NetworkRequest.Builder = NetworkRequest.Builder()
//                //set the transport type do WIFI
//                builder.addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
//                manager.requestNetwork(builder.build(), object : NetworkCallback() {
//                    override fun onAvailable(network: Network) {
//                        manager.bindProcessToNetwork(network)
//                        try {
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
//                        manager.unregisterNetworkCallback(this)
//                    }
//                })
//            }

                result.success(true)
        }

        //    else {
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//        activityContext?.applicationContext?.getSystemService(Context.CONNECTIVITY_SERVICE).bindProcessToNetwork(null)
//      } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//        ConnectivityManager.setProcessDefaultNetwork(null)
//      }
//    }

}