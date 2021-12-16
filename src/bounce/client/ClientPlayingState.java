package bounce.client;

import bounce.common.Character;
import bounce.common.*;
import bounce.common.items.BaseItem;
import bounce.common.items.PileOfGold;
import bounce.common.items.Potion;
import bounce.common.level.Door;
import bounce.common.level.TileMap;
import jig.Vector;
import org.newdawn.slick.*;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


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

	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
        ExplorerGameClient egc = (ExplorerGameClient)game;

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

        egc.items.add(new PileOfGold(244,109)); //Add the potions
        egc.items.add(new PileOfGold(348,394));
        egc.items.add(new Potion(415,461));
        egc.items.add(new Potion(81,265));
//        egc.character.setGamepos(new Vector(32*6, 32*34));


	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
        ExplorerGameClient egc = (ExplorerGameClient)game;


        var screen_offset = lib.to_screen(egc.character.getGamepos().scale(-1), egc.screen_center);
        egc.character.setPosition(egc.screen_center);
        egc.screenox = screen_offset.getX();
        egc.screenoy = screen_offset.getY();

        egc.grid.render(g,screen_offset, egc.character.getGamepos());
        g.setColor(Color.white);
        g.drawString("Gold: " + egc.gold, 10, 50);
        g.drawString("Health: " + egc.character.health, 10, 70);
        g.setColor(Color.gray);






        // testing stuff
//        var v = lib.to_screen(egc.character.gamepos, new Vector(egc.screenox, egc.screenoy));
//        g.drawLine(0,0, v.getX(), v.getY());
//        draw game pos on screen
//        g.setColor(Color.blue);
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
        //System.out.println(egc.character.gamepos);

	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
        Input input = container.getInput();
        ExplorerGameClient egc = (ExplorerGameClient) game;

        if(!egc.is_connected)
            egc.grid.MakePath(new ArrayList<Vector>( List.of(egc.character.getGamepos())));


        //(Kevin) deal with user input
        Vector characterVector = new Vector(0,0);
        var inp = List.of( new Boolean[]{input.isKeyDown(Input.KEY_W), input.isKeyDown(Input.KEY_A), input.isKeyDown(Input.KEY_S), input.isKeyDown(Input.KEY_D)});
        var characterDir = lib.wasd_to_dir(inp);

        if (characterDir != null)
            characterVector = lib.dir_enum_to_unit_vector(characterDir).scale(0.3f);

        var mousePos = new Vector(input.getMouseX(), input.getMouseY());


//        System.out.println(lib.dir_from_point_to_point(mousePos, egc.screen_center));

        //Kevin, m is mouse cords on screen, character is always in the sceen center,
        //angleto gives the angle in degrees rotated by 180 for some reason,
        //divide by 45 to convert into 8 directions, then round to get the angle index,
        int diridx = (int)Math.round((mousePos.angleTo(egc.screen_center)+180)/45);


//        Kevin, commented out until its used for something
//        for(Enemy e : egc.enemies){
//            if(egc.character.collides(e)!= null){
//                System.out.println("character collided with an enemy");
//            }
//        }



        if(egc.is_connected){ //Kevin, run with a server
            var messages = new ArrayList<Message>();
            if (!egc.character.getVelocity().equals(characterVector)){
                messages.add(new Message(Message.MSG_TYPE.SET_VELOCITY, characterVector, egc.ID));
                egc.character.setVelocity(characterVector);
            }
            if(egc.character.curdir != characterDir){
                messages.add(Message.builder(Message.MSG_TYPE.SET_DIR, egc.ID).setEtype(Message.ENTITY_TYPE.CHARACTER));
                egc.character.curdir = characterDir;
            }


            if(input.isKeyPressed(Input.KEY_F))
                messages.add(Message.builder(Message.MSG_TYPE.FIRE_PROJECTILE, egc.ID));

            if(diridx >= 0)
                messages.add(Message.builder(Message.MSG_TYPE.MOUSE_IDX, egc.ID));

            messages.stream().forEach(m -> {
                try {
                    egc.out_stream.writeObject(m);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            for(var m = egc.in_messages.poll(); m != null; m = egc.in_messages.poll()){
                egc.handle_message(m);
            }

        } else {
            //Kevin, character array to imitate server so its easier to copy functionality
            var egs_characters = new Character[]{egc.character};

            egc.items.stream().filter(i -> egc.character.collides(i) != null).findAny().ifPresent(item -> {
                //Kevin, for when items have more complex function we need to cast them
                if(item instanceof PileOfGold){
                    var ci = (PileOfGold) item;
                    egc.gold += 50;
                } else if (item instanceof Potion){
                    var ci = (Potion) item;
                    egc.character.health += 50;
                } else {
                    throw new IllegalArgumentException("unknown item");
                }
                egc.items.remove(item);
            });

            //(Kevin) handle stuff when client isnt connected
            egc.character.curdir = characterDir;
            egc.character.setVelocity(characterVector);
            egc.character.update(delta); //Update the position of the player

            //Kevin, check collision with the 8 neighbor tiles of the character and undo their movement if there is a collision
            egc.grid.getNeighbors(egc.character.getGamepos()).stream()
                    .filter(t -> t.type == TileMap.TYPE.WALL || (t.type == TileMap.TYPE.DOOR && !((Door)t).is_open)) //hmmmmmmmmmmmmmm
                    .map(egc.character::collides) // stream of collisions that may be null
                    .filter(Objects::nonNull)
                    .findAny().ifPresent(c -> { // the actual collision object isnt useful, the minpentration doesnt work at all
                        egc.character.setVelocity(egc.character.getVelocity().scale(-1));
                        egc.character.update(delta);
                        egc.character.setVelocity(egc.character.getVelocity().scale(-1));
                    });

            //Kevin, primary attack
            if(input.isKeyPressed(Input.KEY_F) || input.isMousePressed(0))
                egc.character.primary(diridx).ifPresent(egc.projectiles::addAll);

            //(Kevin) update all other entities
            egc.projectiles.stream().forEach(p -> p.update(delta));

            //Kevin, make  an array of characters because thats what the server would give to the method
            egc.enemies.stream().map(e -> e.update(delta, egs_characters,
                    //Kevin, may be cleaned up eventually
                    e.getClass() == ShadowArcher.class ? egc.grid.getranged_dir(e.getGamepos()) : egc.grid.get_dir(e.getGamepos())))

                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(egc.projectiles::addAll);

            //Kevin, check if projectiles collide with enemies
            for (Projectile p : egc.projectiles){
                //Kevin, if projectile isnt sent by archer dont hit enemies
                if(p.sender.getClass() != Archer.class)
                    continue;

                for (Enemy e : egc.enemies) {
                    if (p.collides(e) != null){
                        e.setHealth(e.getHealth() - p.damage);
                        p.setHit(true);
                        break; // each projectile should only collide with a single entity
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
