package com.kyx1999.mybookstore.dao;

import com.kyx1999.mybookstore.model.Book;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface BookMapper {
    int deleteByPrimaryKey(Integer bid);

    int insert(Book record);

    int insertSelective(Book record);

    Book selectByPrimaryKey(Integer bid);

    int updateByPrimaryKeySelective(Book record);

    int updateByPrimaryKey(Book record);

    Book[] getTop4SalesBooksThisWeek();

    Book[] getTopXSalesBooks(Integer amount);

    String[] getCategories();

    Book[] getAllBooks();

    Book[] getSearchBooks(String keyword);

    Integer getBooksCount();

    Book[] getBooksFromX(Integer index);
}
