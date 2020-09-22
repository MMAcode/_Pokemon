package training.busboard.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@EnableAutoConfiguration
public class Website {

    public static void main(String[] args) {
        SpringApplication.run(Website.class, args);
    }

    @RequestMapping("/")
    ModelAndView home() {
        return new ModelAndView("index");
    }

    @RequestMapping("/pokemonInfo")
    ModelAndView pokemonInfo(@RequestParam("nameOrId") String nameOrId) {
        try {
            return new ModelAndView("info", "pokemon", PokeApiClient.getPokemon(nameOrId));
        } catch (PokemonNotFoundException e) {
            return new ModelAndView("not-found", "", null);
        }
    }

}
