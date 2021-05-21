import 'dart:async';

import 'package:flutter/services.dart';

class Shortcut {
  static const MethodChannel _channel = const MethodChannel('shortcut');

  static Future<void> addShortcut({
    String? packageName,
    String? activityName,
    required String assetName,
    required String name,

    /// 这个最后会调用`intent.putExtra(key , value);`
    Map<String, dynamic> intentExtra = const {},
  }) async {
    Map<String, dynamic> map = {
      'asset': assetName,
      'name': name,
    };
    if (packageName != null) {
      map['packageName'] = packageName;
    }
    if (activityName != null) {
      map['activityName'] = activityName;
    }
    for (String key in intentExtra.keys) {
      map[key] = intentExtra[key];
    }
    String result = await _channel.invokeMethod('create', map);
    print(result);
  }
}
