package bounce.common.items;

import jig.Entity;
import jig.Vector;

import java.util.concurrent.atomic.AtomicLong;

public abstract class BaseItem extends Entity {
    //(Kevin) identification stuff
    protected static final AtomicLong ID_COUNTER = new AtomicLong(0);
    public final long id;

    protected Vector gamepos;

    public BaseItem(float x, float y, long i) {
        super(x,y);
        id = i;
    }


    public abstract Vector getGamepos();
}
