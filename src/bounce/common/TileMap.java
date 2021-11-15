package bounce.common;

import jig.Vector;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

public class TileMap {
    public final Tile[][] tiles;

    public TileMap(int tilesx, int tilesy, SpriteSheet ss) {
        this.tiles = new Tile[tilesx][tilesy];
        for (int x = 0; x < tiles.length; x++){
            for (int y = 0; y < tiles[0].length; y++){
                Image i;
                if(y == 0 || x == 0){
                    i = ss.getSprite(0,2);
                } else {
                    i = ss.getSprite(10, 4);
                }
                tiles[x][y] = new Tile(0,0, new Vector(x*32, y*32),i);
            }
        }
    }
    public Tile getTile(Vector gamexy){
        int x = (int)Math.floor( gamexy.getX()/32.0f);
        int y = (int)Math.floor( gamexy.getY()/32.0f);
        return tiles[x][y];
    }
    public Tile getTile(float gamex, float gamey){
        int x = (int)Math.floor(gamex/32.0f);
        int y = (int)Math.floor(gamey/32.0f);
        return tiles[x][y];
    }
    public void render(Graphics g, Vector screen_offset, Vector character_pos){

        //(Kevin) render a square around the player, proper render order is enforced in the for loops
        int midx = (int)Math.floor(character_pos.getX()/32.0f);
        int midy = (int)Math.floor(character_pos.getY()/32.0f);
        int dist = 18;
        for (int y = Math.max(0, midy - dist); y < Math.min( tiles[0].length, midy + dist); y++){
            for (int x = Math.min( tiles.length-1, midx +dist); x >= Math.max(0, midx-dist);  x--){
                Tile t = tiles[x][y];
                t.setPosition(lib.to_screen(t.gamepos, screen_offset, -16));
                t.render(g);
            }
        }

    }
}
