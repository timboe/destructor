package timboe.destructor.entity;

import com.badlogic.gdx.scenes.scene2d.Actor;
import timboe.destructor.Param;
import timboe.destructor.enums.Colour;
import timboe.destructor.enums.TileType;

import static timboe.destructor.enums.Colour.kRED;

public class Tile extends Entity {

  public Colour colour;
  public TileType type;
  public int level;

  public Tile(int x, int y) {
    super(x, y);
    setType(TileType.kGROUND, kRED, 1);
  }

  public void setType(TileType t, Colour c, int l) {
    colour = c;
    type = t;
    level = l;
  }

}
