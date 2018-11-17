/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.adb.query;

import javax.transaction.NotSupportedException;

import de.mhus.lib.core.parser.AttributeMap;

/**
 * <p>ANull class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class ANull extends APart {

	private AAttribute attr;
	private boolean is;

	/**
	 * <p>Constructor for ANull.</p>
	 *
	 * @param attr a {@link de.mhus.lib.adb.query.AAttribute} object.
	 * @param is a boolean.
	 */
	public ANull(AAttribute attr, boolean is) {
		this.attr = attr;
		this.is = is;
	}

	/** {@inheritDoc} */
	@Override
	public void getAttributes(AQuery<?> query, AttributeMap map) {
		attr.getAttributes(query, map);
	}

	public AAttribute getAttr() {
		return attr;
	}

	public boolean isIs() {
		return is;
	}

	@Override
	public void append(APart pa) throws NotSupportedException {
		throw new NotSupportedException();
	}

}
