import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the card game.
 * Each player has a unique ID, a hand of cards, preferences, and interacts with adjacent decks.
 */
public class Player {

    private final int playerId;
    private final List<Card> hand;
    private int preferredDenomination;
    private final Deck leftDeck;
    private final Deck rightDeck;
    private final File playerFile;
    private volatile boolean gameInProgress;
    private final CardGame game;

    public Player(int playerId, int preferredDenomination, Deck leftDeck, Deck rightDeck, CardGame game) {
        this.playerId = playerId;
        this.hand = new ArrayList<>();
        this.preferredDenomination = preferredDenomination;
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.game = game;
        this.playerFile = new File("player" + playerId + "_output.txt");
        this.gameInProgress = true;
        initializeLogFile();
    }

    public void initializeLogFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(playerFile))) {
            writer.write("Player " + playerId + " initial hand: " + handToString() + "\n");
        } catch (IOException e) {
            System.err.println("Error initializing log file for player " + playerId);
        }
    }

    public void playTurn() {
        while (gameInProgress) {
            synchronized (this) {
                if (isWinningCondition()) {
                    writeToFile("Player " + playerId + " wins with hand: " + handToString() + "\n");
                    game.signalWinner(playerId);
                    notifyOtherPlayers(playerId);
                    gameInProgress = false;
                    return;
                }

                try {
                    Card drawnCard = leftDeck.drawCard();
                    hand.add(drawnCard);
                    writeToFile("Player " + playerId + " draws a " + drawnCard.getValue() + " from deck " + leftDeck.getDeckId() + "\n");

                    Card discardedCard = discardCard();
                    rightDeck.addCard(discardedCard);
                    writeToFile("Player " + playerId + " discards a " + discardedCard.getValue() + " to deck " + rightDeck.getDeckId() + "\n");
                    writeToFile("Player " + playerId + " current hand is " + handToString() + "\n");
                } catch (Exception e) {
                    System.err.println("Error during player " + playerId + "'s turn: " + e.getMessage());
                }
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public boolean isWinningCondition() {
        int[] cardCounts = new int[100];
        for (Card card : hand) {
            cardCounts[card.getValue()]++;
            if (cardCounts[card.getValue()] == 4) {
                return true;
            }
        }
        return false;
    }

    public void notifyOtherPlayers(int winnerId) {
        List<Player> allPlayers = game.getPlayers();
        for (Player player : allPlayers) {
            if (player.getPlayerId() != winnerId) {
                player.logWinnerNotification(winnerId);
                player.endGame();
            }
        }
    }

    public void logWinnerNotification(int winnerId) {
        String message = "Player " + winnerId + " has informed player " + playerId + " that player " + winnerId + " has won.\n" +
                "Player " + playerId + " exits.\n" +
                "Player " + playerId + " hand: " + handToString() + "\n";
        writeToFile(message);
    }

    public void writeToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(playerFile, true))) {
            writer.write(message);
        } catch (IOException e) {
            System.err.println("Error writing to log file for player " + playerId);
        }
    }

    public void receiveCard(Card card) {
        hand.add(card);
    }

    public Card discardCard() {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getValue() != preferredDenomination) {
                return hand.remove(i);
            }
        }
        return hand.remove(0);
    }

    private String handToString() {
        StringBuilder sb = new StringBuilder();
        for (Card card : hand) {
            sb.append(card.getValue()).append(" ");
        }
        return sb.toString().trim();
    }

    public void endGame() {
        synchronized (this) {
            this.gameInProgress = false;
        }
    }

    public int getPlayerId() {
        return playerId;
    }
}
