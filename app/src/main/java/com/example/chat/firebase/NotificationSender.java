package com.example.chat.firebase;
import android.content.Context;
import android.util.Log;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationSender {
    private static final String TAG = "NotificationSender";
    private final static String url="https://lucid-chat.onrender.com/send-notification";
    public static void sendNotification(Context context, String deviceToken, String title, String body, JSONObject extraData) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("device_token", deviceToken);
            payload.put("title", title);
            payload.put("body", body);
            payload.put("data", extraData != null ? extraData : new JSONObject());

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    payload,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(TAG, "Notification sent successfully: " + response.toString());
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.e(TAG, "Failed to send notification: " + error.getMessage());
                        }
                    }
            );
            RequestQueue requestQueue = Volley.newRequestQueue(context);
            requestQueue.add(jsonObjectRequest);

        } catch (JSONException e) {
            Log.e(TAG, "Error creating JSON payload: " + e.getMessage());
        }
    }
}
