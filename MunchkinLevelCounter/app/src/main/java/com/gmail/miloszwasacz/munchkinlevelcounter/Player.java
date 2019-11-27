package com.gmail.miloszwasacz.munchkinlevelcounter;

public class Player {

    public String name;
    public int level;

    public Player(String name) {
        this.name = name;
        this.level = 1;
    }

    public Player(String name, int level) {
        this.name = name;
        this.level = level;
    }
}
