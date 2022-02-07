import Flutter
import UIKit
import NetworkExtension
import SystemConfiguration.CaptiveNetwork

public class SwiftFlutterIotWifiPlugin: NSObject, FlutterPlugin {
    var ssidCache: String?

    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "flutter_iot_wifi", binaryMessenger: registrar.messenger())
        let instance = SwiftFlutterIotWifiPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }

    private func debug(_ msg: String) {
        NSLog("[FlutterIotWifi] \(msg)")
    }

    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if #available(iOS 11.0, *) {
        } else {
            debug("unsupported iOS version, min 11.0")
            result(false)
            return
        }

        switch call.method {
        case "connect":
            debug("connect")
            ssidCache = ""

            if let args = call.arguments as? Dictionary<String, Any> {
                let ssid = args["ssid"] as? String
                let password = args["password"] as? String
                if ssid == nil || password == nil || ssid == "" || password == "" {
                    debug("invalid ssid or password")
                } else {
                    connect(ssid!, password!)
                    result(true)
                    return
                }
            } else {
                debug("ssid and password required")
            }
            break
        case "disconnect":
            if ssidCache != nil && ssidCache != "" {
                disconnect(ssidCache!)
                result(true)
                return
            } else {
                debug("no ssid cache to disconnect")
            }
            break
        case "current":
            let ssid = current()
            result(ssid)
            return
        default:
            debug("\(call.method) not implemented")

        }
        result(false)
    }

    private func connect(_ ssid: String, _ password: String) {
        if #available(iOS 11.0, *) {
            let hotspotConfig = NEHotspotConfiguration(ssid: ssid, passphrase: password, isWEP: false)
            hotspotConfig.joinOnce = true
            NEHotspotConfigurationManager.shared.apply(hotspotConfig) { [unowned self] (error) in
                if let error = error {
                    if (error.localizedDescription.contains("already associated")) {
                        debug("connection already established")
                    } else {
                        debug("[Error] \(error)")
                    }
                } else {
                    debug("connected!")
                    ssidCache = ssid
                }
            }
        }
    }

    private func disconnect(_ ssid: String) {
        if #available(iOS 11.0, *) {
            NEHotspotConfigurationManager.shared.removeConfiguration(forSSID: ssid)
        }
    }

      public func current() -> String? {
        if #available(iOS 11.0, *) {
          var ssid: String?
          if let interfaces = CNCopySupportedInterfaces() as NSArray? {
            for interface in interfaces {
              if let interfaceInfo = CNCopyCurrentNetworkInfo(interface as! CFString) as NSDictionary? {
                ssid = interfaceInfo[kCNNetworkInfoKeySSID as String] as? String
                break
              }
            }
          }
          return ssid
        }

        return nil
      }
}
