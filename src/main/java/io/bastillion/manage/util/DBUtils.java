/**
 * Copyright 2017 Sean Kavanagh - sean.p.kavanagh6@gmail.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.bastillion.manage.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBUtils {
    public static String DB_PATH;

    public DBUtils() {
    }

    public static Connection getConn() {
        Connection con = null;

        try {
            System.setProperty("h2.baseDir", DB_PATH);
            String user = AppConfig.getProperty("dbUser");
            String password = null;
            if (AppConfig.isPropertyEncrypted("dbPassword")) {
                password = AppConfig.decryptProperty("dbPassword");
            } else {
                password = AppConfig.getProperty("dbPassword");
            }

            String connectionURL = AppConfig.getProperty("dbConnectionURL");
            if (connectionURL != null && connectionURL.contains("CIPHER=")) {
                password = "filepwd " + password;
            }

            con = DriverManager.getConnection(connectionURL, user, password);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return con;
    }

    public static void closeConn(Connection con) {
        try {
            if (con != null) {
                con.close();
            }

            con = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void closeStmt(Statement stmt) {
        try {
            if (stmt != null) {
                stmt.close();
            }

            stmt = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void closeRs(ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }

            rs = null;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
}
