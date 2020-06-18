package com.kyx1999.mybookstore.controller;

import com.kyx1999.mybookstore.model.Book;
import com.kyx1999.mybookstore.model.OrderInfo;
import com.kyx1999.mybookstore.model.User;
import com.kyx1999.mybookstore.service.BookService;
import com.kyx1999.mybookstore.service.BulletinService;
import com.kyx1999.mybookstore.service.OrderService;
import com.kyx1999.mybookstore.service.UserService;
import com.kyx1999.mybookstore.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/manage")
public class ManageController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private BulletinService bulletinService;

    @GetMapping("/books")
    public String books(Model model, HttpServletRequest request) {
        Tools.setLayout(userService, bulletinService, model, request, false);

        User user = Tools.getUserByRequest(userService, request);
        if (user != null && user.getIdentity().equals("管理员")) {
            Book[] books = bookService.getBooksByPage(1);
            model.addAttribute("booksCount", bookService.getBooksCount());
            StringBuilder booksTables = new StringBuilder();
            if (books != null && books.length != 0) {
                booksTables.append(Tools.generateBooksTables(books));
            }
            booksTables.append("<table>" +
                    "<tbody>" +
                    "<tr>" +
                    "<th><input class=\"form-control\" name=\"bnameNew\" placeholder=\"书名*\" type=\"text\"/></th>" +
                    "<th><input class=\"form-control\" name=\"authorNew\" placeholder=\"作者*\" type=\"text\"/></th>" +
                    "<th><input class=\"form-control\" name=\"pressNew\" placeholder=\"出版社*\" type=\"text\"/></th>" +
                    "<th>出版日期：<input class=\"form-control\" name=\"dateNew\" type=\"date\"/></th>" +
                    "</tr>" +
                    "<tr>" +
                    "<th><input class=\"form-control\" name=\"categoryNew\" placeholder=\"分类*\" type=\"text\"/></th>" +
                    "<th><input class=\"form-control\" min=\"0\" name=\"priceNew\" placeholder=\"价格*\" type=\"number\"/></th>" +
                    "<th><input class=\"form-control\" min=\"0\" name=\"amountNew\" placeholder=\"库存*\" step=\"1\" type=\"number\"/></th>" +
                    "<th id=\"sNew\">销量：0</th>" +
                    "</tr>" +
                    "<tr>" +
                    "<th colspan=\"2\">" +
                    "上传图片：" +
                    "<input name=\"pictureNew\" style=\"display: none;\" type=\"text\"/>" +
                    "<form action=\"/api/upload\" class=\"main-contact-form\" enctype=\"multipart/form-data\" method=\"post\" target=\"New\">" +
                    "<input accept=\"image/jpeg\" class=\"form-control\" name=\"file\" type=\"file\"/>" +
                    "<br>" +
                    "<p id=\"pNew\">尚未上传</p>" +
                    "<button class=\"btn btn-primary\" type=\"submit\">上传</button>" +
                    "</form>" +
                    "<iframe name=\"New\" style=\"display: none;\"></iframe>" +
                    "</th>" +
                    "<td colspan=\"2\"><textarea class=\"form-control\" name=\"descnNew\" placeholder=\"简介*\"></textarea></td>" +
                    "</tr>" +
                    "<tr>" +
                    "<th colspan=\"3\"></th>" +
                    "<th><button class=\"btn btn-primary\" onclick=\"addBook()\">添加商品</button></th>" +
                    "</tr>" +
                    "</tbody>" +
                    "</table>");
            model.addAttribute("booksTables", booksTables.toString());
        } else {
            return "/error/404";
        }

        return "/manage/books";
    }

    @GetMapping("/sales")
    public String sales(Model model, HttpServletRequest request) {
        Tools.setLayout(userService, bulletinService, model, request, false);

        User user = Tools.getUserByRequest(userService, request);
        if (user == null || !user.getIdentity().equals("管理员")) {
            return "/error/404";
        }

        return "/manage/sales";
    }

    @GetMapping("/bulletins")
    public String bulletins(Model model, HttpServletRequest request) {
        Tools.setLayout(userService, bulletinService, model, request, false);

        User user = Tools.getUserByRequest(userService, request);
        if (user != null && user.getIdentity().equals("管理员")) {
            model.addAttribute("bulletinContent", Tools.generateBulletinContent(bulletinService));
        } else {
            return "/error/404";
        }

        return "/manage/bulletins";
    }

    @GetMapping("/orders")
    public String orders(Model model, HttpServletRequest request) {
        Tools.setLayout(userService, bulletinService, model, request, false);

        User user = Tools.getUserByRequest(userService, request);
        if (user != null && user.getIdentity().equals("管理员")) {
            OrderInfo[] orderInfos = orderService.getOrderInfosByPage(1);
            model.addAttribute("ordersCount", orderService.getOrdersCount());
            if (orderInfos != null && orderInfos.length != 0) {
                model.addAttribute("ordersTables", Tools.generateOrdersTables(orderInfos, bookService, orderService));
            } else {
                model.addAttribute("ordersTables", "<table><tbody><tr class=\"odd-row\"><th class=\"first last\">暂无订单</th></tr></tbody></table>");
            }
        } else {
            return "/error/404";
        }

        return "/manage/orders";
    }
}
