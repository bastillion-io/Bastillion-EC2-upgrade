/**
 * Copyright 2017 Sean Kavanagh - sean.p.kavanagh6@gmail.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file stmtcept in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either stmtpress or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.bastillion.manage.util.DBUtils;

import java.sql.Connection;
import java.sql.Statement;

public class Upgrade {
    public Upgrade() {
    }

    public static void main(String[] args) {
        if (args.length != 1 || !args[0].contains("BastillionConfig.properties")) {
            System.err.println("Must run command as: java -jar bastillion-ec2-upgrade.jar <whatever path>/BastillionConfig.properties");
            System.exit(1);
        }

        DBUtils.DB_PATH = args[0].replaceAll("BastillionConfig.properties", "");
        Connection con = DBUtils.getConn();
        if (con != null) {
            Statement stmt = null;

            try {
                stmt = con.createStatement();
                stmt.executeUpdate("create table if not exists license (id INTEGER PRIMARY KEY AUTO_INCREMENT, license_tx varchar not null)");
                DBUtils.closeStmt(stmt);

            } catch (Exception ex) {
                if (stmt != null) {
                    System.out.println(stmt.toString());
                }
                ex.printStackTrace();
            } finally {
                DBUtils.closeConn(con);
            }

            System.out.println("Upgrade successful");
        }

    }
}
