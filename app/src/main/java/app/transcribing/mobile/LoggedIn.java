package app.transcribing.mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoggedIn extends AppCompatActivity {
    private class Post {
        String title;
        String image_url;
        String sub;

        Post(String title, String tor_full_url, String sub) {
            this.title = title;
            this.image_url = tor_full_url;
            this.sub = sub;
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
                        posts[i] = new Post(obj.getString("title"), obj.getString("tor_full_url"), obj.getString("tor_subreddit"));
                    }
                    return posts;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
            }
            return new Post[]{};
        }

        protected void onPostExecute(Post[] result) {
            RecyclerView mRecyclerView = findViewById(R.id.recycler_view);

            List<DataModel> dataModelList = new ArrayList<>();

            for (Post p : result) {
                dataModelList.add(new DataModel(p.title, p.sub, p.image_url));
            }

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            mRecyclerView.setLayoutManager(mLayoutManager);

            // specify an adapter and pass in our data model list

            RecyclerView.Adapter mAdapter = new MyAdapter(dataModelList, getApplicationContext());
            mRecyclerView.setAdapter(mAdapter);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = getIntent();
        String token = intent.getStringExtra(MainActivity.EXTRA_TOKEN);
        setContentView(R.layout.activity_logged_in);

        new getPosts().execute();

    }

    private static void getPosts() {

    }
}
