package timboe.destructor.pathfinding;

import com.badlogic.gdx.Gdx;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import sun.awt.geom.AreaOp;
import timboe.destructor.Pair;
import timboe.destructor.Param;
import timboe.destructor.Util;
import timboe.destructor.entity.Building;
import timboe.destructor.entity.Sprite;
import timboe.destructor.entity.Tile;
import timboe.destructor.enums.Cardinal;
import timboe.destructor.enums.TileType;
import timboe.destructor.manager.GameState;
import timboe.destructor.manager.World;

/**
 * Created by Tim on 28/12/2017.
 */

public class OrderlyQueue {
  final Tile queueStart;
  private List<Tile> queue = new LinkedList<Tile>();
  final Building myBuilding;

  public OrderlyQueue(int x, int y, List<Tile> customQueue, Building b) {
    myBuilding = b;
    if (customQueue == null) doQueue(x, y);
    else queue = customQueue;
    queueStart = queue.get(0);
    repath();
  }


  public List<Tile> getQueue() {
    return queue;
  }


  public Tile getQueuePathingTarget() {
    return queue.get( queue.size()-1 );
  }

  public void moveAlongMoveAlong() {
    // Now try and move everyone along
    for (int i = 0; i < queue.size(); ++i) {
      final Tile tile = queue.get(i);
      if (tile == null) Gdx.app.error("WTF?!?!","");
      Sprite toRemove = null;
      for (Sprite s : tile.containedSprites) {
        // Get parking space
        final Cardinal parking = tile.parkingSpaces.get(s);
        if (parking == null) continue; // I'm not partked here, e.g. moving over the entrance tile

        if (parking == tile.queueExit) { // Is this the final space of the tile?
          if (i == 0) { // Is this the final tile?
            // Is the sprite *actually here*
            if (myBuilding.spriteProcessing == null && s.nudgeDestination.isZero()) { // Arrived
              if (toRemove != null) Gdx.app.error("moveAlongMoveAlong", "should only ever be one toRemove");
              // Goodby - this particle is now DEAD
              toRemove = s; // from its tile
              GameState.getInstance().killSprite(s); // from the game manager
              myBuilding.processSprite(s); // Now the last ref to the sprite is held only by the building
            }
          } else { // Not the final tile.
            // Is the entrance of the next tile free?
            Tile nextTile = queue.get(i - 1);
            Cardinal nextTileEntrance = nextTile.queueExit.next90( nextTile.queueClockwise );
            if (!nextTile.parkingSpaces.containsValue(nextTileEntrance)) {
              // Move me to here
              if (toRemove != null) Gdx.app.error("moveAlongMoveAlong", "should only ever be one toRemove");
              toRemove = s;
              // Move on to next tile
              nextTile.parkSprite(s, nextTileEntrance);
            }
          }
        } else { // This is *NOT* the final space on this tile, can we move on?
          Cardinal nextParking = parking.next90( tile.queueClockwise );
          if (!tile.parkingSpaces.containsValue(nextParking)) {
            // Move me to here
            tile.parkSprite(s, nextParking);
          }
        }
      }
      // Can only remove reference from the tile at the end
      if (toRemove != null) tile.deRegSprite(toRemove);
    }
  }

  // New sprite is trying to enter the queue
  public Pair<Tile, Cardinal> getFreeLocationInQueue() {
    // Back iterate over the queue
    Tile previousT = null;
    Cardinal previousD = null;
    ListIterator<Tile> liTile = queue.listIterator( queue.size() );

    while(liTile.hasPrevious()) {
      final Tile t = liTile.previous();
      final Cardinal queueStart = t.queueExit.next90( t.queueClockwise );
      Cardinal D = queueStart;
      do {
        if (t.parkingSpaces.containsValue(D)) { // Someone is here - go for the previous place
          Gdx.app.debug("getFreeLocationInQueue","Accepted sprite to "+t.coordinates+" "+D.getString());
          if (previousT != null) return new Pair<Tile, Cardinal>(previousT, previousD);
          return null;
        } else { // We can put the sprite here! Make a note
          previousT = t;
          previousD = D;
        }
        D = D.next90( t.queueClockwise ); // Not as we are reverse iterating
      } while (D != queueStart);
    }

    // Check final slot
    Tile queueFinal = queue.get(0);
    if (!queueFinal.parkingSpaces.containsValue(queueFinal.queueExit)) {
      return new Pair<Tile,Cardinal>(queueFinal, queueFinal.queueExit);
    }

    // If we made it to the end - we can also place in the first slot
    if (previousT != null) return new Pair<Tile, Cardinal>(previousT, previousD);
    return null;
  }

    // Moves on any sprites under the queue
  public void moveOn() {
    for (Tile t : queue) {
      t.moveOnSprites();
    }
  }

  // Re-do the pathing grid for all tiles in the queue and all touching it
  private void repath() {
    for (Tile t : queue) {
      for (Cardinal D : Cardinal.n8) {
        World.getInstance().updateTilePathfinding(t.n8.get(D));
      }
    }
  }

  public static void hintQueue(final Tile start) {
    switch (GameState.getInstance().queueType) {
      case kSIMPLE: hintSimpleQueue(start); break;
      case kSPIRAL: hintSpiralQueue(start); break;
      default: Gdx.app.error("hintQueue","Unknown - " + GameState.getInstance().queueType);
    }
  }

