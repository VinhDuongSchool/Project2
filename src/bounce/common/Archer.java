package bounce.common;

import jig.Vector;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.Optional;


public class Archer extends Character {
    Image temp_im;

    public Archer(Vector gp, Vector v, long id) {
        super(gp,v,id);
        addImage(lib.game_sprites.getSprite(1,10));
        health = 75;
        defense = 25;
        stamina = 100;
        magic = 0;
        attack = 75;
        speed = 0.3f;
    }

    @Override
    public Optional<ArrayList<Projectile>> primary() { //Primary attack
//                egc.projectiles.add(new Projectile(egc.character.getGamepos(), egc.character.getVelocity(), 0, lib.angle_index_to_dir[diridx])); //Set the initial location to the player.
        temp_im = lib.game_sprites.getSprite(0,11);
        addImage(temp_im);
        countdown = 500;

        //Kevin, make a new projectile with the proper stats and return it
        var d = lib.angle_index_to_dir[lookingDirIdx];
        var p = new Projectile(gamepos, lib.dir_enum_to_unit_vector(d).scale(0.4f), d, this);
        var ar = new ArrayList<Projectile>();
        ar.add(p);
        return Optional.of(ar);
    }

    @Override
    public void update(final int delta) { //To end the timer.
        super.update(delta);
        if (countdown <= 0) {
            removeImage(temp_im);
        } else {
            countdown -= delta;
        }
    }

}
