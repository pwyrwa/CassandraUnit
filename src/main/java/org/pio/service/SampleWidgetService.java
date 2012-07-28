package org.pio.service;

import org.pio.cassandra.CacheEntry;
import org.pio.cassandra.CassandraAccessor;
import org.pio.cassandra.ColumnFamilies;
import org.pio.model.SampleWidget;

/**
 * User: pwyrwins
 * Date: 7/28/12
 * Time: 1:31 PM
 */
public class SampleWidgetService {

    private static final String WIDGET_COLUMN = "widget";
    private CassandraAccessor cassandraAccessor;

    public SampleWidgetService(CassandraAccessor cassandraAccessor)
    {
        this.cassandraAccessor = cassandraAccessor;
    }


    public void storeWidget(SampleWidget sampleWidget)
    {
        cassandraAccessor.updateCache(ColumnFamilies.widgetCF(), sampleWidget, sampleWidget.getId(), WIDGET_COLUMN);
    }

    public SampleWidget getWidget(String id)
    {
        CacheEntry<SampleWidget> cachedWidget = cassandraAccessor.getValueFromCache(ColumnFamilies.widgetCF(), id, WIDGET_COLUMN,
                SampleWidget.class);
        if(cachedWidget.isInCache())
        {
            return cachedWidget.getModel();
        }
        return null;
    }


}
