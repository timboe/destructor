package timboe.vev.input;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import timboe.vev.enums.BuildingType;
import timboe.vev.manager.GameState;
import timboe.vev.manager.Sounds;
import timboe.vev.manager.UI;

/**
 * Created by Tim on 02/01/2018.
 */

public class QueueLengthSlider extends ChangeListener {
  @Override
  public void changed(ChangeEvent event, Actor actor) {
    GameState.getInstance().queueSize = (int) ((Slider)actor).getValue();
    BuildingType bt = GameState.getInstance().buildingBeingPlaced;
    UI.getInstance().buildingWindowQSize.get(bt).setText(""+ GameState.getInstance().queueSize);
    UI.getInstance().buildingWindowQPrice.get(bt).setText( UI.getInstance().formatter.format( GameState.getInstance().buildingPrices.get(bt) ) );
    Sounds.getInstance().click();
  }
}
