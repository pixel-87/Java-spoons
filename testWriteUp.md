# Testing

For this project we used JUnit 5.9.1.

## Card

As this card makes use of an elegant record class, the one test we did could be considered redundant as this is an inbuilt class, however this was both of our first time using a record class so we did one test to confirm it worked as expected.

## Deck

Deck has five tests in total with most relating to its single threaded performance.
The tests **getDeckId(), drawCard(), addCard(Card card), and getCards()** all relate to testing the base functionality of the deck itself before moving on to testing its multithreaded performance. These four tests worked as expected straight out of the gate, this was expected as Deck made use of a `ConcurrentLinkedQueue` meaning there was little we needed to do beyond using the built in methods.

The final test is a test relating to a deck's multithreaded performance. The test **twoThreadedDrawAndAdd()** simulates a player (a thread) discarding a card from their hand to the bottom of a deck, and another player picking up the top card of the deck.

This test aims to simuilate a turn of the game from the perspective of a deck. This test immediately passed but this was deceptive as the test wasn't designed properly. Initially both players would draw a card and then discard the same card, this was intended to be a basic version of the entire game but would only reorder the deck , as a player would simply move a card from the top to the bottom. This test was redesigned so that each player would only draw or discard a card to the deck only. Luckily the class was designed well that this inital oversite did not affect the rest of the build.

## Player

Player features multiple tests to test core functionality of Player's methods.
These tests feature two helper methods called **generateCardPack(int NumCards), createPlayerWithHand(int[] cardValues)** to help create cards for decks and players that in the full game is read in from a file, using the helper methods instead of loading in a text file (e.g., deck1.txt) means that any errors in our pack loader wouldn't affect the rest of the tests.

The test **testPlayerConstructorValid()** checks that if a player is constructed properly. This checks numerous attributes: checking the player ID is as expected, that a player initially has no hand of cards, that a preferred demonination is set correctly, that each player's left and right deck are full of cards and that the output file for that player exists.

The tests **testIsWinningHand() and testIsWinningNonWinningHand()** both check if the player wins with a winning or non winning hand. There are two tests to ensure that when a player has a non winning hand we don't get a false positive win.
