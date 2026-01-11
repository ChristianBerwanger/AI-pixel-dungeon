/*
@author: Christian Berwanger
Class to connect to my python script
 */
package com.shatteredpixel.shatteredpixeldungeon.desktop;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;

import javax.net.ServerSocketFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class GameSocketServer implements Runnable {
    private int port;

    public GameSocketServer(int port) {
        this.port = port;
    }
    @Override
    public void run(){
        try(ServerSocket server_socket = new ServerSocket(port)){
            System.out.println("Waiting for Python on port " + port + "...");
            Socket client_socket = server_socket.accept();
            System.out.println("Python Connected!");

            BufferedReader in = new BufferedReader(new InputStreamReader(client_socket.getInputStream()));
            PrintWriter out = new PrintWriter(client_socket.getOutputStream(), true);

            String input_line;
            while ((input_line = in.readLine()) != null) {
                System.out.println("Received: " + input_line);
                process_request(input_line, out);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void process_request(String json_command, PrintWriter out){
        // Access Memory from main thread
        Gdx.app.postRunnable(() -> {
            JsonValue root = new JsonReader().parse(json_command);
            String action = root.getString("action");
            switch (action) {
                case "MOVE":
                    System.out.println("MOVE");
                    break;
                case "WAIT":
                    System.out.println("WAIT");
                    break;
                case "ATTACK":
                    System.out.println("ATTACK");
                    break;
                case "LVLUP":
                    System.out.println("LVLUP");
                    break;
                case "DESCENT":
                    System.out.println("DESCENT");
                    break;
                case "ASCENT":
                    System.out.println("ASCENT");
                    break;
                case "PICKUP":
                    System.out.println("PICKUP");
                    break;
                case "RESET":
                    System.out.println("RESET");
                    break;
            }
            out.println(getGameState());
        });
    }

    private String getGameState(){
        if (Dungeon.hero == null || Dungeon.level == null) {
            return "{\"status\": \"loading\"}";
        }
        int hp = Dungeon.hero.HP;
        int maxHp = Dungeon.hero.HT;
        int depth = Dungeon.depth;
        int lvl = Dungeon.hero.lvl;
        int exp = Dungeon.hero.exp;
        int gold = Dungeon.gold;

        int pos = Dungeon.hero.pos;
        int mapWidth = Dungeon.level.width();
        int x = pos % mapWidth;
        int y = pos / mapWidth;
        return String.format(
                "{" +
                        "\"status\": \"playing\"," +
                        "\"hp\": %d," +
                        "\"max_hp\": %d," +
                        "\"lvl\": %d," +
                        "\"xp\": %d," +
                        "\"gold\": %d," +
                        "\"depth\": %d," +
                        "\"pos_x\": %d," +
                        "\"pos_y\": %d" +
                        "}",
                hp, maxHp, lvl, exp, gold, depth, x, y
        );
    }
}
