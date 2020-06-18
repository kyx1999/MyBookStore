package com.kyx1999.mybookstore.service;

import com.kyx1999.mybookstore.dao.BookMapper;
import com.kyx1999.mybookstore.model.Book;
import com.kyx1999.mybookstore.model.CartItem;
import com.kyx1999.mybookstore.model.OrderItem;
import com.kyx1999.mybookstore.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookService {

    @Autowired
    private BookMapper bookMapper;

    public int insertSelective(Book record) {
        return bookMapper.insertSelective(record);
    }

    public int deleteByPrimaryKey(Integer bid) {
        return bookMapper.deleteByPrimaryKey(bid);
    }

    public Book selectByPrimaryKey(Integer bid) {
        return bookMapper.selectByPrimaryKey(bid);
    }

    public int updateByPrimaryKeySelective(Book record) {
        return bookMapper.updateByPrimaryKeySelective(record);
    }

    public Book[] getTop4SalesBooksThisWeek() {
        Book[] books = bookMapper.getTop4SalesBooksThisWeek();
        if (books == null) {
            books = new Book[0];
        }
        if (books.length < 4) {
            int count = books.length;
            Book[] temp = new Book[4];
            System.arraycopy(books, 0, temp, 0, count);
            books = temp;
            Book[] addTop4SalesBooks = bookMapper.getTopXSalesBooks(4 - count);
            if (addTop4SalesBooks != null) {
                for (Book book : addTop4SalesBooks) {
                    books[count++] = book;
                }
            }
        }
        return books;
    }

    public Book[] getTopXSalesBooks(Integer amount) {
        return bookMapper.getTopXSalesBooks(amount);
    }

    public String[] getCategories() {
        return bookMapper.getCategories();
    }

    public Book[] getAllBooks() {
        return bookMapper.getAllBooks();
    }

    public Book[] getSearchBooks(String keyword) {
        return bookMapper.getSearchBooks(keyword);
    }

    public Book[] getBooksByCartItems(CartItem[] cartItems) {
        if (cartItems.length == 0) {
            return null;
        }

        Book[] books = new Book[cartItems.length];
        for (int i = 0; i < cartItems.length; i++) {
            books[i] = bookMapper.selectByPrimaryKey(cartItems[i].getBid());
        }

        return books;
    }

    public Book[] getBooksByOrderItems(OrderItem[] orderItems) {
        if (orderItems.length == 0) {
            return null;
        }

        Book[] books = new Book[orderItems.length];
        for (int i = 0; i < orderItems.length; i++) {
            books[i] = bookMapper.selectByPrimaryKey(orderItems[i].getBid());
        }

        return books;
    }

    public StringBuilder getSales() {
        StringBuilder sales = new StringBuilder();
        sales.append("编号,书名,分类,价格,库存,销量\r\n");
        Book[] books = bookMapper.getAllBooks();
        for (Book book : books) {
            sales.append(book.getBid()).append(",").append(Tools.csvCheck(book.getBname())).append(",").append(Tools.csvCheck(book.getCategory())).append(",").append(book.getPrice()).append(",").append(book.getAmount()).append(",").append(book.getSales()).append("\r\n");
        }
        return sales;
    }

    public Integer getBooksCount() {
        return bookMapper.getBooksCount();
    }

    public Book[] getBooksByPage(Integer page) {
        return bookMapper.getBooksFromX((page-1) * 10);
    }
}
