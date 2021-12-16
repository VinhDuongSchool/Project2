package bounce.common;

import bounce.client.ExplorerGameClient;
import jig.ConvexPolygon;
import jig.ResourceManager;
import jig.Vector;
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

    Animation test;





    public Warrior(final float x, final float y, final float vx, final float vy, Image img, long id) {
        super(x,y,vx,vy,img,id);
        health = 100;
        defense = 50;
        stamina = 100;
        magic = 0;
        attack = 50;
        speed = 25;
        // load all animaions

        //this spritesheet will not be used as an animation
        IdleSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManIdle), 183,138);
        Image one = IdleSpriteSheet.getSprite(0,0).getScaledCopy(.5f);
        temp_im = one;
        //addImage(IdleSpriteSheet.getSprite(0,0).getScaledCopy(.5f), new Vector(-10.0f,0));
        //default starting img added
        // all of these spritesheets will be used for animations
        attackNorthSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManAttackNorth).getScaledCopy(.5f),72,79);//vector offset needs to be -5 -1

        attackNorthEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManAttackNorthEast).getScaledCopy(.5f),85,72); //vector offset unchanged
        attackEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManAttackEast).getScaledCopy(.5f),117,65); // vector offset -10 -1
        attackSouthEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManAttackSouthEast).getScaledCopy(.5f),104,68); // vector offset -20
        attackSouthSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManAttackSouth).getScaledCopy(.5f),79,86); // vector offset unchanged
        attackSouthWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManAttackSouthWest).getScaledCopy(.5f),86,78);//vector unchanged
        attackWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManAttackWest).getScaledCopy(.5f),115,69);// vector offset -15 -1
        attackNorthWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManAttackNorthWest).getScaledCopy(.5f),99,66);// vector offset -5 f-1

        //same with these
        WalkingNorthSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManWalkingNorth).getScaledCopy(.5f),49,79); // vector offset -10 -1
        WalkingNorthEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManWalkingNorthEast).getScaledCopy(.5f),75,69);// vector unchanged
        WalkingEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManWalkingEast).getScaledCopy(.5f),95,61);// vector unchanged
        WalkingSouthEastSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManWalkingSouthEast).getScaledCopy(.5f),75,66); // vector unchanged
        WalkingSouthSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManWalkingSouth).getScaledCopy(.5f),52,67); // vector unchanged
        WalkingSouthWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManWalkingSouthWest).getScaledCopy(.5f),79,66);// vector offset -15 -1
        WalkingWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManWalkingWest).getScaledCopy(.5f),101,61);// vector unchanged
        WalkingNorthWestSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManWalkingNorthWest).getScaledCopy(.5f),82,71);// vector unchanged

        // same with this one
        DeathSpriteSheet = new SpriteSheet(ResourceManager.getImage(ExplorerGameClient.SpearManDeath).getScaledCopy(.5f),77,79); // vector offset -10 -1
        test = new Animation(DeathSpriteSheet,50);


    }

    @Override
    public Optional<ArrayList<Projectile>> primary(int diridx) {
        removeImage(temp_im);
        addAnimation(test, new Vector(-10.0f,1f));
        test.setLooping(false);
        test.restart();
//        //0 and 8 map to the same value
//        ArrayList<lib.DIRS> attack_dirs;
//        if (diridx == 0 || diridx == 8){
//            //Kevin, deal with edge case
//            attack_dirs = new ArrayList<>(List.of(new lib.DIRS[]{lib.DIRS.NORTHEAST, lib.DIRS.NORTH, lib.DIRS.EAST}));
//        } else {
//            //Kevin, otherwise get neighbors directly
//            attack_dirs = new ArrayList<>(List.of(new lib.DIRS[]{lib.angle_index_to_dir[diridx-1], lib.angle_index_to_dir[diridx], lib.angle_index_to_dir[diridx+1]}));
//        }
//
//        //Kevin, if we already attacked cant attack again
//        if (attack_timer > 0)
//            return Optional.empty();
//
//        temp_im = ExplorerGameClient.game_sprites.getSprite(0,11);
//        addImage(temp_im);
//        countdown = 500;
//
//        //Kevin, set attack timer, for each dir in the list create a new shape,
//        //keep a reference to the shape so we can delete it later,
//        //add the shape to the entity with the specific offset for the dir it should be in
//        attack_timer = 1000;
//        for(var d : attack_dirs){
//            var s = new ConvexPolygon(lib.sqr.getPoints());
//            attack_shapes.add(s);
//            addShape(s,lib.dir_enum_to_dir_vector(d).scale(32));
//        }
        return Optional.empty();

    }

    @Override
    public void update(int delta) { //To check the countdown timer.
        if(test.isStopped()){
            removeAnimation(test);
            temp_im = IdleSpriteSheet.getSprite(0,0).getScaledCopy(.5f);
            addImage(temp_im, new Vector(-10.0f,0f));
        }
        super.update(delta);
        if (countdown <= 0) {
            removeImage(temp_im);
        } else {
            countdown -= delta;
        }

    }
}
