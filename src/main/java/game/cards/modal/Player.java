package game.cards.modal;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class Player {

    private int id;
    private String name, status;
    private String key;
    private List<Card> cards = new ArrayList<>();
    private static int playersCount = 0;

    public Player(String playerName) {
        playersCount++;
        this.id = playersCount;
        this.name = playerName;
        this.key = Base64.getEncoder().encodeToString((playerName + playersCount).getBytes()).replaceAll("=", "").replaceAll("A", "Z").replaceAll("1", "0");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public List<Card> getCards() {
        return cards;
    }

    public void setCards(List<Card> cards) { this.cards = cards; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }
}
