package datastructures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PairedHashMapTableTest extends TableSpec {

    @Override
    protected Table<String, Integer, Long> getInstance() {
        return new PairedHashMapTable<>();
    }

    @Test
    void tableShouldReturnAStringRepresentationOfItsContents() {

        assertEquals("Table has 0 Columns and 0 Rows.Add more data to visualize the Table",
                table.representation(20));

        table.addColumn(123456);
        table.addColumn(234567);

        assertEquals("Table has 2 Columns and 0 Rows.Add more data to visualize the Table",
                table.representation(20));

        table.addRow(345678L);
        table.addRow(45789L);

        table.set(123456, 345678L, "13");
        table.set(123456, 45789L, null);
        table.set(234567, 345678L, "null");


        String expected = "+--------------------+--------------------+--------------------+" + System.getProperty("line.separator") +
                "|                    |              123456|              234567|" + System.getProperty("line.separator") +
                "+--------------------+--------------------+--------------------+" + System.getProperty("line.separator") +
                "|              345678|                  13|                null|" + System.getProperty("line.separator") +
                "|               45789|            <<null>>|                    |" + System.getProperty("line.separator") +
                "+--------------------+--------------------+--------------------+" + System.getProperty("line.separator") +
                "+-----+-----+-----+" + System.getProperty("line.separator") +
                "|     |12...|23...|" + System.getProperty("line.separator") +
                "+-----+-----+-----+" + System.getProperty("line.separator") +
                "|34...|   13| null|" + System.getProperty("line.separator") +
                "|45789|<<...|     |" + System.getProperty("line.separator") +
                "+-----+-----+-----+" + System.getProperty("line.separator") +
                "+--------------------+--------------------+--------------------+" + System.getProperty("line.separator") +
                "|              header|              123456|              234567|" + System.getProperty("line.separator") +
                "+--------------------+--------------------+--------------------+" + System.getProperty("line.separator") +
                "|              345678|                  13|                null|" + System.getProperty("line.separator") +
                "|               45789|            <<null>>|                    |" + System.getProperty("line.separator") +
                "+--------------------+--------------------+--------------------+" + System.getProperty("line.separator") +
                "+-----+-----+-----+" + System.getProperty("line.separator") +
                "|he...|12...|23...|" + System.getProperty("line.separator") +
                "+-----+-----+-----+" + System.getProperty("line.separator") +
                "|34...|   13| null|" + System.getProperty("line.separator") +
                "|45789|<<...|     |" + System.getProperty("line.separator") +
                "+-----+-----+-----+" + System.getProperty("line.separator");
        String actual =
                table.representation(20) + System.getProperty("line.separator") +
                        table.representation(5) + System.getProperty("line.separator") +
                        table.representation(20, "header") + System.getProperty("line.separator") +
                        table.representation(5, "header") + System.getProperty("line.separator");

        assertEquals(expected, actual);

    }
}
