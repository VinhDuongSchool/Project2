package bounce.client;

import bounce.common.Message;
import bounce.common.entities.Character;
import bounce.common.entities.*;
import bounce.common.items.BaseItem;
import bounce.common.items.PileOfGold;
import bounce.common.items.Potion;
import bounce.common.level.Door;
import bounce.common.level.TileMap;
import bounce.common.lib;
import jig.Vector;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.io.IOException;
import java.util.*;


/**
 * This state is active when the Game is being played. In this state, sound is
 * turned on, the bounce counter begins at 0 and increases until 10 at which
 * point a transition to the Game Over state is initiated. The user can also
 * control the ball using the WAS & D keys.
 *
 * Transitions From StartUpState
 *
 * Transitions To GameOverState
 */
public class ClientPlayingState extends BasicGameState {

    lib.DIRS ldir;
    int lastMouseIdx = 0;
    Controller rightcontroller;
    Controller leftcontroller;

	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
        ExplorerGameClient egc = (ExplorerGameClient)game;

        if(egc.controllerused)
            ldir = lib.DIRS.SOUTH;

        if(egc.is_connected)
            return;

        container.setSoundOn(true);


//        egc.enemies.add(new Enemy(64,32, 0, 0, egc.game_sprites.getSprite(0, 9))); //Add the enemies
//        egc.enemies.add(new Zombie(
//                new Vector(64,32),
//                new Vector(0, 0),
//                ExplorerGameClient.game_sprites.getSprite(0, 9))); //Add the enemies
//        egc.enemies.add(new ShadowArcher(
//                new Vector(64,32),
//                new Vector(0, 0),
//                ExplorerGameClient.game_sprites.getSprite(3, 8))); //Add the enemies
//        egc.enemies.add(new Enemy(32*3,32*5, 0, 0, egc.game_sprites.getSprite(0, 9)));
//        egc.enemies.add(new Enemy(32,32, 0, 0, egc.game_sprites.getSprite(1, 8)));



        egc.items.add(new PileOfGold(2*32,2*32)); //Add the potions
        egc.items.add(new PileOfGold(15*32,5*32));
        egc.items.add(new Potion(5*32,25*32));
        egc.items.add(new Potion(15*32,32*32));

        egc.items.add(new Potion(31*32,5*32));
//        egc.character.setGamepos(new Vector(30*32,5*32));



	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
        ExplorerGameClient egc = (ExplorerGameClient)game;

        if(egc.character == null)
            throw new IllegalStateException("character not initialized");

        var screen_offset = lib.to_screen(egc.character.getGamepos().scale(-1), egc.screen_center);
        egc.character.setPosition(egc.screen_center);
        egc.screenox = screen_offset.getX();
        egc.screenoy = screen_offset.getY();

        egc.grid.render(g,screen_offset, egc.character.getGamepos());
        g.setColor(Color.white);
        g.drawString("Gold: " + egc.character.gold, 10, 50);
        g.drawString("Health: " + egc.character.health, 10, 70);
        g.setColor(Color.gray);






        // testing stuff
//        var v = lib.to_screen(egc.character.gamepos, new Vector(egc.screenox, egc.screenoy));
//        g.drawLine(0,0, v.getX(), v.getY());
//        draw game pos on screen
//        g.setColor(Color.blue);
//        g.drawRect(egc.character.getGamepos().getX(), egc.character.getGamepos().getY(), 32, 32);
//        g.drawRect(egc.grid.tiles[10][10].gamepos.getX(),egc.grid.tiles[10][10].gamepos.getY(), 32,32);
//        for (var e : egc.enemies){
//            g.drawRect(e.getGamepos().getX(), e.getGamepos().getY(),32,32);
//        }
//        g.drawLine(mp.getX(), mp.getY(), egc.screen_center.getX(),  egc.screen_center.getY());
//        egc.character.setPosition(egc.character.getGamepos());
//        egc.character.getShapes().forEach(s -> g.draw(new Polygon(s.getPoints())));
//        g.drawRect(egc.character.getGamepos().getX()-16, egc.character.getGamepos().getY()-16, 32,32);
//        for(var r : egc.grid.rooms){
//            r.room_hitbox.render(g);
//            g.drawRect(r.x, r.y, r.width, r.height);
//        }
//        g.setColor(Color.gray);
//        System.out.print(egc.character.gamepos + " ");
//        System.out.println(Math.floor(egc.character.gamepos.getX() / 32.0f));
//        var p = new Polygon();
//        var v = new Vector(0,0);
//        var v2 = lib.to_screen(v, new Vector(egc.screenox, egc.screenoy));
//        p.addPoint(v2.getX(), v2.getY());
//        v = new Vector(32,32);
//        v2 = lib.to_screen(v, new Vector(egc.screenox, egc.screenoy));
//        p.addPoint(v2.getX(), v2.getY());
//        v = new Vector(0,32);
//        v2 = lib.to_screen(v, new Vector(egc.screenox, egc.screenoy));
//        p.addPoint(v2.getX(), v2.getY());
//        v = new Vector(32,0);
//        v2 = lib.to_screen(v, new Vector(egc.screenox, egc.screenoy));
//        p.addPoint(v2.getX(), v2.getY());
//        g.draw(p);


