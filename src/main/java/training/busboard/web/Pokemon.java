package training.busboard.web;

import java.util.List;

public class Pokemon {
    private final String name;
    private final String pictureUrl;
    private final List<String> abilities;
    private final List<String> evolutions;
    private final List<String> pokemonIds;
    private final String[][] evolutions2;


    public Pokemon(String name, String pictureUrl, List<String> abilities, List<String> evolutions,List<String> pokemonIds) {
        this.name = name;
        this.pictureUrl = pictureUrl;
        this.abilities = abilities;
        this.evolutions = evolutions;
        this.pokemonIds = pokemonIds;

        this.evolutions2 = new String[evolutions.size()][];
        for (int i = 0; i<evolutions.size(); i++) {
            this.evolutions2[i]=new String[]{evolutions.get(i),pokemonIds.get(i)};
        }
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

    public List<String> getPokemonIds() { return pokemonIds; }

    public String[][] getEvolutions2() {
        return evolutions2;
    }
}
