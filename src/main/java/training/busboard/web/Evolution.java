package training.busboard.web;

public final class Evolution {
  private final String name;
  private final String pictureUrl;

  public Evolution(String name, String pictureUrl) {
    this.name = name;
    this.pictureUrl = pictureUrl;
  }

  public String getName() {
    return name;
  }

  public String getPictureUrl() {
    return pictureUrl;
  }
}