  private static void hintSpiralQueue(final Tile start) {
    Tile t = start;
    int step = 0, move = 3, toAdd =3;
    boolean inc = true;
    Cardinal D = Cardinal.kE;
    while (step++ < GameState.getInstance().queueSize) {
      if (!t.buildable()) return;
      t.setHighlightColour(Param.HIGHLIGHT_YELLOW);
      t = t.n8.get(D);
      if (--move == 0) {
        if (inc) ++toAdd;
        inc = !inc;
        move += toAdd;
        D = D.next90(false);
      }
    }
  }

  private static void hintSimpleQueue(final Tile start) {
    int step = 0, x = start.coordinates.x, y = start.coordinates.y, move = 3;
    World w = World.getInstance();
    while (step++ < GameState.getInstance().queueSize) {
      if (!Util.inBounds(x,y,false) || !w.getTile(x,y).buildable()) return;
      w.getTile(x,y).setHighlightColour(Param.HIGHLIGHT_YELLOW);
      if (Math.abs(move) > 1) {
        x += 1 * Math.signum(move);
        move -= 1 * Math.signum(move);
      } else {
        --y;
        move *= -3;
      }
    }
  }

  public void doQueue(int xStart, int yStart) {
    switch (GameState.getInstance().queueType) {
      case kSIMPLE: doSimpleQueue(xStart, yStart); break;
      case kSPIRAL: doSpiralQueue(xStart, yStart); break;
      default: Gdx.app.error("hintQueue","Unknown - " + GameState.getInstance().queueType);
    }
    queue.get( queue.size()-1 ).type = TileType.kGROUND; // Re-set to ground to make pathable
  }

  private void doSpiralQueue(int xStart, int yStart) {
    Tile t = World.getInstance().getTile(xStart, yStart);
    int step = 0, move = 3, toAdd =3;
    boolean inc = true;
    Cardinal D = Cardinal.kE, previousD = Cardinal.kN;
    while (step++ < GameState.getInstance().queueSize) {
      if (!t.buildable()) return;
      Cardinal from, to;
      if (D == previousD) {
        from = D;
        to = D.next90(true).next90(true); // 180deg
      } else { // We just turned a corner
        from = D;
        to = D.next90(false);
      }
      Cardinal exit = getExitLocation(to);
      boolean isClockwise = getQueueClockwise(from, to);
      t.setQueue(from, to, myBuilding, exit, isClockwise);
      queue.add(t);

      t = t.n8.get(D);
      previousD = D;
      if (--move == 0) {
        if (inc) ++toAdd;
        inc = !inc;
        move += toAdd;
        D = D.next90(false);
      }
    }
  }


  private void doSimpleQueue(int xStart, int yStart) {
    int step = 0, x = xStart, y = yStart, move = 3, element = 0;
    World w = World.getInstance();
    Vector<Pair<Cardinal, Cardinal>> v = new Vector<Pair<Cardinal, Cardinal>>();
    v.add(new Pair<Cardinal, Cardinal>(Cardinal.kE, Cardinal.kN));
    v.add(new Pair<Cardinal, Cardinal>(Cardinal.kE, Cardinal.kW));
    v.add(new Pair<Cardinal, Cardinal>(Cardinal.kS, Cardinal.kW));
    v.add(new Pair<Cardinal, Cardinal>(Cardinal.kW, Cardinal.kN));
    v.add(new Pair<Cardinal, Cardinal>(Cardinal.kW, Cardinal.kE));
    v.add(new Pair<Cardinal, Cardinal>(Cardinal.kS, Cardinal.kE));
    while (step++ < GameState.getInstance().queueSize) {
      Cardinal D = getExitLocation(v.get(element).getValue());
      boolean isClockwise = getQueueClockwise(v.get(element).getKey(), v.get(element).getValue());
      Tile t = w.getTile(x, y);
      if (!t.buildable()) break;
      t.setQueue(v.get(element).getKey(), v.get(element).getValue(), myBuilding, D, isClockwise);
      queue.add( w.getTile(x, y) );
      if (++element == v.size()) element = 0;
      if (Math.abs(move) > 1) {
        x += 1 * Math.signum(move);
        move -= 1 * Math.signum(move);
      } else {
        --y;
        move *= -3;
      }
    }
  }

  // TODO figure out the pattern!
  private boolean getQueueClockwise(Cardinal from, Cardinal to) {
    if      (from == Cardinal.kE && to == Cardinal.kN) return true;
    // E -> S false
    // E -> W false

    else if (from == Cardinal.kW && to == Cardinal.kS) return true;
    // W -> N fase
    // W -> E false

    else if (from == Cardinal.kS && to == Cardinal.kE) return true;
    // S -> W false
    else if (from == Cardinal.kS && to == Cardinal.kN) return true;

    else if (from == Cardinal.kN && to == Cardinal.kW) return true;
    // N -> E false
    else if (from == Cardinal.kN && to == Cardinal.kS) return true;

    return false;
  }

  private Cardinal getExitLocation(Cardinal to) {
    if (to == Cardinal.kN || to == Cardinal.kE) return Cardinal.kNE;
    return Cardinal.kSW;
  }
}