        for (BaseItem e : egc.items) {
            e.setPosition(lib.to_screen(e.getGamepos(), new Vector(egc.screenox, egc.screenoy)));
            e.render(g);
        }

        for (Enemy e : egc.enemies) {//Render all the enemies.
            e.setPosition(lib.to_screen(e.getGamepos(), new Vector(egc.screenox, egc.screenoy)));
            e.render(g);
        }
        egc.character.render(g); //Render the character onto the screen.

        egc.projectiles.stream().forEach(p -> {
            p.setPosition(lib.to_screen(p.getGamepos(), new Vector(egc.screenox, egc.screenoy)));
            p.render(g);
        });

        if(egc.is_connected){
            for (var c : egc.allies.values()){
                if (c.client_id != egc.ID){
                    c.setPosition(lib.to_screen(c.getGamepos(), new Vector(egc.screenox, egc.screenoy)));
                    c.render(g);
                }
            }
        }

	}

//    public void controllerButtonPressed(int controller, int button){
//
//    }


	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
        Input input = container.getInput();
        ExplorerGameClient egc = (ExplorerGameClient) game;

        if(input.isKeyPressed(Input.KEY_T)){
            egc.grid = null;
            egc.grid = new TileMap(100,100);
        }


        if(egc.controllerused){
            leftcontroller = Controllers.getController(5);
            rightcontroller = Controllers.getController(6);
        }

        //System.out.println("xaxis : " + leftcontroller.getAxisValue(1));
        //System.out.println("yaxis: " + leftcontroller.getXAxisValue());

        /*
         * 1 is down dpad for left controler
         * 0 is left dpad for left controller
         * 3 is right dpad for left controller
         * 2 is up dpad for left controller
         *
         * 0 is a for right controller
         * 2 is b for right controller
         */

        if(input.isKeyPressed(Input.KEY_1)){
            egc.enterState(ExplorerGameClient.GAMEOVERSTATE);
        }
        if(input.isKeyPressed(Input.KEY_2)){
            egc.enterState(ExplorerGameClient.WINSTATE);
        }
        if(egc.character == null)
            throw new IllegalStateException("character not initialized");

        if(!egc.is_connected)
            egc.grid.MakePath(new ArrayList<Vector>( List.of(egc.character.getGamepos())));

        //(Kevin) deal with user input
        var inp = List.of( new Boolean[]{input.isKeyDown(Input.KEY_W), input.isKeyDown(Input.KEY_A), input.isKeyDown(Input.KEY_S), input.isKeyDown(Input.KEY_D)});
        if(egc.controllerused) {
            inp = List.of( new Boolean[]{leftcontroller.isButtonPressed(2), leftcontroller.isButtonPressed(0), leftcontroller.isButtonPressed(1), leftcontroller.isButtonPressed(3)});
        }


        var cMovDir = lib.wasd_to_dir(inp);

        if ( cMovDir != null){
            ldir = cMovDir;
        }
        //Kevin, m is mouse cords on screen, character is always in the sceen center,
        //angleto gives the angle in degrees rotated by 180 for some reason,
        //divide by 45 to convert into 8 directions, then round to get the angle index,
        var mousePos = new Vector(input.getMouseX(), input.getMouseY());
        int cLookingDirIdx = (int)Math.round((mousePos.angleTo(egc.screen_center)+180)/45);
        if(egc.controllerused){
            for(int i = 0; i<lib.angle_index_to_dir.length ; i++ ){
                if( lib.angle_index_to_dir[i] == ldir){
                    cLookingDirIdx = i;
                }


            }
        }



        if(egc.is_connected){ //Kevin, run with a server
            var messages = new ArrayList<Message>();

            if(egc.character.getCurdir() != cMovDir){
                messages.add(Message.builder(Message.MSG_TYPE.SET_DIR, egc.ID).setEtype(Message.ENTITY_TYPE.CHARACTER).setDir(cMovDir));
                egc.character.setCurdir(cMovDir);
            }

            if (egc.character.dead) //If character is dead then don't do anything.
                egc.enterState(ExplorerGameClient.GAMEOVERSTATE);

            //win condition
            if(egc.enemies.isEmpty() && egc.grid.rooms.stream().allMatch(r -> r.completed))
                egc.enterState(ExplorerGameClient.WINSTATE);


            if(egc.controllerused){
                if(rightcontroller.isButtonPressed(0)){
                    messages.add(Message.builder(Message.MSG_TYPE.PRIMARY, egc.ID));
                }
            }else if(input.isKeyPressed(Input.KEY_F) ) {
                messages.add(Message.builder(Message.MSG_TYPE.PRIMARY, egc.ID));
            }
            if(cLookingDirIdx != lastMouseIdx){
                messages.add(Message.builder(Message.MSG_TYPE.MOUSE_IDX, egc.ID).setIntData(cLookingDirIdx));
                lastMouseIdx = cLookingDirIdx;
                egc.character.lookingDirIdx = lastMouseIdx;
            }

            if(!egc.character.dead) {
                messages.stream().forEach(m -> {
                    try {
                        egc.out_stream.writeObject(m);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            for(var m = egc.in_messages.poll(); m != null; m = egc.in_messages.poll()){
                egc.handle_message(m);
            }

            egc.allies.values().stream().filter(c -> !c.dead).forEach(Character::doAnim);

        } else {
            //Kevin, character array to imitate server so its easier to copy functionality
            var egs_characters = new Character[]{egc.character};

            if (input.isKeyPressed(Input.KEY_L)) { //If L key is pressed then game resumes as normal.
                egc.character.dead = false;
                egc.character.health = egc.character.maxHealth;
            }

            if (egc.character.dead) { //If character is dead then don't do anything.
                egc.enterState(ExplorerGameClient.GAMEOVERSTATE);
            }

            //win condition
            if(egc.enemies.isEmpty() && egc.grid.rooms.stream().allMatch(r -> r.completed)){
                egc.enterState(ExplorerGameClient.WINSTATE);
            }

            if (input.isKeyPressed(Input.KEY_K) || egc.character.health <= 0) { //If k key is pressed or player runs out of health the do death scene.
                egc.character.dieScene();
            }


            egc.items.stream().filter(i -> egc.character.collides(i) != null).findAny().ifPresent(item -> {
                //Kevin, for when items have more complex function we need to cast them
                if(item instanceof PileOfGold){
                    var ci = (PileOfGold) item;
                    egc.character.gold += 50;
                } else if (item instanceof Potion){
                    var ci = (Potion) item;
                    if (egc.character.health + 50 > egc.character.maxHealth) { //Check how much health to restore.
                        egc.character.health = egc.character.maxHealth;
                    } else {
                        egc.character.health += 50;
                    }

                } else {
                    throw new IllegalArgumentException("unknown item");
                }
                egc.items.remove(item);
            });


            //(Kevin) handle stuff when client isnt connected
            egc.character.setCurdir(cMovDir);
            egc.character.lookingDirIdx = cLookingDirIdx;

            {// this is in this order for a reason otherwise colliding with walls will move the player backwards
                egc.character.update(delta);
                egc.character.doAnim();

                //Kevin, check collision with the 8 neighbor tiles of the character and undo their movement if there is a collision
                egc.grid.getNeighbors(egc.character.getGamepos()).stream()
                        .filter(t -> t.type == TileMap.TYPE.WALL || (t.type == TileMap.TYPE.DOOR && !((Door) t).is_open)) //hmmmmmmmmmmmmmm
                        .map(egc.character::collides) // stream of collisions that may be null
                        .filter(Objects::nonNull)
                        .findAny().ifPresent(c -> { // the actual collision object isnt useful, the minpentration doesnt work at all
                            if (egc.character.attack_timer <= 0) // when attacking we dont move
                                egc.character.setGamepos(egc.character.getGamepos().add(egc.character.getVelocity().scale(-1).scale(delta)));
                        });

                //Kevin, primary attack
                if(egc.controllerused){
                    if(rightcontroller.isButtonPressed(0)){
                        egc.character.primary().ifPresent(egc.projectiles::addAll);
                    }

                }else if (input.isKeyPressed(Input.KEY_F) || input.isMousePressed(0))
                    egc.character.primary().ifPresent(egc.projectiles::addAll);
            }

            //(Kevin) update all other entities
            egc.projectiles.stream().forEach(p -> p.update(delta));

            //Kevin, make  an array of characters because thats what the server would give to the method
            egc.enemies.stream().map(e -> e.update(delta, egs_characters,
                    //Kevin, may be cleaned up eventually
                    e.getClass() == ShadowArcher.class ? egc.grid.getranged_dir(e.getGamepos()) : egc.grid.get_dir(e.getGamepos()))
                    )
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(egc.projectiles::addAll);

            for (Enemy e: egc.enemies) { //If zombie enemy is attacking and collides with the character then decrease the character health by 4.
                if (e.getClass() == Zombie.class) {
                    if (e.attacking) {
                        if (e.collides(egc.character) != null) {
                            egc.character.health -= e.damage;
                            e.attacking = false;
                        }
                    }
                }
            }

            //check player attacks, must be after both player and character are updated
            Arrays.stream(egs_characters).filter(c -> !c.hit_in_this_attack && c.attack_timer > 0).forEach(c -> {
                for(var e : egc.enemies){
                    if(c.collides(e) != null){
                        e.setHealth(e.getHealth() - c.attack);
                        //player attack freeze is 500ms
                        e.attack_timer += 450;
                        c.hit_in_this_attack = true;
                        if(e.getHealth() <= 0)
                            c.gold += 5;
                        break;
                    }
                }
            });

            //Kevin, check if projectiles collide with enemies
            for (Projectile p : egc.projectiles){
                //Kevin, if projectile isnt sent by archer dont hit enemies
                if(p.sender.getClass() == Archer.class || p.sender.getClass() == Mage.class || p.sender.getClass() == Rogue.class) {
                    for (Enemy e : egc.enemies) {
                        if (p.collides(e) != null) {
                            e.setHealth(e.getHealth() - p.damage);
                            if(e.getHealth() <= 0) {
                                try {
                                    var c = (Character) p.sender;
                                    c.gold += 5;
                                } catch (ClassCastException ex){
                                    ex.printStackTrace();
                                }
                            }
                            p.setHit(true);
                            break; // each projectile should only collide with a single entity
                        }
                    }
                } else { // must be an enemy projectile
                    if(egc.character.collides(p) != null){
                        egc.character.health -= p.damage;
                        p.setHit(true);
                    }
                }

                bounce.common.level.Tile currentProjectileTile = egc.grid.getTile(p.getGamepos()); //Get the tile the projectile is at.
                if (currentProjectileTile.type == TileMap.TYPE.WALL) { //If the tile is a wall
                    if (p.collides(currentProjectileTile) != null) { //Remove projectile if it collides with wall.
                        p.setHit(true);
                    }
                }
            }


            //Kevin, update grid
            egc.grid.update(delta, egs_characters, egc.enemies.isEmpty()).ifPresent(egc.enemies::addAll);

            //Kevin, temp room open/close keys
            if(input.isKeyPressed(Input.KEY_O)){
                if( egc.grid.curRoom != null){
                    egc.grid.curRoom.completed = true;
                    egc.enemies.clear();
                } else {
                    System.out.println("no room");
                }
            }
            if(input.isKeyPressed(Input.KEY_P))
                egc.grid.rooms.forEach(r -> r.completed = false);


            //(Kevin) remove dead/hit/etc stuff
            egc.enemies.removeIf(e -> e.getHealth() <= 0);
            egc.projectiles.removeIf(Projectile::getHit);
        }
    }



    @Override
    public int getID() {
        return ExplorerGameClient.PLAYINGSTATE;
	}

}
