package bounce.common.items;

import bounce.common.lib;
import jig.ConvexPolygon;
import jig.ResourceManager;
import jig.Vector;


public class PileOfGold extends BaseItem {
    public int amt;

    public PileOfGold(final float x, final float y, long i) {
        super(x,y, i);
        gamepos = new Vector(x,y);
        addImage(ResourceManager.getImage(lib.PILEOFGOLD)); //Add the image.
        addShape(new ConvexPolygon(lib.sqr.getPoints()));
    }

    public PileOfGold(final float x, final float y){
        this(x,y,ID_COUNTER.getAndIncrement());
    }


    public Vector getGamepos() {
        return gamepos;
    }

    public void update(final int delta) { //Update the position based off the game position
        setPosition(gamepos);
    }


}
