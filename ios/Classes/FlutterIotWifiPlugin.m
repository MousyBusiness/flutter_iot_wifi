#import "FlutterIotWifiPlugin.h"
#if __has_include(<flutter_iot_wifi/flutter_iot_wifi-Swift.h>)
#import <flutter_iot_wifi/flutter_iot_wifi-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "flutter_iot_wifi-Swift.h"
#endif

@implementation FlutterIotWifiPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftFlutterIotWifiPlugin registerWithRegistrar:registrar];
}
@end
