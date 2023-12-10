@FunctionalInterface
interface TriFunction<T, U, V, R>{
    /**
     * Applies this function to the given arguments.
     * @param t the first parameter
     * @param u the second parameter
     * @param v the third parameter
     * @return the output of this function
     */
    R apply(T t, U u, V v);
}