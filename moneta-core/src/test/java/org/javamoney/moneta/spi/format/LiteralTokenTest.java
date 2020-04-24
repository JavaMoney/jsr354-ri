/**
 * Copyright (c) 2012, 2020, Anatole Tresch, Werner Keil and others by the @author tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.javamoney.moneta.spi.format;

import org.javamoney.moneta.FastMoney;
import org.testng.annotations.Test;

import javax.money.format.MonetaryParseException;

import java.io.IOException;

import static org.testng.Assert.*;

public class LiteralTokenTest {

    @Test
    public void testParse() {
        LiteralToken token = new LiteralToken(" some text ");
        ParseContext context = new ParseContext(" some text here");
        token.parse(context);
        assertEquals(context.getIndex(), 11);
    }

    @Test
    public void testParse_throws_exception() {
        LiteralToken token = new LiteralToken(" some text ");
        ParseContext context = new ParseContext("here is some text here");
        try {
            token.parse(context);
        } catch (MonetaryParseException e) {
            assertEquals(e.getInput(), "here is some text here");
            assertEquals(e.getErrorIndex(), 0);
            assertEquals(e.getMessage(), "Parse Error");
        }
        assertEquals(context.getIndex(), 0);
        assertFalse(context.isComplete());
        assertTrue(context.hasError());
        assertEquals(context.getErrorMessage(), "Parse Error");
    }

    @Test
    public void testPrint() throws IOException {
        LiteralToken token = new LiteralToken(" some text ");
        FastMoney amount = FastMoney.of(0,"USD");
        StringBuilder sb = new StringBuilder();
        token.print(sb, amount);
        assertEquals(sb.toString(), " some text ");
    }

    @Test
    public void testToString() {
        LiteralToken token = new LiteralToken(" some text ");
        assertEquals(token.toString(), "LiteralToken [token= some text ]");
    }
}