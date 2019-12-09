package com.gmail.miloszwasacz.munchkinlevelcounter;

import android.app.Application;

public class Variables extends Application
{

    private int maxPlayerLevel;
    private int minLevel;

    public int getMaxPlayerLevel()
    {
        return maxPlayerLevel;
    }

    public void setMaxPlayerLevel(Integer maxPlayerLevel)
    {
        this.maxPlayerLevel = maxPlayerLevel;
    }

    public int getMinLevel()
    {
        return minLevel;
    }

    public void setMinLevel(Integer minLevel)
    {
        this.minLevel = minLevel;
    }
}
