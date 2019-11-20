package app.transcribing.mobile;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.logging.Logger;

import app.transcribing.mobile.LazyLoading.ImageLoader;

public class transcribe extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcribe);

        Intent i = getIntent();

        ImageLoader.instance.DisplayImage(i.getStringExtra(LoggedIn.EXTRA_IMG), findViewById(R.id.preview_singlepost));

        LinearLayout templates = findViewById(R.id.templatesll);

        for (Templates t : Templates.values()) {
            Button b = new Button(this);

            b.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));

            b.setText(t.name);

            b.setOnClickListener(e -> {
                if (((EditText)findViewById(R.id.transcribetext)).getText().length() != 0) {
                    new AlertDialog.Builder(this).setMessage("This will override the current text. Are you sure?").setPositiveButton("Yes", (dialog, which) -> {
                        ((EditText)findViewById(R.id.transcribetext)).setText(t.text);
                    }).setNegativeButton("No", (__, ___) -> {}).show();
                }else {
                    ((EditText)findViewById(R.id.transcribetext)).setText(t.text);
                }
            });

            templates.addView(b);
        }
    }
}
