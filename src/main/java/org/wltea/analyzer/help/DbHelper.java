package org.wltea.analyzer.help;

import com.mysql.cj.jdbc.Driver;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author MUSI
 * @Date 2022/12/17 10:58 AM
 * @Description
 * @Version
 **/
public class DbHelper {


    static String url = "jdbc:mysql://192.168.31.130:43306/es_plugins?characterEncoding=utf8&useSSL=false&allowPublicKeyRetrieval=true";
    static String username = "root";
    static String password = "mysqlroot";

    private static final Logger logger = ESPluginLoggerFactory.getLogger(DbHelper.class.getName());

    static {
        try {
            logger.info("加载驱动class 》》》》》》");
            Class.forName(Driver.class.getName());
            logger.info("驱动class加载完成 》》》》》》");
        } catch (Exception e) {
            logger.info("加载驱动class 失败 》》》》》》");
        }
    }

    public static List<String> getHotWords(){
        List<String> hotWords = new ArrayList<>();
        String sql = " SELECT keyword from es_word WHERE `status` = 1 ";
        Connection connect = getConnect();
        try {
            PreparedStatement preparedStatement = connect.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String keyword = resultSet.getString("keyword");
                hotWords.add(keyword);
            }
            resultSet.close();
            preparedStatement.close();
            if (!hotWords.isEmpty()){
                for (String hotWord : hotWords) {
                    String updateSql = " update es_word set `status` = 0 where  `keyword` = ? ";
                    PreparedStatement statement = connect.prepareStatement(updateSql);
                    statement.setString(1, hotWord);
                    statement.executeUpdate();
                }
            }
            connect.close();
        }catch (Exception ex){
            logger.info("【getHotWords】发生异常", ex);
        }
        logger.info("【getHotWords】获取到的词段数量 {}", hotWords.size());
        return hotWords;
    }

    private static Connection getConnect(){
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        for (String hotWord : DbHelper.getHotWords()) {
            System.out.println(hotWord);
        }
    }
}
