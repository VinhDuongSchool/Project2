package bounce.common;

import bounce.client.ExplorerGameClient;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.Optional;


public class Mage extends Character {
    Image temp_im;

    public Mage(final float x, final float y, final float vx, final float vy, Image img, long id) {
        super(x,y,vx,vy,img,id);
        health = 25;
        defense = 10;
        stamina = 100;
        magic = 100;
        attack = 100;
        speed = 25;
    }

    @Override
    public Optional<ArrayList<Projectile>> primary(int dir_index) { //Primary attack
        temp_im = ExplorerGameClient.game_sprites.getSprite(0,11);
        addImage(temp_im);
        countdown = 500;

        return Optional.empty();
    }

    @Override
    public void update(int delta) { //To check the countdown timer.
        super.update(delta);
        if (countdown <= 0) {
            removeImage(temp_im);
        } else {
            countdown -= delta;
        }
    }
}
