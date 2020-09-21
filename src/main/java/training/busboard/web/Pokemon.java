package training.busboard.web;

import java.util.List;

public class Pokemon {
  private final String name;
  private final String pictureUrl;
  private final List<String> abilities;
  private final List<Evolution> evolutions;

  public Pokemon(String name, String pictureUrl, List<String> abilities, List<Evolution> evolutions) {
    this.name = name;
    this.pictureUrl = pictureUrl;
    this.abilities = abilities;
    this.evolutions = evolutions;
  }

  public String getName() {
    return name;
  }

  public String getPictureUrl() {
    return pictureUrl;
  }

  public List<String> getAbilities() {
    return abilities;
  }

  public List<Evolution> getEvolutions() {
    return evolutions;
  }
}
