package game.cards.controller;

import game.cards.bluff.Game;
import game.cards.modal.Card;
import game.cards.modal.Player;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
public class ApplicationController {

    @GetMapping("bluff/start")
    public boolean startBluff(@RequestParam(value="deckCount") int deckCount) {
        if (Game.getPlayers().size() > 2) {
            Game.startGame(deckCount);
            return true;
        } else {
            return false;
        }
    }

    @GetMapping("bluff/players")
    public HashMap<Integer, String> getBluffPlayers() {
        return Game.getPlayers();
    }

    @GetMapping("bluff/activePlayer")
    public int getBluffActivePlayer() {
        return Game.getActivePlayerId();
    }

    @GetMapping("bluff/bluffed")
    public HashMap<String, String> getLastBluffed() {
        HashMap<String, String> lastBluffed = new HashMap<>();
        int playerId = Game.getLastBluffedPlayerId();
        lastBluffed.put("playerName", playerId == 0 ? "" : String.valueOf(Game.getPlayer(playerId).getName()));
        lastBluffed.put("cardRank", String.valueOf(Game.getLastBluffedCard()));
        lastBluffed.put("cardCount", String.valueOf(Game.getLastBluffedCardsCount()));
        lastBluffed.put("totalCardCount", String.valueOf(Game.getRunningCardsCount()));
        return lastBluffed;
    }

    @GetMapping("bluff/winners")
    public List<String> getBluffWinners() {
        return Game.getWinners();
    }

    @PostMapping("bluff/addPlayer")
    public HashMap<String, String> addBluffPlayer(@RequestBody String playerName) {
        HashMap<String, String> playerData = new HashMap<>();
        Player player = Game.addPlayer(playerName);
        playerData.put("id", String.valueOf(player.getId()));
        playerData.put("key", String.valueOf(player.getKey()));
        return playerData;
    }

    @GetMapping("bluff/{playerKey}/pass")
    public boolean bluffPass(@PathVariable(value="playerKey") String playerKey) {
        if (Game.isActivePlayer(playerKey)) {
            Game.pass();
            return true;
        } else {
            return false;
        }
    }

    @GetMapping("bluff/{playerKey}/showCards")
    public List<Card> showBluffCards(@PathVariable(value="playerKey") String playerKey) {
        if (Game.isActivePlayer(playerKey)) {
            return Game.showCards();
        } else {
            return new ArrayList<>();
        }
    }

    @PostMapping("bluff/{playerKey}/throughCards/{bluffedCard}")
    public boolean throughCards(@PathVariable(value="playerKey") String playerKey, @PathVariable(value="bluffedCard") String bluffedCard, @RequestBody List<Card> cards) {
        if (Game.isActivePlayer(playerKey)) {
            Game.throughCards(cards, bluffedCard);
            return true;
        } else {
            return false;
        }
    }

    @GetMapping("bluff/{playerKey}/cards")
    public List<Card> getBluffPlayerCards(@PathVariable(value="playerKey") String playerKey) {
        try {
            return Game.getPlayer(playerKey).getCards();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @GetMapping("bluff/started")
    public boolean isBluffStarted() {
        return Game.isGameStarted();
    }

    @GetMapping("bluff/stopCardsGameApi")
    public boolean stopGame() {
        Game.stopGame();
        return true;
    }

    @DeleteMapping("bluff/{playerKey}/removePlayer")
    public boolean removeBluffPlayer(@PathVariable(value="playerKey") String playerKey) {
        Game.removePlayer(playerKey);
        if (Game.getPlayers().isEmpty()) {
            Game.resetGame();
        }
        return true;
    }
}