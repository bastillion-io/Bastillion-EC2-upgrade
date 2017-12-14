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
import com.ec2box.manage.util.AppConfig;
import com.ec2box.manage.util.DBUtils;
import com.ec2box.manage.util.KeyStoreUtil;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

public class Upgrade {
    public Upgrade() {
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Must run command as: java -jar ec2box-upgrade-0.35.00.jar <whatever path>/ec2box.h2.db");
            System.exit(1);
        }

        DBUtils.DB_PATH = args[0].replaceAll("\\/ec2box.(h2|mv).db", "").replaceAll("ec2box.(h2|mv).db", ".");
        Connection con = DBUtils.getConn();
        if (con != null) {
            PropertiesConfiguration prop = AppConfig.prop;
            prop.getLayout().setComment("dbPath", "");
            prop.clearProperty("dbPath");
            prop.getLayout().setComment("dbOptions", "");
            prop.clearProperty("dbOptions");
            prop.getLayout().setComment("enableOTP", "");
            prop.clearProperty("enableOTP");


            prop.getLayout().setComment("ec2Regions", "");
            prop.clearProperty("ec2Regions");
            prop.addProperty("ec2Regions", "ec2.us-east-1.amazonaws.com:US East (N. Virginia),ec2.us-east-2.amazonaws.com:US East (Ohio),ec2.us-west-1.amazonaws.com:US West (N. California),ec2.us-west-2.amazonaws.com:US West (Oregon),ec2.ca-central-1.amazonaws.com:Canada (Central),ec2.ap-south-1.amazonaws.com:Asia Pacific (Mumbai),ec2.ap-northeast-2.amazonaws.com:Asia Pacific (Seoul),ec2.ap-southeast-1.amazonaws.com:Asia Pacific (Singapore),ec2.ap-southeast-2.amazonaws.com:Asia Pacific (Sydney),ec2.ap-northeast-1.amazonaws.com:Asia Pacific (Tokyo),ec2.eu-central-1.amazonaws.com:EU (Frankfurt),ec2.eu-west-1.amazonaws.com:EU (Ireland),ec2.eu-west-2.amazonaws.com:EU (London),ec2.sa-east-1.amazonaws.com:South America (São Paulo)");
            prop.getLayout().setComment("ec2Regions", "ec2 region select values");

            prop.getLayout().setComment("alarmState", "");
            prop.clearProperty("alarmState");
            prop.addProperty("alarmState", "OK:OK,INSUFFICIENT_DATA:Insufficient Data,ALARM:Alarm");
            prop.getLayout().setComment("alarmState", "alarm state select values");

            prop.getLayout().setComment("systemStatus", "");
            prop.clearProperty("systemStatus");
            prop.addProperty("systemStatus", "ok:OK,impaired:Impaired,insufficient-data:Insufficient Data,not-applicable:Not-Applicable");
            prop.getLayout().setComment("systemStatus", "system status select values");

            prop.getLayout().setComment("instanceStatus", "");
            prop.clearProperty("instanceStatus");
            prop.addProperty("instanceStatus", "ok:OK,impaired:Impaired,insufficient-data:Insufficient Data,not-applicable:Not-Applicable");
            prop.getLayout().setComment("instanceStatus", "instance status select values");

            prop.getLayout().setComment("instanceState", "");
            prop.clearProperty("instanceState");
            prop.addProperty("instanceState", "pending:Pending,running:Running,shutting-down:Shutting-down,terminated:Terminated,stopping:Stopping,stopped:Stopped");
            prop.getLayout().setComment("instanceState", "default instance state");

            if (prop.getProperty("oneTimePassword") == null) {
                prop.addProperty("oneTimePassword", "optional");
                prop.getLayout().setComment("oneTimePassword", "enable two-factor authentication with a one-time password - 'required', 'optional', or 'disabled'");
            }

            if (prop.getProperty("use256EncryptionKey") == null) {
                prop.addProperty("use256EncryptionKey", "false");
                prop.getLayout().setComment("use256EncryptionKey", "Requires JDK with \"Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files\" installed - http://www.oracle.com/technetwork/java/javase/downloads/index.html");
            }

            if (prop.getProperty("sessionTimeout") == null) {
                prop.addProperty("sessionTimeout", "15");
                prop.getLayout().setComment("sessionTimeout", "The session time out value of application in minutes");
            }

            if (prop.getProperty("jaasModule") == null) {
                prop.addProperty("jaasModule", "");
                prop.getLayout().setComment("jaasModule", "specify a external authentication module (ex: ldap-ol: ldap-ad).  Edit the jaas.conf to set connection details");
            }

            if (prop.getProperty("dbUser") == null) {
                prop.addProperty("dbUser", "ec2box");
                prop.getLayout().setComment("dbUser", "Database user");
            }

            if (prop.getProperty("dbPassword") == null) {
                prop.addProperty("dbPassword", "");
                prop.getLayout().setComment("dbPassword", "Database password");
            }

            if (prop.getProperty("dbDriver") == null) {
                prop.addProperty("dbDriver", "org.h2.Driver");
                prop.getLayout().setComment("dbDriver", "Database JDBC driver");
            }

            if (prop.getProperty("dbConnectionURL") == null) {
                prop.addProperty("dbConnectionURL", "jdbc:h2:ec2db/ec2box;CIPHER=AES;");
                prop.getLayout().setComment("dbConnectionURL", "Connection URL to the DB");
            }

            if (prop.getProperty("userTagName") == null) {
                prop.addProperty("userTagName", "ec2box-user");
                prop.getLayout().setComment("userTagName", "The optional tag on an instance that defines the host user to use");
            }

            String dbPassword = null;
            String dbPasswordConfirm = null;
            if (!StringUtils.isEmpty(AppConfig.getProperty("dbPassword"))) {
                if (!AppConfig.isPropertyEncrypted("dbPassword")) {
                    AppConfig.encryptProperty("dbPassword", AppConfig.getProperty("dbPassword"));
                }
            } else {
                while (true) {
                    if (dbPassword != null && dbPassword.equals(dbPasswordConfirm)) {
                        if (StringUtils.isNotEmpty(dbPassword)) {
                            AppConfig.encryptProperty("dbPassword", dbPassword);
                        } else {
                            System.out.println("Generating random database password");
                            dbPassword = RandomStringUtils.randomAscii(32);
                            AppConfig.encryptProperty("dbPassword", dbPassword);
                        }
                        break;
                    }
                    if (System.console() == null) {
                        Scanner in = new Scanner(System.in);
                        System.out.println("Please enter a new database password: ");
                        dbPassword = in.nextLine();
                        System.out.println("Please confirm database password: ");
                        dbPasswordConfirm = in.nextLine();
                    } else {
                        dbPassword = new String(System.console().readPassword("Please enter a new database password: "));
                        dbPasswordConfirm = new String(System.console().readPassword("Please confirm database password: "));
                    }

                    if (!dbPassword.equals(dbPasswordConfirm)) {
                        System.out.println("Passwords do not match");
                    }
                }
            }

            try {
                Statement ex = con.createStatement();
                ex.executeUpdate("alter table session_log add column first_nm varchar");
                ex.executeUpdate("alter table session_log add column last_nm varchar");
                ex.executeUpdate("alter table session_log add column username varchar");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("alter table terminal_log add column display_nm varchar");
                ex.executeUpdate("alter table terminal_log add column user varchar");
                ex.executeUpdate("alter table terminal_log add column host varchar");
                ex.executeUpdate("alter table terminal_log add column port INTEGER");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("update session_log s set first_nm = (select first_nm from users u where s.user_id = u.id)");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("update session_log s set last_nm = (select last_nm from users u where s.user_id = u.id)");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("update session_log s set username = (select username from users u where s.user_id = u.id)");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("alter table session_log alter username set not null");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("alter table session_log drop column user_id");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("delete from users where enabled=false");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("alter table users drop column enabled");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("alter table users add constraint username_unique unique(username)");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("update terminal_log t set display_nm = (select display_nm from system s where t.system_id = s.id)");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("update terminal_log t set user = (select user from system s where t.system_id = s.id)");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("update terminal_log t set host = (select host from system s where t.system_id = s.id)");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("update terminal_log t set port = (select port from system s where t.system_id = s.id)");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("update terminal_log t set instance_id = 1 where instance_id is null");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("alter table terminal_log alter display_nm set not null");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("alter table terminal_log alter user set not null");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("alter table terminal_log alter host set not null");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("alter table terminal_log alter port set not null");
                DBUtils.closeStmt(ex);
                ex = con.createStatement();
                ex.executeUpdate("alter table terminal_log drop column system_id");
                DBUtils.closeStmt(ex);
            } catch (Exception ex) {
                System.err.println("Upgrade failed");
                ex.printStackTrace();
                DBUtils.closeConn(con);
                System.exit(1);
            }

            try {
                Statement ex = con.createStatement();
                ex.executeUpdate("alter table session_log add column ip_address varchar");
                DBUtils.closeStmt(ex);
            } catch (Exception ex) {
                System.err.println("Upgrade failed");
                ex.printStackTrace();
                DBUtils.closeConn(con);
                System.exit(1);
            }


            try {
                Statement ex = con.createStatement();
                ex.executeUpdate("alter table users add column auth_type varchar not null default 'BASIC'");
                DBUtils.closeStmt(ex);
            } catch (Exception ex) {
                System.err.println("Upgrade failed");
                ex.printStackTrace();
                DBUtils.closeConn(con);
                System.exit(1);
            }

            try {
                Statement ex = con.createStatement();
                ex.executeUpdate("alter user ec2box SET PASSWORD '" + dbPassword + "'");
                DBUtils.closeStmt(ex);
            } catch (Exception ex) {
                System.err.println("Upgrade failed");
                ex.printStackTrace();
                System.exit(1);
            } finally {
                DBUtils.closeConn(con);
            }

            KeyStoreUtil.initializeKeyStore();
            System.out.println("Upgrade successful");
        }

    }

}
