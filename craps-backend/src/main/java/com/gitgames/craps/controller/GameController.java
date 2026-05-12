package com.gitgames.craps.controller;

import com.gitgames.craps.controller.dto.*;
import com.gitgames.craps.service.GameService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GameStateResponse createGame(@RequestBody CreateGameRequest request,
                                         HttpServletRequest httpRequest) {
        String platformUserId = (String) httpRequest.getAttribute("platformUserId");
        return gameService.createGame(request, platformUserId);
    }

    @GetMapping("/{id}")
    public GameStateResponse getGame(@PathVariable String id) {
        return gameService.getGame(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void abandonGame(@PathVariable String id) {
        gameService.abandonGame(id);
    }

    @PostMapping("/{id}/bets")
    @ResponseStatus(HttpStatus.CREATED)
    public PlaceBetResponse placeBet(@PathVariable String id,
                                      @RequestBody PlaceBetRequest request) {
        return gameService.placeBet(id, request);
    }

    @DeleteMapping("/{id}/bets/{betId}")
    public RemoveBetResponse removeBet(@PathVariable String id,
                                        @PathVariable String betId) {
        return gameService.removeBet(id, betId);
    }

    @PostMapping("/{id}/bets/{betId}/toggle")
    public GameStateResponse toggleBet(@PathVariable String id,
                                        @PathVariable String betId) {
        return gameService.toggleBet(id, betId);
    }

    @PostMapping("/{id}/roll")
    public RollResponse roll(@PathVariable String id) {
        return gameService.roll(id);
    }

    @PostMapping("/{id}/cashout")
    public CashOutResponse cashOut(@PathVariable String id) {
        return gameService.cashOut(id);
    }
}
