package bounce.common;

import bounce.client.ExplorerGameClient;
import jig.ConvexPolygon;
import jig.Entity;
import jig.Shape;
import jig.Vector;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.HashMap;


public class Mage extends Character {
    public Mage(final float x, final float y, final float vx, final float vy, Image img, long id) {
        super(x,y,vx,vy,img,id);
        health = 25;
        defense = 10;
        stamina = 100;
        magic = 100;
        attack = 100;
        speed = 25;
    }

    @Override
    public void primary(ArrayList<lib.DIRS> dirs, final lib.DIRS d) { //Primary attack
        addImage(ExplorerGameClient.game_sprites.getSprite(3,11));
        countdown = 500;
    }

    @Override
    public void update(int delta) { //To check the countdown timer.
        super.update(delta);
        if (countdown > 0) {
            countdown -= delta;
            if (countdown <= 0) {
                removeImage(ExplorerGameClient.game_sprites.getSprite(3,11));
            }
        }

    }
}