package com.kyx1999.mybookstore.util;

import com.kyx1999.mybookstore.model.*;
import com.kyx1999.mybookstore.service.*;
import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class Tools {
    public static void setLayout(UserService userService, BulletinService bulletinService, Model model, HttpServletRequest request, Boolean isShowBulletin) {
        Integer identityType = 0;
        User user = Tools.getUserByRequest(userService, request);
        if (user != null) {
            if (user.getIdentity().equals("用户")) {
                identityType = 1;
            } else if (user.getIdentity().equals("管理员")) {
                identityType = 2;
            }
        }
        model.addAttribute("identityType", identityType);

        if (isShowBulletin.equals(true)) {
            Bulletin[] bulletins = bulletinService.getTop3Bulletins();
            if (bulletins == null) {
                model.addAttribute("isShowBulletin", false);
                return;
            }
            Integer bulletinCount = bulletins.length;
            if (bulletinCount.equals(0)) {
                model.addAttribute("isShowBulletin", false);
                return;
            }
            model.addAttribute("isShowBulletin", true);
            DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            model.addAttribute("bulletinCount", bulletinCount);
            for (int i = 0; i < bulletinCount; i++) {
                model.addAttribute("bulletinDate" + (i + 1), simpleDateFormat.format(bulletins[i].getTime()));
                model.addAttribute("bulletin" + (i + 1), bulletins[i].getContent());
            }
        } else {
            model.addAttribute("isShowBulletin", false);
        }
    }

    public static String generateCommentsInPageX(Integer bid, Integer page, UserService userService, CommentService commentService) {
        StringBuilder result = new StringBuilder();
        Comment[] comments = commentService.selectByBookIdAndPage(bid, page);
        if (comments != null) {
            for (Comment comment : comments) {
                User user = userService.selectByPrimaryKey(comment.getUid());
                result.append(generateComment(user.getUname(), comment.getTime(), comment.getContent()));
            }
        }
        return result.toString();
    }

    public static User getUserByRequest(UserService userService, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        User user = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("uid")) {
                    user = userService.selectByPrimaryKey(Integer.valueOf(cookie.getValue()));
                }
            }
        }
        return user;
    }

    public static String generateCategory(Boolean isActive, String category) {
        return "<li><a" + (isActive ? " id=\"selected\"" : "") + " class=\"btn btn-primary btn-transparent\" href=\"#\" data-filter=\"" + (category.equals("全部") ? "*" : ("." + category)) + "\">" + category + "</a></li>";
    }

    public static String generateBook(Book book, Boolean isSearch) {
        return "<div class=\"" + (isSearch ? "col-md-3" : "col-sm-4 masonry-portfolio-item") + " mb30 " + book.getCategory() + "\">" +
                "<div class=\"hover-effect smoothie\">" +
                "<a class=\"smoothie\" href=\"#\">" +
                "<img alt=\"Image\" class=\"img-responsive smoothie\" height=\"500\" width=\"500\" src=\"/images/books/" + book.getBid() + ".jpg\">" +
                "</a>" +
                "<div class=\"hover-overlay smoothie text-center\">" +
                "<div class=\"vertical-align-top\">" +
                "<h4>" + book.getBname() + "</h4>" +
                "<span class=\"hover-overlay-cat\"作者：>" + book.getAuthor() + "</span>" +
                "</div>" +
                "</div>" +
                "<div class=\"hover-caption dark-overlay smoothie text-center\">" +
                "<div class=\"vertical-align-bottom\">" +
                "<a href=\"/book?bid=" + book.getBid() + "\" class=\"btn btn-primary btn-green\">了解更多</a>" +
                "</div>" +
                "</div>" +
                "</div>" +
                "</div>";
    }

    public static String generateCartContentByUserId(Integer uid, BookService bookService, CartItemService cartItemService) {
        StringBuilder cartContent = new StringBuilder("<tbody><tr><th>序号</th><th>书名</th><th>单价</th><th style=\"width: 200px;\">数量</th><th>小计</th><th>操作</th></tr>");

        Float total = 0F;
        CartItem[] cartItems = cartItemService.getCartItemsByUserId(uid);
        if (cartItems != null && cartItems.length != 0) {
            Book[] books = bookService.getBooksByCartItems(cartItems);
            for (int i = 0; i < books.length; i++) {
                Float subtotal = books[i].getPrice() * cartItems[i].getQty();
                cartContent.append(generateCartItem(i + 1, books[i].getBid(), books[i].getBname(), books[i].getPrice(), cartItems[i].getQty(), subtotal));
                total += subtotal;
            }
        } else {
            return "<tbody><tr class=\"odd-row\"><th class=\"first last\">购物车为空</th></tr></tbody>";
        }

        DecimalFormat decimalFormat = new DecimalFormat(".00");
        cartContent.append("</tbody><tfoot><tr><th colspan=\"3\"></th><th>合计：</th><th id=\"total\">").append(decimalFormat.format(total)).append("</th><th><button class=\"btn btn-primary\" onclick=\"submitOrder()\">结账</button></th></tr></tfoot>");

        return cartContent.toString();
    }

    public static String generateBooksTables(Book[] books) {
        StringBuilder booksTables = new StringBuilder();
        for (Book book : books) {
            booksTables.append(generateBooksTable(book));
        }
        return booksTables.toString();
    }

    public static String csvCheck(String string) {
        boolean flag = false;
        if (string.contains("\"")) {
            int i = 0;
            while (i < string.length()) {
                if (string.charAt(i) == '"') {
                    string = string.substring(0, i) + '"' + string.substring(i);
                    flag = true;
                    i++;
                }
                i++;
            }
        }
        if (flag || string.contains(",")) {
            string = '"' + string + '"';
        }
        return string;
    }

    public static String generateBulletinContent(BulletinService bulletinService) {
        Bulletin[] bulletins = bulletinService.getAllBulletins();
        StringBuilder bulletinContent = new StringBuilder();
        bulletinContent.append("<tbody><tr><th>序号</th><th>内容</th><th>时间</th><th>是否有效</th><th>操作</th></tr>");
        int i = 0;
        if (bulletins != null) {
            for (i = 0; i < bulletins.length; i++) {
                bulletinContent.append(generateBulletin(i + 1, bulletins[i]));
            }
        }
        bulletinContent.append(generateBulletin(i + 1, null));
        bulletinContent.append("</tbody>");
        return bulletinContent.toString();
    }

    public static String generateOrdersTables(OrderInfo[] orderInfos, BookService bookService, OrderService orderService) {
        Map<OrderInfo, OrderItem[]> orders = orderService.getOrdersByOrderInfos(orderInfos);
        Float total = 0F;
        StringBuilder ordersTables = new StringBuilder();
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        for (OrderInfo orderInfo : orderInfos) {
            ordersTables.append("<table><tbody><tr><th>订单编号：").append(orderInfo.getOid()).append("</th><th>用户ID：").append(orderInfo.getUid()).append("</th><th colspan=\"2\">下单时间：").append(simpleDateFormat.format(orderInfo.getTime())).append("</th><th colspan=\"1\">订单状态：").append(orderInfo.getStatus()).append("</th></tr><tr><th>序号</th><th>书名</th><th>单价</th><th>数量</th><th>小计</th></tr>");
            OrderItem[] orderItems = orders.get(orderInfo);
            Book[] books = bookService.getBooksByOrderItems(orderItems);
            for (int i = 0; i < books.length; i++) {
                Float subtotal = books[i].getPrice() * orderItems[i].getQty();
                ordersTables.append("<tr>").append("<td>").append(i + 1).append("</td><td>").append(books[i].getBname()).append("</td><td>").append(books[i].getPrice()).append("</td><td>").append(orderItems[i].getQty()).append("</td><td>").append(subtotal).append("</td></tr>");
                total += subtotal;
            }
            ordersTables.append("</tbody><tfoot><tr><th>合计：</th><th>").append(decimalFormat.format(total)).append("</th><th></th><th><button class=\"btn btn-primary\" onclick=\"finishOrder(").append(orderInfo.getOid()).append(")\">完成订单</button></th><th><button class=\"btn btn-danger\" onclick=\"deleteOrder(").append(orderInfo.getOid()).append(")\">删除订单</button></th></tr></tfoot></table>");
        }
        return ordersTables.toString();
    }

    private static String generateComment(String uname, Date time, String content) {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return "<div class=\"media\">" +
                "<div class=\"media-body\">" +
                "<div class=\"well\">" +
                "<div class=\"media-heading\">" +
                "<span class=\"heading-font\">" + uname + "</span>&nbsp;" +
                "<small>" + simpleDateFormat.format(time) + "</small>" +
                "</div>" +
                "<p>" + content + "</p>" +
                "</div>" +
                "</div>" +
                "</div>";
    }

    private static String generateCartItem(Integer index, Integer bid, String bname, Float price, Integer qty, Float subtotal) {
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        return "<tr>" +
                "<td>" + index + "<p id=\"bid" + index + "\" style=\"display: none;\">" + bid + "</p></td>" +
                "<td>" + bname + "</td>" +
                "<td id=\"p" + index + "\">" + decimalFormat.format(price) + "</td>" +
                "<td id=\"q" + index + "\">" + qty + "</td>" +
                "<td id=\"s" + index + "\">" + decimalFormat.format(subtotal) + "</td>" +
                "<td>" +
                "<div class=\"row\">" +
                "<button id=\"b" + index + "\" class=\"btn btn-primary\" onclick=\"changeCartItem(" + index + ")\">修改数量</button>" +
                "&nbsp;" +
                "<button class=\"btn btn-danger\" onclick=\"deleteCartItem(" + index + ")\">删除</button>" +
                "</div>" +
                "</td>" +
                "</tr>";
    }

    private static String generateBooksTable(Book book) {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        return "<table>" +
                "<tbody id=\"t" + book.getBid() + "\">" +
                "<tr class=\"odd-row\">" +
                "<th class=\"first\">书名：" + book.getBname() + "</th>" +
                "<th>作者：" + book.getAuthor() + "</th>" +
                "<th>出版社：" + book.getPress() + "</th>" +
                "<th class=\"last\">出版日期：" + simpleDateFormat.format(book.getDate()) + "</th>" +
                "</tr>" +
                "<tr>" +
                "<th class=\"first\">分类：" + book.getCategory() + "</th>" +
                "<th>价格：" + decimalFormat.format(book.getPrice()) + "</th>" +
                "<th>库存：" + book.getAmount() + "</th>" +
                "<th id=\"s" + book.getBid() + "\" class=\"last\">销量：" + book.getSales() + "</th>" +
                "</tr>" +
                "<tr class=\"odd-row\">" +
                "<th class=\"first\" colspan=\"2\" rowspan=\"2\"><img alt=\"Image\" src=\"/images/books/" + book.getBid() + ".jpg\" height=\"300\" width=\"300\"/></th>" +
                "<th class=\"last\" colspan=\"2\">简介：</th>" +
                "</tr>" +
                "<tr>" +
                "<td class=\"first last\" colspan=\"2\">" + book.getDescn() + "</td>" +
                "</tr>" +
                "<tr class=\"odd-row\">" +
                "<th class=\"first\" colspan=\"2\"></th>" +
                "<th>" +
                "<button class=\"btn btn-primary\" onclick=\"changeBook(" + book.getBid() + ")\">修改商品</button>" +
                "</th>" +
                "<th class=\"last\">" +
                "<button class=\"btn btn-danger\" onclick=\"deleteBook(" + book.getBid() + ")\">删除商品</button>" +
                "</th>" +
                "</tr>" +
                "</tbody>" +
                "</table>";
    }

    private static String generateBulletin(Integer index, Bulletin bulletin) {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return "<tr>" +
                "<td>" + index + (bulletin != null ? "<p id=\"bltid" + index + "\" style=\"display: none;\">" + bulletin.getBltid() + "</p>" : "") + "</td>" +
                "<td id=\"c" + index + "\">" + (bulletin != null ? bulletin.getContent() : "<input id=\"i" + index + "\" class=\"form-control\" name=\"content\" type=\"text\"/>") + "</td>" +
                "<td>" + simpleDateFormat.format(bulletin != null ? bulletin.getTime() : new Date()) + "</td>" +
                "<td id=\"v" + index + "\">" + (bulletin != null ? (bulletin.getValid() ? "是" : "否") : "<select id=\"slt" + index + "\" class=\"form-control\" name=\"valid\"><option value=\"true\" selected>是</option><option value=\"false\">否</option></select>") + "</td>" +
                "<td>" +
                "<div class=\"row\">" +
                (bulletin != null ? "<button id=\"b" + index + "\" class=\"btn btn-primary\" onclick=\"changeBulletin(" + index + ")\">修改</button>&nbsp;<button class=\"btn btn-danger\" onclick=\"deleteBulletin(" + index + ")\">删除</button>\n" : "<button id=\"b" + index + "\" class=\"btn btn-primary\" onclick=\"addBulletin(" + index + ")\">添加公告</button>") +
                "</div>" +
                "</td>" +
                "</tr>";
    }
}
