# shortcut

A flutter plugin can create desktop shortcut not actions,currently just support android.

## About shortcut

because i didn't find a plugin can add desktop shortcut on android,so i create it by myself.

## Installing

1. Add shortcut to your pubspec.yaml file:

```yaml
dependencies:
  shortcut:
```

2. Import shortcut in files that it will be used:

```dart
import 'package:shortcut/shortcut.dart';
```

## Add a shortcut to desktop

```dart
Shortcut.addShortcut(
    assetName: 'assets/image/xxx',
    name: 'Hello',
    packageName: 'com.nightmare',
    activityName: 'com.nightmare.MainActivity',
    intentExtra: {
    'route': '/rom',
    },
);
```
now i didn't create single class for options or anothor object,maybe in the future.