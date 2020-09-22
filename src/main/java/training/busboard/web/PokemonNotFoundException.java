package training.busboard.web;

public class PokemonNotFoundException extends Exception {
    public PokemonNotFoundException() {
        super("Pokemon not found");
    }
}
