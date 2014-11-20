package net.muji.passport.android.app.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

/**
 * Created by teamlab on 2014/11/20.
 */
public class MarketUtil {
    /**
     * 判断手机中是否安装了appmarket
     *
     * @param context
     * @return true 有，else false
     */
    public static boolean hasMarketApp(Context context) {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_MARKET);

        PackageManager packageManager = context.getPackageManager();

        List<ResolveInfo> apps = packageManager.queryIntentActivities(intent, 0);

        return apps.size() > 0;
    }

    /**
     * 打开app市场
     *
     * @param context
     */
    public static void openAppMarket(Context context) {

        //跳到appstore上去
        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.setData(Uri.parse("market://details?id=com.baidu.BaiduMap"));
        intent.setData(Uri.parse("market://details?id=" + context.getPackageName()));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }
}
