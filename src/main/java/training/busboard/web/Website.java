package training.busboard.web;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class Website {

    @RequestMapping("/")
    ModelAndView home() {
        return new ModelAndView("index");
    }

    @RequestMapping("/pokemonInfo")
    ModelAndView pokemonInfo(@RequestParam("nameOrId") String nameOrId) {
        JsonObject response = makeRequest("https://pokeapi.co/api/v2/pokemon/" + nameOrId);
        if (response == null) return new ModelAndView("not-found", "", null);
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

        return new ModelAndView("info", "pokemon", new Pokemon(name, pictureUrl, abilities, evolutions));
    }

    private JsonObject makeRequest(String url) {
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

    public static void main(String[] args) {
        SpringApplication.run(Website.class, args);
    }

}
