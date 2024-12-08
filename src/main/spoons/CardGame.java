// Updated CardGame Class
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Represents the main CardGame class to manage the game.
 * Handles initialization, gameplay flow, and results processing.
 */
public class CardGame {

    /** Number of players in the game. */
    private final int numPlayers;

    /** List of players participating in the game. */
    private final List<Player> players;

    /** List of decks in the game. */
    private final List<Deck> decks;

    /** List of cards in the game. */
    private final List<Card> cardPack;

    /** Threads managing the gameplay for each player. */
    private final List<Thread> gameThreads;

    /** Flag to indicate if the game is over. */
    private volatile boolean gameOver;

    /**
     * Constructor for the CardGame class.
     *
     * @param numPlayers Number of players.
     * @param cardPack   List of cards in the game.
     */
    public CardGame(int numPlayers, List<Card> cardPack) {
        this.numPlayers = numPlayers;
        this.cardPack = cardPack;
        this.players = new ArrayList<>();
        this.decks = new ArrayList<>();
        this.gameThreads = new ArrayList<>();
        this.gameOver = false;
    }

    /**
     * Entry point of the game. Initializes and starts the game based on user input.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);

            // Prompt for number of players
            System.out.println("Please enter the number of players:");
            int numPlayers = scanner.nextInt();
            scanner.nextLine();

            // Prompt for the location of the pack to load
            System.out.println("Please enter the location of pack to load:");
            String filePath = "src\\main\\resources\\" + scanner.nextLine();

            scanner.close();

            List<Card> cardPack = loadPack(filePath);
            if (cardPack.size() < numPlayers * 8) {
                throw new IllegalArgumentException("Invalid card pack: insufficient cards.");
            }

            CardGame game = new CardGame(numPlayers, cardPack);
            game.initializeGame();
            game.startGame();
        } catch (Exception e) {
            System.err.println("Error initializing game: " + e.getMessage());
        }
    }

    /**
     * Loads a card pack from a file. Each line in the file represents a card value.
     *
     * @param filePath Path to the file containing card values.
     * @return List of Card objects.
     * @throws IOException If there is an error reading the file.
     * @throws IllegalArgumentException If the file contains invalid data.
     */
    public static List<Card> loadPack(String filePath) throws IOException {
        List<String> lines = java.nio.file.Files.readAllLines(java.nio.file.Paths.get(filePath));

        // Convert each line to an integer and wrap it in a Card object
        List<Card> cardPack = new ArrayList<>();
        for (String line : lines) {
            try {
                int value = Integer.parseInt(line.trim());
                cardPack.add(new Card(value));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid card value in pack: " + line, e);
            }
        }

        return cardPack;
    }

    /**
     * Initializes the game by creating players and decks and distributing cards.
     */
    public void initializeGame() {
        for (int i = 0; i < numPlayers; i++) {
            decks.add(new Deck(i + 1));
        }

        for (int i = 0; i < numPlayers; i++) {
            Player player = new Player(i + 1, i + 1, decks.get(i), decks.get((i + 1) % numPlayers), this);
            players.add(player);
        }

        distributeCards();

        for (Player player : players) {
            player.initializeLogFile();
        }
    }

    /**
     * Distributes cards to players and decks in a round-robin fashion.
     */
    public void distributeCards() {
        int numCardsPerPlayer = 4;
        int numCardsPerDeck = 4;

        // Distribute cards to players
        for (int i = 0; i < numCardsPerPlayer * numPlayers; i++) {
            players.get(i % numPlayers).receiveCard(cardPack.get(i));
        }

        // Distribute cards to decks
        for (int i = numCardsPerDeck * numPlayers; i < cardPack.size(); i++) {
            decks.get(i % numPlayers).addCard(cardPack.get(i));
        }
    }

    /**
     * Starts the game by initiating threads for each player.
     */
    public void startGame() {
        for (Player player : players) {
            Thread thread = new Thread(player::playTurn);
            gameThreads.add(thread);
            thread.start();
        }

        for (Thread thread : gameThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println("Game interrupted: " + e.getMessage());
            }
        }

        endGame();
    }

    /**
     * Ends the game, signals players, and logs results.
     */
    public void endGame() {
        for (Player player : players) {
            player.endGame();
        }
    }

    /**
     * Signals that the game is over and sets the winner.
     *
     * @param winnerId The ID of the player who won.
     */
    public void signalWinner(int winnerId) {
        synchronized (this) {
            if (!gameOver) {
                gameOver = true;
                System.out.println("Player " + winnerId + " has won the game!");
                endGame();
            }
        }
    }

    /**
     * Checks if the game is over.
     *
     * @return True if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Getter for the number of players.
     *
     * @return Number of players.
     */
    public int getNumPlayers() {
        return numPlayers;
    }

    /**
     * Getter for the players list.
     *
     * @return List of players.
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Getter for the decks list.
     *
     * @return List of decks.
     */
    public List<Deck> getDecks() {
        return decks;
    }
}