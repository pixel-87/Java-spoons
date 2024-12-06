import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class CardTest {

    @Test
    void getValue() {
        Card card = new Card(5);

        assertEquals(5, card.getValue(), "Card value should be 5");
    }
}