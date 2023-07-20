/*
 *     FisherLib
 *     Copyright (C) 2022  Fisher2911
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.fisher2911.common.util;

import java.util.List;
import java.util.function.Consumer;

public class Observable<T> {

    private final T value;
    private final List<Consumer<T>> observers;

    public Observable(T value, List<Consumer<T>> observers) {
        this.value = value;
        this.observers = observers;
    }

    public void edit(Consumer<T> editor) {
        editor.accept(this.value);
        this.observers.forEach(observer -> observer.accept(this.value));
    }

    public void observe(Consumer<T> observer) {
        this.observers.add(observer);
    }

}
