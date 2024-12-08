import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Test class for the CardGame class, ensuring proper functionality
 * of game initialization, card distribution, and game flow.
 */
public class CardGameTest {

    /**
     * Tests for CardGame Constructor
     */

    // Positive test: Valid CardGame creation
    @Test
    public void testCardGameConstructorValid() {
        List<Card> cardPack = generateCardPack(40);
        CardGame game = new CardGame(4, cardPack);

        assertEquals(4, game.getNumPlayers());
        assertNotNull(game.getPlayers());
        assertNotNull(game.getDecks());
        assertNotNull(game.getNumPlayers());
    }

    // Negative test: Insufficient cards
    @Test
    public void testCardGameConstructorInsufficientCards() {
        List<Card> cardPack = generateCardPack(10);
        // Use assertThrows to check if IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            new CardGame(4, cardPack); });
    }


    // Boundary test: Minimum number of players (2 players)
    @Test
    public void testCardGameConstructorMinimumPlayers() {
        List<Card> cardPack = generateCardPack(16);
        CardGame game = new CardGame(2, cardPack);

        assertEquals(2, game.getNumPlayers());
    }

    /**
     * Tests for loadPack Method
     */

    // Positive test: Valid file load
    @Test
    public void testLoadPackValidFile() throws IOException {
        List<String> lines = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8");
        java.nio.file.Files.write(java.nio.file.Paths.get("test_pack.txt"), lines);

        List<Card> cardPack = CardGame.loadPack("test_pack.txt");
        assertEquals(8, cardPack.size());
        assertEquals(1, cardPack.get(0).getValue());
    }

    // Negative test: File with invalid card value
    @Test public void testLoadPackInvalidCardValue() throws IOException {
        // Create a list of strings representing invalid card values
        List<String> lines = Arrays.asList("1", "two", "3", "4");
        // Write these lines to a test file
        Files.write(Paths.get("test_invalid_pack.txt"), lines);
        // Use assertThrows to check if IllegalArgumentException is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            CardGame.loadPack("test_invalid_pack.txt");
        });
    }


    // Boundary test: Empty file
    @Test
    public void testLoadPackEmptyFile() throws IOException {
        // Create an empty file named "test_empty_pack.txt"
        Files.write(Paths.get("test_empty_pack.txt"), Collections.emptyList());

        // Assert that loading an empty pack file throws IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            CardGame.loadPack("test_empty_pack.txt");
        });
    }

    /**
     * Tests for distributeCards Method
     */

    // Positive test: Distribute cards evenly
    @Test
    public void testDistributeCardsEvenly() {
        List<Card> cardPack = generateCardPack(40);
        CardGame game = new CardGame(4, cardPack);
        game.initializeGame();

        // Assert each player has 4 cards
        for (Player player : game.getPlayers()) {
            assertEquals(4, player.getHand().size());
        }

        // Assert each deck has 4 cards
        for (Deck deck : game.getDecks()) {
            assertEquals(4, deck.getCards().size());
        }
    }

    // Negative test: Insufficient cards for distribution
    @Test
    public void testDistributeCardsInsufficientCards() {
        List<Card> cardPack = generateCardPack(10);

        // Assert that an IllegalArgumentException is thrown when creating a new CardGame
        assertThrows(IllegalArgumentException.class, () -> new CardGame(4, cardPack));
    }


    // Boundary test: Large card pack
    @Test
    public void testDistributeCardsLargePack() {
        List<Card> cardPack = generateCardPack(100);
        CardGame game = new CardGame(4, cardPack);
        game.initializeGame();

        // Assert each player has 4 cards
        for (Player player : game.getPlayers()) {
            assertEquals(4, player.getHand().size());
        }

        // Assert remaining cards are evenly distributed to decks
        for (Deck deck : game.getDecks()) {
            assertTrue(deck.getCards().size() >= 4);
        }
    }

    /**
     * Tests for endGame Method
     */

    // Positive test: Verify game ends when a player wins
    @Test
    public void testEndGamePlayerWins() {
        List<Card> cardPack = generateCardPack(40);
        CardGame game = new CardGame(4, cardPack);
        game.initializeGame();

        // Simulate a player having a winning hand
        game.getPlayers().get(0).receiveCard(new Card(5));
        game.getPlayers().get(0).receiveCard(new Card(5));
        game.getPlayers().get(0).receiveCard(new Card(5));
        game.getPlayers().get(0).receiveCard(new Card(5));

        game.endGame();
        assertTrue(game.getPlayers().get(0).isWinningCondition());
    }

    // Negative test: Game ends in a draw
    @Test
    public void testEndGameDraw() {
        List<Card> cardPack = generateCardPack(40);
        CardGame game = new CardGame(4, cardPack);

        // Initialize and end the game
        game.initializeGame();
        game.endGame();

        // Verify that no player has won the game
        boolean anyPlayerWon = game.getPlayers().stream().anyMatch(Player::isWinningCondition);
        assertFalse(anyPlayerWon, "No player should win after the game ends in a draw.");
    }

    /**
     * Helper method to generate a pack of cards.
     *
     * @param numCards Number of cards to generate.
     * @return List of Card objects.
     */
    private List<Card> generateCardPack(int numCards) {
        return IntStream.range(1, numCards + 1).mapToObj(Card::new).collect(Collectors.toList());
    }
}
