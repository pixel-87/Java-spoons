import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    // Single threaded tests, testing the class works as intended for a single thread.
    @Test
    void getDeckId() {
        Deck deck = new Deck(1);
        assertEquals(1, deck.getDeckId(),"Deck id should be 1");
    }

    @Test
    void addCard() {
        Deck deck = new Deck(1);
        Card card = new Card(5);
        deck.addCard(card);
        assertEquals(card, deck.drawCard(),"Card added should be drawn, which should be 5");
    }

    @Test
    void drawCard() {
        Deck deck = new Deck(1);
        Card card1 = new Card(5);
        Card card2 = new Card(6);

        deck.addCard(card1);
        deck.addCard(card2);

        assertEquals(card1, deck.drawCard(), "First card should have value 5");
        assertEquals(card2, deck.drawCard(), "Second card should have value 6");
    }


    // Multithreaded tests, tests a maximum of 2 threads/players as this is the maximum a deck should be interacting with.
    @Test
    void twoThreadedDrawAndAdd() throws InterruptedException {
        int numCards = 4; // Each deck should only have 4 cards at a time.
        Deck deck = new Deck(1);

        // Each deck should only ever have 4 cards at max by design.
        for (int i = 1; i <= numCards; i++) {
            deck.addCard(new Card(i));
        }

        Runnable drawAndDiscardTask = () -> {
            for (int i = 1; i <= numCards; i++) {
                Card DrawnCard = deck.drawCard();
                boolean hasWon = false; // Simplified victory condition check, fully implemented in ____ class.
                if (hasWon) {
                    break;
                }
                deck.addCard(DrawnCard); // Functionality to discard correct card isn't needed for this test.
            }
        };

        // Creating and starting threads.
        Thread drawThread = new Thread(drawAndDiscardTask);
        Thread discardThread = new Thread(drawAndDiscardTask);
        drawThread.start();
        discardThread.start();

        drawThread.join();
        discardThread.join();

        // verifying that the final card count of the deck is still 4.
        int finalCardCount = 0;
        while (deck.drawCard() != null) {
            finalCardCount++;
        }

        assertEquals(numCards, finalCardCount,"Total card count should be 4, same as when initialised");
    }
}