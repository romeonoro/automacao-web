package com.automacao.automacaoweb.controller;

import com.automacao.automacaoweb.service.LedService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/led")
public class LedController {

    private final LedService ledService;

    // Injeção de dependência via construtor
    public LedController(LedService ledService) {
        this.ledService = ledService;
    }

    @GetMapping("/on")
    public String turnOnLed() { // <- Este método no Controller
        return ledService.turnOnLed(); // <- CHAMA este método no Service
    }

    @GetMapping("/off")
    public String turnOffLed() { // <- Este método no Controller
        return ledService.turnOffLed(); // <- CHAMA este método no Service
    }
}