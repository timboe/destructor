package timboe.vev.enums;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import timboe.vev.Param;

/**
 * Created by Tim on 01/01/2018.
 */

public enum Particle {
  kH,
  kW,
  kZ,
  kE,
  kM,
  kQ;

  private static final List<Particle> values = Collections.unmodifiableList(Arrays.asList(values()));
  private static final int size = values.size();
  private static final Random R = new Random();

  public static Particle random() {
    return values.get(R.nextInt(size));
  }

  public String getString() {
    switch (this) {
      case kH: return "H";
      case kW: return "W";
      case kZ: return "Z";
      case kE: return "e";
      case kM: return "μ";
      case kQ: return "q";
      default: return "?";
    }
  }

  public static Particle getParticleFromColour(Colour c) {
    switch (c) {
      case kBLACK: return kH;
      case kRED_DARK: return kW;
      case kGREEN_DARK: return kZ;
      case kRED: return kE;
      case kGREEN: return kM;
      case kBLUE: return kQ;
      default: return null;
    }
  }

  public Color getHighlightColour() {
    switch (this) {
      case kH: return Param.PARTICLE_H;
      case kW: return Param.PARTICLE_W;
      case kZ: return Param.PARTICLE_Z;
      case kE: return Param.PARTICLE_E;
      case kM: return Param.PARTICLE_M;
      case kQ: return Param.PARTICLE_Q;
      default:
        Gdx.app.error("getHighlightColour","Unknown particle " + getString());
        return null;
    }
  }

  // Offset of chevrons so that they don't overlap
  public int getStandingOrderOffset() {
    switch (this) {
      case kH: return 0;
      case kW: return 2;
      case kZ: return 4;
      case kE: return 6;
      case kM: return 8;
      case kQ: return 10;
      default: return 0;
    }
  }

  public Colour getColourFromParticle() {
    switch (this) {
      case kH: return Colour.kBLACK;
      case kW: return Colour.kRED_DARK;
      case kZ: return Colour.kGREEN_DARK;
      case kE: return Colour.kRED;
      case kM: return Colour.kGREEN;
      case kQ: return Colour.kBLUE;
      default: return null;
    }
  }

  // TODO tweak
  public int getCreateEnergy() {
    switch (this) {
      case kH: return 125 + 150;
      case kW: return 80 + 20;
      case kZ: return 90 + 20;
      case kE: return 1 + 10;
      case kM: return 1 + 10;
      case kQ: return 1 + 5;
      default: return 0;
    }
  }

  public float getCreateChance() {
    switch (this) {
      case kH: return 0.05f;
      case kW: return 0.15f;
      case kZ: return 0.15f;
      case kE: return 0.2f;
      case kM: return 0.2f;
      case kQ: return 0.25f;
      default: return 0;
    }
  }

  public float getDisassembleTime() {
    switch (this) {
      case kH: return 8f;
      case kW: return 2.5f;
      case kZ: return 2.5f;
      case kE: return 1f;
      case kM: return 1f;
      case kQ: return 0.5f;
      default: return 0;
    }
  }

  public static String getStringFromColour(Colour c) {
    return getParticleFromColour(c).getString();
  }

}
