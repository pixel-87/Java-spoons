import org.junit.jupiter.api.Test;


import java.util.List;

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

    @Test
    void getCards() {
        Deck deck = new Deck(1);
        Card card1 = new Card(5);
        Card card2 = new Card(6);
        Card card3 = new Card(7);

        deck.addCard(card1);
        deck.addCard(card2);
        deck.addCard(card3);

        List<Card> cards = deck.getCards();
        assertEquals(3, cards.size(),"Cards should be 3");

        assertTrue(cards.contains(card1), "Card should contain card 1");
        assertTrue(cards.contains(card2), "Card should contain card 2");
        assertTrue(cards.contains(card3), "Card should contain card 3");
    }

    // Multithreaded tests, tests a maximum of 2 threads/players as this is the maximum a deck should be interacting with.
    @Test
    void twoThreadedDrawAndAdd() throws InterruptedException {
        int numCards = 4; // Each deck should only have 4 cards at a time.
        Deck deck = new Deck(1);
        // stored deck initial deck contents.
        List<Card> initialDeck = deck.getCards();
        // Each deck should only ever have 4 cards at max by design.
        for (int i = 1; i <= numCards; i++) {
            deck.addCard(new Card(i));
        }
        // Card for add thread to discard.
        Card card5 = new Card(5);

        Runnable drawCardTask = deck::drawCard;

        Runnable addCardTask = () -> deck.addCard(card5);

        // Creating and starting threads.
        Thread drawThread = new Thread(drawCardTask);
        Thread discardThread = new Thread(addCardTask);
        drawThread.start();
        discardThread.start();

        drawThread.join();
        discardThread.join();

        // verifying that the final card count of the deck is still 4.
        int finalCardCount = 0;
        while (deck.drawCard() != null) {
            finalCardCount++;
        }
        List<Card> finalDeck = deck.getCards();
        // Testing that the card count hasn't changed in the deck.
        assertEquals(numCards, finalCardCount, "Total card count should be 4, same as when initialised");
        // Testing that the deck is not the same as it was initially.
        assertNotEquals(initialDeck, finalDeck,"The final deck should be different was what it was initially");
    }

}