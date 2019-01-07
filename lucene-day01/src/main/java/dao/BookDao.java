package dao;

import po.Book;

import java.util.List;

public interface BookDao {


    List<Book> findAll();

}
