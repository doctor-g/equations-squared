package edu.bsu.pvgestwicki.etschallenge.android;

import playn.android.GameActivity;
import playn.core.PlayN;

import edu.bsu.pvgestwicki.etschallenge.core.AlgebraGame;

public class AlgebraGameActivity extends GameActivity {

  @Override
  public void main(){
    platform().assets().setPathPrefix("edu/bsu/pvgestwicki/etschallenge/resources");
    PlayN.run(new AlgebraGame());
  }
}
