package webservice.controller;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

//This controller manages the client request for obtaining the .html files
@Controller
@EnableAutoConfiguration
public class PagesController {

    @RequestMapping(value="/animali_a_rischio", method = RequestMethod.GET)
    public String  getPageAnimaliARischio() {
        return "animali_a_rischio.html";
    }

    @RequestMapping(value="/animali_fuori_habitat", method = RequestMethod.GET)
    public String  getPageAnimaliFuoriHabitat() {
        return "animali_fuori_habitat.html";
    }

    @RequestMapping(value="/aree_ricercatori", method = RequestMethod.GET)
    public String  getPageAreeRicercatori() {
        return "aree_ricercatori.html";
    }

    @RequestMapping(value="/dirigenti", method = RequestMethod.GET)
    public String  getPageDirigenti() {
        return "dirigenti.html";
    }

    @RequestMapping(value="/ricerca_avvistamenti", method = RequestMethod.GET)
    public String  getPageRicercaAvvistamenti() {
        return "ricerca_avvistamenti.html";
    }

    @RequestMapping(value="/segnalazione_avvistamento", method = RequestMethod.GET)
    public String  getPageSegnalazioneAvvistamento() {
        return "segnalazione_avvistamento.html";
    }
}
