package datastructures;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A data structure which maintains it's values in a tabular form.
 * Each element in the Table is associated with a Row and a Column.
 * Table supports {@code null}s for values.
 * All Column headers are unique
 * All Row identifiers are unique
 *
 * @param <V> The type of values stored in this Table.
 * @param <C> The type of Column headers for this Table.
 * @param <R> The type of identifiers for Rows of this Table.
 * @author Hari Krishnan
 */
public interface Table<V, C, R> {

    /**
     * Adds a new Column to this Table, and leaves the value for all existing Rows to remain unset.
     * If this Table already contains a Column with specified header, the call leaves the Table
     * unchanged and returns {@code false}
     *
     * @param header The Column header to add to this Table.
     * @return {@code true} if this Table did not already contain a Column with the specified header.
     */
    boolean addColumn(C header);

    /**
     * Adds a new Column to this Table, and sets the value for existing Rows to the value in the Map,
     * keyed by it's identifier. Rows missing entries in the map is associated with {@code null}.
     * Additional keys(other than known Row identifiers) are ignored.
     *
     * @param header The Column header to add to this Table
     * @param values A mapping between existing Row identifiers and their values under this Column.
     *               Additional keys are ignored, missing keys are assumed to have value {@code null}.
     * @throws DuplicateIdentifierException If this Table already contained a Column with the specified header.
     */
    void addColumn(C header, Map<R, V> values) throws DuplicateIdentifierException;

    /**
     * Adds a new Row to this Table, and leaves the value for all existing Columns to remain unset
     * If this Table already contains a Row with specified identifier, the call leaves the Table
     * unchanged and returns {@code false}
     *
     * @param identifier The Row identifier to add to this Table.
     * @return {@code true} if this Table did not already contain a Row with the specified identifier.
     */
    boolean addRow(R identifier);

    /**
     * Adds a new Row to this Table, and sets the value for existing Columns to the value in the Map,
     * keyed by it's header. Columns missing entries in the map is associated with {@code null}.
     * Additional keys(other than known Column headers) are ignored.
     *
     * @param identifier The Row identifier to add to this Table
     * @param values     A mapping between existing Column headers and their values for this Row.
     *                   Additional keys are ignored, missing keys are assumed to have value {@code null}.
     * @throws DuplicateIdentifierException If this Table already contained a Row with the specified identifier.
     */
    void addRow(R identifier, Map<C, V> values) throws DuplicateIdentifierException;

    /**
     * Returns a mapping between all known Columns and it's value for given Row identifier.
     * Missing values are mapped to {@code null}.
     *
     * @param identifier The identifier for the Row to get.
     * @return a mapping between all known Columns and it's value for the identifier.
     * @throws NoSuchElementException if this Table does not contain a Row with the specified identifier.
     */
    Map<C, V> getRow(R identifier) throws NoSuchElementException;

    /**
     * Returns a mapping between all known Rows and it's value for given Column header.
     * Missing values are mapped to {@code null}.
     *
     * @param header The header for the Column to get.
     * @return a mapping between all known Rows and it's value under the header.
     * @throws NoSuchElementException if this Table does not contain a Column with the specified header.
     */
    Map<R, V> getColumn(C header) throws NoSuchElementException;

    /**
     * Get the value associated with the specified Column and Row.
     *
     * @param header     The Row identifier
     * @param identifier The Column header
     * @return the value associated with given Column and Row. {@code null} is no such value exists.
     */
    V get(C header, R identifier);

    /**
     * Get the value associated with the specified Column and Row, but if no such value exists, return a default value.
     *
     * @param header       The Row identifier
     * @param identifier   The Column header
     * @param defaultValue The default value to return if Table does not contain value
     * @return the value associated with given Column and Row. {@code null} is no such value exists.
     */
    Object getOrElse(C header, R identifier, Object defaultValue);


    /**
     * Associate the specified value with the specified Column and Row.
     *
     * @param header     The Column header
     * @param identifier The Row identifier
     * @param value      The value to associate with specified Column and Row
     * @throws NoSuchElementException if either of specified Column or Row does not exist in this Table.
     */
    void set(C header, R identifier, V value) throws NoSuchElementException;

    /**
     * Sets the value associated with the specified Column and Row to be null.
     * If this Table did not contain a value for the specified Column or Row, the call leaves the Table
     * unchanged and returns {@code false}
     *
     * @param header     The Column header
     * @param identifier The Row identifier
     * @return {@code true} if this Table contained the specified Column and Row.
     */
    boolean clear(C header, R identifier);

