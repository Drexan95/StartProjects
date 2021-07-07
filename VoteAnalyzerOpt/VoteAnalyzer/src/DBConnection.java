import java.sql.*;

public class DBConnection {
    private static Connection connection;

    private static String dbName = "learn";
    private static String dbUser = "root";
    private static String dbPass = "ynzohwtd101";

    static Statement statement;
    private  static StringBuilder sql = new StringBuilder();
    public static int count = 0;


    public DBConnection() throws SQLException {

    }

    public static Connection getConnection() throws SQLException {
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(
                        "jdbc:mysql://localhost:3306/learn?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Tomsk&allowPublicKeyRetrieval=true&useSSL=false&rewriteBatchedStatements=true"
                        , dbUser, dbPass);
                connection.createStatement().execute("DROP TABLE IF EXISTS voter_count");
                connection.createStatement().execute("CREATE TABLE voter_count(" +
                        "id INT NOT NULL AUTO_INCREMENT, " +
                        "name TINYTEXT NOT NULL, " +
                        "birthDate DATE  NOT NULL, " +
                                "`station` INT NOT NULL, "+
                        "workTime TINYTEXT NOT NULL, " +
                        "PRIMARY KEY(id))" );
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        try {
            statement = connection.createStatement();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        connection.setAutoCommit(false);
        return connection;
    }

    public static void executeInsertVoters() throws SQLException {

        try {
            statement.executeBatch();
            connection.commit();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }


    public static void countWorkTime(short station, String workTime) throws SQLException {

        workTime = workTime.replace('.', '-');
        sql.append( station + "','" + workTime + "')");
        statement.addBatch(sql.toString());
        sql.setLength(0);
       count++;

    }

    public static void countVoter(String name, String birthDay) throws SQLException {
        if(count>= 100000){
            executeInsertVoters();
            count = 0;
        }
        birthDay = birthDay.replace('.', '-');
         sql.append("INSERT INTO voter_count(name, birthDate,`station`,workTime) " +
                "VALUES" + "('" + name + "', '" + birthDay + "','");

   }

        public static void printVoterCounts () throws SQLException
        {
            String sql = "SELECT name, birthDate, COUNT(*) AS count FROM voter_count GROUP BY name, birthDate HAVING COUNT(*) > 1";
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
            while (rs.next()) {
                System.out.println("\t" + rs.getString("name") + " (" +
                        rs.getString("birthDate") + ") - " + rs.getInt("count"));
            }
            rs.close();
        }
        public static void printWorkTimeStation () throws SQLException {
            String sql = "SELECT `station`,workTime FROM work_station_time";
            ResultSet rs = DBConnection.getConnection().createStatement().executeQuery(sql);
            while (rs.next()) {
                System.out.println("\t" + rs.getShort("station") + "-" + rs.getString("workTime"));
            }
            rs.close();
        }


    }

