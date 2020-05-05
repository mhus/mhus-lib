/**
 * Copyright 2018 Mike Hummel
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 * <p>http://www.apache.org/licenses/LICENSE-2.0
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.core.util;

import java.util.HashMap;
import java.util.Map;

public class InjectorMap implements Injector {

    private HashMap<Class<?>, Injector> injectors = new HashMap<Class<?>, Injector>();

    @Override
    public void doInject(Object obj) throws Exception {
        if (obj == null) return;
        for (Map.Entry<Class<?>, Injector> i : injectors.entrySet()) {
            if (i.getKey().isInstance(obj)) i.getValue().doInject(obj);
        }
    }

    public void put(Class<?> clazz, Injector injector) {
        injectors.put(clazz, injector);
    }

    public Injector get(Class<?> clazz) {
        return injectors.get(clazz);
    }

    public void remove(Class<?> clazz) {
        injectors.remove(clazz);
    }
}