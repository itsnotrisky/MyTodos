package com.amca.android.mytodos;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.amca.android.mytodos.customwindow.ActivityWindow;
import android.os.Bundle;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MyTodos extends ActivityWindow {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_todos);
        
        final Button buttonStart = (Button) findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                Intent intent = new Intent(MyTodos.this, TodoList.class);
                startActivity(intent);
            }
        });
        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        Calendar mydate = Calendar.getInstance();
        mydate.setTimeInMillis(tsLong*1000);
        Toast.makeText(MyTodos.this, ts + " => " + mydate.get(Calendar.DAY_OF_MONTH)+"."+mydate.get(Calendar.MONTH)+"."+mydate.get(Calendar.YEAR), Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_my_todos, menu);
        return true;
    }
}
