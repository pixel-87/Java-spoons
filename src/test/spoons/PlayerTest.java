import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
        List<Card> cardPack = generateCardPack(40);
        CardGame game = new CardGame(4, cardPack);
        Deck leftDeck = new Deck(1);
        Deck rightDeck = new Deck(2);
        Player player = new Player(1, 5, leftDeck, rightDeck, game);

        assertEquals(1, player.getPlayerId());
        assertTrue(player.getHand().isEmpty());
        assertEquals(5, player.getPreferredDenomination());
        assertNotNull(player.getLeftDeck());
        assertNotNull(player.getRightDeck());

        // Check if the file was created with the right name
        File playerFile = new File("player1_output.txt");
        assertTrue(playerFile.exists());
    }


    // Boundary test: Minimum playerId
    @Test
    public void testPlayerConstructorWithMinimumPlayerId() {
        List<Card> cardPack = generateCardPack(40);
        CardGame game = new CardGame(4, cardPack);
        Deck leftDeck = new Deck(1);
        Deck rightDeck = new Deck(2);
        Player player = new Player(0, 5, leftDeck, rightDeck, game);

        assertEquals(0, player.getPlayerId());
    }

    /**
     * Tests for isWinningCondition Method
     */

    // Positive test: Winning hand with four matching cards
    @Test
    public void testIsWinningConditionWinningHand() {
        Player player = createPlayerWithHand(new int[]{5, 5, 5, 5});
        assertTrue(player.isWinningCondition());
    }

    // Negative test: Non-winning hand with different values
    @Test
    public void testIsWinningConditionNonWinningHand() {
        Player player = createPlayerWithHand(new int[]{1, 2, 3, 4});
        assertFalse(player.isWinningCondition());
    }

    // Boundary test: Large hand with one winning set
    @Test
    public void testIsWinningConditionLargeHand() {
        Player player = createPlayerWithHand(new int[]{3, 3, 3, 3, 7, 8, 9, 10, 2, 1});
        assertTrue(player.isWinningCondition());
    }

    /**
     * Tests for discardCard Method
     */

    // Positive test: Discard a non-preferred card
    @Test
    public void testDiscardCardNonPreferred() {
        Player player = createPlayerWithHand(new int[]{5, 7, 5, 9});
        player.setPreferredDenomination(5);

        Card discarded = player.discardCard();
        assertEquals(7, discarded.value());
    }



    // Boundary test: Discard when all cards match the preferred denomination
    @Test
    public void testDiscardCardAllPreferred() {
        Player player = createPlayerWithHand(new int[]{7, 7, 7, 7});
        player.setPreferredDenomination(7);

        Card discarded = player.discardCard();
        assertEquals(7, discarded.value());
    }

    /**
     * Tests for writeToFile Method
     */

    // Positive test: Valid log message
    @Test
    public void testWriteToFileValidMessage() {
        List<Card> cardPack = generateCardPack(40);
        CardGame game = new CardGame(4, cardPack);
        Player player = new Player(1, 5, new Deck(1), new Deck(2), game);
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
        List<Card> cardPack = generateCardPack(40);
        CardGame game = new CardGame(4, cardPack);
        Player player = new Player(1, 5, new Deck(1), new Deck(2), game);
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
     * Ensures that correct card is discarded, according to player denomination.
     */
    @Test
    public void testDiscardLogic() {
        Player player = createPlayerWithHand(new int[]{3, 5, 3, 6});
        player.setPreferredDenomination(3);
        Card discardedCard = player.discardCard();
        assertNotEquals(3, discardedCard.value(), "Discarded card should not be the preferred denomination 3"); }


    /**
     * Helper method to generate a pack of cards.
     *
     * @param numCards Number of cards to generate.
     * @return List of Card objects.
     */
    private List<Card> generateCardPack(int numCards) {
        return IntStream.range(1, numCards + 1).mapToObj(Card::new).collect(Collectors.toList());
    }

    /**
     * Helper method to create a Player with a predefined hand.
     */
    private Player createPlayerWithHand(int[] cardValues) {
        Deck leftDeck = new Deck(1);
        Deck rightDeck = new Deck(2);
        List<Card> cardPack = generateCardPack(16);
        CardGame game = new CardGame(2, cardPack);
        Player player = new Player(1, 5, leftDeck, rightDeck, game);

        for (int value : cardValues) {
            player.receiveCard(new Card(value));
        }
        return player;
    }
}
