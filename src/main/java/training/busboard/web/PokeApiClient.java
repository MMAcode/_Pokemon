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
    JsonObject response = makeRequest("https://pokeapi.co/api/v2/pokemon/" + nameOrId);
    if (response == null) throw new PokemonNotFoundException("Pokemon " + nameOrId + " not found");
    String name = response.get("name").getAsString();
    String pictureUrl = response.get("sprites").getAsJsonObject().get("front_default").getAsString();
    List<String> abilities = new ArrayList<>();
    response.get("abilities").getAsJsonArray().forEach(jsonElement -> abilities.add(jsonElement.getAsJsonObject().get("ability").getAsJsonObject().get("name").getAsString()));
    response = makeRequest(response.get("species").getAsJsonObject().get("url").getAsString());
    System.out.println(response);
    response = makeRequest(response.get("evolution_chain").getAsJsonObject().get("url").getAsString());

    List<String> evolutions = new ArrayList<>();

    JsonObject node = response.getAsJsonObject("chain");

    while (true) {
      evolutions.add(node.get("species").getAsJsonObject().get("name").getAsString());
      JsonArray evolvesTo = node.getAsJsonArray("evolves_to");
      if (evolvesTo.size() == 0) break;
      node = evolvesTo.get(0).getAsJsonObject();
    }

    return new Pokemon(name, pictureUrl, abilities, evolutions);
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

}
