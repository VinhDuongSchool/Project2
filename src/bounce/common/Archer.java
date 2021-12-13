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


public class Archer extends Character {
    //private Vector gamepos;
    //private Vector velocity; //Velocity vectore.
    public Archer(final float x, final float y, final float vx, final float vy, Image img, long id) {
        super(x,y,vx,vy,img,id);
        health = 75;
        defense = 25;
        stamina = 100;
        magic = 0;
        attack = 75;
        speed = 50;
        //gamepos = new Vector(x,y);
        //velocity = new Vector(vx, vy);
    }

    @Override
    public void primary(ArrayList<lib.DIRS> dirs, final lib.DIRS d) { //Primary attack
        ExplorerGameClient.projectiles.add(new Projectile(gamepos, velocity, 0, d)); //To add a new projectile.
    }

    @Override
    public void update(int delta) { //To end the timer.
        super.update(delta);
        if (countdown > 0) {
            countdown -= delta;
            if (countdown <= 0) {
                removeImage(ExplorerGameClient.game_sprites.getSprite(1,11));
            }
        }

    }
}