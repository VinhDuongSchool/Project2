package bounce.common;

import bounce.client.ExplorerGameClient;
import jig.ConvexPolygon;
import jig.ResourceManager;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.SpriteSheet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Warrior extends Character {
    Image temp_im;

    SpriteSheet IdleSpriteSheet;

    SpriteSheet attackNorthSpriteSheet;
    SpriteSheet attackNorthWestSpriteSheet;
    SpriteSheet attackWestSpriteSheet;
    SpriteSheet attackSouthWestSpriteSheet;
    SpriteSheet attackSouthSpriteSheet;
    SpriteSheet attackSouthEastSpriteSheet;
    SpriteSheet attackEastSpriteSheet;
    SpriteSheet attackNorthEastSpriteSheet;

    SpriteSheet WalkingNorthSpriteSheet;
    SpriteSheet WalkingNorthWestSpriteSheet;
    SpriteSheet WalkingWestSpriteSheet;
    SpriteSheet WalkingSouthWestSpriteSheet;
    SpriteSheet WalkingSouthSpriteSheet;
    SpriteSheet WalkingSouthEastSpriteSheet;
    SpriteSheet WalkingEastSpriteSheet;
    SpriteSheet WalkingNorthEastSpriteSheet;

    SpriteSheet DeathSpriteSheet;





    public Warrior(final float x, final float y, final float vx, final float vy, Image img, long id) {
        super(x,y,vx,vy,img,id);
        health = 100;
        defense = 50;
        stamina = 100;
        magic = 0;
        attack = 50;
        speed = 25;
        // load all animaions

        IdleSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManIdle).getScaledCopy(), ,);

        attackNorthSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManAttackNorth).getScaledCopy(),,);
        attackNorthEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManAttackNorthEast).getScaledCopy(),,);
        attackEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManAttackEast).getScaledCopy(),,);
        attackSouthEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManAttackSouthEast).getScaledCopy(),,);
        attackSouthSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManAttackSouth).getScaledCopy(),,);
        attackSouthWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManAttackSouthWest).getScaledCopy(),,);
        attackWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManAttackWest).getScaledCopy(),,);
        attackNorthWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManAttackNorthWest).getScaledCopy(),,);

        WalkingNorthSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManWalkingNorth).getScaledCopy(),,);
        WalkingNorthEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManWalkingNorthEast).getScaledCopy(),,);
        WalkingEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManWalkingEast).getScaledCopy(),,);
        WalkingSouthEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManWalkingSouthEast).getScaledCopy(),,);
        WalkingSouthSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManWalkingSouth).getScaledCopy(),,);
        WalkingSouthWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManWalkingSouthWest).getScaledCopy(),,);
        WalkingWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManWalkingWest).getScaledCopy(),,);
        WalkingNorthWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManWalkingNorthWest).getScaledCopy(),,);

        DeathSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManDeath).getScaledCopy(),,);


    }

    @Override
    public Optional<ArrayList<Projectile>> primary(int diridx) {
        //0 and 8 map to the same value
        ArrayList<lib.DIRS> attack_dirs;
        if (diridx == 0 || diridx == 8){
            //Kevin, deal with edge case
            attack_dirs = new ArrayList<>(List.of(new lib.DIRS[]{lib.DIRS.NORTHEAST, lib.DIRS.NORTH, lib.DIRS.EAST}));
        } else {
            //Kevin, otherwise get neighbors directly
            attack_dirs = new ArrayList<>(List.of(new lib.DIRS[]{lib.angle_index_to_dir[diridx-1], lib.angle_index_to_dir[diridx], lib.angle_index_to_dir[diridx+1]}));
        }

        //Kevin, if we already attacked cant attack again
        if (attack_timer > 0)
            return Optional.empty();

        temp_im = ExplorerGameClient.game_sprites.getSprite(0,11);
        addImage(temp_im);
        countdown = 500;

        //Kevin, set attack timer, for each dir in the list create a new shape,
        //keep a reference to the shape so we can delete it later,
        //add the shape to the entity with the specific offset for the dir it should be in
        attack_timer = 1000;
        for(var d : attack_dirs){
            var s = new ConvexPolygon(lib.sqr.getPoints());
            attack_shapes.add(s);
            addShape(s,lib.dir_enum_to_dir_vector(d).scale(32));
        }
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
