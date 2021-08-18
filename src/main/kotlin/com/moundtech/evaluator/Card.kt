package com.moundtech.evaluator

import kotlin.math.ln

/**
 * An immutable class representing a card from a normal 52-card deck.
 */
class Card(rank: Int, suit: Int) {
    /**
     * Returns the value of the card as an integer.
     * The value is represented as the bits `xxxAKQJT 98765432 CDHSrrrr xxPPPPPP`,
     * where `x` means unused, `AKQJT 98765432` are bits turned on/off
     * depending on the rank of the card, `CDHS` are the bits corresponding to the
     * suit, and `PPPPPP` is the prime number of the card.
     * @return the value of the card.
     */
    val value : Int

    /**
     * Returns the rank of the card.
     * @return rank of the card as an integer.
     * @see Card.ACE
     *
     * @see Card.DEUCE
     *
     * @see Card.TREY
     *
     * @see Card.FOUR
     *
     * @see Card.FIVE
     *
     * @see Card.SIX
     *
     * @see Card.SEVEN
     *
     * @see Card.EIGHT
     *
     * @see Card.NINE
     *
     * @see Card.TEN
     *
     * @see Card.JACK
     *
     * @see Card.QUEEN
     *
     * @see Card.KING
     */
    private val rank: Int
        get() = value shr 8 and 0xF

    /**
     * Returns the suit of the card.
     * @return Suit of the card as an integer.
     * @see Card.SPADES
     *
     * @see Card.HEARTS
     *
     * @see Card.DIAMONDS
     *
     * @see Card.CLUBS
     */
    private val suit: Int
        get() = value and 0xF000

    /**
     * Returns a string representation of the card.
     * For example, the king of spades is "Ks", and the jack of hearts is "Jh".
     * @return a string representation of the card.
     */
    override fun toString(): String {
        val rank = RANKS[rank]
        val suit = SUITS[(ln(suit.toDouble()) / ln(2.0)).toInt() - 12]
        return "" + rank + suit
    }

    companion object {
        // Ranks
        const val DEUCE = 0
        const val TREY = 1
        const val FOUR = 2
        const val FIVE = 3
        const val SIX = 4
        const val SEVEN = 5
        const val EIGHT = 6
        const val NINE = 7
        const val TEN = 8
        const val JACK = 9
        const val QUEEN = 10
        const val KING = 11
        const val ACE = 12

        // Suits
        const val CLUBS = 0x8000
        const val DIAMONDS = 0x4000
        const val HEARTS = 0x2000
        const val SPADES = 0x1000

        // Rank symbols
        private const val RANKS = "23456789TJQKA"
        private const val SUITS = "shdc"

        /**
         * Create a new [Card] instance from the given string.
         * The string should be a two-character string where the first character
         * is the rank and the second character is the suit. For example, "Kc" means
         * the king of clubs, and "As" means the ace of spades.
         * @param string Card to create as a string.
         * @return a new [Card] instance corresponding to the given string.
         */
        fun fromString(string: String?): Card {
            require(!(string == null || string.length != 2)) { "Card string must be non-null with length of exactly 2." }
            val rank = RANKS.indexOf(string[0])
            val suit = SPADES shl SUITS.indexOf(string[1])
            return Card(rank, suit)
        }

        /**
         * Returns whether the given rank is valid or not.
         * @param rank rank to check.
         * @return true if the rank is valid, false otherwise.
         */
        private fun isValidRank(rank: Int): Boolean {
            return rank in DEUCE..ACE
        }

        /**
         * Returns whether the given suit is valid or not.
         * @param suit suit to check.
         * @return true if the suit is valid, false otherwise.
         */
        private fun isValidSuit(suit: Int): Boolean {
            return suit == CLUBS || suit == DIAMONDS || suit == HEARTS || suit == SPADES
        }
    }

    /**
     * Creates a new card with the given rank and suit.
     * @param rank the rank of the card, e.g. [Card.SIX]
     * @param suit the suit of the card, e.g. [Card.CLUBS]
     */
    init {
        require(isValidRank(rank)) { "Invalid rank." }
        require(isValidSuit(suit)) { "Invalid suit." }
        value = 1 shl rank + 16 or suit or (rank shl 8) or Tables.PRIMES[rank].toInt()
    }
}