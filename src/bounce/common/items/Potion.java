package bounce.common.items;

import bounce.common.lib;
import jig.ConvexPolygon;
import jig.ResourceManager;
import jig.Vector;


public class Potion extends BaseItem {

    public Potion(final float x, final float y, long i) {
        super(x,y, i);
        gamepos = new Vector(x,y);
        addImage(ResourceManager.getImage(lib.POTION));
        addShape(new ConvexPolygon(lib.sqr.getPoints()));
    }

    public Potion(final float x, final float y){
        this(x,y,ID_COUNTER.getAndIncrement());
    }

    public Vector getGamepos() {
        return gamepos;
    }

    public void update(final int delta) {
        setPosition(gamepos);
    }


}
