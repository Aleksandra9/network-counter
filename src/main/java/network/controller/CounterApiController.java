package network.controller;

import network.service.CounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class CounterApiController implements CounterApi {
    private final CounterService counterService;

    public CounterApiController(CounterService counterService) {
        this.counterService = counterService;
    }

    @Override
    public ResponseEntity<Number> counterGet(Principal principal) {
        return ResponseEntity.ok(counterService.getCount(principal.getName()));
    }
}
