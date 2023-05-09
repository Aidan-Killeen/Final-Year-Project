package com.example.dungeonsprawl;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity
{
    static String levelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        Constants.SCREEN_WIDTH = dm.widthPixels;
        Constants.SCREEN_HEIGHT = dm.heightPixels;

        setContentView(R.layout.activity_main);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String message = intent.getStringExtra(Menu.EXTRA_MESSAGE);
        levelName = message;


        //creates success button and message in hidden state
        Button menuButton = (Button) findViewById(R.id.menuButton);
        menuButton.setVisibility(View.GONE);
        TextView successMess = findViewById(R.id.sucessMessage);
        successMess.setVisibility(View.GONE);

        GameView.menuButton = menuButton;
        GameView.successText = successMess;


    }
    public void returnMenu(View view) {
        Intent intent = new Intent(this, Menu.class);
        startActivity(intent);
    }
}