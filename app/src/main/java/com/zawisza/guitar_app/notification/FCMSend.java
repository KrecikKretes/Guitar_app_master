package com.zawisza.guitar_app.notification;


import android.annotation.SuppressLint;
import android.os.StrictMode;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class FCMSend {

    private static final String BASE_URL="https://fcm.googleapis.com/fcm/send";
    private static String SERVER_KEY;

    public static void SetServerKey() {
        //FCMSend.SERVER_KEY = "key=" + SERVER_KEY;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection("Keys").document("SERVER_KEY");
        documentReference.get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            FCMSend.SERVER_KEY = "key=" + document.getString("key");
                        }
                    }
                });
    }

    protected String title = null, body = null, to = null, click_action = null, id = null;
    protected HashMap<String, String> data = null;
    protected boolean topic, apple, activ;
    protected String result;

    public String Result() {
        return result;
    }

    public static class Builder {
        private final FCMSend mFcm;

        public Builder(String to) {
            mFcm = new FCMSend();
            mFcm.to = to;
        }

        public Builder(String to, boolean topic) {
            mFcm = new FCMSend();
            mFcm.to = to;
            mFcm.topic = topic;
        }

        public Builder setTitle(String title) {
            mFcm.title = title;
            return this;
        }

        public Builder setBody(String body) {
            mFcm.body = body;
            return this;
        }

        public Builder setID(String id) {
            mFcm.id = id;
            return this;
        }

        public Builder setApple(boolean apple){
            mFcm.apple = apple;
            return this;
        }

        public Builder setActiv(boolean activ){
            mFcm.activ = activ;
            return this;
        }

        @SuppressLint("NewApi")
        public FCMSend send() {
            if (SERVER_KEY == null){
                mFcm.result = "No Server Key";
            }else{
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                try {
                    JSONObject json = new JSONObject();
                    if (mFcm.topic){
                        json.put("to", "/topics/" + mFcm.to);
                    }else{
                        json.put("to", mFcm.to);
                    }

                    if(mFcm.apple){
                        JSONObject notification = new JSONObject();

                        notification.put("body", mFcm.title);
                        notification.put("documentID", mFcm.id);
                        notification.put("sound", "default");
                        if(mFcm.activ){
                            notification.put("collection", "Enrollments");
                            notification.put("title", "Pojawiły się nowe zapisy!");
                        }else{
                            notification.put("collection", "Announcements");
                            notification.put("title", "Pojawiło się nowe ogłoszenie!");
                        }
                        if (mFcm.click_action != null)
                            notification.put("click_action", mFcm.click_action);
                        json.put("notification", notification);

                    }else{
                        if (mFcm.data != null) {
                            JSONObject data = new JSONObject();
                            mFcm.data.forEach((key, value) -> {
                                try {
                                    data.put(key, value);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                            json.put("data", data);
                        }
                        JSONObject data = new JSONObject();
                        data.put("body",mFcm.title);
                        data.put("documentID", mFcm.id);
                        if(mFcm.activ){
                            data.put("collection", "Enrollments");
                            data.put("title", "Pojawiły się nowe zapisy!");
                        }else{
                            data.put("collection", "Announcements");
                            data.put("title","Pojawiło się nowe ogłoszenie!");
                        }
                        json.put("data", data);
                    }

                    HttpURLConnection conn = (HttpURLConnection) new URL(BASE_URL).openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestProperty("Content-Type", "application/json;");
                    conn.setRequestProperty("Authorization", SERVER_KEY);
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setRequestMethod("POST");
                    OutputStream os = conn.getOutputStream();
                    os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                    os.close();
                    InputStream in = new BufferedInputStream(conn.getInputStream());
                    //String result = IOUtils.toString(in, "UTF-8");
                    in.close();
                    conn.disconnect();
                    mFcm.result = "Wysłano powiadomienie";
                } catch (JSONException | IOException e) {
                    //mFcm.result = e.getMessage();
                    mFcm.result= "Not work";
                }
            }
            return mFcm;
        }
    }
}


