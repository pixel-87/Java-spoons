import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;

/**
 * Test class for the Player class, testing all critical functionality
 * including constructor behavior and key methods.
 */
public class PlayerTest {

    /**
     * Tests for Player Constructor
     */

    // Positive test: Valid player creation
    @Test
    public void testPlayerConstructorValid() {
        Deck leftDeck = new Deck(1);
        Deck rightDeck = new Deck(2);
        Player player = new Player(1, 5, leftDeck, rightDeck);

        assertEquals(1, player.getPlayerId());
        assertTrue(player.getHand().isEmpty());
        assertEquals(5, player.getPreferredDenomination());
        assertNotNull(player.getLeftDeck());
        assertNotNull(player.getRightDeck());

        // Check if the file was created with the right name
        File playerFile = new File("player1_output.txt");
        assertTrue(playerFile.exists());
    }

    // Negative test: Null decks
    @Test
    public void testPlayerConstructorWithNullDecks() {
        assertThrows(NullPointerException.class, () -> {
            new Player(1, 5, null, null);
        });
    }

    // Boundary test: Minimum playerId
    @Test
    public void testPlayerConstructorWithMinimumPlayerId() {
        Deck leftDeck = new Deck(1);
        Deck rightDeck = new Deck(2);
        Player player = new Player(0, 5, leftDeck, rightDeck);

        assertEquals(0, player.getPlayerId());
    }

    /**
     * Tests for isWinningCondition Method
     */

    // Positive test: Winning hand with four matching cards
    @Test
    public void testIsWinningConditionWinningHand() {
        Player player = createPlayerWithHand(1, new int[]{5, 5, 5, 5});
        assertTrue(player.isWinningCondition());
    }

    // Negative test: Non-winning hand with different values
    @Test
    public void testIsWinningConditionNonWinningHand() {
        Player player = createPlayerWithHand(1, new int[]{1, 2, 3, 4});
        assertFalse(player.isWinningCondition());
    }

    // Boundary test: Large hand with one winning set
    @Test
    public void testIsWinningConditionLargeHand() {
        Player player = createPlayerWithHand(1, new int[]{3, 3, 3, 3, 7, 8, 9, 10, 2, 1});
        assertTrue(player.isWinningCondition());
    }

    /**
     * Tests for discardCard Method
     */

    // Positive test: Discard a non-preferred card
    @Test
    public void testDiscardCardNonPreferred() {
        Player player = createPlayerWithHand(1, new int[]{5, 7, 5, 9});
        player.setPreferredDenomination(5);

        Card discarded = player.discardCard();
        assertEquals(7, discarded.getValue());
    }

    // Negative test: Discard from empty hand
    @Test
    public void testDiscardCardFromEmptyHand() {
        Player player = createPlayerWithHand(1, new int[]{});

        assertThrows(IllegalStateException.class, player::discardCard);
    }


    // Boundary test: Discard when all cards match the preferred denomination
    @Test
    public void testDiscardCardAllPreferred() {
        Player player = createPlayerWithHand(1, new int[]{7, 7, 7, 7});
        player.setPreferredDenomination(7);

        Card discarded = player.discardCard();
        assertEquals(7, discarded.getValue());
    }

    /**
     * Tests for writeToFile Method
     */

    // Positive test: Valid log message
    @Test
    public void testWriteToFileValidMessage() {
        Player player = new Player(1, 5, new Deck(1), new Deck(2));
        player.writeToFile("Test log message.");

        // Check the file content
        try (BufferedReader reader = new BufferedReader(new FileReader("player1_output.txt"))) {
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Test log message.")) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        } catch (IOException e) {
            fail("IOException occurred: " + e.getMessage());
        }
    }

    // Boundary test: Long log message
    @Test
    public void testWriteToFileLongMessage() {
        Player player = new Player(1, 5, new Deck(1), new Deck(2));
        String longMessage = "A".repeat(1000);
        player.writeToFile(longMessage);

        try (BufferedReader reader = new BufferedReader(new FileReader("player1_output.txt"))) {
            String line;
            boolean found = false;
            while ((line = reader.readLine()) != null) {
                if (line.contains(longMessage)) {
                    found = true;
                    break;
                }
            }
            assertTrue(found);
        } catch (IOException e) {
            fail("IOException occurred: " + e.getMessage());
        }
    }

    /**
     * Helper method to create a Player with a predefined hand.
     */
    private Player createPlayerWithHand(int playerId, int[] cardValues) {
        Deck leftDeck = new Deck(1);
        Deck rightDeck = new Deck(2);
        Player player = new Player(playerId, 5, leftDeck, rightDeck);

        for (int value : cardValues) {
            player.receiveCard(new Card(value));
        }
        return player;
    }
}
