package app.transcribing.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import app.transcribing.mobile.LazyLoading.ImageLoader;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_TOKEN = "app.transcribing.mobile.TOKEN";

    public static final String user_agent = "android:app.transcribing.mobile:0.0.1 (by /u/daoverwatchguy)";

    private static final String AUTH_URL =
            "https://www.reddit.com/api/v1/authorize.compact?client_id=%s" + //https://www.reddit.com/api/v1/authorize.compact?client_id=DTam2q-mIcdJNQ
                    "&response_type=code&redirect_uri=%s&state=%s&" +
                    "duration=permanent&scope=identity+read+submit+report+vote";

    private static final String CLIENT_ID = "DTam2q-mIcdJNQ";

    private static final String REDIRECT_URI =
            "https%3A%2F%2Ftranscribing.app%2Freddit_callback.html"; //intent-filter in manifest

    private static final String STATE = "rgogfauhro9a5tuz0q53thngot5rzhau";

    private static final String ACCESS_TOKEN_URL =
            "https://www.reddit.com/api/v1/access_token";

    static SharedPreferences tokenPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImageLoader.instance = new ImageLoader(getApplicationContext());
        tokenPrefs = getApplicationContext().getSharedPreferences("tokens", MODE_PRIVATE);

        setContentView(R.layout.not_logged_in);
        if (tokenPrefs.contains("refresh_token")) {
            getAccessToken(token -> {
                findViewById(R.id.signin).setOnClickListener(null);
                Intent intent = new Intent(this, LoggedIn.class);
                intent.putExtra(EXTRA_TOKEN, token);
                startActivity(intent);
            }, error -> {
                tokenPrefs.edit().remove("refresh_token").apply();
            }, tokenPrefs.getString("refresh_token", null));
        }
    }

    private void getTokenAndSaveToPref(String code) {
        OkHttpClient client = new OkHttpClient();
        String authString = CLIENT_ID + ":";
        String encodedAuthString = Base64.encodeToString(authString.getBytes(),
                Base64.NO_WRAP);

        Request request = new Request.Builder()
                .addHeader("User-Agent", user_agent)
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(ACCESS_TOKEN_URL)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        "grant_type=authorization_code&code=" + code +
                                "&redirect_uri=" + REDIRECT_URI))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("getRedditPermissions", "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                JSONObject data = null;
                try {
                    data = new JSONObject(json);
                    String refreshToken = data.optString("refresh_token");

                    tokenPrefs.edit().putString("refresh_token", refreshToken).apply();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public interface callbackOneArgNoReturn {
        void x(String y);
    }

    public static void getAccessToken(final callbackOneArgNoReturn cbIfSuccess, final callbackOneArgNoReturn cbIfFailure, String token) {
        OkHttpClient client = new OkHttpClient();
        String authString = CLIENT_ID + ":";
        String encodedAuthString = Base64.encodeToString(authString.getBytes(),
                Base64.NO_WRAP);

        Request request = new Request.Builder()
                .addHeader("User-Agent", user_agent)
                .addHeader("Authorization", "Basic " + encodedAuthString)
                .url(ACCESS_TOKEN_URL)
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        "grant_type=refresh_token&refresh_token=" + token))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("AccessFromRefreshToken", "ERROR: " + e);
                cbIfFailure.x(e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                JSONObject data = null;
                try {
                    data = new JSONObject(json);
                    cbIfSuccess.x(data.optString("access_token"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent() != null && Intent.ACTION_VIEW.equals(getIntent().getAction())) {
            Uri uri = getIntent().getData();
            if (uri.getQueryParameter("error") != null) {
                String error = uri.getQueryParameter("error");
                Log.e("getRedditPermissions", "An error has occurred while trying to get permissions from reddit: " + error);
            } else {
                String state = uri.getQueryParameter("state");
                if (STATE.equals(state)) {
                    String code = uri.getQueryParameter("code");
                    getTokenAndSaveToPref(code);
                }
            }
        }

        if (tokenPrefs.contains("refresh_token")) {
            getAccessToken(token -> {
                Intent intent = new Intent(this, LoggedIn.class);
                intent.putExtra(EXTRA_TOKEN, token);
                startActivity(intent);
            }, error -> {
                tokenPrefs.edit().remove("refresh_token").apply();
            }, tokenPrefs.getString("refresh_token", null));
        }
    }

    public void startSignIn(View view) {
        String url = String.format(AUTH_URL, CLIENT_ID, REDIRECT_URI, STATE);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }
}
