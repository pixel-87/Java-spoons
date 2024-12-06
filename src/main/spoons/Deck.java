import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * The deck of cards with thread-safe methods for adding and discarding cards.
 * Decks are identified by a unique ID and use a ConcurrentLinkedQueue.
 */
public class Deck {
    private final int deckId;
    // ConcurrentLinkedQueue does all the locking, no need to use synchronized or specific lock method
    private final ConcurrentLinkedQueue<Card> cards = new ConcurrentLinkedQueue<Card>();

    /**
     * Constructor to init a deck with an ID.
     * @param deckId Unique ID for a given deck.
     */
    public Deck(int deckId) {
        this.deckId = deckId;
    }

    /**
     * Returns the deck ID.
     * @return The deck's ID.
     */
    public int getDeckId() {
        return deckId;
    }

    /**
     * Draws (which removes and returns) the top card of the deck.
     * @return The top card of the deck.
     */
    public Card drawCard() {
        return cards.poll();
    }

    /**
     * Adds a card to the bottom / end of the deck.
     * @param card The card being added to the bottom of the deck.
     */
    public void addCard(Card card) {
        cards.add(card);
    }
}