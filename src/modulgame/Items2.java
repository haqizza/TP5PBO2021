/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modulgame;

import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author haqiz
 */
public class Items2 extends GameObject {
    
    public Items2(int x, int y, ID id){
        super(x, y, id);
       
        //speed = 1;
    }

    @Override
    public void tick() {
        
    }

    @Override
    public void render(Graphics g) {
        g.setColor(Color.decode("#00cc00"));
        g.fillRect(x, y, 20, 20);
    }
}
