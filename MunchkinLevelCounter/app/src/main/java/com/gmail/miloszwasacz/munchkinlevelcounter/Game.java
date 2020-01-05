package com.gmail.miloszwasacz.munchkinlevelcounter;

public class Game
{
    public String name;
    public String content;
    public int maxLevel;
    public int minLevel;

    public Game(String name, String content, int maxLevel, int minLevel)
    {
        this.name = name;
        this.content = content;
        this.maxLevel = maxLevel;
        this.minLevel = minLevel;
    }
}
