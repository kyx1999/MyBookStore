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
            DateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
            model.addAttribute("bulletinCount", bulletinCount);
            for (int i = 0; i < bulletinCount; i++) {
                model.addAttribute("bulletinDate" + (i + 1), simpleDateFormat.format(bulletins[i].getTime()));
                model.addAttribute("bulletin" + (i + 1), bulletins[i].getContent());
            }
        } else {
            model.addAttribute("isShowBulletin", false);
        }
    }

    public static String getCommentsHTMLInPageX(Integer bid, Integer page, UserService userService, CommentService commentService) {
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
        return "<div class=\"" + (isSearch ? "col-md-3" : "col-sm-4 masonry-portfolio-item") + " mb30 " + book.getCategory() + "\">\n" +
                "<div class=\"hover-effect smoothie\">\n" +
                "<a class=\"smoothie\" href=\"#\">\n" +
                "<img alt=\"Image\" class=\"img-responsive smoothie\" height=\"500\" width=\"500\" src=\"images/books/" + book.getBid() + ".jpg\">\n" +
                "</a>\n" +
                "<div class=\"hover-overlay smoothie text-center\">\n" +
                "<div class=\"vertical-align-top\">\n" +
                "<h4>" + book.getBname() + "</h4>\n" +
                "<span class=\"hover-overlay-cat\"作者：>" + book.getAuthor() + "</span>\n" +
                "</div>\n" +
                "</div>\n" +
                "<div class=\"hover-caption dark-overlay smoothie text-center\">\n" +
                "<div class=\"vertical-align-bottom\">\n" +
                "<a href=\"book?bid=" + book.getBid() + "\" class=\"btn btn-primary btn-green\">了解更多</a>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>\n" +
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

    private static String generateComment(String uname, Date time, String content) {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日");
        return "<div class=\"media\">\n" +
                "<div class=\"media-body\">\n" +
                "<div class=\"well\">\n" +
                "<div class=\"media-heading\">\n" +
                "<span class=\"heading-font\">" + uname + "</span>&nbsp;\n" +
                "<small>" + simpleDateFormat.format(time) + "</small>\n" +
                "</div>\n" +
                "<p>" + content + "</p>\n" +
                "</div>\n" +
                "</div>\n" +
                "</div>";
    }

    private static String generateCartItem(Integer index, Integer bid, String bname, Float price, Integer qty, Float subtotal) {
        DecimalFormat decimalFormat = new DecimalFormat(".00");
        return "<tr>\n" +
                "<td>" + index + "<p id=\"bid" + index + "\" style=\"display: none;\">" + bid + "</p></td>\n" +
                "<td>" + bname + "</td>\n" +
                "<td id=\"p" + index + "\">" + decimalFormat.format(price) + "</td>\n" +
                "<td id=\"q" + index + "\">" + qty + "</td>\n" +
                "<td id=\"s" + index + "\">" + decimalFormat.format(subtotal) + "</td>\n" +
                "<td>\n" +
                "<div class=\"row\">" +
                "<button id=\"b" + index + "\" class=\"btn btn-primary\" onclick=\"changeCartItem(" + index + ")\">修改数量</button>\n" +
                "&nbsp;\n" +
                "<button class=\"btn btn-danger\" onclick=\"deleteCartItem(" + index + ")\">删除</button>\n" +
                "</div>" +
                "</td>\n" +
                "</tr>";
    }
}
