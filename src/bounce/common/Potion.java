package bounce.common;

import bounce.client.ExplorerGameClient;
import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

import java.util.concurrent.atomic.AtomicLong;


public class Potion extends Entity {
    private Vector gamepos;

    public Potion(final float x, final float y) {
        super(x,y);
        gamepos = new Vector(x,y);
        addImage(ResourceManager.getImage(ExplorerGameClient.POTION));
        addShape(new ConvexPolygon(lib.sqr.getPoints()));
    }

    public Vector getGamepos() {
        return gamepos;
    }

    public void update(final int delta) {
        setPosition(gamepos);
    }


}
