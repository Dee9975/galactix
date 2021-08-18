package com.moundtech.evaluator

import java.util.*

/** Utility methods for evaluating or creating a hand of cards.  */
object Hand {
    /**
     * Evaluates the given hand and returns its value as an integer.
     * Based on Kevin Suffecool's 5-card hand evaluator and with Paul Senzee's pre-computed hash.
     * @param cards a hand of cards to evaluate
     * @return the value of the hand as an integer between 1 and 7462
     */
    fun evaluate(cards: Array<Card?>): Int {
        // Only 5-card hands are supported
        require(!(cards == null || cards.size != 5)) { "Exactly 5 cards are required." }

        // Binary representations of each card
        val c1: Int = cards[0]?.value ?: 0
        val c2: Int = cards[1]?.value ?: 0
        val c3: Int = cards[2]?.value ?: 0
        val c4: Int = cards[3]?.value ?: 0
        val c5: Int = cards[4]?.value ?: 0

        // No duplicate cards allowed
        require(!hasDuplicates(intArrayOf(c1, c2, c3, c4, c5))) { "Illegal hand." }

        // Calculate index in the flushes/unique table
        val index = c1 or c2 or c3 or c4 or c5 shr 16

        // Flushes, including straight flushes
        if (c1 and c2 and c3 and c4 and c5 and 0xF000 != 0) {
            return Tables.Flushes.TABLE[index].toInt()
        }

        // Straight and high card hands
        val value = Tables.Unique.TABLE[index].toInt()
        if (value != 0) {
            return value
        }

        // Remaining cards
        val product = (c1 and 0xFF) * (c2 and 0xFF) * (c3 and 0xFF) * (c4 and 0xFF) * (c5 and 0xFF)
        return Tables.Hash.Values.TABLE[hash(product)].toInt()
    }

    /**
     * Creates a new 5-card hand from the given string.
     * @param string the string to create the hand from, such as "Kd 5s Jc Ah Qc"
     * @return a new hand as an array of cards
     * @see Card
     */
    fun fromString(string: String): Array<Card?> {
        val parts = string.split(" ").toTypedArray()
        val cards = arrayOfNulls<Card>(parts.size)
        require(parts.size == 5) { "Exactly 5 cards are required." }
        var index = 0
        for (part in parts) cards[index++] = Card.fromString(part)
        return cards
    }

    /**
     * Converts the given hand into concatenation of their string representations
     * @param cards a hand of cards
     * @return a concatenation of the string representations of the given cards
     */
    fun toString(cards: Array<Card?>): String {
        val builder = StringBuilder()
        for (i in cards.indices) {
            builder.append(cards[i])
            if (i < cards.size - 1) builder.append(" ")
        }
        return builder.toString()
    }

    /**
     * Checks if the given array of values has any duplicates.
     * @param values the values to check
     * @return true if the values contain duplicates, false otherwise
     */
    private fun hasDuplicates(values: IntArray): Boolean {
        Arrays.sort(values)
        for (i in 1 until values.size) {
            if (values[i] == values[i - 1]) return true
        }
        return false
    }

    private fun hash(key: Int): Int {
        var key = key
        key += -0x16e555cb
        key = key xor (key ushr 16)
        key += key shl 8
        key = key xor (key ushr 4)
        return key + (key shl 2) ushr 19 xor Tables.Hash.Adjust.TABLE[key ushr 8 and 0x1FF].toInt()
    }
}