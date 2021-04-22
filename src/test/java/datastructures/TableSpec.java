package datastructures;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

abstract class TableSpec {

    protected Table<String, Integer, Long> table;

    protected abstract Table<String, Integer, Long> getInstance();

    @BeforeEach
    void setUp() {
        table = getInstance();
    }

    @Test
    void tableShouldAcceptColumnsIgnoringRepeats() {
        assertTrue(table.addColumn(5), "Expected 'true' when adding new column.");
        assertTrue(table.addColumn(7), "Expected 'true' when adding new column.");


        Set<Integer> expected = new HashSet<>();
        expected.add(5);
        expected.add(7);

        assertEquals(expected, table.getColumnHeaders());

        assertFalse(table.addColumn(5), "Expected 'false' when adding duplicate column.");

        assertEquals(expected, table.getColumnHeaders());
    }

    @Test
    void tableShouldAcceptRowsIgnoringRepeats() {
        assertTrue(table.addRow(5L), "Expected 'true' when adding new row.");
        assertTrue(table.addRow(7L), "Expected 'true' when adding new row.");


        Set<Long> expected = new HashSet<>();
        expected.add(5L);
        expected.add(7L);

        assertEquals(expected, table.getRowIdentifiers());

        assertFalse(table.addRow(5L), "Expected 'false' when adding duplicate row.");

        assertEquals(expected, table.getRowIdentifiers());
    }

    @Test
    void tableShouldAcceptRowsAndColumnsSimultaneously() {

        assertTrue(table.addRow(5L), "Expected 'true' when adding new row.");
        assertTrue(table.addRow(7L), "Expected 'true' when adding new row.");
        assertTrue(table.addColumn(5), "Expected 'true' when adding new column.");
        assertTrue(table.addColumn(7), "Expected 'true' when adding new column.");


        Set<Long> expectedRows = new HashSet<>();
        expectedRows.add(5L);
        expectedRows.add(7L);

        Set<Integer> expectedColumns = new HashSet<>();
        expectedColumns.add(5);
        expectedColumns.add(7);

        assertEquals(expectedRows, table.getRowIdentifiers());
        assertEquals(expectedColumns, table.getColumnHeaders());

        assertFalse(table.addColumn(5), "Expected 'false' when adding duplicate column.");
        assertFalse(table.addRow(5L), "Expected 'false' when adding duplicate row.");


        assertEquals(expectedColumns, table.getColumnHeaders());
        assertEquals(expectedRows, table.getRowIdentifiers());
    }

    @Test
    void tableShouldAcceptColumnsWithValuesForRows() {
        Set<Integer> expectedColumns = new HashSet<>();
        expectedColumns.add(1);
        table.addColumn(1);
        table.addRow(1L);
        table.addRow(2L);

        Map<Long, String> values = new HashMap<>();

        values.put(1L, "21");
        values.put(2L, "22");
        values.put(4L, "24");
        Set<Long> expectedRows = new HashSet<>();
        expectedRows.add(1L);
        expectedRows.add(2L);

        assertThrows(DuplicateIdentifierException.class, () -> table.addColumn(1, values),
                "Expected Exception when adding duplicate Column with Row information");
        assertEquals(expectedColumns, table.getColumnHeaders());
        assertDoesNotThrow(() -> table.addColumn(2, values));
        expectedColumns.add(2);
        assertEquals(expectedColumns, table.getColumnHeaders());
        assertEquals(expectedRows, table.getRowIdentifiers(),
                "Expected invalid Row identifiers to be ignored");


    }

    @Test
    void tableShouldAcceptRowsWithValuesForColumns() {
        Set<Long> expectedRows = new HashSet<>();
        expectedRows.add(1L);
        table.addRow(1L);
        table.addColumn(1);
        table.addColumn(2);

        Map<Integer, String> values = new HashMap<>();

        values.put(1, "21");
        values.put(2, "22");
        values.put(4, "24");
        Set<Integer> expectedColumns = new HashSet<>();
        expectedColumns.add(1);
        expectedColumns.add(2);

        assertThrows(DuplicateIdentifierException.class, () -> table.addRow(1L, values),
                "Expected Exception when adding duplicate Row with Column information");
        assertEquals(expectedRows, table.getRowIdentifiers());
        assertDoesNotThrow(() -> table.addRow(2L, values));
        expectedRows.add(2L);
        assertEquals(expectedRows, table.getRowIdentifiers());
        assertEquals(expectedColumns, table.getColumnHeaders(),
                "Expected invalid Column headers to be ignored");


    }

