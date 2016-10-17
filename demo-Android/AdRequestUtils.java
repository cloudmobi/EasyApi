package com.cloudmobi.easyapi;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.LinkedBlockingQueue;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.content.Context.MODE_PRIVATE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/* 
    调用方法说明:
    四个参数分别为:广告位id,广告图片宽度,广告图片高度,每次请求获取的广告数量

    AdRequestUtils adRequestUtils = AdRequestUtils.getInstance(10,500,500,2);

    adRequestUtils.getAdsInfo(context, new AdRequestUtils.AdsListerner() {
        @Override
        public void adsGetSucess(List<AdsVo> adsVoList) {
            Log.i("AdRequestUtils", "===广告信息===" + adsVoList.size());
        }
    
        @Override
        public void adsGetFailed(String errorMsg) {
            Log.i("AdRequestUtils", "===错误信息==" + errorMsg);
        }
    });
*/
public class AdRequestUtils {

    public String urlHost = "http://api.cloudmobi.net:30001/api/v1/realtime/m/get";
    public Context context;
    private static String token;
    private static String imgw;
    private static String imgh;
    private static String adsNum;
    private static String gaid = " ";
    private String sdkVersion = "1.0";
    private boolean isDebug = false;

    public interface AdsListerner{
        void adsGetSucess(List<AdsVo> adsVoList);
        void adsGetFailed(String errorMsg);
    }

    private AdRequestUtils(){}
    private static AdRequestUtils adRequestUtils = null;

    /**
     *  获取广告的工具对象
     * @param slotId    广告位id
     * @param imgwidth   广告图片的宽
     * @param imgheigh   广告图片的高
     * @param adsNumer   一次请求的广告数量
     * @return
     */
    public static AdRequestUtils getInstance(int slotId,int imgwidth,int imgheigh,int adsNumer){
        token = String.valueOf(slotId);
        imgw = String.valueOf(imgwidth);
        imgh = String.valueOf(imgheigh);
        adsNum = String.valueOf(adsNumer);

        if (adRequestUtils == null){
            adRequestUtils = new AdRequestUtils();
        }
        return adRequestUtils;
    }

    public void getAdsInfo(final Context context, final AdsListerner adsListerner){
        this.context = context;

        GaIdUtils.getGaId(context, new GaIdUtils.GaIdListener() {
            @Override
            public void getGaIdSuccess(String gaId) {
                gaid = gaId;
                getAdsInfoInternal(context,adsListerner);
            }

            @Override
            public void getGaIdFailed(String errorMsg) {
                getAdsInfoInternal(context,adsListerner);
            }
        });

    }

    private void getAdsInfoInternal(Context context,AdsListerner adsListerner){

        //获取参数,拼接url
        StringBuilder stringBuilder = new StringBuilder(urlHost);
        Map<String, String> params = getParams(context);
        ApiUtils.appendUrlParameter(stringBuilder,params,true);
        String url = stringBuilder.toString();
        Log.i("AdRequestUtils","finalurl::" + url);

        new AdsThread(url,adsListerner).start();

    }

    private Map<String, String> getParams(Context context) {
        Map<String, String> params = new HashMap();

        params.put("token",token);
        params.put("os","Android");
        params.put("osv", String.valueOf(Build.VERSION.RELEASE));
        params.put("dt", "phone");
        params.put("nt", String.valueOf(ApiUtils.getNetworkType(context)));
        params.put("imgw", imgw);
        params.put("imgh", imgh);
        params.put("pn", ApiUtils.getAppPackageName(context));
        params.put("sv", sdkVersion);
        params.put("adnum",adsNum);
        params.put("gaid", gaid);
        params.put("aid", ApiUtils.getAndroidId(context));
        params.put("imei", ApiUtils.getIMEI(context));
        params.put("icc", ApiUtils.getNetworkCountryIso(context));
        params.put("gp", ApiUtils.isGooglePlayInstalled(context));
        params.put("dpd", Build.PRODUCT);
        params.put("cn", ApiUtils.getNetworkOperatorName(context));
        params.put("tz", ApiUtils.getTimeZone());
        params.put("lang", Locale.getDefault().getLanguage());
        params.put("isdebug", isDebug ? "1" : "0");

        return params;
    }
}


