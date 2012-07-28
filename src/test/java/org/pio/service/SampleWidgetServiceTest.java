package org.pio.service;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.pio.cassandra.BaseCassandraTest;
import org.pio.model.SampleWidget;

/**
 * User: pwyrwins
 * Date: 7/28/12
 * Time: 1:41 PM
 */
public class SampleWidgetServiceTest extends BaseCassandraTest {

    SampleWidgetService service;


    @Before
    public void setup()
    {
        service = new SampleWidgetService(cassandraAccessor);
    }
    @Test
    public void test_store_get_ok()
    {
        SampleWidget widget = new SampleWidget("1L", "myModel", "myDescription");

        service.storeWidget(widget);

        SampleWidget restoredWidget = service.getWidget("1L");
        assertEquals(restoredWidget, widget);
    }



}
