/*
 * Copyright 2002-2023 the original author or authors.
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

package org.springframework.expression.spel.ast;

import java.math.BigDecimal;

import org.springframework.expression.TypedValue;

public class BigDecimalLiteral extends Literal {
	private final TypedValue typedValue;

	public BigDecimalLiteral(final String originalValue, final int startPos, final int endPos, final BigDecimal value) {
		super(originalValue, startPos, endPos);
		this.typedValue = new TypedValue(value);
	}

	@Override
	public TypedValue getLiteralValue() {
		return this.typedValue;
	}
}
