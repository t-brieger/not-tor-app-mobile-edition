package app.transcribing.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import app.transcribing.mobile.LazyLoading.ImageLoader;

public class single_post extends AppCompatActivity {

    public void click_claim(View v) {

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

        Intent intent = getIntent();
        String post = intent.getStringExtra(LoggedIn.EXTRA_POST);

        ImageLoader.instance.DisplayImage(getIntent().getStringExtra(LoggedIn.EXTRA_IMG), findViewById(R.id.preview_singlepost));

        originalLayoutParams = findViewById(R.id.preview_singlepost).getLayoutParams();
    }
}
