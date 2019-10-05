package app.transcribing.mobile;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
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
    public static final String EXTRA_POST = "app.transcribing.mobile.POST";
    public static final String EXTRA_IMG = "app.transcribing.mobile.IMAGE_URL";
    public static final String EXTRA_SUB = "app.transcribing.mobile.COMMUNITY";

    private class Post {
        String title;
        String originalTitle;
        String image_url;
        String sub;
        String id;

        Post(String title, String tor_full_url, String sub, String id) {
            this.title = title;
            this.originalTitle = title.split("\\|", 3)[2];
            //remove quotes
            this.originalTitle = this.originalTitle.substring(2, this.originalTitle.length() - 1);
            this.image_url = tor_full_url;
            this.sub = sub;
            this.id = id;
        }
    }

    private class getPosts extends AsyncTask<Void, Void, Post[]> {
        Activity a;
        getPosts(Activity a) {
            this.a = a;
        }
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
                        posts[i] = new Post(obj.getString("title"), obj.getString("tor_full_url"), obj.getString("tor_subreddit"), obj.getString("name"));
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
                dataModelList.add(new DataModel(p.originalTitle, p.sub, p.image_url, e -> {
                    Intent intent = new Intent(a, single_post.class);
                    intent.putExtra(EXTRA_POST, p.id);
                    intent.putExtra(MainActivity.EXTRA_TOKEN, getIntent().getStringExtra(MainActivity.EXTRA_TOKEN));
                    intent.putExtra(EXTRA_IMG, p.image_url);
                    intent.putExtra(EXTRA_SUB, p.sub);
                    startActivity(intent);
                }));
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

        new getPosts(this).execute();

    }
}
