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

Test **testDiscardNonPreferred()** tested that given a hand of random cards, the player discards the correct card according to their denomination. 

There are two tests relating to writing to writing to files, **testWriteToFileValidMessage, and testWriteToFileLongMessage**. Both test that outputs can be written.

There were no specific tests focusing on multithread nature of this game as Player as to test multiple players in a game we could just test the entire running application.

## CardGame
This test class also features a helper method **generateCardPack(int numCards)** seen previously to help generate cards instead of having to read a file. 

The tests in CardGame relate a lot to the reading and writing to files. 
**testCardGameConstructorValid()** just tests that a cardGame object can be created properly, and when the game is meade with insufficient cards in **testCardGameConstructorInsufficientCards()**. 

There are 3 tests relating to the loadPack method, about reading the pack of cards file into the game: **testLoadPackValidFile(), testLoadPackInvalidCardValue(), testLoadPackEmptyFile()** These all test that the deck is read into the game correctly or throws errors if not. 

There are two tests for distributing cards among players and decks testing when having insufficient cards or a large number of cards to distribute. The tests are **testDistributeCardsInsufficientCards(), and testDistributeCardsLargePack()**.
The first test could be considered redundant, as in the game it should be caught if a deck is read that has an insufficient number of cards and therefore should never reach the distributeCards method, but this was useful during testing if the incorrect number of cards was added to another method being tested.
Testing on a large pack was also useful as this tested that our game worked with a large number of players and decks. Both of these tests worked successfully immediately.

The final test of this class is **testEndGamePlayerWins()** This test is to ensure that a player wins the game when they reach the winnning condition of having a hand of the same 4 cards. 