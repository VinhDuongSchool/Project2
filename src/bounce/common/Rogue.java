package bounce.common;

import jig.Vector;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.Optional;


public class Rogue extends Character {
    Image temp_im;

    public Rogue(Vector gp, Vector v, long id) {
        super(gp,v,id);
        addImage(lib.game_sprites.getSprite(2,10));
        health = 25;
        defense = 25;
        stamina = 100;
        magic = 0;
        attack = 200;
        speed = 0.3f;
    }

    @Override
    public Optional<ArrayList<Projectile>> primary() { //Primary attack
        temp_im = lib.game_sprites.getSprite(0,11);
        addImage(temp_im);
        countdown = 500;

        return Optional.empty();
    }

    @Override
    public void update(final int delta) { //To check the countdown timer.
        super.update(delta);
        if (countdown <= 0) {
            removeImage(temp_im);
        } else {
            countdown -= delta;
        }
    }

}
