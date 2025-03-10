/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.expression.spel;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.expression.spel.standard.SpelExpression;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests the evaluation of basic literals: boolean, integer, hex integer, long, real, null, date
 * and complex ones like BigInteger and BigDecimal
 *
 * @author Andy Clement
 */
public class LiteralTests extends AbstractExpressionTests {
	@Test
	public void testLiteralBoolean01() {
		evaluate("false", "false", Boolean.class);
	}

	@Test
	public void testLiteralBoolean02() {
		evaluate("true", "true", Boolean.class);
	}

	@Test
	public void testLiteralInteger01() {
		evaluate("1", "1", Integer.class);
	}

	@Test
	public void testLiteralInteger02() {
		evaluate("1415", "1415", Integer.class);
	}

	@Test
	public void testLiteralString01() {
		evaluate("'Hello World'", "Hello World", String.class);
	}

	@Test
	public void testLiteralString02() {
		evaluate("'joe bloggs'", "joe bloggs", String.class);
	}

	@Test
	public void testLiteralString03() {
		evaluate("'hello'", "hello", String.class);
	}

	@Test
	public void testLiteralString04() {
		evaluate("'Tony''s Pizza'", "Tony's Pizza", String.class);
		evaluate("'Tony\\r''s Pizza'", "Tony\\r's Pizza", String.class);
	}

	@Test
	public void testLiteralString05() {
		evaluate("\"Hello World\"", "Hello World", String.class);
	}

	@Test
	public void testLiteralString06() {
		evaluate("\"Hello ' World\"", "Hello ' World", String.class);
	}

	@Test
	public void testHexIntLiteral01() {
		evaluate("0x7FFFF", "524287", Integer.class);
		evaluate("0x7FFFFL", 524287L, Long.class);
		evaluate("0X7FFFF", "524287", Integer.class);
		evaluate("0X7FFFFl", 524287L, Long.class);
	}

	@Test
	public void testLongIntLiteral01() {
		evaluate("0xCAFEBABEL", 3405691582L, Long.class);
	}

	@Test
	public void testLongIntInteractions01() {
		evaluate("0x20 * 2L", 64L, Long.class);
		// ask for the result to be made into an Integer
		evaluateAndAskForReturnType("0x20 * 2L", 64, Integer.class);
		// ask for the result to be made into an Integer knowing that it will not fit
		evaluateAndCheckError("0x1220 * 0xffffffffL", Integer.class, SpelMessage.TYPE_CONVERSION_ERROR, 0);
	}

	@Test
	public void testSignedIntLiterals() {
		evaluate("-1", -1, Integer.class);
		evaluate("-0xa", -10, Integer.class);
		evaluate("-1L", -1L, Long.class);
		evaluate("-0x20l", -32L, Long.class);
	}

	@ParameterizedTest
	@ValueSource(strings = {"Bi", "BI", "bI", "bi"})
	public void testBigIntegerLiterals(final String suffixVariant) {
		evaluateComparable("-1" + suffixVariant, new BigInteger("-1"), BigInteger.class);
		evaluateComparable("1" + suffixVariant, new BigInteger("1"), BigInteger.class);
		evaluateComparable("1342356" + suffixVariant, new BigInteger("1342356"), BigInteger.class);
		evaluateComparable("-999999" + suffixVariant, new BigInteger("-999999"), BigInteger.class);
	}

	@Test
	public void testBigIntegerLiterals_BadExpressions() {
		parseAndCheckError("-3.4bI", SpelMessage.REAL_CANNOT_BE_BIG_INTEGER);
	}

	@ParameterizedTest
	@ValueSource(strings = {"Bi", "BI", "bI", "bi"})
	public void testBigIntegerLiterals_Hex(final String suffixVariant) {
		evaluateComparable("-0x20" + suffixVariant, new BigInteger("-32"), BigInteger.class);
		evaluateComparable("0x20" + suffixVariant, new BigInteger("32"), BigInteger.class);
		evaluateComparable("0x7FFFF" + suffixVariant, new BigInteger("524287"), BigInteger.class);
		evaluateComparable("0x3B2A6" + suffixVariant, new BigInteger("242342"), BigInteger.class);
		evaluateComparable("0x5B7D" + suffixVariant, new BigInteger("23421"), BigInteger.class);
		evaluateComparable("0x5BD7" + suffixVariant, new BigInteger("23511"), BigInteger.class);
		evaluateComparable("0x57BD" + suffixVariant, new BigInteger("22461"), BigInteger.class);
	}

