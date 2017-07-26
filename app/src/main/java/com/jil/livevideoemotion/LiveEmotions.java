package com.jil.livevideoemotion;

/**
 * Created by jil on 26/7/17.
 */

public class LiveEmotions {

  private Emotions emoticons;
  private int xCordinate;
  private int yCordinate;

  public LiveEmotions(Emotions emoticons, int xCordinate, int yCordinate) {
    this.emoticons = emoticons;
    this.xCordinate = xCordinate;
    this.yCordinate = yCordinate;
  }

  public Emotions getEmoticons() {
    return emoticons;
  }

  public void setEmoticons(Emotions emoticons) {
    this.emoticons = emoticons;
  }

  public int getxCordinate() {
    return xCordinate;
  }

  public void setxCordinate(int xCordinate) {
    this.xCordinate = xCordinate;
  }

  public int getyCordinate() {
    return yCordinate;
  }

  public void setyCordinate(int yCordinate) {
    this.yCordinate = yCordinate;
  }
}
