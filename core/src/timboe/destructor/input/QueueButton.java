package timboe.destructor.input;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import timboe.destructor.Param;
import timboe.destructor.enums.BuildingType;
import timboe.destructor.enums.QueueType;
import timboe.destructor.manager.GameState;
import timboe.destructor.manager.UI;

/**
 * Created by Tim on 02/01/2018.
 */

public class QueueButton extends ChangeListener {
  @Override
  public void changed(ChangeEvent event, Actor actor) {
    GameState.getInstance().queueType = (QueueType)actor.getUserObject();
    BuildingType bt = UI.getInstance().buildingBeingPlaced;
    UI.getInstance().buildingWindowQSimple.get(bt).setChecked(GameState.getInstance().queueType == QueueType.kSIMPLE);
    UI.getInstance().buildingWindowQSpiral.get(bt).setChecked(GameState.getInstance().queueType == QueueType.kSPIRAL);
  }
}
