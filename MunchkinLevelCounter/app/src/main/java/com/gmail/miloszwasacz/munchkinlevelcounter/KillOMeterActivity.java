package com.gmail.miloszwasacz.munchkinlevelcounter;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class KillOMeterActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kill_o_meter);

        getSupportActionBar().setTitle("Kill-O-Meter");
    }
}
