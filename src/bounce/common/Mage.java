package bounce.common;

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
}