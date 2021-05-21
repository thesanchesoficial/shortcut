#import "ShortcutPlugin.h"
#if __has_include(<shortcut/shortcut-Swift.h>)
#import <shortcut/shortcut-Swift.h>
#else
// Support project import fallback if the generated compatibility header
// is not copied when this plugin is created as a library.
// https://forums.swift.org/t/swift-static-libraries-dont-copy-generated-objective-c-header/19816
#import "shortcut-Swift.h"
#endif

@implementation ShortcutPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftShortcutPlugin registerWithRegistrar:registrar];
}
@end
