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

import de.mhus.lib.core.MCollection;
import de.mhus.lib.core.parser.AttributeMap;

public class AOr extends APart {

	private APart[] operations;

	public AOr(APart ... operations) {
		this.operations = operations;
	}

	@Override
	public void getAttributes(AQuery<?> query, AttributeMap map) {
		for (APart part : operations)
			part.getAttributes(query, map);
	}

	public APart[] getOperations() {
		return operations;
	}

	@Override
	public void append(APart pa) {
		operations = MCollection.append(operations, pa);
	}

}
