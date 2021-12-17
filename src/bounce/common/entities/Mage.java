package bounce.common.entities;

import bounce.common.lib;
import jig.Vector;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.Optional;


public class Mage extends Character {
    Image temp_im;

    public Mage(Vector gp, Vector v, long id) {
        super(gp,v,id);
        addImage(lib.game_sprites.getSprite(3,10));
        health = 25;
        defense = 10;
        stamina = 100;
        magic = 100;
        attack = 100;
        speed = 0.3f;
    }

    public void doAnim() {

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