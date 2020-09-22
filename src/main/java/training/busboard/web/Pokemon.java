package training.busboard.web;

import java.util.List;

public class Pokemon {
    private final String name;
    private final String pictureUrl;
    private final List<String> abilities;
    private final List<String> evolutions;

    public Pokemon(String name, String pictureUrl, List<String> abilities, List<String> evolutions) {
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

    public List<String> getEvolutions() {
        return evolutions;
    }
}
