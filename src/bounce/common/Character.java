package bounce.common;

import jig.Entity;
import org.newdawn.slick.Image;


public class Character extends Entity {


    public Character(final float x, final float y, Image img) {
        super(x,y);

        addImageWithBoundingBox(img);
    }



}
