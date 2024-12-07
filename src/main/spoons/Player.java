
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player in the card game. Each player has a unique ID, a hand of
 * cards, preferences, and interacts with adjacent decks.
 */
public class Player {

    /**
     * Unique identifier for the player.
     */
    private final int playerId;

    /**
     * Collection of cards representing the player's hand.
     */
    private final List<Card> hand;

    /**
     * The card denomination the player prioritizes (based on their ID).
     */
    private int preferredDenomination;

    /**
     * Reference to the deck from which the player draws cards.
     */
    private final Deck leftDeck;

    /**
     * Reference to the deck where the player discards cards.
     */
    private final Deck rightDeck;

    /**
     * The player’s log file for recording actions (e.g., player1_output.txt).
     */
    private final File playerFile;

    /**
     * Flag to indicate whether the game is still active.
     */
    private volatile boolean gameInProgress;

    /**
     * Constructor for the Player class.
     *
     * @param playerId Unique identifier for the player.
     * @param preferredDenomination Preferred card denomination for the player.
     * @param leftDeck Reference to the deck the player draws from.
     * @param rightDeck Reference to the deck the player discards to.
     */
    public Player(int playerId, int preferredDenomination, Deck leftDeck, Deck rightDeck) {
        // checks if left or right deck is null and throws an error in that case.
        if (leftDeck == null || rightDeck == null) {
            throw new NullPointerException();
        }

        this.playerId = playerId;
        this.hand = new ArrayList<>();
        this.preferredDenomination = preferredDenomination;
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.playerFile = new File("player" + playerId + "_output.txt");
        this.gameInProgress = true;
        initializeLogFile();
    }

    /**
     * Initializes the log file for the player. Creates a new file and writes
     * the initial header information.
     */
    public void initializeLogFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(playerFile))) {
            writer.write("Player " + playerId + " initial hand: " + handToString() + "\n");
        } catch (IOException e) {
            System.err.println("Error initializing log file for player " + playerId);
        }
    }

    /**
     * Executes the player's turn by drawing, discarding, and logging actions.
     */
    public void playTurn() {
        while (gameInProgress) {
            synchronized (this) {
                if (isWinningCondition()) {
                    writeToFile("Player " + playerId + " wins with hand: " + handToString() + "\n");
                    gameInProgress = false; // End the game
                    return;
                }

                try {
                    // Draw a card
                    Card drawnCard = leftDeck.drawCard();
                    hand.add(drawnCard);
                    writeToFile("Player " + playerId + " draws a " + drawnCard.getValue() + " from deck " + leftDeck.getDeckId() + "\n");

                    // Discard a card
                    Card discardedCard = discardCard();
                    rightDeck.addCard(discardedCard);
                    writeToFile("Player " + playerId + " discards a " + discardedCard.getValue() + " to deck " + rightDeck.getDeckId() + "\n");

                    // Log the updated hand
                    writeToFile("Player " + playerId + " current hand is " + handToString() + "\n");
                } catch (Exception e) {
                    System.err.println("Error during player " + playerId + "'s turn: " + e.getMessage());
                }
            }

            try {
                Thread.sleep(100); // Simulate turn delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Checks if the player's hand contains four cards of the same value.
     *
     * @return True if the player meets the winning condition, false otherwise.
     */
    public boolean isWinningCondition() {
        int[] cardCounts = new int[100]; // Assuming card values are 0–99
        for (Card card : hand) {
            cardCounts[card.getValue()]++;
            if (cardCounts[card.getValue()] == 4) {
                return true;
            }
        }
        return false;
    }

    /**
     * Writes a message to the player's log file.
     *
     * @param message The message to be written.
     */
    public void writeToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(playerFile, true))) {
            writer.write(message);
        } catch (IOException e) {
            System.err.println("Error writing to log file for player " + playerId);
        }
    }

    /**
     * Receives a card and adds it to the player's hand.
     *
     * @param card The card to be added.
     */
    public void receiveCard(Card card) {
        hand.add(card);
    }

    /**
     * Removes a card from the player's hand based on the strategy and returns
     * it.
     *
     * @return The card to be discarded.
     */
    public Card discardCard() {
         << << << < HEAD
        if (hand.isEmpty()) {
            throw new IllegalStateException("Cannot discard from an empty hand");
        };

        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getValue() != preferredDenomination) {
                return hand.remove(i); // Remove and return the first non-preferred card
                 >>> >>> > 07c434f8bc7ff45d5b94ac1b469c837df4f3644b
            }
        }
        // Fallback: Discard the first card if all match the preferred denomination
        return hand.remove(0);
    }

    /**
     * Converts the player's hand to a string representation.
     *
     * @return A string representation of the player's hand.
     */
    private String handToString() {
        StringBuilder sb = new StringBuilder();
        for (Card card : hand) {
            sb.append(card.getValue()).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Signals the player that the game has ended.
     */
    public void endGame() {
        synchronized (this) {
            this.gameInProgress = false;
        }
    }

    /**
     * Getter for the player ID.
     *
     * @return The player's unique ID.
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * Getter for the preferred denomination.
     *
     * @return The player's preferred denomination.
     */
    public int getPreferredDenomination() {
        return preferredDenomination;
    }

    public Deck getLeftDeck() {
        return leftDeck;
    }

    public Deck getRightDeck() {
        return rightDeck;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setPreferredDenomination(int newPreferredDenomination) {
        this.preferredDenomination = newPreferredDenomination;
    }
}
