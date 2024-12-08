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

    // Player's unique identifier
    private final int playerId;

    // List representing the player's hand of cards
    private final List<Card> hand;

    // Player's preferred card denomination (for discarding purposes)
    private int preferredDenomination;

    // References to the left and right decks the player interacts with
    private final Deck leftDeck;
    private final Deck rightDeck;

    // Log file for recording player's actions
    private final File playerFile;

    // Flag indicating if the player is still in a game
    private volatile boolean gameInProgress;

    // Reference to the game this player is a part of
    private final CardGame game;

    /**
     * Constructor to initialise a player with basic attributes and a log file.
     *
     * @param playerId Unique ID for the player
     * @param preferredDenomination The denomination this player prefers for discarding
     * @param leftDeck The deck on the left of the player
     * @param rightDeck The deck on the right of the player
     * @param game The game this player is part of
     */
    public Player(int playerId, int preferredDenomination, Deck leftDeck, Deck rightDeck, CardGame game) {
        this.playerId = playerId;
        this.hand = new ArrayList<>();
        this.preferredDenomination = preferredDenomination;
        this.leftDeck = leftDeck;
        this.rightDeck = rightDeck;
        this.game = game;
        this.playerFile = new File("player" + playerId + "_output.txt");
        this.gameInProgress = true;
        initialiseLogFile();  // Initialise the log file for the player
    }

    /**
     * Initialises the player's log file with initial hand information.
     */
    public void initialiseLogFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(playerFile))) {
            writer.write("Player " + playerId + " initial hand: " + handToString() + "\n");
        } catch (IOException e) {
            System.err.println("Error initialising log file for player " + playerId);
        }
    }

    /**
     * Represents the player's turn during the game.
     * The player draws a card, discards one, and records actions in the log.
     */
    public void playTurn() {
        while (gameInProgress) {
            synchronized (this) {  // Ensure thread safety during game interaction
                if (isWinningCondition()) {
                    writeToFile("Player " + playerId + " wins with hand: " + handToString() + "\n");
                    game.signalWinner(playerId);  // Notify game that this player has won
                    notifyOtherPlayers(playerId);  // Notify other players of the winner
                    gameInProgress = false;  // End the game for this player
                    return;
                }

                try {
                    // Draw a card from the left deck
                    Card drawnCard = leftDeck.drawCard();
                    hand.add(drawnCard);
                    writeToFile("Player " + playerId + " draws a " + drawnCard.getValue() + " from deck " + leftDeck.getDeckId() + "\n");

                    // Discard a card to the right deck
                    Card discardedCard = discardCard();
                    rightDeck.addCard(discardedCard);
                    writeToFile("Player " + playerId + " discards a " + discardedCard.getValue() + " to deck " + rightDeck.getDeckId() + "\n");
                    writeToFile("Player " + playerId + " current hand is " + handToString() + "\n");
                } catch (Exception e) {
                    System.err.println("Error during player " + playerId + "'s turn: " + e.getMessage());
                }
            }

            // Sleep for a brief moment to allow the game to proceed
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();  // Handle interruption
            }
        }
    }

    /**
     * Checks if the player has met the winning condition (4 of a kind).
     *
     * @return true if the player wins, false otherwise
     */
    public boolean isWinningCondition() {
        int[] cardCounts = new int[100];  // Array to count the occurrences of each card value
        for (Card card : hand) {
            cardCounts[card.getValue()]++;
            if (cardCounts[card.getValue()] == 4) {  // If there are 4 of the same card value, the player wins
                return true;
            }
        }
        return false;
    }

    /**
     * Notifies all other players that a specific player has won.
     *
     * @param winnerId The ID of the player who won
     */
    public void notifyOtherPlayers(int winnerId) {
        List<Player> allPlayers = game.getPlayers();  // Get list of all players in the game
        for (Player player : allPlayers) {
            if (player.getPlayerId() != winnerId) {
                player.logWinnerNotification(winnerId);  // Inform each player of the winner
                player.endGame();  // End the game for each player
            }
        }
    }

    /**
     * Logs the winner notification for this player.
     *
     * @param winnerId The ID of the winning player
     */
    public void logWinnerNotification(int winnerId) {
        String message = "Player " + winnerId + " has informed player " + playerId + " that player " + winnerId + " has won.\n" +
                "Player " + playerId + " exits.\n" +
                "Player " + playerId + " hand: " + handToString() + "\n";
        writeToFile(message);  // Log the winner notification
    }

    /**
     * Writes a message to the player's log file.
     *
     * @param message The message to be written to the log
     */
    public void writeToFile(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(playerFile, true))) {
            writer.write(message);  // Append message to the log file
        } catch (IOException e) {
            System.err.println("Error writing to log file for player " + playerId);
        }
    }

    /**
     * Adds a card to the player's hand.
     *
     * @param card The card to be added to the hand
     */
    public void receiveCard(Card card) {
        hand.add(card);
    }

    /**
     * Discards a card from the player's hand, preferring a specific denomination.
     *
     * @return The card that was discarded
     */
    public Card discardCard() {
        for (int i = 0; i < hand.size(); i++) {
            if (hand.get(i).getValue() != preferredDenomination) {
                return hand.remove(i);  // Discard the first card not matching the preferred denomination
            }
        }
        return hand.removeFirst();  // If all cards match the preferred denomination, discard the first one
    }

    /**
     * Converts the player's hand to a string representation.
     *
     * @return A string of card values in the player's hand
     */
    private String handToString() {
        StringBuilder sb = new StringBuilder();
        for (Card card : hand) {
            sb.append(card.getValue()).append(" ");  // Append each card value to the string builder
        }
        return sb.toString().trim();  // Return the string representation of the hand
    }

    /**
     * Ends the game for this player.
     */
    public void endGame() {
        synchronized (this) {  // Ensure thread safety when ending the game
            this.gameInProgress = false;
        }
    }

    // Getters and setters for player attributes

    public int getPlayerId() {
        return playerId;
    }

    public List<Card> getHand() {
        return hand;
    }

    public int getPreferredDenomination() {
        return preferredDenomination;
    }

    public void setPreferredDenomination(int newPreferredDenomination) {
        this.preferredDenomination = newPreferredDenomination;
    }

    public Deck getLeftDeck() {
        return leftDeck;
    }

    public Deck getRightDeck() {
        return rightDeck;
    }
}
