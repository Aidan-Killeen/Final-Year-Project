package com.example.dungeonsprawl;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;


public class Menu extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.dungeonsprawl.MESSAGE";

    LinearLayout linear;

    Menu process = this;

    public static String levelToLoad = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);

        test();
    }
    @Override
    public void onBackPressed()
    {
        //do nothing
    }
    
    private void test()
    {
        linear = (LinearLayout) findViewById(R.id.linear);

        InputStream is = null;
        try {
            //create buttons for each entry in levels file
            String data = "Levels.json";
            is = this.getResources().getAssets().open(data);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String json = new String(buffer, "UTF-8");

            JSONObject obj = new JSONObject(json);
            JSONArray levels = obj.getJSONArray("Levels");

            for (int i = 0; i < levels.length(); i++)
            {
                JSONObject temp = levels.getJSONObject(i);
                String tempName = temp.getString("LevelName");

                Button tempB = new Button(this);
                tempB.setHeight(50);
                tempB.setWidth(50);
                tempB.setTag(tempName);
                tempB.setText("Level:  " + tempName);
                tempB.setAllCaps(false);
                tempB.setOnClickListener(btnClicked);

                linear.addView(tempB);
            }
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(), "An Error has occured", Toast.LENGTH_SHORT).show();
        }

    }

    View.OnClickListener btnClicked = new View.OnClickListener()
    {
        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            Toast.makeText(getApplicationContext(), "Level selected: " + tag.toString(), Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(process, MainActivity.class);
            levelToLoad = tag.toString();

            intent.putExtra(EXTRA_MESSAGE, levelToLoad);
            startActivity(intent);
        }
    };

}