	@Test
	public void testLiteralReal01_CreatingDoubles() {
		evaluate("1.25", 1.25d, Double.class);
		evaluate("2.99", 2.99d, Double.class);
		evaluate("-3.141", -3.141d, Double.class);
		evaluate("1.25d", 1.25d, Double.class);
		evaluate("2.99d", 2.99d, Double.class);
		evaluate("-3.141d", -3.141d, Double.class);
		evaluate("1.25D", 1.25d, Double.class);
		evaluate("2.99D", 2.99d, Double.class);
		evaluate("-3.141D", -3.141d, Double.class);
	}

	@Test
	public void testLiteralReal02_CreatingFloats() {
		// For now, everything becomes a double...
		evaluate("1.25f", 1.25f, Float.class);
		evaluate("2.5f", 2.5f, Float.class);
		evaluate("-3.5f", -3.5f, Float.class);
		evaluate("1.25F", 1.25f, Float.class);
		evaluate("2.5F", 2.5f, Float.class);
		evaluate("-3.5F", -3.5f, Float.class);
	}

	@Test
	public void testLiteralReal03_UsingExponents() {
		evaluate("6.0221415E+23", "6.0221415E23", Double.class);
		evaluate("6.0221415e+23", "6.0221415E23", Double.class);
		evaluate("6.0221415E+23d", "6.0221415E23", Double.class);
		evaluate("6.0221415e+23D", "6.0221415E23", Double.class);
		evaluate("6E2f", 6E2f, Float.class);
	}

	@Test
	public void testLiteralReal04_BadExpressions() {
		parseAndCheckError("6.1e23e22", SpelMessage.MORE_INPUT, 6, "e22");
		parseAndCheckError("6.1f23e22", SpelMessage.MORE_INPUT, 4, "23e22");
	}

	@ParameterizedTest
	@ValueSource(strings = {"Bd", "BD", "bD", "bd"})
	public void testBigDecimalLiterals(final String suffixVariant) {
		evaluateComparable("0.1" + suffixVariant, new BigDecimal("0.1"), BigDecimal.class);
		evaluateComparable("0.0" + suffixVariant, new BigDecimal("0.0"), BigDecimal.class);
		evaluateComparable("-0.1" + suffixVariant, new BigDecimal("-0.1"), BigDecimal.class);
		evaluateComparable("1.7" + suffixVariant, new BigDecimal("1.7"), BigDecimal.class);
		evaluateComparable("-1.7" + suffixVariant, new BigDecimal("-1.7"), BigDecimal.class);
		evaluateComparable("0.0000100" + suffixVariant, new BigDecimal("0.00001"), BigDecimal.class);
		evaluateComparable("1.25" + suffixVariant, new BigDecimal("1.25"), BigDecimal.class);
		evaluateComparable("2.99" + suffixVariant, new BigDecimal("2.99"), BigDecimal.class);
		evaluateComparable("-3.141" + suffixVariant, new BigDecimal("-3.141"), BigDecimal.class);
	}

	@ParameterizedTest
	@ValueSource(strings = {"Bd", "BD", "bD", "bd"})
	public void testBigDecimalLiterals_UsingExponents(final String suffixVariant) {
		evaluateComparable("6.0221415E+23" + suffixVariant, new BigDecimal("6.0221415E23"), BigDecimal.class);
		evaluateComparable("6.0221415e+23" + suffixVariant, new BigDecimal("6.0221415E23"), BigDecimal.class);
		evaluateComparable("6E2" + suffixVariant, new BigDecimal("6E2"), BigDecimal.class);
	}

	@Test
	public void testLiteralNull01() {
		evaluate("null", null, null);
	}

	@Test
	public void testConversions() {
		// getting the expression type to be what we want - either:
		evaluate("new Integer(37).byteValue()", (byte) 37, Byte.class); // calling byteValue() on Integer.class
		evaluateAndAskForReturnType("new Integer(37)", (byte) 37, Byte.class); // relying on registered type converters
	}

	@Test
	public void testNotWritable() throws Exception {
		SpelExpression expr = (SpelExpression)parser.parseExpression("37");
		assertThat(expr.isWritable(new StandardEvaluationContext())).isFalse();
		expr = (SpelExpression)parser.parseExpression("37L");
		assertThat(expr.isWritable(new StandardEvaluationContext())).isFalse();
		expr = (SpelExpression)parser.parseExpression("true");
		assertThat(expr.isWritable(new StandardEvaluationContext())).isFalse();
	}
}
