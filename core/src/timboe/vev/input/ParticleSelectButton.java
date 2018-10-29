package timboe.vev.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import timboe.vev.enums.Particle;
import timboe.vev.manager.GameState;

/**
 * Created by Tim on 13/01/2018.
 */

public class ParticleSelectButton extends ChangeListener {
  @Override
  public void changed(ChangeEvent event, Actor actor) {
    Particle p = (Particle) actor.getUserObject();
    boolean invert = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
    GameState.getInstance().reduceSelectedSet(p, invert);
  }
}