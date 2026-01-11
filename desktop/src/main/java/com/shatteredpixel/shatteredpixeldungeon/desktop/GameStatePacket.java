package com.shatteredpixel.shatteredpixeldungeon.desktop;

import com.shatteredpixel.shatteredpixeldungeon.items.weapon.melee.Mace;

import java.util.ArrayList;

public class GameStatePacket {
    public MetaData meta = new MetaData();
    public HeroData hero = new HeroData();
    public int[] terrain;
    public boolean[] visited;
    public boolean[] visible;
    public ArrayList<MobData> mobs = new ArrayList<>();

    public static class MetaData {
        public int width;
        public int height;
        public int depth;
    }
    public static class HeroData {
        public int pos;
        public int hp;
        public int maxHp;
        public int exp;
        public int weapon;
        public int armor;
    }
    public static class MobData {
        public int pos;
        public int type;
        public int id;
        public int hp;
        public int maxHp;
    }
}
