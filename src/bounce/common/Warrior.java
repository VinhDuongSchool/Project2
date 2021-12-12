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


public class Warrior extends Character {
    public Warrior(final float x, final float y, final float vx, final float vy, Image img, long id) {
        super(x,y,vx,vy,img,id);
        health = 100;
        defense = 50;
        stamina = 100;
        magic = 0;
        attack = 50;
        speed = 25;
    }

    @Override
    public void primary(ArrayList<lib.DIRS> dirs) { //Primary attack
        super.primary(dirs);
        addImage(ExplorerGameClient.game_sprites.getSprite(0,11));
        countdown = 500;
    }

    @Override
    public void update(int delta) { //To check the countdown timer.
        super.update(delta);
        if (countdown > 0) {
            countdown -= delta;
            if (countdown <= 0) {
                removeImage(ExplorerGameClient.game_sprites.getSprite(0,11));
            }
        }

    }
}