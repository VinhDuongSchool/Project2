package bounce.common.items;

import jig.Entity;
import jig.Vector;

public abstract class BaseItem extends Entity {
    protected Vector gamepos;

    public BaseItem(float x, float y) {
        super(x,y);
    }

    public abstract Vector getGamepos();
}
