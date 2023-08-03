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

package io.github.fisher2911.command.argument;

import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class ArgumentResult<T> {

    private final T result;
    private final Error error;

    private ArgumentResult(T result, Error error) {
        this.result = result;
        this.error = error;
    }

    public static <T> ArgumentResult<T> success(T result) {
        return new ArgumentResult<>(result, null);
    }

    public static <T> ArgumentResult<T> failure(Error error) {
        return new ArgumentResult<>(null, error);
    }

    public static <T> ArgumentResult<T> exception(Throwable throwable) {
        return ArgumentResult.failure(new Error(Error.Type.EXCEPTION, throwable));
    }

    public static <T> ArgumentResult<T> invalidArgument(String message) {
        return ArgumentResult.failure(new Error(Error.Type.INVALID_ARGUMENT, message));
    }

    public static <T> ArgumentResult<T> noPermission(String permission) {
        return ArgumentResult.failure(new Error(Error.Type.NO_PERMISSION, permission));
    }

    public ArgumentResult<T> handleResult(Consumer<T> consumer) {
        if (this.result != null) {
            consumer.accept(this.result);
        }
        return this;
    }

    public ArgumentResult<T> handleFailure(Consumer<Error> consumer) {
        if (this.error != null) {
            consumer.accept(this.error);
        }
        return this;
    }

    public @Nullable T getResult() {
        return this.result;
    }

    public @Nullable Error getError() {
        return this.error;
    }

    public boolean isSuccess() {
        return this.result != null;
    }

    public boolean isFailure() {
        return this.error != null;
    }

    public static class Error {

        private final Type type;
        private final Object object;

        public Error(Type type, Object object) {
            this.type = type;
            this.object = object;
        }

        public Type getType() {
            return this.type;
        }

        public <T> T getObject() {
            return (T) this.object;
        }

        public enum Type {

            EXCEPTION(Throwable.class),
            INVALID_ARGUMENT(String.class),
            NO_PERMISSION(String.class);

            private final Class<?> clazz;

            Type(Class<?> clazz) {
                this.clazz = clazz;
            }

            public Class<?> getClazz() {
                return this.clazz;
            }
        }

    }
}
