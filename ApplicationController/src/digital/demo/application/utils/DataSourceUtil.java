package digital.demo.application.utils;


import SQLite.JDBCDataSource;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;

import oracle.adfmf.framework.api.AdfmfJavaUtilities;

public class DataSourceUtil {
    
    private static Connection conn = null;
    
    private static List stmts = new ArrayList();
    
    private static Exception exception = null;
    
    public static void initialise() throws Exception {
        try {
            File dbFile = new File(getDatabaseDirectoryPath());
            if (!dbFile.exists()) {
                createDatabase ();
            }
        }
        catch (Exception exp) {
            exception = exp;
        }
    }
    
    public static void deInitialise() {
        closeConnection();
    }
    
    private static void createDatabase() throws Exception {
        Statement pStmt = null;

        try {
            ClassLoader cl = DataSourceUtil.class.getClassLoader();
            String databaseSQLFilePath = "sql/database.sql";
            InputStream is = cl.getResourceAsStream(databaseSQLFilePath);
            if (is == null) {
                throw new Exception ("Could not find database init file under: " + databaseSQLFilePath);
            }
            
            BufferedReader bReader = new BufferedReader(new InputStreamReader(is));
            
            String strstmt = "";
            String ln = bReader.readLine();
            while (ln != null) {
                if (ln.startsWith("REM") || ln.startsWith("COMMIT")) {
                    ln = bReader.readLine();
                    continue;
                }
                strstmt = strstmt + ln;
                if (strstmt.endsWith(";")) {
                    System.out.println(strstmt);
                    stmts.add(strstmt);
                    strstmt = "";
                    ln = bReader.readLine();
                    continue;
                }
                ln = bReader.readLine();
            }

            
            getConnection().setAutoCommit(false);
            for (int i = 0; i < stmts.size(); i++) {
                pStmt = getConnection().createStatement();
                pStmt.executeUpdate((String)stmts.get(i));
            }
            getConnection().commit();
            
        } catch (Exception expGeneral) {
            if (expGeneral instanceof Exception) {
                throw (Exception) expGeneral;
            }
            throw new Exception ("Exception while creating SQLite Database.", expGeneral);
        } finally {
            closeAll(pStmt, null);
        }
    }
    
    public static Connection getConnection() throws Exception {
        if (conn == null) {
            try {
                // create a database connection
                String connStr = "jdbc:sqlite:" + getDatabaseDirectoryPath();
                conn = new JDBCDataSource(connStr).getConnection();
            } catch (Exception expGeneral) {
                if (expGeneral instanceof Exception) {
                    throw (Exception) expGeneral;
                }
                throw new Exception ("Exception while creating SQLite Connection.", expGeneral);
            }
        }
        return conn;
    }
    
    public static String getDatabaseDirectoryPath () {
        return AdfmfJavaUtilities.getDirectoryPathRoot(AdfmfJavaUtilities.ApplicationDirectory) + "/application.db";
    }
    
    public static void closeAll(Statement statement, ResultSet rs) {
        try {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    System.out.println  ("" + e);
                }
            }
        } finally {
            rs = null;
        }
        try {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    System.out.println  ("" + e);
                }
            }
        } finally {
            statement = null;
        }

    }
    
    private static void closeConnection() {
        try {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.out.println  ("" + e);
                }
            }
        } finally {
            conn = null;
        }
    }


}
