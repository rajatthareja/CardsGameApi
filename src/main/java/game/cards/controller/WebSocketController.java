package game.cards.controller;

import game.cards.bluff.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    private final SimpMessagingTemplate template;

    @Autowired
    WebSocketController(SimpMessagingTemplate template) {
        this.template = template;
    }

    @MessageMapping("/update")
    public void update(String message) {
        switch (message) {
            case "players":
                this.template.convertAndSend("/players", Game.getPlayers());
                break;
            case "started":
                this.template.convertAndSend("/started", Game.isGameStarted());
                break;
            case "activePlayerId":
                this.template.convertAndSend("/activePlayerId", Game.getActivePlayerId());
                break;
        }
    }
}