    @Test
    void tableShouldUpdateColumnsWithValuesForRows() {
        table.addColumn(1);
        table.addRow(1L);
        table.addRow(2L);

        Map<Long, String> values = new HashMap<>();

        values.put(1L, "21");
        values.put(2L, "22");

        assertDoesNotThrow(() -> table.addColumn(2, values));

        assertEquals(values, table.getColumn(2));

        values.put(1L, "2112");
        values.put(2L, "2211");
        values.put(4L, "2414");

        Map<Long, String> expected = new HashMap<>();
        expected.put(1L, "2112");
        expected.put(2L, "2211");

        assertDoesNotThrow(() -> table.updateColumn(2, values));

        assertEquals(expected, table.getColumn(2));


    }

    @Test
    void tableShouldUpdateRowsWithValuesForColumns() {

        table.addRow(1L);
        table.addColumn(1);
        table.addColumn(2);

        Map<Integer, String> values = new HashMap<>();

        values.put(1, "21");
        values.put(2, "22");

        assertDoesNotThrow(() -> table.addRow(2L, values));
        assertEquals(values, table.getRow(2L));

        values.put(1, "2121");
        values.put(2, "2222");
        values.put(4, "2424");

        assertDoesNotThrow(() -> table.updateRow(2L, values));

        Map<Integer, String> expected = new HashMap<>();

        expected.put(1, "2121");
        expected.put(2, "2222");

        assertEquals(expected, table.getRow(2L));
    }

    @Test
    void tableShouldReturnRowByIdentifier() {
        table.addColumn(1);
        table.addColumn(2);
        table.addColumn(4);

        table.addRow(1L);
        table.addRow(2L);

        table.set(1, 1L, "11");
        table.set(4, 1L, "41");

        Map<Integer, String> expected = new HashMap<>();

        expected.put(1, "11");
        expected.put(4, "41");
        assertEquals(expected, table.getRow(1L));

        expected.remove(1);
        expected.remove(4);
        assertEquals(expected, table.getRow(2L));

        assertThrows(NoSuchElementException.class, () -> table.getRow(3L),
                "Expected Exception when getting invalid Row");
    }

    @Test
    void tableShouldReturnColumnByHeader() {
        table.addRow(1L);
        table.addRow(2L);
        table.addRow(4L);

        table.addColumn(1);
        table.addColumn(2);

        table.set(1, 1L, "11");
        table.set(1, 4L, "14");

        Map<Long, String> expected = new HashMap<>();

        expected.put(1L, "11");
        expected.put(4L, "14");
        assertEquals(expected, table.getColumn(1));

        expected.remove(1L);
        expected.remove(4L);
        assertEquals(expected, table.getColumn(2));

        assertThrows(NoSuchElementException.class, () -> table.getColumn(3),
                "Expected Exception when getting invalid Column");
    }

    @Test
    void tableShouldReturnValueAtSpecifiedColumnAndRow() {

        table.addRow(3L);
        table.addRow(4L);

        Map<Long, String> row1 = new HashMap<>();
        row1.put(3L, "13");
        row1.put(5L, "15");
        Map<Long, String> row2 = new HashMap<>();
        row2.put(3L, "23");
        row2.put(4L, "24");

        assertDoesNotThrow(() -> table.addColumn(1, row1));
        assertDoesNotThrow(() -> table.addColumn(2, row2));

        assertEquals("13", table.get(1, 3L));
        assertNull(table.get(1, 4L));
        assertNull(table.get(1, 5L));
        assertEquals("23", table.get(2, 3L));
        assertEquals("24", table.get(2, 4L));


    }

    @Test
    void tableShouldReturnValueAtSpecifiedColumnAndRowOrReturnDefault() {
        table.addColumn(1);
        table.addColumn(2);
        table.addRow(3L);
        table.addRow(4L);

        table.set(1, 3L, "13");
        table.set(1, 4L, null);
        table.set(2, 3L, "null");

        assertEquals("13", table.getOrElse(1, 3L, "defaultValue"));
        assertNull(table.getOrElse(1, 4L, "defaultValue"),
                "Expected 'null' when value was null. Got default Value instead.");
        assertEquals("null", table.getOrElse(2, 3L, "defaultValue"));
        assertEquals("defaultValue", table.getOrElse(2, 4L, "defaultValue"));
    }

    @Test
    void tableShouldAcceptValueForSpecifiedColumnAndRow() {
        table.addColumn(1);
        table.addRow(3L);

        table.set(1, 3L, "13");
        assertEquals("13", table.get(1, 3L));

    }

    @Test
    void tableShouldClearValueForSpecifiedColumnAndRow() {
        table.addColumn(1);
        table.addRow(3L);

        table.set(1, 3L, "13");
        assertEquals("13", table.get(1, 3L));
        table.clear(1, 3L);
        assertNull(table.get(1, 3L));
        assertThrows(NoSuchElementException.class, () -> table.clear(1, 4L), "Expected Exception when clearing non existing Row");
        assertThrows(NoSuchElementException.class, () -> table.clear(2, 3L), "Expected Exception when clearing non existing Column");

    }

