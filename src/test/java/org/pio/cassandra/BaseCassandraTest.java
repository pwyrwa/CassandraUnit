package org.pio.cassandra;

import java.io.IOException;

import org.apache.cassandra.config.ConfigurationException;
import org.apache.thrift.transport.TTransportException;
import org.cassandraunit.DataLoader;
import org.cassandraunit.dataset.json.ClassPathJsonDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.netflix.astyanax.AstyanaxContext;
import com.netflix.astyanax.Keyspace;
import com.netflix.astyanax.connectionpool.NodeDiscoveryType;
import com.netflix.astyanax.connectionpool.impl.ConnectionPoolConfigurationImpl;
import com.netflix.astyanax.connectionpool.impl.CountingConnectionPoolMonitor;
import com.netflix.astyanax.impl.AstyanaxConfigurationImpl;
import com.netflix.astyanax.thrift.ThriftFamilyFactory;

/**
 * Base test providing lifecycle for using embedded cassandra
 *
 * User: piotwy01
 * Date: 7/11/12
 * Time: 4:10 PM
 */
public class BaseCassandraTest
{

    protected CassandraAccessor cassandraAccessor;

    protected Keyspace keyspace;

    @BeforeClass
    public static void startCassandra()
            throws IOException, TTransportException, ConfigurationException, InterruptedException
    {
        EmbeddedCassandraServerHelper.startEmbeddedCassandra("cassandra.yaml");
    }

    @Before
    public void setUp() throws IOException, TTransportException, ConfigurationException, InterruptedException
    {
        DataLoader dataLoader = new DataLoader("TestCluster", "localhost:9272");
        dataLoader.load(new ClassPathJsonDataSet("dataset.json"));

        AstyanaxContext<Keyspace> context = new AstyanaxContext.Builder().forCluster("TestCluster")
                .forKeyspace("test_keyspace")
                .withAstyanaxConfiguration(new AstyanaxConfigurationImpl().setDiscoveryType(NodeDiscoveryType.NONE))
                .withConnectionPoolConfiguration(
                        new ConnectionPoolConfigurationImpl("testConnectionPool").setPort(9272).setMaxConnsPerHost(1)
                                .setSeeds("localhost:9272"))
                .withConnectionPoolMonitor(new CountingConnectionPoolMonitor())
                .buildKeyspace(ThriftFamilyFactory.getInstance());

        context.start();
        keyspace = context.getEntity();
        cassandraAccessor = new CassandraAccessor(new ObjectMapper(), keyspace);
    }

    @After
    public void clearCassandra()
    {
        EmbeddedCassandraServerHelper.cleanEmbeddedCassandra();
    }

    @AfterClass
    public static void stopCassandra()
    {
        EmbeddedCassandraServerHelper.stopEmbeddedCassandra();
    }

}
