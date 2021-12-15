package bounce.common;

import bounce.client.ExplorerGameClient;
import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

import java.util.concurrent.atomic.AtomicLong;


public class PileOfGold extends Entity {
    private Vector gamepos;

    public PileOfGold(final float x, final float y) {
        super(x,y);
        gamepos = new Vector(x,y);
        addImage(ResourceManager.getImage(ExplorerGameClient.PILEOFGOLD)); //Add the image.
        addShape(new ConvexPolygon(lib.sqr.getPoints()));
    }

    public Vector getGamepos() {
        return gamepos;
    }

    public void update(final int delta) { //Update the position based off the game position
        setPosition(gamepos);
    }


}
