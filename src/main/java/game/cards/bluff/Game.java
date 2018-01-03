package game.cards.bluff;

import game.cards.modal.Card;
import game.cards.modal.Deck;
import game.cards.modal.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Comparator.comparing;

public class Game {

    private static List<Player> players = new ArrayList<>();
    private static List<Card> runningCards = new ArrayList<>();
    private static int lastBluffedPlayerId;
    private static int lastBluffedCardsCount;
    private static int passPlayerCount;
    private static String lastBluffedCard;
    private static int activePlayerId;
    private static List<Player> winners = new ArrayList<>();
    private static boolean gameStarted = false;

    public static int getRunningCardsCount() {
        return runningCards.size();
    }

    public static HashMap<Integer, String> getPlayers() {
        HashMap<Integer, String> players = new HashMap<>();
        Game.players.forEach(p -> players.put(p.getId(), p.getName()));
        return players;
    }

    public static int getActivePlayerId() {
        return activePlayerId;
    }

    public static int getLastBluffedPlayerId() {
        return lastBluffedPlayerId;
    }

    public static int getLastBluffedCardsCount() {
        return lastBluffedCardsCount;
    }

    public static String getLastBluffedCard() {
        return lastBluffedCard;
    }

    public static Player getPlayer(String playerKey) {
        return players.stream().filter(e -> e.getKey().equals(playerKey)).findFirst().get();
    }

    public static List<String> getWinners() {
        return Game.winners.stream().map(Player::getName).collect(Collectors.toList());
    }

    public static void throughCards(List<Card> cards, String bluffedCard) {
        if (runningCards.isEmpty()) {
            lastBluffedCard = bluffedCard;
        }
        lastBluffedCardsCount = cards.size();
        lastBluffedPlayerId = activePlayerId;
        addRunningCards(removeCards(cards, activePlayerId));
        passPlayerCount = 0;
        changeActivePlayer();
    }

    public static List<Card> showCards() {
        List<Card> lastShowCards = new ArrayList<>();
        lastShowCards.addAll(runningCards.subList(runningCards.size() - lastBluffedCardsCount, runningCards.size()));
        if (lastShowCards.stream().allMatch(e -> e.getRank().equals(lastBluffedCard))) {
            addCards(runningCards, activePlayerId);
            activePlayerId = lastBluffedPlayerId;
            checkActivePlayer();
        } else {
            addCards(runningCards, lastBluffedPlayerId);
        }
        resetBluffed();
        return lastShowCards;
    }

    public static void pass() {
        passPlayerCount++;
        if (passPlayerCount == players.size()) {
            passPlayerCount = 0;
            activePlayerId = lastBluffedPlayerId;
            checkActivePlayer();
            resetBluffed();
        } else {
            changeActivePlayer();
        }
    }

    private static void resetBluffed() {
        lastBluffedPlayerId = 0;
        lastBluffedCard = null;
        lastBluffedCardsCount = 0;
        runningCards.clear();
    }

    public static Player addPlayer(String playerName) {
        Player player = new Player(playerName);
        players.add(player);
        return player;
    }

    public static void startGame(int deckCount) {
        resetGame();
        List<Deck> decks = new ArrayList<>();
        for (int index=0;index<deckCount;index++)
            decks.add(new Deck());
        List<Card> allCards = new ArrayList<>();
        decks.forEach(e -> allCards.addAll(e.getCards()));
        Collections.shuffle(allCards);
        int cardsCount = allCards.size() / players.size();
        int startIndex = 0;
        for (Player player : players) {
            List<Card> cards = allCards.subList(startIndex, (startIndex + cardsCount));
            cards = cards.stream().sorted(comparing(Card::getRank)).collect(Collectors.toList());
            player.setCards(cards);
            startIndex += cardsCount;
        }
        activePlayerId = players.get(0).getId();
        gameStarted = true;
    }

    public static void resetGame() {
        activePlayerId = 0;
        passPlayerCount = 0;
        lastBluffedCardsCount = 0;
        lastBluffedCard = null;
        lastBluffedPlayerId = 0;
        runningCards.clear();
        winners.clear();
        gameStarted = false;
    }

    public static void stopGame() {
        players.clear();
        resetGame();
    }

    public static Player getPlayer(int playerId) {
        return players.stream().filter(e -> e.getId() == playerId).findFirst().get();
    }

    private static void changeActivePlayer() {
        int index = IntStream.range(0, players.size())
                .filter(e -> players.get(e).getId() == activePlayerId)
                .findFirst().getAsInt();
        if (index == players.size() - 1)
            index = 0;
        else
            index+=1;
        activePlayerId = players.get(index).getId();
        checkActivePlayer();
    }

    private static void checkActivePlayer() {
        Player activePlayer = getPlayer(activePlayerId);
        if (activePlayer.getCards().isEmpty()) {
            changeActivePlayer();
            winners.add(activePlayer);
            if (lastBluffedPlayerId == activePlayer.getId()) {
                passPlayerCount = 0;
                resetBluffed();
            }
            players.remove(activePlayer);
        }
    }

    private static void addRunningCards(List<Card> cards) {
        runningCards.addAll(cards);
    }

    private static List<Card> removeCards(List<Card> cards, int playerId) {
        List<Card> removedCards = new ArrayList<>();
        if (players.stream().anyMatch(e -> e.getId() == playerId)) {
            Player player = getPlayer(playerId);
            List<Card> newCards = new ArrayList<>();
            newCards.addAll(player.getCards());
            Predicate<Card> filterForRemove = e -> cards.stream().anyMatch(f -> e.getRank().equals(f.getRank()) && e.getSuit().equals(f.getSuit()));
            removedCards = newCards.stream().filter(filterForRemove).collect(Collectors.toList());
            newCards.removeIf(filterForRemove);
            player.setCards(newCards);
        }
        return removedCards;
    }

    private static void addCards(List<Card> cards, int playerId) {
        if (players.stream().anyMatch(e -> e.getId() == playerId)) {
            Player player = getPlayer(playerId);
            List<Card> newCards = new ArrayList<>();
            newCards.addAll(player.getCards());
            newCards.addAll(cards);
            newCards = newCards.stream().sorted(comparing(Card::getRank)).collect(Collectors.toList());
            player.setCards(newCards);
        }
    }

    public static boolean isGameStarted() {
        return gameStarted;
    }

    public static void removePlayer(String playerKey) {
        if (getPlayer(playerKey).getId() == activePlayerId) {
            changeActivePlayer();
        }
        players.removeIf(player -> player.getKey().equals(playerKey));
    }
}