package timboe.destructor.entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Align;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import timboe.destructor.Param;
import timboe.destructor.enums.Cardinal;
import timboe.destructor.enums.Colour;
import timboe.destructor.enums.Particle;
import timboe.destructor.manager.Textures;

public class Entity extends Actor {

  public boolean mask;
  public Colour tileColour;
  public int level;
  protected int scale;
  public int x, y;
  protected int frames;
  protected int frame;
  protected float time;
  public boolean selected;
  public Rectangle boundingBox = new Rectangle();
  public boolean doTint = false;

  protected EnumMap<Particle, List<Tile>> buildingPathingLists;

  public List<Tile> getPathingList() {
    return pathingList;
  }

  protected List<Tile> pathingList; // Used by building and sprite
  protected Particle pathingParticle; // Used only by building


  public final TextureRegion[] textureRegion = new TextureRegion[Param.MAX_FRAMES];

  public Entity(int x, int y, int scale) {
    construct(x, y, scale);
  }

  public Entity(int x, int y) {
    construct(x ,y, Param.TILE_S);
  }

  private void construct(int x, int y, int scale) {
    this.scale = scale;
    this.x = x;
    this.y = y;
    this.frames = 1;
    this.frame = 0;
    this.time = 0;
    textureRegion[0] = null;
    selected = false;
    setBounds(x * scale, y * scale, scale, scale);
  }

  protected Entity() {
    textureRegion[0] = null;
  }

  public void setTexture(final String name, final int frames, boolean flipped) {
    for (int frame = 0; frame < frames; ++frame) {
      final String texName = name + (frames > 1 ? "_" + frame : "");
      TextureRegion r = Textures.getInstance().getTexture(texName, flipped);
      if (r == null) {
        Gdx.app.error("setTexture", "Texture error " + texName);
        r = Textures.getInstance().getTexture("missing3", false);
      }
      setTexture(r, frame);
    }
    this.frames = frames;
  }

  private void setTexture(TextureRegion r, int frame) {
    textureRegion[frame] = r;
    setWidth(textureRegion[frame].getRegionWidth());
    setHeight(textureRegion[frame].getRegionHeight());
    setOrigin(Align.center);
  }

  @Override
  public void draw(Batch batch, float alpha) {
    if (textureRegion[frame] == null) return;
    if (doTint) {
      batch.setColor(getColor());
      doDraw(batch);
      batch.setColor(1f,1f,1f,1f);
      doTint = false; // Only lasts one frame - needs to be re-set in act
    } else {
      doDraw(batch);
    }
//    draw(TextureRegion region, float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY, float rotation)
  }

  private void doDraw(Batch batch) {
    batch.draw(textureRegion[frame],this.getX(),this.getY(),this.getOriginX(),this.getOriginY(),this.getWidth(),this.getHeight(),this.getScaleX(),this.getScaleY(),this.getRotation());
  }

  public void drawSelected(ShapeRenderer sr) {
    if (!selected) return;
    float off = Param.FRAME * 0.25f / (float)Math.PI;
    final float xC = getX() + getWidth()/2f, yC = getY() + getHeight()/2f;
    for (float a = (float)-Math.PI; a < Math.PI; a += 2f*Math.PI/3f) {
      sr.rectLine(xC + getWidth() * ((float) Math.cos(a + off)),
          yC + getHeight() * ((float) Math.sin(a + off)),
          xC + getWidth()/2f * ((float) Math.cos(a + off + Math.PI / 6f)),
          yC + getHeight()/2f * ((float) Math.sin(a + off + Math.PI / 6f)),
          2);
    }
  }

  public void drawPath(ShapeRenderer sr) {
    if (!selected) return;
    drawList(pathingList, sr);
    if (buildingPathingLists == null) return;
    for (Particle p : Particle.values()) {
      if (!buildingPathingLists.containsKey(p)) continue;
      if (p == pathingParticle) continue; // we already drew this
      drawList(buildingPathingLists.get(p), sr);
    }
  }

