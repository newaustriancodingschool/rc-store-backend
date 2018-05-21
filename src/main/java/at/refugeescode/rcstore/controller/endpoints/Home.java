package at.refugeescode.rcstore.controller.endpoints;

import at.refugeescode.rcstore.controller.logic.HomeService;
import at.refugeescode.rcstore.models.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class Home {

    private final HomeService homeService;

    @GetMapping
    @RolesAllowed("ROLE_USER")
    public String home() {
        return "home";
    }

    @ModelAttribute("items")
    @RolesAllowed("ROLE_USER")
    public List<Item> getItems() {
        return homeService.getItems();
    }

}
