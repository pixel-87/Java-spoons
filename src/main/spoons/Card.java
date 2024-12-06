/**
 * Represents a playing card with a face value. Immutable and thread-safe.
 */
public class Card {

    /**
     * The face value of the card (non-negative integer).
     */
    private final int value;

    /**
     * Class constructor.
     *
     * @param value face value of the Card
     */
    public Card(int value) {
        this.value = value;
    }

    /**
     * Getter for the face value of the card.
     *
     * @return face value of the Card
     */
    public int getValue() {
        return value;
    }
}
