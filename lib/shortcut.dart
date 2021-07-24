import 'dart:async';

import 'package:flutter/services.dart';

class Shortcut {
  static const MethodChannel _channel = const MethodChannel('shortcut');

  static Future<void> addShortcut({
    String? packageName,
    String? activityName,
    String? file,
    required String assetName,
    required String name,
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

    if (file != null) {
      map['file'] = file;
    }

    for (String key in intentExtra.keys) {
      map[key] = intentExtra[key];
    }

    await _channel.invokeMethod('create', map);
    
  }

  static Future<void> searchShortcut({
    required String name,
  }) async {

    await _channel.invokeMethod('search', name);

  }
}
