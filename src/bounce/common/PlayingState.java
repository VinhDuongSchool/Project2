package bounce.common;

import bounce.client.ExplorerGameClient;
import jig.Vector;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


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
public class PlayingState extends BasicGameState {


	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		container.setSoundOn(true);
	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
        ExplorerGameClient egc = (ExplorerGameClient)game;


        var s1 = egc.game_sprites.getSprite(10, 4);
        var s2 = egc.game_sprites.getSprite(2, 4);
        var s3 = egc.game_sprites.getSprite(0, 2);

        Vector cords;
        for (float x = 0; x<10; x++){
            for (float y = 0; y<5; y++){
                cords =  lib.to_screen(x*32,y*32, new Vector(egc.screenox, egc.screenoy));
                g.drawImage(s1,cords.getX(), cords.getY());
            }
        }

        cords =  lib.to_screen(0,0, new Vector(egc.screenox, egc.screenoy));
        g.drawImage(s3, cords.getX(), cords.getY());

        cords =  lib.to_screen(0,32, new Vector(egc.screenox, egc.screenoy));
        g.drawImage(s2, cords.getX(), cords.getY());

        egc.character.render(g); //Render the character onto the screen.

	}

	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
        Input input = container.getInput();
        ExplorerGameClient egc = (ExplorerGameClient) game;

        if (egc.is_connected){

        } else {
            if (input.isKeyDown(Input.KEY_UP)){
                egc.screenoy += 5;
            }
            if (input.isKeyDown(Input.KEY_DOWN)){
                egc.screenoy -= 5;
            }
            if (input.isKeyDown(Input.KEY_LEFT)){
                egc.screenox += 5;
            }
            if (input.isKeyDown(Input.KEY_RIGHT)){
                egc.screenox -= 5;
            }


            if (input.isKeyDown(Input.KEY_W)){ //Move the player in the direction of the key pressed.
                egc.character.setVelocity(new Vector(0f,-0.1f));
            } else if (input.isKeyDown(Input.KEY_A)){
                egc.character.setVelocity(new Vector(-0.1f,0f));
            } else if (input.isKeyDown(Input.KEY_S)){
                egc.character.setVelocity(new Vector(0f,0.1f));
            } else if (input.isKeyDown(Input.KEY_D)){
                egc.character.setVelocity(new Vector(0.1f,0f));
            } else {
                egc.character.setVelocity(new Vector(0f,0f));
            }

            egc.character.update(delta); //Update the position of the player
        }

	}

	@Override
	public int getID() {
		return ExplorerGameClient.PLAYINGSTATE;
	}

}
