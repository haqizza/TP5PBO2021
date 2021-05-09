/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.util.ArrayList;
import java.util.Collections;
/**
 *
 * @author Fauzan
 */
public class Game extends Canvas implements Runnable{
    Window window;
    public static final int WIDTH = 800;
    public static final int HEIGHT = 600;
    
    private String username = "";
    private String username2 = "";
    
    private Clip clip;
    
    private int score = 0;
    
    private int time = 20;    
    private int playTime = 0;
    private int allScore = 0;

    private double enemySpeed = 3;
    
    private Thread thread;
    private boolean running = false;
    
    private Handler handler;

    
    public enum STATE{
        Game,
        GameOver
    };
    
    public STATE gameState = STATE.Game;
    
    public Game(String username, double speed){
        //BGM
        playSound("/Torimase8.wav");
        
        window = new Window(WIDTH, HEIGHT, "TP praktikum 5", this);
        
        handler = new Handler();
        
        this.addKeyListener(new KeyInput(handler, this));
        
        this.username = username;
        
        // Kecepatan enemy dan waktu disesuaikan
        enemySpeed = speed;
        if(speed == 5){
            time = 10;
        }
        else if(speed == 7){
            time = 5;
        }
        
        if(gameState == STATE.Game){
            handler.addObject(new Items(100,150, ID.Item));
            handler.addObject(new Items(200,350, ID.Item));           
            handler.addObject(new Enemy(0,0, ID.Enemy, enemySpeed));
            handler.addObject(new Enemy(0,180, ID.Enemy2, enemySpeed));
            handler.addObject(new Player(200,200, ID.Player)); // Player 2
        }
    }
    
