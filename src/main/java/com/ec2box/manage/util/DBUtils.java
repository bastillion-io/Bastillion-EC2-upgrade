/**
 * Copyright 2017 Sean Kavanagh - sean.p.kavanagh6@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ec2box.manage.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBUtils {
    public static String DB_PATH = null;

    public DBUtils() {
    }

    public static Connection getConn() {
        Connection con = null;

        try {
            System.setProperty("h2.implicitRelativePath", "true");
            Class.forName("org.h2.Driver");
            String user = "ec2box";
            String password="filepwd 0WJLnwhpA47EepT1A4drVnDn3vYRvJhpZi0sVdvN9SmlbKw";
            con = DriverManager.getConnection("jdbc:h2:" + DB_PATH + "/ec2box;CIPHER=AES", user, password);
        } catch (Exception var3) {
            var3.printStackTrace();
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
