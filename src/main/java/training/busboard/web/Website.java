package training.busboard.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;

@Controller
@EnableAutoConfiguration
public class Website {

    @RequestMapping("/")
    ModelAndView home() {
        return new ModelAndView("index");
    }

    @RequestMapping("/pokemonInfo")
    ModelAndView pokemonInfo(@RequestParam("name") String name) {
        ArrayList<String> abilities = new ArrayList<>();
        abilities.add("limber");
        abilities.add("imposter");
        ArrayList<String> evolutions = new ArrayList<>();
        evolutions.add("level-up");
        return new ModelAndView("info", "pokemon", new Pokemon("Ditto", "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/132.png", abilities, evolutions));
    }

    public static void main(String[] args) {
        SpringApplication.run(Website.class, args);
    }

}