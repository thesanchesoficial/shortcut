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

/**
 * ShortcutPlugin
 */
public class ShortcutPlugin implements FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
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
            addShortcut((String) call.argument("name"), shortcutInfoIntent,getImageFromAssetsFile(key));
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else {
            result.notImplemented();
        }
    }
    private Bitmap getImageFromAssetsFile(String fileName)
    {
        Bitmap image = null;

        AssetManager am =  flutterPluginBinding.getApplicationContext().getAssets();

        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    /**
     * 添加桌面图标快捷方式
     *
     * @param name         快捷方式名称
     * @param actionIntent 快捷方式图标点击动作
     */
    public void addShortcut(String name, Intent actionIntent,Bitmap bitmap) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            //  创建快捷方式的intent广播
            Intent shortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
            // 添加快捷名称
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
            //  快捷图标是允许重复(不一定有效)
            shortcut.putExtra("duplicate", false);
            // 快捷图标
            // 使用资源id方式
//            Intent.ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(activity, R.mipmap.icon);
//            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);
            // 使用Bitmap对象模式
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, bitmap);
            // 添加携带的下次启动要用的Intent信息
            shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, actionIntent);
            // 发送广播
            mContext.sendBroadcast(shortcut);
        } else {
            ShortcutManager shortcutManager = (ShortcutManager) mContext.getSystemService(Context.SHORTCUT_SERVICE);
            if (null == shortcutManager) {
                // 创建快捷方式失败
                Log.e("MainActivity", "Create shortcut failed");
                return;
            }
            ShortcutInfo shortcutInfo = new ShortcutInfo.Builder(mContext, name)
                    .setShortLabel(name)
                    .setIntent(actionIntent)
                    .setLongLabel(name)
                    .setIcon(Icon.createWithBitmap(bitmap))
                    .build();

            PendingIntent shortcutCallbackIntent = PendingIntent.getBroadcast(mContext, 0, actionIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            shortcutManager.requestPinShortcut(shortcutInfo, shortcutCallbackIntent.getIntentSender());
        }
    }
}
