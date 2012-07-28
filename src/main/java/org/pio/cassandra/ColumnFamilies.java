package org.pio.cassandra;

import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.serializers.StringSerializer;

/**
 *
 * Repository for all cassandra columns
 * User: pwyrwins
 * Date: 7/28/12
 * Time: 12:02 PM
 */
public final class ColumnFamilies {

    private static ColumnFamily<String, String> widgetCF = new ColumnFamily<String, String>("sampleWidget", StringSerializer.get(),
            StringSerializer.get());

    public static ColumnFamily<String, String> widgetCF() {
        return widgetCF;
    }

}
