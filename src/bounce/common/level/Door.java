package bounce.common.level;

import bounce.common.lib;

public class Door extends Tile {
    public boolean is_open = false;
    public boolean is_on_n_or_s;

    public Door(float x, float y, TileMap.TYPE t, Room r, boolean is_vert){
        super(x, y, lib.game_sprites.getSprite(is_vert ? 1 : 2, 2), t, r);
        assert t == TileMap.TYPE.DOOR;
        this.is_on_n_or_s  = is_vert;
    }

    public void open(){
        is_open = true;
        removeImage(curim);
        var i = lib.game_sprites.getSprite(is_on_n_or_s ? 11 : 12, 4);
        addImage(i);
    }

    public void close(){
        is_open = false;
        removeImage(curim);
        var i = lib.game_sprites.getSprite(is_on_n_or_s ? 1 : 2, 2);
        addImage(i);

    }
}
