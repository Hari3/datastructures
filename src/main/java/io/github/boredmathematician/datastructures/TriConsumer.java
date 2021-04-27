package io.github.boredmathematician.datastructures;

import java.util.function.Consumer;

/**
 * Represents an operation that accepts three input arguments and returns no result.
 * This is the three-arity specialization of {@link Consumer}, and is package-private.
 * Unlike most other functional interfaces, {@code TriConsumer} is expected
 * to operate via side-effects.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #accept(T, U, V)}.
 *
 * <b> Note that unlike {@link Consumer}, this does not implement a
 * * {@code after} functionality, since it was not required for
 * * the it's intended use in {@link TimedMap} interface</b>
 *
 * @param <T> the type of the first argument to the operation
 * @param <U> the type of the second argument to the operation
 * @param <V> the type of the third argument to the operation
 * @see Consumer
 */
@FunctionalInterface
interface TriConsumer<T, U, V> {

    /**
     * Performs this operation on the given arguments.
     *
     * @param t the first input argument
     * @param u the second input argument
     */
    void accept(T t, U u, V v);
}

