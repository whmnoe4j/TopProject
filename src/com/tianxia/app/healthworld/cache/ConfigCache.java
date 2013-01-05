package com.tianxia.app.healthworld.cache;

import java.io.File;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

import com.tianxia.app.healthworld.AppApplication;
import com.tianxia.lib.baseworld.BaseApplication;
import com.tianxia.lib.baseworld.utils.FileUtils;
import com.tianxia.lib.baseworld.utils.NetworkUtils;
import com.tianxia.lib.baseworld.utils.StringUtils;

public class ConfigCache {
    private static final String TAG = ConfigCache.class.getName();

    public static final int CONFIG_CACHE_MOBILE_TIMEOUT  = 3600000;  //1 hour
    public static final int CONFIG_CACHE_WIFI_TIMEOUT    = 3600000;   //5 minute

    public static String getUrlCache(String url) {
        if (url == null) {
            return null;
        }
        String result = null;
        if(BaseApplication.mNetWorkState!=NetworkUtils.NETWORN_NONE){
        File file = new File(AppApplication.mSdcardDataDir + "/" + StringUtils.replaceUrlWithPlus(url));
        if (file.exists() && file.isFile()) {
            long expiredTime = System.currentTimeMillis() - file.lastModified();
            Log.d(TAG, file.getAbsolutePath() + " expiredTime:" + expiredTime/60000 + "min");
            //1. in case the system time is incorrect (the time is turn back long ago)
            //2. when the network is invalid, you can only read the cache
            if (AppApplication.mNetWorkState != NetworkUtils.NETWORN_NONE && expiredTime < 0) {
                return null;
            }
            if(AppApplication.mNetWorkState == NetworkUtils.NETWORN_WIFI && expiredTime > CONFIG_CACHE_WIFI_TIMEOUT) {
                return null;
            } else if (AppApplication.mNetWorkState == NetworkUtils.NETWORN_MOBILE && expiredTime > CONFIG_CACHE_MOBILE_TIMEOUT) {
                return null;
            }
            try {
                result = FileUtils.readTextFile(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        }else{
        	try {
        		File file = new File(AppApplication.mSdcardDataDir + "/" + StringUtils.replaceUrlWithPlus(url));
        		if (file.exists() && file.isFile()) {
        			result = FileUtils.readTextFile(file);
        		}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void setUrlCache(String data, String url) {
        if (AppApplication.mSdcardDataDir == null) {
            return;
        }
        File dir = new File(AppApplication.mSdcardDataDir); 
        if (!dir.exists() && Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            dir.mkdirs();
        }
        File file = new File(AppApplication.mSdcardDataDir + "/" + StringUtils.replaceUrlWithPlus(url));
        try {
            //创建缓存数据到磁盘，就是创建文件
            FileUtils.writeTextFile(file, data);
        } catch (IOException e) {
            Log.d(TAG, "write " + file.getAbsolutePath() + " data failed!");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
