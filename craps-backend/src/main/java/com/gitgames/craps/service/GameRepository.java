package com.gitgames.craps.service;

import com.gitgames.craps.model.Game;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameRepository {

    private final ConcurrentHashMap<String, Game> games = new ConcurrentHashMap<>();

    public void save(Game game) {
        games.put(game.getId(), game);
    }

    public Game findById(String id) {
        Game game = games.get(id);
        if (game == null) throw new GameNotFoundException(id);
        return game;
    }

    public void delete(String id) {
        games.remove(id);
    }
}
