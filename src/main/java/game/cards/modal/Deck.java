package game.cards.modal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {

    private List<Card> cards = new ArrayList<>();
    private static String[] SUITS = {"Club", "Diamond", "Heart", "Spade"};
    private static String[] RANKS = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

    public Deck() {
        for(String suit: SUITS) {
            for(String rank: RANKS) {
                cards.add(new Card(suit, rank));
            }
        }
    }

    public List<Card> getCards() {
        Collections.shuffle(cards);
        return cards;
    }
}
