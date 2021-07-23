package com.nightmare.shortcut;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.PluginRegistry;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

public class ShortcutPlugin implements FlutterPlugin, MethodCallHandler {
    private MethodChannel channel;
    Context mContext;
    private FlutterPluginBinding flutterPluginBinding;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "shortcut");
        channel.setMethodCallHandler(this);
        mContext = flutterPluginBinding.getApplicationContext();
        this.flutterPluginBinding = flutterPluginBinding;
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

        if (call.method.equals("create")) {
            Intent shortcutInfoIntent = new Intent();
            Map<String, String> map = (Map<String, String>) call.arguments;
            if (map.containsKey("packageName") && map.containsKey("activityName")) {
                shortcutInfoIntent.setClassName(map.get("packageName"), map.get("activityName"));
            }
            for (String key : map.keySet()) {
                shortcutInfoIntent.putExtra(key, map.get(key));
            }
            shortcutInfoIntent.setAction(Intent.ACTION_MAIN);
            String key = flutterPluginBinding.getFlutterAssets().getAssetFilePathByName(map.get("asset"));
            addShortcut((String) call.argument("name"), shortcutInfoIntent, getImageFromAssetsFile(key));
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else {
            result.notImplemented();
        }
    }
    private Bitmap getImageFromAssetsFile(String fileName) {

        fileName = fileName.replaceAll("flutter_assets/", "");

        Bitmap image = null;

        Bitmap bitmap = BitmapFactory.decodeFile(fileName);

        System.out.println("AAAAAA");
        System.out.println(bitmap);
        System.out.println(fileName);
        System.out.println(fileName.replaceAll("flutter_assets/", ""));
        System.out.println("BBBBBB");

        if(fileName.contains("file:///")) {
            image = bitmap;
        } else {

            AssetManager am =  flutterPluginBinding.getApplicationContext().getAssets();

            try {
                InputStream is = am.open(fileName);
                image = BitmapFactory.decodeStream(is);
                is.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
        
        return image;
    }
    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    public void addShortcut(String name, Intent actionIntent, Bitmap bitmap) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
            shortcut.putExtra("duplicate", false);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
            mContext.sendBroadcast(shortcut);
        } else {
            ShortcutManager shortcutManager = (ShortcutManager) mContext.getSystemService(Context.SHORTCUT_SERVICE);
            if (null == shortcutManager) {
                Log.e("MainActivity", "Create shortcut failed");
                return;
            }
            ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(mContext, name)
                .setShortLabel(name)
                .setIntent(actionIntent)
                .setLongLabel(name)
                .setIcon(Icon.createWithBitmap(bitmap))
                .build();

            PendingIntent shortcutCallbackIntent = PendingIntent.getBroadcast(
                mContext, 
                0, 
                actionIntent, 
                PendingIntent.FLAG_UPDATE_CURRENT
            );

            shortcutManager.requestPinShortcut(
                shortcutInfo, 
                shortcutCallbackIntent.getIntentSender()
            );
        }
    }
}
