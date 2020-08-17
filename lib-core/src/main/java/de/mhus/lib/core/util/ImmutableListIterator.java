/**
 * Copyright (C) 2020 Mike Hummel (mh@mhus.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.core.util;

import java.util.ListIterator;

public class ImmutableListIterator<E> implements ListIterator<E> {

    private ListIterator<E> parent;

    public ImmutableListIterator(ListIterator<E> parent) {
        this.parent = parent;
    }

    @Override
    public void add(E o) {}

    @Override
    public boolean hasNext() {
        return parent.hasNext();
    }

    @Override
    public boolean hasPrevious() {
        return parent.hasPrevious();
    }

    @Override
    public E next() {
        return parent.next();
    }

    @Override
    public int nextIndex() {
        return parent.nextIndex();
    }

    @Override
    public E previous() {
        return parent.previous();
    }

    @Override
    public int previousIndex() {
        return parent.previousIndex();
    }

    @Override
    public void remove() {}

    @Override
    public void set(E o) {}
}
