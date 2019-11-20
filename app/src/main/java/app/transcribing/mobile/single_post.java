package app.transcribing.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import app.transcribing.mobile.LazyLoading.ImageLoader;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static app.transcribing.mobile.MainActivity.user_agent;

public class single_post extends AppCompatActivity {
    private void postComment(String content, String parent, String token, MainActivity.callbackOneArgNoReturn cb) throws UnsupportedEncodingException {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .addHeader("User-Agent", user_agent)
                .addHeader("Authorization", "bearer " + token)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .url("https://oauth.reddit.com/api/comment")
                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"),
                        "api_type=json&text=" + URLEncoder.encode(content, StandardCharsets.UTF_8.toString()) + "&thing_id=" + parent))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("postClaim", "ERROR: " + e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();

                JSONObject data = null;
                try {
                    data = new JSONObject(json);
                    if (data.has("json"))
                        data = data.getJSONObject("json");
                    else {
                        if (data.getInt("error") == 401) {
                            Toast.makeText(getApplicationContext(), "access token expired, getting new one", Toast.LENGTH_LONG).show();
                            //spaghetti galore.
                            MainActivity.getAccessToken((newToken) -> {
                                        try {
                                            postComment(content, parent, newToken, cb);
                                        } catch (UnsupportedEncodingException e) {
                                        }
                                    }, (e) -> {
                                    },
                                    getIntent().getStringExtra(MainActivity.EXTRA_RTOKEN));
                        } else {
                            Log.e(single_post.class.getCanonicalName(), "unknown problem while posting comment - error" + data.getInt("error"));
                        }
                        return;
                    }

                    if (data.getJSONArray("errors").length() != 0) {
                        Log.e(single_post.class.getCanonicalName(), "unknown problem while posting comment - " + data.getJSONArray("errors").toString(4));
                        return;
                    }

                    //I'm too lazy to create another callback interface with 0 params, so this'll stay for now
                    cb.x(null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void click_claim(View v) {
        try {
            postComment("claim -- this is an automated action by not-tor-app-mobile, please contact me with any questions.", getIntent().getStringExtra(LoggedIn.EXTRA_TORPOST), getIntent().getStringExtra(MainActivity.EXTRA_TOKEN), (unused) -> {
                Intent intent = new Intent(this, transcribe.class);
                intent.putExtra(MainActivity.EXTRA_TOKEN, getIntent().getStringExtra(MainActivity.EXTRA_TOKEN));
                intent.putExtra(MainActivity.EXTRA_RTOKEN, getIntent().getStringExtra(MainActivity.EXTRA_RTOKEN));
                intent.putExtra(LoggedIn.EXTRA_IMG, getIntent().getStringExtra(LoggedIn.EXTRA_IMG));
                intent.putExtra(LoggedIn.EXTRA_ORIGINALPOST, getIntent().getStringExtra(LoggedIn.EXTRA_ORIGINALPOST));
                intent.putExtra(LoggedIn.EXTRA_TORPOST, getIntent().getStringExtra(LoggedIn.EXTRA_TORPOST));
                startActivity(intent);
            });
        }catch (UnsupportedEncodingException e) {
            Log.e(this.getClass().getCanonicalName(), "yoínk");
        }
    }

    private boolean isFullScreen = false;
    private ViewGroup.LayoutParams originalLayoutParams;

    public void click_img(View v) {
        isFullScreen = !isFullScreen;

        if (isFullScreen) {
            findViewById(R.id.preview_singlepost).setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        } else {
            findViewById(R.id.preview_singlepost).setLayoutParams(originalLayoutParams);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        ImageLoader.instance.DisplayImage(getIntent().getStringExtra(LoggedIn.EXTRA_IMG), findViewById(R.id.preview_singlepost));

        originalLayoutParams = findViewById(R.id.preview_singlepost).getLayoutParams();
    }
}
