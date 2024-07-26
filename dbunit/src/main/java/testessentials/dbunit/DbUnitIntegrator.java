package testessentials.dbunit;

import static java.util.Objects.requireNonNull;
import static org.dbunit.database.DatabaseConfig.FEATURE_QUALIFIED_TABLE_NAMES;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dbunit.*;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import testessentials.dbunit.annotation.DataSourceContextProvider;
import testessentials.dbunit.annotation.DatabaseExpectation;
import testessentials.dbunit.annotation.DatabaseSetUp;

@RequiredArgsConstructor
public class DbUnitIntegrator {

  private static final String COMPOSITE_FILENAME_PATTERN = "dbunit/composites/%s.xml";
  private static final String EXPECTED_SUFFIX = "-expected.xml";
  private static final String FILENAME_PATTERN = "dbunit/%s/%s%s";
  private static final String SETUP_SUFFIX = "-setup.xml";

  protected final Set<String> tablesNamesToDeleteFrom = new HashSet<>();

  private final Class<?> testClass;
  private final Method testMethod;

  public void beforeTestMethod() throws Exception {
    String methodName = testMethod.getName();
    DatabaseSetUp databaseSetUp = testMethod.getAnnotation(DatabaseSetUp.class);
    requireNonNull(databaseSetUp, "You need to setup a database setup via @DatabaseSetUp!");
    List<IDataSet> dataSets = new ArrayList<>();
    addCompositesIfNecessary(dataSets, databaseSetUp.dependsOn());
    InputStream inputStream =
        getInputStreamForDataSet(databaseSetUp.value(), methodName, SETUP_SUFFIX);
    dataSets.add(createReplacementDataSet(new FlatXmlDataSetBuilder().build(inputStream)));
    collectTablesForDeletion(dataSets);
    IDatabaseConnection databaseConnection = createDatabaseConnection(findDataSource());
    turnOffConstraintChecking(databaseConnection);
    IDatabaseTester databaseTester = new DefaultDatabaseTester(databaseConnection);
    CompositeDataSet compositeDataSet = new CompositeDataSet(dataSets.toArray(new IDataSet[] {}));
    databaseTester.setDataSet(compositeDataSet);
    databaseTester.onSetup();
  }

  private void addCompositesIfNecessary(List<IDataSet> dataSets, String[] compositeNames)
      throws DataSetException {
    if (ArrayUtils.isNotEmpty(compositeNames)) {
      for (String dependsOn : compositeNames) {
        InputStream compositeInputStream = getInputStreamForCompositeDataSet(dependsOn);
        dataSets.add(
            createReplacementDataSet(new FlatXmlDataSetBuilder().build(compositeInputStream)));
      }
    }
  }

  private InputStream getInputStreamForCompositeDataSet(String dependsOn) {
    String filename = String.format(COMPOSITE_FILENAME_PATTERN, dependsOn);
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
    requireNonNull(
        inputStream,
        "input stream for composite '"
            + dependsOn
            + "' is null! Please check the file is existing in"
            + " `dbunit/composites`! Filename was: "
            + filename);
    return inputStream;
  }

  private void turnOffConstraintChecking(IDatabaseConnection databaseConnection)
      throws SQLException {
    try (Statement statement = databaseConnection.getConnection().createStatement()) {
      statement.executeUpdate("SET FOREIGN_KEY_CHECKS=0;");
    }
  }

  private InputStream getInputStreamForDataSet(
      String annotationValue, String methodName, String suffix) {
    String specifiedFilename = StringUtils.defaultIfBlank(annotationValue, methodName);
    String filename =
        String.format(FILENAME_PATTERN, getClass().getSimpleName(), specifiedFilename, suffix);
    InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
    requireNonNull(
        inputStream,
        "input stream for '"
            + suffix
            + "' is null! Please check the file is existing in"
            + " `dbunit/YOUR_TEST_SIMPLE_CLASSNAME`! Filename was: "
            + filename);
    return inputStream;
  }

