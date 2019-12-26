package com.ofwiki.pagehelper.dialect;

/**
 * @author HuangJS
 * @date 17-11-20 上午11:43.
 */
public class DialectResolver {
    private static final String DBMSNAME_MSSQL = "Microsoft SQL Server";
    private static final String DBMSNAME_POSTGRESQL = "PostgreSQL";
    private static final String DBMSNAME_ORACLE = "Oracle";
    private static final String DBMSNAME_MYSQL = "MySQL";

    public DialectResolver() {
    }

    public static Dialect resolve(String databaseName) {
        try {
            return (Dialect)(databaseName.equalsIgnoreCase("MySQL")?new MySql5Dialect():(!databaseName.equalsIgnoreCase("Microsoft SQL Server") && !databaseName.toUpperCase().contains("MICROSOFT")?null:new MsSql2012Dialect()));
        } catch (Exception var2) {
            throw new IllegalArgumentException("Un-support Database : " + databaseName + " make sure the value contains by : [MYSQL,MSSQL2012]");
        }
    }

    private static enum DialectType {
        MYSQL,
        MSSQL2012,
        MSSQL;

        private DialectType() {
        }
    }
}
