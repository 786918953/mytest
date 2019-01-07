package dao.impl;

import dao.BookDao;
import po.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl implements BookDao {
    @Override
    public List<Book> findAll() {

        List<Book> bookList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/lucene", "root", "root");
            String sql = "select * from book";
            statement = connection.prepareStatement(sql);
            rs = statement.executeQuery();
            while (rs.next()) {
                Book book = new Book();
                book.setId(rs.getInt("id"));
                book.setBookName(rs.getString("bookname"));
                book.setPrice(rs.getFloat("price"));
                book.setPic(rs.getString("pic"));
                book.setBookDesc(rs.getString("bookdesc"));
                bookList.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return bookList;
    }
}
