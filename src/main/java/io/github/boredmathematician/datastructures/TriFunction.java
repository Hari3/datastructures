package io.github.boredmathematician.datastructures;

import java.util.function.Function;

/**
 * Represents a function that accepts three arguments and produces a result.
 * This is the three-arity specialization of {@link Function}, and is package-private.
 *
 * <p>This is a <a href="package-summary.html">functional interface</a>
 * whose functional method is {@link #apply(T, U, V)}.
 *
 * <b> Note that unlike {@link Function}, this does not implement a
 * {@code andThen} functionality, since it was not required for
 * the it's intended use in {@link TimedMap} interface</b>
 *
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <V> the type of the third argument to the function
 * @param <R> the type of the result of the function
 * @see Function
 */
@FunctionalInterface
interface TriFunction<T, U, V, R> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @param v the third function argument
     * @return the function result
     */
    R apply(T t, U u, V v);

}

