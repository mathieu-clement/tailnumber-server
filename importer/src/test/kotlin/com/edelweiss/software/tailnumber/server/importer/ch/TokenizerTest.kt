package com.edelweiss.software.tailnumber.server.importer.ch

import com.edelweiss.software.tailnumber.server.importer.ch.Tokenizer.LabelToken
import com.edelweiss.software.tailnumber.server.importer.ch.Tokenizer.ValueToken
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TokenizerTest {
    private  val tokenizerInput = ("<b>Manufacturer</b>&#160;GLASFLÜGEL ING EUGEN HÄNLE&#160;&#160;" +
            "<b>Aircraft Model/Type</b>&#160;STANDARD LIBELLE&#160;" +
            "&#160;<b>ICAO Aircraft Type<br/></b>GLID<br/>")
                .replace("&#160;", "")
                .replace("<br/>", "", true)
    private val tokenizer = Tokenizer(tokenizerInput)

    @Test
    fun tokenizerTokens() {
        Assertions.assertTrue(tokenizer.hasNext())
        Assertions.assertEquals(LabelToken("Manufacturer"), tokenizer.next())
        Assertions.assertTrue(tokenizer.hasNext())
        Assertions.assertEquals(
            ValueToken("GLASFLÜGEL ING EUGEN HÄNLE"),
            tokenizer.next()
        )
        Assertions.assertTrue(tokenizer.hasNext())
        Assertions.assertEquals(
            LabelToken("Aircraft Model/Type"),
            tokenizer.next()
        )
        Assertions.assertTrue(tokenizer.hasNext())
        Assertions.assertEquals(
            ValueToken("STANDARD LIBELLE"),
            tokenizer.next()
        )
        Assertions.assertTrue(tokenizer.hasNext())
        Assertions.assertEquals(
            LabelToken("ICAO Aircraft Type"),
            tokenizer.next()
        )
        Assertions.assertTrue(tokenizer.hasNext())
        Assertions.assertEquals(ValueToken("GLID"), tokenizer.next())
    }

    @Test
    fun tokenizerAsMap() {
        Assertions.assertEquals(
            mutableMapOf(
                "Manufacturer" to "GLASFLÜGEL ING EUGEN HÄNLE",
                "Aircraft Model/Type" to "STANDARD LIBELLE",
                "ICAO Aircraft Type" to "GLID"
            ), tokenizer.asMap()
        )
    }
}