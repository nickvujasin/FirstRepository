package com.rest.dao.impl.mybatis;

import static org.junit.Assert.assertNotNull;

import javax.cache.CacheManager;
import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.h2.H2DataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-test.xml")
public class BaseDAOTest {
	
	@Autowired
	private DataSource dataSource;
	
	@Autowired
	protected CacheManager cacheManager;
	
	@Before
	public void clearTables() throws Exception {
		// Before every test do a clean insert with the data in the given xml file.
		loadDataSet(dataSource, "db/UnitTestData.xml");
		
		// Before every test clear the entire cache.
		for (String cacheName : cacheManager.getCacheNames()) {
			cacheManager.getCache(cacheName).clear();
		}
	}

	@Test
	public void testDataSourceNotNull() {
		assertNotNull(dataSource);
	}
	
	/**
	 * Loads data into the database from a DBUnit data set. This performs a
	 * "CLEAN_INSERT", so that those tables which are referenced in the given data
	 * set are first truncated, then the data is loaded. The format of the data is a
	 * FlatXmlDataSet {@link http://dbunit.sourceforge.net/components.html#dataset}
	 * <p>
	 * Beware of foreign key constraints in the schema -- order can be very
	 * important inside the data set.
	 * </p>
	 * 
	 * @param dataSource  the DataSource to use
	 * @param dataSetPath the <em>classpath</em> name of the data set to load.
	 */
	private static void loadDataSet(final DataSource dataSource, final String dataSetPath) throws Exception {
		IDatabaseConnection connection = connect(dataSource);
		
		IDataSet ds = new FlatXmlDataSetBuilder().build(new ClassPathResource(dataSetPath).getFile());

		DatabaseOperation.CLEAN_INSERT.execute(connection, ds);

		connection.close();
	}

	/**
	 * Delete data from the given tables.
	 * @param dataSource
	 * @param tables
	 * @throws Exception
	 */
	public static void clearTables(final DataSource dataSource, final String[] tables) throws Exception {
		
		IDatabaseConnection connection = connect(dataSource);

		QueryDataSet ds = new QueryDataSet(connection);
		
		for (String table : tables) {
			ds.addTable(table);
		}

		DatabaseOperation.DELETE_ALL.execute(connection, ds);
		
		connection.close();
	}

	/**
	 * Delete data from the given table.
	 * @param dataSource
	 * @param table
	 * @throws Exception
	 */
	public static void clearTable(final DataSource dataSource, final String table) throws Exception {
		IDatabaseConnection connection = connect(dataSource);

		QueryDataSet ds = new QueryDataSet(connection);
		ds.addTable(table);

		DatabaseOperation.DELETE_ALL.execute(connection, ds);
		
		connection.close();
	}

	/**
	 * Connect to the given data source.
	 * @param dataSource
	 * @return
	 * @throws Exception
	 */
	private static IDatabaseConnection connect(final DataSource dataSource) throws Exception {

		DataSourceDatabaseTester tester = new DataSourceDatabaseTester(dataSource);
		IDatabaseConnection connection = tester.getConnection();

		DatabaseConfig config = connection.getConfig();

		// Configuring H2.
		config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new H2DataTypeFactory());
		config.setProperty(DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES, false);
		config.setProperty(DatabaseConfig.FEATURE_CASE_SENSITIVE_TABLE_NAMES, false);

		return connection;
	}
}