    @Test
    void tableShouldClearValuesForSpecifiedRow() {
        table.addColumn(1);
        table.addColumn(2);
        table.addRow(3L);
        table.addRow(4L);
        table.addRow(6L);

        table.set(1, 3L, "13");
        table.set(1, 4L, "14");
        table.set(2, 3L, "23");
        table.set(2, 4L, "24");

        assertTrue(table.clearRow(3L));
        assertThrows(NoSuchElementException.class, () -> table.clearRow(5L), "Expected exception when clearing non existing Row");
        assertFalse(table.clearRow(6L), "Expected false when clearing empty Row");

        assertNull(table.get(1, 3L));
        assertNull(table.get(2, 3L));
        assertEquals("14", table.get(1, 4L));
        assertEquals("24", table.get(2, 4L));

    }

    @Test
    void tableShouldClearValuesForSpecifiedColumn() {
        table.addColumn(1);
        table.addColumn(2);
        table.addColumn(6);
        table.addRow(3L);
        table.addRow(4L);

        table.set(1, 3L, "13");
        table.set(1, 4L, "14");
        table.set(2, 3L, "23");
        table.set(2, 4L, "24");

        assertTrue(table.clearColumn(1));
        assertThrows(NoSuchElementException.class, () -> table.clearColumn(5), "Expected exception when clearing non existing Column");
        assertFalse(table.clearColumn(6), "Expected false when clearing empty Column");

        assertNull(table.get(1, 3L));
        assertNull(table.get(1, 4L));
        assertEquals("23", table.get(2, 3L));
        assertEquals("24", table.get(2, 4L));

    }

    @Test
    void tableShouldRemoveSpecifiedRow() {
        table.addColumn(1);
        table.addColumn(2);
        table.addRow(3L);
        table.addRow(4L);
        table.addRow(6L);

        table.set(1, 3L, "13");
        table.set(1, 4L, "14");
        table.set(2, 3L, "23");
        table.set(2, 4L, "24");

        assertTrue(table.removeRow(3L));
        assertFalse(table.removeRow(5L), "Expected false when removing non existing Row");
        assertTrue(table.removeRow(6L), "Expected true when removing empty Row");

        assertFalse(table.containsRow(3L));
        assertTrue(table.containsRow(4L));
        assertFalse(table.containsRow(5L));
        assertFalse(table.containsRow(6L));

        assertFalse(table.contains(1, 3L));
        assertFalse(table.contains(2, 3L));
        assertEquals("14", table.get(1, 4L));
        assertEquals("24", table.get(2, 4L));

    }

    @Test
    void tableShouldRemoveSpecifiedColumn() {
        table.addColumn(1);
        table.addColumn(2);
        table.addColumn(6);
        table.addRow(3L);
        table.addRow(4L);

        table.set(1, 3L, "13");
        table.set(1, 4L, "14");
        table.set(2, 3L, "23");
        table.set(2, 4L, "24");

        assertTrue(table.removeColumn(1));
        assertFalse(table.removeColumn(5), "Expected false when removing non existing Column");
        assertTrue(table.removeColumn(6), "Expected true when removing empty Column");

        assertFalse(table.containsColumn(1));
        assertTrue(table.containsColumn(2));
        assertFalse(table.containsColumn(5));
        assertFalse(table.containsColumn(6));

        assertFalse(table.contains(1, 3L));
        assertFalse(table.contains(1, 4L));
        assertEquals("23", table.get(2, 3L));
        assertEquals("24", table.get(2, 4L));

    }

    @Test
    void tableShouldCheckAvailabilityForValueInSpecifiedColumnAndRow() {
        table.addColumn(1);
        table.addColumn(2);
        table.addRow(3L);
        table.addRow(4L);

        table.set(1, 3L, "13");
        table.set(1, 4L, null);
        table.set(2, 3L, "null");

        assertTrue(table.contains(1, 3L));
        assertTrue(table.contains(1, 4L), "Expected true when value was null.");
        assertTrue(table.contains(2, 3L));
        assertFalse(table.contains(2, 4L));

    }

    @Test
    void tableShouldDisplayItsContentsInTheConsole() {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream old = System.out;
        System.setOut(new PrintStream(out));


        String expected = table.representation(20) + System.getProperty("line.separator");
        table.show();

        table.addColumn(123456);
        table.addColumn(234567);
        table.addRow(345678L);
        table.addRow(45789L);

        table.set(123456, 345678L, "13");
        table.set(123456, 45789L, null);
        table.set(234567, 345678L, "null");


        expected +=
                table.representation(20) + System.getProperty("line.separator") +
                        table.representation(5) + System.getProperty("line.separator") +
                        table.representation(20, "header") + System.getProperty("line.separator") +
                        table.representation(5, "header") + System.getProperty("line.separator");


        table.show();
        table.show(5);
        table.show("header");
        table.show(5, "header");
        System.setOut(old);
        String actual = out.toString();
        assertEquals(expected, actual);


    }
}
