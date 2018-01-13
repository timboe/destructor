package timboe.destructor.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g3d.particles.influencers.ColorInfluencer;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import timboe.destructor.Param;
import timboe.destructor.Util;
import timboe.destructor.enums.BuildingType;
import timboe.destructor.enums.Particle;
import timboe.destructor.manager.GameState;
import timboe.destructor.manager.Textures;
import timboe.destructor.manager.World;

/**
 * Created by Tim on 10/01/2018.
 */

public class Warp extends Building {

  private float[] rotAngle = {0f, 90f, 0f, -90f};
  private final float[] rotV = {Param.WARP_ROTATE_SPEED, Param.WARP_ROTATE_SPEED, -Param.WARP_ROTATE_SPEED, -Param.WARP_ROTATE_SPEED};
  private final int pathingStartPointSeed = Util.R.nextInt();

  private final EnumMap<Particle, Tile> pathingStartPointWarp = new EnumMap<Particle, Tile>(Particle.class);


  public Warp(Tile t) {
    super(t, BuildingType.kWARP);
    setTexture( Textures.getInstance().getTexture("void", false), 0);
    setTexture( Textures.getInstance().getTexture("void", true), 1);
    setTexture( Textures.getInstance().getTexture("void", false), 2);
    setTexture( Textures.getInstance().getTexture("void", true), 3);
    moveBy(0, -Param.TILE_S/2);
    // UpdatePathingStartPoint is called later by World - we don't have a pathing grid yet!
  }

  @Override
  public void updatePathingStartPoint() {
    // We make our own random here so that the Warp's start points do not jump around on every building place
    Random R = new Random(pathingStartPointSeed);
    for (Particle p : Particle.values()) {
      int placeTry = 0;
      do {
        double rAngle = -Math.PI + (R.nextFloat() * Math.PI * 2);
        int tryX = (int) Math.round(coordinates.x + Param.WARP_SIZE/2 - 2 + ((Param.WARP_SIZE/2 + 1) * Math.cos(rAngle)));
        int tryY = (int) Math.round(coordinates.y + Param.WARP_SIZE/2 - 2 + ((Param.WARP_SIZE/2 + 1) * Math.sin(rAngle)));
        Tile tryTile = World.getInstance().getTile(tryX, tryY);
        if (tryTile.getNeighbours().size() == 0) continue; // Non-pathable
        boolean used = false;
        for (Particle p2 : Particle.values()) {
          if (p == p2) continue;
          if (pathingStartPointWarp.get(p2) == tryTile) used = true;
        }
        if (used) continue; // Another particle has this starting point
        pathingStartPointWarp.put(p, tryTile);
        break;
      } while (++placeTry < Param.N_PATCH_TRIES);
    }
  }

  @Override
  protected Tile getPathingStartPoint(Particle p) {
    return pathingStartPointWarp.get(p);
  }

  public boolean newParticles(int toPlace) {
    boolean placed = false;
    for (int tp = 0; tp < toPlace; ++tp) {
      // TODO introduce different modes e.g. all of one type
      Particle p = Particle.random();
      if (GameState.getInstance().warpEnergy > p.getCreateEnergy()) {
        placeParticle(p);
        GameState.getInstance().warpEnergy -= p.getCreateEnergy();
        placed = true;
      }
    }
    return placed;
  }

  @Override
  public void act(float delta) {
    super.act(delta);
    for (int i = 0; i < 4; ++i) {
      rotAngle[i] += delta * rotV[i];
    }
  }

  @Override
  public void draw(Batch batch, float alpha) {
    for (int i = 0; i < 4; ++i) {
      batch.draw(textureRegion[i],this.getX(),this.getY(),this.getOriginX(),this.getOriginY(),this.getWidth(),this.getHeight(),this.getScaleX(),this.getScaleY(),rotAngle[i]);
    }
  }

}
