package bounce.common;

import jig.Entity;
import jig.Vector;
import org.newdawn.slick.Image;


public class Tile extends Entity {
    public enum type{
        FLOOR,
        WALL,
        DOOR
    }
    //(Kevin) index of this in the tilemap
    public int gridx;
    public int gridy;

    public Vector gamepos;

    public Tile(final float x, final float y, Vector gp, Image img){
       super(x,y);
       addImage(img);
       gamepos = gp;
    }

}
