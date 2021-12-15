package bounce.common.items;

import bounce.client.ExplorerGameClient;
import bounce.common.lib;
import jig.ConvexPolygon;
import jig.ResourceManager;
import jig.Vector;


public class Potion extends BaseItem {

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
