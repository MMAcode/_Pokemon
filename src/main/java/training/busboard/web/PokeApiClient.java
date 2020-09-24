package training.busboard.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

public class PokeApiClient {
    public static Pokemon getPokemon(String nameOrId) throws PokemonNotFoundException {
        JsonObject pokemonUnprocessedObject = makeRequest("https://pokeapi.co/api/v2/pokemon/" + nameOrId);

        if (pokemonUnprocessedObject == null) throw new PokemonNotFoundException();


        List<Evolution> evolutions = getPokemonImageUrlsFromSpeciesNames(pokemonUnprocessedObject);



        return new Pokemon(
                extractNameFromPokemon(pokemonUnprocessedObject),
                extractPictureUrlFromPokemon(pokemonUnprocessedObject),
                extractAbilitiesFromPokemon(pokemonUnprocessedObject),
                evolutions
        );
    }


//    private static ArrayList<String> getPokemonImageUrlsFromSpeciesNames(List<String> speciesNamesInEvolutionOrder, String ourPokemonId) {
//        ArrayList<String> pokemonImageUrls = new ArrayList<>();
//        for (int i = 0; i < speciesNamesInEvolutionOrder.size(); i++) {
//            JsonObject unprocessedSingleSpeciesObject = makeRequest("https://pokeapi.co/api/v2/pokemon-species/" + speciesNamesInEvolutionOrder.get(i));
//            String pokemonId = getPokemonIdFromUnprocessedSingleSpeciesObject(unprocessedSingleSpeciesObject, ourPokemonId);
//            pokemonImageUrls.add("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemonId + ".png");
//        }
//        return pokemonImageUrls;
//    }

    private static List<Evolution> getPokemonImageUrlsFromSpeciesNames(JsonObject pokemonUnprocessedObject) {

        JsonObject speciesUnprocessedObject = makeRequest(pokemonUnprocessedObject.get("species").getAsJsonObject().get("url").getAsString());
        JsonObject evolutionChainUnprocessedObject = makeRequest(speciesUnprocessedObject.get("evolution_chain").getAsJsonObject().get("url").getAsString());
        List<String> speciesNames = extractEvolutionsFromEvolutionsChain(evolutionChainUnprocessedObject);

        ArrayList<JsonObject> allSpecies = new ArrayList<>();
        speciesNames.forEach(name->allSpecies.add(makeRequest("https://pokeapi.co/api/v2/pokemon-species/" + name)));

        ArrayList<String> imageUrls = new ArrayList<>();
        String ourPokemonId = pokemonUnprocessedObject.get("id").getAsString();
        allSpecies.forEach(speciesObject-> {
            String pokemonId = getPokemonId(speciesObject, ourPokemonId);
            imageUrls.add("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemonId + ".png");
        });

        List<Evolution> evolutions = new ArrayList<>();
        for (int i = 0; i < imageUrls.size(); i++){
            evolutions.add(new Evolution(speciesNames.get(i),imageUrls.get(i)));
        }
        return evolutions;
    }


    private static String getPokemonId(JsonObject singleSpeciesObject, String ourPokemonId) {
        String firstPokemonId = "";
        JsonArray varieties = singleSpeciesObject.get("varieties").getAsJsonArray();

        for (int i = 0; i < varieties.size(); i++) {
            JsonObject variety = varieties.get(i).getAsJsonObject();
            String url = variety.getAsJsonObject().get("pokemon").getAsJsonObject().get("url").getAsString();
            String localPokemonIdToCheck = url.split("pokemon/")[1].split("/")[0];
            if (localPokemonIdToCheck.equals(ourPokemonId)) return ourPokemonId;
            if (i==0) firstPokemonId = localPokemonIdToCheck;
        }
        return firstPokemonId;
    }


    private static JsonObject makeRequest(String url) {
        try {
            return new JsonParser().parse(ClientBuilder
                    .newClient()
                    .target(url)
                    .request(MediaType.APPLICATION_JSON)
                    .get(String.class)
            ).getAsJsonObject();
        } catch (NotFoundException e) {
            return null;
        }
    }

    private static String extractNameFromPokemon(JsonObject data) {
        return data.get("name").getAsString();
    }

    private static String extractPictureUrlFromPokemon(JsonObject data) {
        return data.get("sprites").getAsJsonObject().get("front_default").getAsString();
    }

    private static List<String> extractAbilitiesFromPokemon(JsonObject data) {
        List<String> abilities = new ArrayList<>();
        data
                .get("abilities")
                .getAsJsonArray()
                .forEach(jsonElement -> abilities.add(jsonElement.getAsJsonObject().get("ability").getAsJsonObject().get("name").getAsString()));
        return abilities;
    }

    private static List<String> extractEvolutionsFromEvolutionsChain(JsonObject data) {
        List<String> evolutions = new ArrayList<>();

        JsonObject node = data.getAsJsonObject("chain");

        while (true) {
            evolutions.add(node.get("species").getAsJsonObject().get("name").getAsString());
            JsonArray evolvesTo = node.getAsJsonArray("evolves_to");
            if (evolvesTo.size() == 0) break;
            node = evolvesTo.get(0).getAsJsonObject();
        }

        return evolutions;
    }

}