  private void drawList(List<Tile> l, ShapeRenderer sr) {
    if (l == null || l.size() == 0) return;
    final int off = Param.FRAME / 2 % Param.TILE_S;
    Tile fin = l.get( l.size() - 1 );
    for (int i = 0; i < l.size(); ++i) {
      Tile previous = null;
      Tile current = l.get(i);
      if (i == 0) {
        for (Cardinal D : Cardinal.n8) {
          if (current.n8.get(D).mySprite == this) {
            previous = current.n8.get(D);
            break;
          }
        }
      } else {
        previous = l.get(i - 1);
      }
      if (previous == null) continue;
      sr.line(previous.centreScaleTile.x, previous.centreScaleTile.y,
          current.centreScaleTile.x, current.centreScaleTile.y);
      if (current == fin) break;
      if (current == previous.n8.get(Cardinal.kN)) {
        sr.line(previous.centreScaleTile.x, previous.centreScaleTile.y + off,
            previous.centreScaleTile.x + 5, previous.centreScaleTile.y - 5 + off);
        sr.line(previous.centreScaleTile.x, previous.centreScaleTile.y + off,
            previous.centreScaleTile.x - 5, previous.centreScaleTile.y - 5 + off);
      } else if (current == previous.n8.get(Cardinal.kS)) {
        sr.line(previous.centreScaleTile.x, previous.centreScaleTile.y - off,
            previous.centreScaleTile.x + 5, previous.centreScaleTile.y + 5 - off);
        sr.line(previous.centreScaleTile.x, previous.centreScaleTile.y - off,
            previous.centreScaleTile.x - 5, previous.centreScaleTile.y + 5 - off);
      } else if (current == previous.n8.get(Cardinal.kE)) {
        sr.line(previous.centreScaleTile.x + off, previous.centreScaleTile.y,
            previous.centreScaleTile.x - 5 + off, previous.centreScaleTile.y - 5);
        sr.line(previous.centreScaleTile.x + off, previous.centreScaleTile.y,
            previous.centreScaleTile.x - 5 + off, previous.centreScaleTile.y + 5);
      } else if (current == previous.n8.get(Cardinal.kW)) {
        sr.line(previous.centreScaleTile.x - off, previous.centreScaleTile.y,
            previous.centreScaleTile.x + 5 - off, previous.centreScaleTile.y - 5);
        sr.line(previous.centreScaleTile.x - off, previous.centreScaleTile.y,
            previous.centreScaleTile.x + 5 - off, previous.centreScaleTile.y + 5);
      } else if (current == previous.n8.get(Cardinal.kNE)) {
        sr.line(previous.centreScaleTile.x + off, previous.centreScaleTile.y + off,
            previous.centreScaleTile.x + off - 7, previous.centreScaleTile.y + off);
        sr.line(previous.centreScaleTile.x + off, previous.centreScaleTile.y + off,
            previous.centreScaleTile.x + off, previous.centreScaleTile.y + off - 7);
      } else if (current == previous.n8.get(Cardinal.kSW)) {
        sr.line(previous.centreScaleTile.x - off, previous.centreScaleTile.y - off,
            previous.centreScaleTile.x - off + 7, previous.centreScaleTile.y - off);
        sr.line(previous.centreScaleTile.x - off, previous.centreScaleTile.y - off,
            previous.centreScaleTile.x - off, previous.centreScaleTile.y - off + 7);
      } else if (current == previous.n8.get(Cardinal.kNW)) {
        sr.line(previous.centreScaleTile.x - off, previous.centreScaleTile.y + off,
            previous.centreScaleTile.x - off, previous.centreScaleTile.y + off - 7);
        sr.line(previous.centreScaleTile.x - off, previous.centreScaleTile.y + off,
            previous.centreScaleTile.x - off + 7, previous.centreScaleTile.y + off);
      } else if (current == previous.n8.get(Cardinal.kSE)) {
        sr.line(previous.centreScaleTile.x + off, previous.centreScaleTile.y - off,
            previous.centreScaleTile.x + off, previous.centreScaleTile.y - off + 7);
        sr.line(previous.centreScaleTile.x + off, previous.centreScaleTile.y - off,
            previous.centreScaleTile.x + off - 7, previous.centreScaleTile.y - off);
      }
    }
    sr.rect(fin.getX(), fin.getY(), fin.getOriginX(), fin.getOriginY(),
        fin.getWidth(), fin.getHeight(), 1f, 1f, 45f);
  }
}