class AdsThread extends Thread{

    private AdRequestUtils.AdsListerner adsListerner;
    private String url;

    public AdsThread(String url,AdRequestUtils.AdsListerner adsListerner){
        this.url = url;
        this.adsListerner = adsListerner;
    }

    @Override
    public void run() {
        try {
            URL finalUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) finalUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            int code = conn.getResponseCode();

            if (code == 200){
                InputStream is = conn.getInputStream();
                String jsonStr = ApiUtils.stream2String(is);
                Log.i("AdRequestUtils","JsonString::" + jsonStr);
                getAdsVo(jsonStr);
            }else{
                adsListerner.adsGetFailed("Error001::Intent error");
            }
            conn.disconnect();
        } catch (Exception e){
            e.printStackTrace();
            adsListerner.adsGetFailed("Error001::Intent error" + e.getMessage());
        }
    }

    private void getAdsVo(String jsonStr){
        List<AdsVo> adsList = new ArrayList<>();

        if (jsonStr == null){
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(jsonStr);

            String errMsg = jsonObject.getString("err_msg");
            int errCode = jsonObject.getInt("err_no");
            if (errCode == 0){  //获取广告成功
                JSONArray jsonArray = jsonObject.getJSONArray("ad_list");

                for (int i = 0; i < jsonArray.length();i++){
                    JSONObject adsObject = (JSONObject) jsonArray.get(i);
                    AdsVo adsVo = parseAdsFromJson(adsObject);
                    if (adsVo != null){
                        adsList.add(adsVo);
                    }
                }
                adsListerner.adsGetSucess(adsList); //TODO 获取广告成功
            }else{
                adsListerner.adsGetFailed("Error002::Ads request error" + errMsg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            adsListerner.adsGetFailed("Error003::AdsJson parse error" + e.getMessage());
        }
    }

    private AdsVo parseAdsFromJson(JSONObject adsObj) throws JSONException{
        AdsVo adsVo = new AdsVo();
        if (adsObj == null){
            return null;
        }

        adsVo.icon = adsObj.getString("icon");
        adsVo.iconSize = adsObj.getString("icon_size");
        adsVo.image = adsObj.getString("image");
        adsVo.imageSize = adsObj.getString("image_size");
        adsVo.title = adsObj.getString("title");
        adsVo.desc = adsObj.getString("desc");
        adsVo.pkgName = adsObj.getString("pkg_name");
        adsVo.clkUrl = adsObj.getString("clk_url");
        adsVo.landingType = adsObj.getInt("landing_type");
        adsVo.btnDes = adsObj.getString("button");

        adsVo.impTksList = parseStringArrayHelper(adsObj,"imp_tks");
        adsVo.clkTksList = parseStringArrayHelper(adsObj,"clk_tks");

        return adsVo;
    }

    private List<String> parseStringArrayHelper(JSONObject jsonObject,String str) throws JSONException{
        if (jsonObject == null || str == null){
            return Collections.EMPTY_LIST;
        }

        List<String> dataList = new ArrayList<>();

        JSONArray jsonArray = jsonObject.getJSONArray(str);
        if (jsonArray != null){
            for (int i = 0; i < jsonArray.length(); i++){
                dataList.add(jsonArray.getString(i));
            }
        }

        return dataList;
    }
}


class AdsVo{
    public String icon;
    public String iconSize;
    public String image;
    public String imageSize;
    public String title;
    public String pkgName;
    public String desc;
    public int landingType;
    public String clkUrl;
    public String btnDes;
    public List<String> impTksList;
    public List<String> clkTksList;
}


class ApiUtils {
    private static String GOOGLE_PLAY_PKG_NAME = "com.android.vending";
    /**
     * 获取网络类型
     * <uses-permission android:name="android.permission.INTERNET"/>
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
     */
    public static int getNetworkType(Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        int networkType = ConnectivityManager.TYPE_DUMMY;
        if (context.checkCallingOrSelfPermission(ACCESS_NETWORK_STATE) == PERMISSION_GRANTED) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            networkType = activeNetworkInfo != null ? activeNetworkInfo.getType() : ConnectivityManager.TYPE_DUMMY;
        }
        return networkType;
    }

    /**
     * 获取应用包名
     */
    public static String getAppPackageName(final Context context) {
        String pn = "";
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            pn = info.packageName;
        } catch (Exception e) {
            return pn;
        }
        return pn;
    }


    /**
     * 拼接url
     *
     * @param stringBuilder
     * @param params
     * @param isFirstParams
     */
    public static void appendUrlParameter(StringBuilder stringBuilder, Map<String, String> params, boolean isFirstParams) {
        Set<String> keys = params.keySet();
        for (String key : keys) {
            String value = params.get(key);
            if (isNullOrEmpty(key) || isNullOrEmpty(value) || value.equals("null")) {
                continue;
            }

            if (isFirstParams) {
                isFirstParams = false;
                stringBuilder.append("?");
            } else {
                stringBuilder.append("&");
            }
            stringBuilder.append(urlEncodeUTF8(key));
            stringBuilder.append("=");
            stringBuilder.append(urlEncodeUTF8(value));
        }
    }

    public static String urlEncodeUTF8(String s) {
        try {
            return URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * @param is 接收输入流
     * @throws IOException 抛出的异常类型
     * @return 返回此流转换成的字符串
     */
    public static String stream2String(InputStream is) throws IOException {

        //读取到的数据存储到一个缓存空间然后一次性写出作为一个字符串返回
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int temp = -1;

        while ((temp = is.read(buffer)) != -1) {
            //还有数据
            bos.write(buffer, 0, temp);
        }
        String result = bos.toString();
        bos.close();
        return result;
    }

    /**
     * 获取Android Id
     */
    public static String getAndroidId(Context context) {
        String androidId = "";
        try {
            androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return androidId;
    }

    /**
     * @return 获取手机IMEI�?
     * READ_PHONE_STATE
     */
    public static String getIMEI(final Context context) {
        String imei = "";
        try {
            TelephonyManager telephonyManager =
                    (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                imei = telephonyManager.getDeviceId();
                if (TextUtils.isEmpty(imei)) {
                    imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imei;
    }

    /**
     * 获取icc
     * @param context
     * @return
     */
    public static String getNetworkCountryIso(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkCountryIso();
    }

    /**
     * 是否安装googleplay
     * @param context
     * @return
     */
    public static String isGooglePlayInstalled(final Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(GOOGLE_PLAY_PKG_NAME, PackageManager.GET_UNINSTALLED_PACKAGES);
            return "1";
        } catch (PackageManager.NameNotFoundException e) {
            return "2";
        }
    }

    /**
     * 运营商名称
     * @param context
     * @return
     */
    public static String getNetworkOperatorName(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkOperatorName();
    }

    /**
     * 获取时区
     * @return
     */
    public static String getTimeZone() {
        SimpleDateFormat format = new SimpleDateFormat("Z");
        format.setTimeZone(TimeZone.getDefault());
        String timeZone = format.format(getCurrentTime());
        return timeZone;
    }

    public static long getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeInMillis();
    }


    /**
     * 获取手机ip地址
     * @return
     */
    public static String getPhoneIp() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    /**
     * @param str 字符�?
     * @return 字符串是否为�?
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !"".equals(str);
    }

    public static boolean isNullOrEmpty(String str) {
        return (null == str || "".equals(str));
    }
}

class GaIdUtils {
    public interface GaIdListener{
        void getGaIdSuccess(String gaId);
        void getGaIdFailed(String errorMsg);
    }

    public static void getGaId(Context context, GaIdListener gaIdListener){
        new GaIdThread(context,gaIdListener).start();
    }

    static class GaIdThread extends Thread{
        private Context context;
        private GaIdListener gaIdListener;
        public GaIdThread(Context context, GaIdListener gaIdListener){
            this.context = context;
            this.gaIdListener = gaIdListener;
        }

        @Override
        public void run() {
            final SharedPreferences sp = context.getSharedPreferences("GAID",MODE_PRIVATE);
            String gaid = sp.getString("gaid","unknow");

            if (gaid.equals("unknow")){
                try {
                    AdvertisingIdClient.AdInfo adInfo = AdvertisingIdClient.getAdvertisingIdInfo(context);
                    String advertisingId = adInfo.getId();
                    boolean optOutEnabled = adInfo.isLimitAdTrackingEnabled();

                    gaIdListener.getGaIdSuccess(advertisingId);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("gaid",advertisingId);
                    editor.commit();

                } catch (Exception e) {
                    e.printStackTrace();
                    gaIdListener.getGaIdFailed(e.getMessage());
                }

            }else{
                gaIdListener.getGaIdSuccess(gaid);
            }
        }
    }
}

class AdvertisingIdClient {
    public static final class AdInfo {
        private final String advertisingId;
        private final boolean limitAdTrackingEnabled;

        AdInfo(String advertisingId, boolean limitAdTrackingEnabled) {
            this.advertisingId = advertisingId;
            this.limitAdTrackingEnabled = limitAdTrackingEnabled;
        }

        public String getId() {
            return this.advertisingId;
        }

        public boolean isLimitAdTrackingEnabled() {
            return this.limitAdTrackingEnabled;
        }
    }

    public static AdInfo getAdvertisingIdInfo(Context context) throws Exception {
        if (Looper.myLooper() == Looper.getMainLooper())
            throw new IllegalStateException(
                    "Cannot be called from the main thread");

        try {
            PackageManager pm = context.getPackageManager();
            pm.getPackageInfo("com.android.vending", 0);
        } catch (Exception e) {
            throw e;
        }

        AdvertisingConnection connection = new AdvertisingIdClient.AdvertisingConnection();
        Intent intent = new Intent(
                "com.google.android.gms.ads.identifier.service.START");
        intent.setPackage("com.google.android.gms");
        if (context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
            try {
                AdvertisingIdClient.AdvertisingInterface adInterface = new AdvertisingIdClient.AdvertisingInterface(
                        connection.getBinder());
                AdvertisingIdClient.AdInfo adInfo = new AdvertisingIdClient.AdInfo(adInterface.getId(),
                        adInterface.isLimitAdTrackingEnabled(true));
                return adInfo;
            } catch (Exception exception) {
                throw exception;
            } finally {
                context.unbindService(connection);
            }
        }
        throw new IOException("Google Play connection failed");
    }

    private static final class AdvertisingConnection implements
            ServiceConnection {
        boolean retrieved = false;
        private final LinkedBlockingQueue queue = new LinkedBlockingQueue(
                1);

        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                this.queue.put(service);
            } catch (InterruptedException localInterruptedException) {
            }
        }

        public void onServiceDisconnected(ComponentName name) {}

        public IBinder getBinder() throws InterruptedException {
            if (this.retrieved)
                throw new IllegalStateException();
            this.retrieved = true;
            return (IBinder) this.queue.take();
        }
    }

    private static final class AdvertisingInterface implements IInterface {
        private IBinder binder;

        public AdvertisingInterface(IBinder pBinder) {
            binder = pBinder;
        }

        public IBinder asBinder() {
            return binder;
        }

        public String getId() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            String id;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                binder.transact(1, data, reply, 0);
                reply.readException();
                id = reply.readString();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return id;
        }

        public boolean isLimitAdTrackingEnabled(boolean paramBoolean)
                throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            boolean limitAdTracking;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                data.writeInt(paramBoolean ? 1 : 0);
                binder.transact(2, data, reply, 0);
                reply.readException();
                limitAdTracking = 0 != reply.readInt();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return limitAdTracking;
        }
    }
}
