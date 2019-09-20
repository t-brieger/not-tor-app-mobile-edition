package app.transcribing.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoggedIn extends AppCompatActivity {
    private class Post {
        String title;

        Post(String title) {
            this.title = title;
        }
    }

    private class getPosts extends AsyncTask<Void, Void, Post[]> {
        @Override
        protected Post[] doInBackground(Void... unused) {
            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .addHeader("User-Agent", MainActivity.user_agent)
                    .url("https://tor.magma.lol/data")
                    .get()
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                String responseBody = response.body().string();
                JSONArray data = null;
                try {
                    data = new JSONArray(responseBody);
                    Post[] posts = new Post[data.length()];
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject obj = data.getJSONObject(i);
                        posts[i] = new Post(obj.getString("title"));
                    }
                    return posts;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Log.e("GettingPosts", e.toString());
            }
            return new Post[]{};
        }

        protected void onPostExecute(Post[] result) {
            LinearLayout posts = findViewById(R.id.scroll_linearlayout);
            for (Post p : result) {
                TextView tv = new TextView(getApplicationContext());
                tv.setText(p.title);
                tv.setTextColor(0xFFFFFFFF);
                tv.setTextSize(20);
                posts.addView(tv);
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        Intent i = getIntent();
        String token = i.getStringExtra(MainActivity.EXTRA_TOKEN);

        new getPosts().execute();
    }

    private static void getPosts() {

    }
}
