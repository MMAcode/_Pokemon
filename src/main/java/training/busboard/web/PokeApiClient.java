package training.busboard.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

    return new Pokemon(
      extractNameFromPokemon(pokemonData),
      extractPictureUrlFromPokemon(pokemonData),
      extractAbilitiesFromPokemon(pokemonData),
      extractEvolutionsFromEvolutionsChain(evolutionChainData)
    );
  }

  private static JsonObject makeRequest(String url) {
    try {
      HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("User-Agent", "");
      BufferedReader in;
      try {
        in = new BufferedReader(new InputStreamReader(con.getInputStream()));
      } catch (Exception e) {
        return null;
      }
      String inputLine;
      StringBuffer content = new StringBuffer();
      while ((inputLine = in.readLine()) != null) {
        content.append(inputLine);
      }
      in.close();
      con.disconnect();
      return new JsonParser().parse(String.valueOf(content)).getAsJsonObject();
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
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
