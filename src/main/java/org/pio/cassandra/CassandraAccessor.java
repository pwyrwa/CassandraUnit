package org.pio.cassandra;

import javax.annotation.Resource;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.netflix.astyanax.ColumnListMutation;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.MutationBatch;
import com.netflix.astyanax.connectionpool.exceptions.ConnectionException;
import com.netflix.astyanax.model.Column;
import com.netflix.astyanax.model.ColumnFamily;
import com.netflix.astyanax.model.ColumnList;
import com.netflix.astyanax.query.RowQuery;

/**
 * User: pwyrwins
 * Date: 7/9/12
 * Time: 9:33 PM
 */
public class CassandraAccessor {

    private Logger LOG = LoggerFactory.getLogger(CassandraAccessor.class);

    @Resource(name = "cacheJsonMapper")
    private ObjectMapper objectMapper;

    @Resource(name = "cacheKeyspace")
    private Keyspace keyspace;

    public CassandraAccessor() {
    }

    @VisibleForTesting
    CassandraAccessor(ObjectMapper objectMapper, Keyspace keyspace) {
        this.objectMapper = objectMapper;
        this.keyspace = keyspace;
    }

    public <M> CacheEntry<M> getValueFromCache(ColumnFamily<String, String> columnFamily,
                                               String rowKey,
                                               String columnKey, Class<M> type) {
        RowQuery<String, String> rowQuery = keyspace.prepareQuery(columnFamily).getKey(rowKey);

        CacheEntry<M> cacheEntry;
        try {
            ColumnList<String> row = rowQuery.execute().getResult();

            if (row.isEmpty()) {
                cacheEntry = CacheEntry.notInCache();
            } else {
                Column<String> column = row.getColumnByName(columnKey);
                if (column == null) {
                    cacheEntry = CacheEntry.notInCache();
                } else {
                    String value = column.getStringValue();
                    cacheEntry = CacheEntry.inCache(objectMapper.readValue(value, type));
                }
            }
        } catch (Exception e) {
            LOG.error("Exception reading from cassandra {}", e);
            cacheEntry = CacheEntry.error(e);
        }

        return cacheEntry;
    }

    public CacheEntry<?> removeRow(ColumnFamily<String, String> columnFamily,String rowKey) {
        CacheEntry cacheEntry = CacheEntry.updateSuccessful();
        MutationBatch m = keyspace.prepareMutationBatch();
        m.withRow(columnFamily, rowKey).delete();

        try {
            m.execute();
        } catch (ConnectionException e) {
            LOG.error("Exception updating cassandra {}", e);
            cacheEntry = CacheEntry.error(e);
        }
        return cacheEntry;

    }

    public <T> CacheEntry<?> updateCache(ColumnFamily<String, String> columnFamily, T model, String rowKey, String columnKey) {
        CacheEntry cacheEntry = CacheEntry.updateSuccessful();
        MutationBatch m = keyspace.prepareMutationBatch();
        ColumnListMutation<String> columnListMutation = m.withRow(columnFamily, rowKey);
        columnListMutation.putColumn(columnKey, false, null);

        try {
            columnListMutation.putColumn(columnKey, objectMapper.writeValueAsString(model), null);

            m.execute();
        } catch (Exception e) {
            LOG.error("Exception updating cassandra {}", e);
            cacheEntry = CacheEntry.error(e);
        }
        return cacheEntry;
    }
}
