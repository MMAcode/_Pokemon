package training.busboard.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PokeApiClient {
    public static Pokemon getPokemon(String nameOrId) throws PokemonNotFoundException {
        JsonObject pokemonUnprocessedObject = makeRequest("https://pokeapi.co/api/v2/pokemon/" + nameOrId);

        if (pokemonUnprocessedObject == null) throw new PokemonNotFoundException();
        JsonObject speciesUnprocessedObject = makeRequest(pokemonUnprocessedObject.get("species").getAsJsonObject().get("url").getAsString());
        JsonObject evolutionChainUnprocessedObject = makeRequest(speciesUnprocessedObject.get("evolution_chain").getAsJsonObject().get("url").getAsString());

        List<String> speciesNamesInEvolutionOrder = extractEvolutionsFromEvolutionsChain(evolutionChainUnprocessedObject);
        ///////Miro Code
        //naming
        //pokemon:id, name, species(1), evolutionChain(1)id;
        //evolutions: gets speciesIds
        //species : get pokemonIds
        String ourPokemonId = pokemonUnprocessedObject.get("id").getAsString();

        return new Pokemon(
                extractNameFromPokemon(pokemonUnprocessedObject),
                extractPictureUrlFromPokemon(pokemonUnprocessedObject),
                extractAbilitiesFromPokemon(pokemonUnprocessedObject),
                extractEvolutionsFromEvolutionsChain(evolutionChainUnprocessedObject),
                getPokemonIdsFromSpeciesNames(speciesNamesInEvolutionOrder, ourPokemonId)
        );
    }

    private static ArrayList<String> getPokemonIdsFromSpeciesNames(List<String> speciesNamesInEvolutionOrder, String ourPokemonId) {
        ArrayList<String> pokemonsIds = new ArrayList<>();
        for (int i = 0; i < speciesNamesInEvolutionOrder.size(); i++) {
            JsonObject unprocessedSingleSpeciesObject = makeRequest("https://pokeapi.co/api/v2/pokemon-species/" + speciesNamesInEvolutionOrder.get(i));
            String pokemonId = getPokemonIdFromUnprocessedSingleSpeciesObject(unprocessedSingleSpeciesObject, ourPokemonId);
            String imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokemonId + ".png";
            //pokemonsIds.add(pokemonId);
            pokemonsIds.add(imageUrl);
        }
        return pokemonsIds;
    }

    private static String getPokemonIdFromUnprocessedSingleSpeciesObject(JsonObject singleSpeciesObject, String ourPokemonId) {
        String pokemonIdToReturn = "";
        String firstPokemonId = "";
        JsonArray varieties = singleSpeciesObject.get("varieties").getAsJsonArray();

        //try to find pokemon with our id. If found, add it to local pokemonId variable;
        for (int i = 0; i < varieties.size(); i++) {
            JsonObject variety = varieties.get(i).getAsJsonObject();
            String url = variety.getAsJsonObject().get("pokemon").getAsJsonObject().get("url").getAsString();
            String localPokemonIdToCheck = url.split("pokemon/")[1].split("/")[0];
            if (localPokemonIdToCheck.equals(ourPokemonId)) return ourPokemonId;
            if (i==0) firstPokemonId = localPokemonIdToCheck;
        }
        //if our pokemon was not within this single species (=this evolution stage)...
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
