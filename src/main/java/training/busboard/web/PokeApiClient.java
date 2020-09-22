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
        JsonObject pokemonData = makeRequest("https://pokeapi.co/api/v2/pokemon/" + nameOrId);

        if (pokemonData == null) throw new PokemonNotFoundException();

        JsonObject speciesData = makeRequest(pokemonData.get("species").getAsJsonObject().get("url").getAsString());
        JsonObject evolutionChainData = makeRequest(speciesData.get("evolution_chain").getAsJsonObject().get("url").getAsString());

        List<String> evolutions = extractEvolutionsFromEvolutionsChain(evolutionChainData);

        // get pokemonIds from pokemon species (=our pokemon evolutions String array)
                //for each species name(=evolution), get pokemon id from there
                    //use species name to request species object
                    //from object, extract pokemon id (and handle special case of name matching requested name)
        //use ids to get pokemon images

        String pokemonId = extractPokemonIdsFromSinglePokemonSpeciesObject(JsonObject singleSpeciesOb);

        for (String evolution : evolutions){
            JsonObject data = makeRequest("https://pokeapi.co/api/v2/pokemon-species/" + evolution);

            JsonArray varieties = speciesData.get("varieties").getAsJsonArray();
            int evolutionsSize = evolutions.size();
            varieties.forEach(variety -> {
                if (variety.getAsJsonObject().get("name").getAsString().equals(name)){
                    String url = variety.getAsJsonObject().get("url").getAsString();
                    evolutions.add(url.split("pokemon/")[url.split("pokemon/").length-1]);
                }
            });
            if (evolutions.size() == evolutionsSize){
                String url = varieties.get(0).getAsJsonObject().get("url").getAsString();
                evolutions.add(url.split("pokemon/")[url.split("pokemon/").length-1]);
            }
        }


        return new Pokemon(
                extractNameFromPokemon(pokemonData),
                extractPictureUrlFromPokemon(pokemonData),
                extractAbilitiesFromPokemon(pokemonData),
                extractEvolutionsFromEvolutionsChain(evolutionChainData)
        );
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

//    private static List<String> extractPokemonIdsFromSpecies(JsonObject data, String name) {
//        List<String> evolutions = new ArrayList<>();
//
//        JsonObject chain = data.getAsJsonObject("chain");
//        while (true) {
//            String speciesUrl = chain.get("species").getAsJsonObject().get("url").getAsString();
//            JsonObject speciesData = makeRequest(speciesUrl);
//            JsonArray varieties = speciesData.get("varieties").getAsJsonArray();
//            int evolutionsSize = evolutions.size();
//            varieties.forEach(variety -> {
//                if (variety.getAsJsonObject().get("name").getAsString().equals(name)){
//                    String url = variety.getAsJsonObject().get("url").getAsString();
//                    evolutions.add(url.split("pokemon/")[url.split("pokemon/").length-1]);
//                }
//            });
//            if (evolutions.size() == evolutionsSize){
//                String url = varieties.get(0).getAsJsonObject().get("url").getAsString();
//                evolutions.add(url.split("pokemon/")[url.split("pokemon/").length-1]);
//            }
//        }
//
//        return evolutions;
//    }
}
