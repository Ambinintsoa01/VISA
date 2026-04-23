package mg.visa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/formulaire-dossier")
    public String formulaireDossier() {
        return "formulaire-dossier";
    }
}