    public Game(String username, String username2, double speed){
        //BGM
        playSound("/Torimase8.wav");
        
        window = new Window(WIDTH, HEIGHT, "TP praktikum 5", this);
        
        handler = new Handler();
        
        this.addKeyListener(new KeyInput(handler, this));
        
        this.username = username;
        
        // Kecepatan enemy dan waktu disesuaikan
        enemySpeed = speed;
        if(speed == 5){
            time = 10;
        }
        else if(speed == 7){
            time = 5;
        }
        
        if(gameState == STATE.Game){
            handler.addObject(new Items(100,150, ID.Item));
            handler.addObject(new Items(200,350, ID.Item));            
            handler.addObject(new Enemy(0,0, ID.Enemy, enemySpeed));
            handler.addObject(new Enemy(0,180, ID.Enemy2, enemySpeed));
            handler.addObject(new Player(200,200, ID.Player));
            handler.addObject(new Player2(100,100, ID.Player2)); // Player 2
        }
    }
    
    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }
    
    public synchronized void stop(){
        try{
            thread.join();
            running = false;
            clip.stop();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        
        while(running){
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            
            while(delta >= 1){
                tick();
                delta--;
            }
            if(running){
                render();
                frames++;
            }
            
            if(System.currentTimeMillis() - timer > 1000){
                timer += 1000;
//                System.out.println("FPS: " + frames);
                frames = 0;
                if(gameState == STATE.Game){
                    if(time>0){
                        time--;
                        playTime++;
                        allScore = playTime + score;
                    }else{
                        gameState = STATE.GameOver;
                    }
                }
                else if(gameState == STATE.GameOver){
                    running = false;
                }
            }
        }        
        // Simpan data score player
        dbConnection db = new dbConnection();

        if(username2.length() > 0){
            db.saveScore(username + " & " + username2, allScore);
        } else {
            db.saveScore(username, allScore);
        }
               
        stop();
    }
    
    private void tick(){
        handler.tick();
        if(gameState == STATE.Game){
            GameObject playerObject = null;
            GameObject playerObject2 = null;
            GameObject enemyObject = null;            
            GameObject enemyObject2 = null;


            for(int i=0;i< handler.object.size(); i++){
                if(handler.object.get(i).getId() == ID.Player){
                   playerObject = handler.object.get(i);
                }
                if(handler.object.get(i).getId() == ID.Player2){
                   playerObject2 = handler.object.get(i);
                }   
                if(handler.object.get(i).getId() == ID.Enemy){
                   enemyObject = handler.object.get(i);
                }
                if(handler.object.get(i).getId() == ID.Enemy2){
                   enemyObject2 = handler.object.get(i);
                }
            }
            
            if(playerObject != null || playerObject2 != null){              
                for(int i=0;i< handler.object.size(); i++){
                    if(handler.object.get(i).getId() == ID.Item){
                        if(checkCollision(playerObject, handler.object.get(i))){
                            playSound("/Eat.wav");
                            handler.removeObject(handler.object.get(i));
                            
                            // Score didapatkan diacak dengan range 1-10
                            score = score + getRandomNumber(1, 10);
                            // Waktu didapatkan diacak dengan range 1-7
                            time = time + getRandomNumber(1, 7);
                            
                            // Add item baru
                            // Cek apakah object empty
                            if(handler.object.size() > 0){
                                addItem();
//                                addItem();

                            }
                            
                            break;
                        }
                        
                        if(playerObject2 != null && checkCollision(playerObject2, handler.object.get(i))){
                            playSound("/Eat.wav");
                            handler.removeObject(handler.object.get(i));
                            score = score + 10;
                            time = time + 5;
                            
                            // Add item baru
                            // Cek apakah object empty
                            if(handler.object.size() > 0){
                                addItem();
                            }
                            
                            break;
                        }
                    }
                }
                
                // Cek apakah player menabrak enemy
                if(checkCollision(playerObject, enemyObject) || checkCollision(playerObject, enemyObject2)){
                    handler.removeObject(playerObject);
                    playerObject = null;
                }
                if(playerObject2 != null && (checkCollision(playerObject2, enemyObject) || checkCollision(playerObject, enemyObject2))){
                    handler.removeObject(playerObject2);
                    playerObject2 = null;
                }
                if(playerObject == null && playerObject2 == null){
                    gameState = STATE.GameOver;
                }
            }
            
        }
    }
    
    public int getRandomNumber(int min, int max){
        ArrayList<Integer> list = new ArrayList<Integer>();
        for (int i=min; i<max; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        return list.get(0);
    }
    
    public void addItem(){
        int x = getRandomNumber(10, 790);
        int y = getRandomNumber(10, 590);
        handler.addObject(new Items(x, y, ID.Item));
    }
    
    public static boolean checkCollision(GameObject player, GameObject item){
        boolean result = false;
        
        int sizePlayer = 50;
        int sizeItem = 20;
        
        int playerLeft = player.x;
        int playerRight = player.x + sizePlayer;
        int playerTop = player.y;
        int playerBottom = player.y + sizePlayer;
        
        int itemLeft = item.x;
        int itemRight = item.x + sizeItem;
        int itemTop = item.y;
        int itemBottom = item.y + sizeItem;
        
        if((playerRight > itemLeft ) &&
        (playerLeft < itemRight) &&
        (itemBottom > playerTop) &&
        (itemTop < playerBottom)
        ){
            result = true;
        }
        
        return result;
    }
    
    private void render(){
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        
        Graphics g = bs.getDrawGraphics();
        
        g.setColor(Color.decode("#F1f3f3"));
        g.fillRect(0, 0, WIDTH, HEIGHT);
                
        
        
        if(gameState ==  STATE.Game){
            handler.render(g);
            
            Font currentFont = g.getFont();
            Font newFont = currentFont.deriveFont(currentFont.getSize() * 1.4F);
            g.setFont(newFont);

            g.setColor(Color.BLACK);
            g.drawString("Score: " +Integer.toString(score), 20, 20);

            g.setColor(Color.BLACK);
            g.drawString("Time: " +Integer.toString(time), WIDTH-120, 20);
        }else{    
            Font currentFont = g.getFont();
            Font newFont = currentFont.deriveFont(currentFont.getSize() * 3F);
            g.setFont(newFont);

            g.setColor(Color.BLACK);
            g.drawString("GAME OVER", WIDTH/2 - 120, HEIGHT/2 - 30);

            currentFont = g.getFont();
            Font newScoreFont = currentFont.deriveFont(currentFont.getSize() * 0.5F);
            g.setFont(newScoreFont);

            g.setColor(Color.BLACK);
            g.drawString("Score: " +Integer.toString(allScore), WIDTH/2 - 50, HEIGHT/2 - 10);
            
            g.setColor(Color.BLACK);
            g.drawString("Press Space to Continue", WIDTH/2 - 100, HEIGHT/2 + 30);
        }
                
        g.dispose();
        bs.show();
    }
    
    public static int clamp(int var, int min, int max){
        if(var >= max){
            return var = max;
        }else if(var <= min){
            return var = min;
        }else{
            return var;
        }
    }
    
    public void close(){
        clip.stop();
        window.CloseWindow();
    }
    
    public void playSound(String filename){
        try {
            // Open an audio input stream.
            URL url = this.getClass().getResource(filename);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
            // Get a sound clip resource.
            clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
           e.printStackTrace();
        } catch (IOException e) {
           e.printStackTrace();
        } catch (LineUnavailableException e) {
           e.printStackTrace();
        }
    }
}
