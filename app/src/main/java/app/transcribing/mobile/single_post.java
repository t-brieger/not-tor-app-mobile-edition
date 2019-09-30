package app.transcribing.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class single_post extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        Intent intent = getIntent();
        String post = intent.getStringExtra(LoggedIn.EXTRA_POST);

        ((TextView)findViewById(R.id.textView)).setText(post);
    }
}