  private IDatabaseConnection createDatabaseConnection(DataSourceContext dataSourceContext)
      throws Exception {
    IDatabaseTester databaseTester =
        new JdbcDatabaseTester(
            dataSourceContext.driverClassName(),
            dataSourceContext.databaseConnectionUrl() + "/" + dataSourceContext.databaseName(),
            dataSourceContext.databaseUser(),
            dataSourceContext.databasePassword());
    IDatabaseConnection connection = databaseTester.getConnection();
    connection.getConfig().setProperty(FEATURE_QUALIFIED_TABLE_NAMES, true);
    connection.getConfig().setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN, "`?`");
    return connection;
  }

  public void afterTestMethod() throws Exception {
    String methodName = testMethod.getName();
    DatabaseExpectation databaseExpectation = testClass.getAnnotation(DatabaseExpectation.class);
    IDatabaseConnection databaseConnection = createDatabaseConnection(findDataSource());
    IDatabaseTester databaseTester = new DefaultDatabaseTester(databaseConnection);
    try {
      if (databaseExpectation != null) {
        List<IDataSet> expectedDataSets = new ArrayList<>();
        addCompositesIfNecessary(expectedDataSets, databaseExpectation.dependsOn());
        InputStream inputStream =
            getInputStreamForDataSet(databaseExpectation.value(), methodName, EXPECTED_SUFFIX);
        expectedDataSets.add(
            createReplacementDataSet(new FlatXmlDataSetBuilder().build(inputStream)));
        for (IDataSet expectedDataSet : expectedDataSets) {
          String[] tableNames = expectedDataSet.getTableNames();
          IDataSet actualDataSet = databaseTester.getConnection().createDataSet();
          for (String tableName : tableNames) {
            ITable expectedTable = expectedDataSet.getTable(tableName);
            ITable actualTable = actualDataSet.getTable(tableName);
            String[] ignoreColumns = getIgnoreColumns(expectedTable, actualTable);
            Assertion.assertEqualsIgnoreCols(expectedTable, actualTable, ignoreColumns);
          }
        }
      }
    } finally {
      databaseTester.onTearDown();
      turnOffConstraintChecking(databaseConnection);
      cleanUpUsedTables(databaseConnection);
    }
  }

  private DataSourceContext findDataSource() {
    Optional<Method> optionalMethod =
        Arrays.stream(testClass.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(DataSourceContextProvider.class))
            .findFirst();
    if (optionalMethod.isPresent()) {
      try {
        return (DataSourceContext) optionalMethod.get().invoke(this);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new IllegalStateException(e);
      }
    } else {
      throw new IllegalStateException(
          "No @DataSourceProvider method found for test class: " + testClass.getName());
    }
  }

  private String[] getIgnoreColumns(ITable expectedTable, ITable actualTable)
      throws DataSetException {
    Set<String> ignoredColumns = new HashSet<>();
    for (Column column : actualTable.getTableMetaData().getColumns()) {
      ignoredColumns.add(column.getColumnName());
    }
    for (Column column : expectedTable.getTableMetaData().getColumns()) {
      ignoredColumns.remove(column.getColumnName());
    }
    return ignoredColumns.toArray(new String[] {});
  }

  private ReplacementDataSet createReplacementDataSet(FlatXmlDataSet dataSet) {
    return Replacements.addReplacements(new ReplacementDataSet(dataSet));
  }

  private void collectTablesForDeletion(List<IDataSet> dataSets) throws DataSetException {
    for (IDataSet dataSet : dataSets) {
      String[] tableNames = dataSet.getTableNames();
      if (ArrayUtils.isNotEmpty(tableNames)) {
        Collections.addAll(tablesNamesToDeleteFrom, tableNames);
      }
    }
  }

  private void cleanUpUsedTables(IDatabaseConnection databaseConnection) throws SQLException {
    for (String tableName : tablesNamesToDeleteFrom) {
      try (Statement statement = databaseConnection.getConnection().createStatement()) {
        statement.executeUpdate("truncate " + tableName);
      }
    }
    tablesNamesToDeleteFrom.clear();
  }
}
