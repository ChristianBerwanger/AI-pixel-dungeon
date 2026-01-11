/*
@author: Christian Berwanger
Class to connect to my python script
 */
package com.shatteredpixel.shatteredpixeldungeon.desktop;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Scene;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
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
import java.util.ArrayList;


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
                    performMove("UP");
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
        // Hero Data
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

        int[] terrain;
        boolean[] visited;
        boolean[] visible;

        ArrayList<Mob> mobs = new ArrayList<>();


        StringBuilder gameState_json = new StringBuilder();
        gameState_json.append("{");
        gameState_json.append("\"meta");

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
    private void performMove(String direction){
        if(Dungeon.hero == null || Dungeon.level == null) return;
        int pos = Dungeon.hero.pos;
        int world_width = Dungeon.level.width();
        int target = -1;
        switch (direction.toUpperCase()) {
            case "UP": target = pos - world_width; break;
            case "DOWN": target = pos + world_width; break;
            case "LEFT":  target = pos - 1; break;
            case "RIGHT":  target = pos + 1; break;

            case "UP_LEFT": target = pos - world_width - 1; break;
            case "UP_RIGHT": target = pos - world_width + 1; break;
            case "DOWN_LEFT": target = pos + world_width - 1; break;
            case "DOWN_RIGHT": target = pos + world_width + 1; break;
        }
        if (target >= 0 && target < Dungeon.level.length()) {

            // We get the current active screen (Scene)
            Scene currentScene = ShatteredPixelDungeon.scene();

            // We check if we are actually in the game (and not the main menu)
            if (currentScene instanceof GameScene) {
                // CAST the scene to GameScene so we can access handleCell
                GameScene game = (GameScene) currentScene;

                // "handleCell" simulates a tap on that tile.
                // The game will decide if it's a move, an attack, or opening a door.
                game.handleCell(target);
            }
        }
    }
}
