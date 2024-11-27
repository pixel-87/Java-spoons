University of Exeter Software Development Coursework

A multi-threaded card playing simulation (Spoons).

Game will start with n players and decks, where n is positive.

Each player will start with 4 cards and try to get 4 cards of the same number.
if the game is not won immediately, a player can take a card from the deck on their left and discard a card of their choosing to the deck on their right.

# Game playing strategy

If a player doesn't immediately win, they will draw cards until they do.
When choosing what card to discard they will prefer cards of their own index.
E.g. if player 1 has one 1 faced card, three 2s, and they have just drawn another 1 face card, they will discard a 2 faced card and keep the 1 faced card.

# Starting the game

To start the game, you must execute the CardGame class, whose main method requests via CLI the number of players (n) and the location of a valid input pack.

## For example

```
java CardGame
Please enter the number of players:
4
Please enter the location of a pack to load:
four.txt
```

A valid input pack is a plain text file, where each row contains a single non-negative int, and has _8n_ rows. It is legal for the face value of a card to exceed n.
This also means a valid pack is one in which there is no possible winning hand.