    /**
     * Sets the value associated with all the existing Columns in this Table for specified Row to be {@code null}.
     * If this Table did not contain the specified Row, the call leaves the Table unchanged and returns {@code false}
     *
     * @param identifier The identifier for the Row to clear.
     * @return {@code true} if this Table contained the specified Row.
     */
    boolean clearRow(R identifier);

    /**
     * Sets the value associated with all the existing Rows in this Table for specified Column to be {@code null}.
     * If this Table did not contain the specified Column, the call leaves the Table unchanged and returns {@code false}
     *
     * @param header The header for the Column to clear.
     * @return {@code true} if this Table contained the specified Column.
     */
    boolean clearColumn(C header);

    /**
     * Removes the specified Row from this Table.
     * If this Table did not contain the specified Row, the call leaves the Table unchanged and returns {@code false}
     *
     * @param identifier The identifier for the Row to remove.
     * @return {@code true} if this Table contained the specified Column and Row.
     */
    boolean removeRow(R identifier);

    /**
     * Removes the specified Column from this Table.
     * If this Table did not contain the specified Column, the call leaves the Table unchanged and returns {@code false}
     *
     * @param header The header for the Column to remove.
     * @return {@code true} if this Table contained the specified Column.
     */
    boolean removeColumn(C header);

    /**
     * Returns a {@link Set} of all Column headers of this Table.
     *
     * @return A {@link Set} of Column headers.
     */
    Set<C> getColumnHeaders();

    /**
     * Returns a {@link Set} of all Row identifiers of this Table.
     *
     * @return A {@link Set} of Row identifiers.
     */
    Set<R> getRowIdentifiers();

    /**
     * Returns true if this Table contains a mapping for the specified Column and Row.
     *
     * @param header   The header of the Column to check existence.
     * @param identity The identity of the Row to check existence.
     * @return {@code true} if a non-null value was mapped to the specified Column and Row.
     */
    boolean contains(C header, R identity);

    /**
     * Returns true if this Table contains a Column with the specified header.
     *
     * @param header The header of the Column to check existence.
     * @return {@code true} if the Table contains the specified Column.
     */
    boolean containsColumn(C header);

    /**
     * Returns true if this Table contains a Row with the specified identifier.
     *
     * @param identifier The identifier of the Row to check existence.
     * @return {@code true} if the Table contains the specified Row.
     */
    boolean containsRow(R identifier);

    /**
     * Display the contents of this Table in the console.
     * Each Column header, Row identifier and all the value
     * are displayed using the {@link String} representation returned by it's {@code toString()},
     * padded with enough spaces to make each cell width 20 characters.
     * If representation is more that 20 characters long, the first 17 characters are displayed followed by {@code ...}
     */
    default void show() {
        show(20);
    }

    /**
     * Display the contents of this Table in the console, with an empty Header.
     *
     * @param width The desired width od each cell.
     */
    default void show(int width) {
        System.out.println(representation(width));
    }

    /**
     * Display the contents of this Table in the console, in a tabular form.
     * The table header is show at the nexus of Column header(shown horizontally) and Row header(shown vertically)
     * Each Column header, Row identifier and all the value
     * are displayed using the {@link String} representation returned by it's {@code toString()},
     * padded with enough spaces so that the width of the cell matches the supplied width.
     * If representation is more that the supplied characters long, the first {@code width - 3} characters are displayed followed by {@code ...}
     *
     * @param width The desired width od each cell.
     */

    default void show(int width, String tableHeader) {
        System.out.println(representation(width, tableHeader));
    }

    /**
     * Returns the representation of this Table, with a blank title
     *
     * @param width The desired width od each cell.
     */
    default String representation(int width) {
        StringBuilder header = new StringBuilder();
        while (header.length() != width)
            header.append(" ");
        return representation(width, header.toString());
    }

    /**
     * Return the contents of this Table, represented as a String, with each Row delimited by a new line character.
     * The table header is show at the nexus of Column header(shown horizontally) and Row header(shown vertically)
     * Each Column tableHeader, Row identifier and all the value
     * are represented using the {@link String} representation returned by it's {@code toString()},
     * padded with enough spaces so that the width of the 'cell' matches the supplied width.
     * If representation is more that the supplied characters long, the first {@code width - 3} characters are used followed by {@code ...}
     *
     * @param width The desired width od each cell.
     */
    String representation(int width, String tableHeader);

}
