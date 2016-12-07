package com.example.user.a403final;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    /** Called when the user clicks the Course Grid button */
    public void showGrid(View view) {
        Intent intent = new Intent(this, CourseGrid.class);
        Button b = (Button)view;
        String buttonText = b.getText().toString();
        intent.putExtra("buttonText",buttonText);
        startActivity(intent);
    }

}

