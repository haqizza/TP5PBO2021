/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author haqiz
 */
public class Enemy  extends GameObject{
    
    private double speed;
    private int dirX = 1;
    private int dirY = 1;
    
    public Enemy(int x, int y, ID id, double speed){
        super(x, y, id);
        
        this.speed = speed;
    }
    
    @Override
    public void tick() {
        // Speed enemy disesuaikan, diimplementasikan dengan jauh jarak jalan
        x += dirX * speed;
        y += dirY * speed;
        
        //Enemy berjalan sendiri (memantul)
        if(x >= Game.WIDTH - 60){
            dirX = -1;
        } else if(y >= Game.HEIGHT - 80){
            dirY = -1;
        } else if(x <= 5){
            dirX = 1;
        } else if(y <= 5){
            dirY = 1;
        }

    }

    @Override
    public void render(Graphics g) {
        
        g.setColor(Color.decode("#cc0000"));
        g.fillRect(x, y, 20, 20);
    }
}
