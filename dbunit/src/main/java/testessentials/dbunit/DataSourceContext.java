package testessentials.dbunit;

public record DataSourceContext(
    String driverClassName,
    String databaseConnectionUrl,
    String databaseName,
    String databaseUser,
    String databasePassword) {}
