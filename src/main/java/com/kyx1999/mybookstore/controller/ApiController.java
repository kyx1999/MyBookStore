package com.kyx1999.mybookstore.controller;

import com.kyx1999.mybookstore.model.Book;
import com.kyx1999.mybookstore.model.CartItemKey;
import com.kyx1999.mybookstore.model.User;
import com.kyx1999.mybookstore.service.*;
import com.kyx1999.mybookstore.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private BookService bookService;

    @Autowired
    private CartItemService cartItemService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CommentService commentService;

    @ResponseBody
    @GetMapping("/search")
    public String search(@RequestParam("keyword") String keyword) {
        if (keyword == null || keyword.equals("")) {
            return "<div class=\"white-wrapper special-max-width section-inner\"><div class=\"row nopaddingleftright\"><div class=\"col-md-12\"><div class=\"text-center\"><h1>对不起，没有找到您想要的内容。T_T</h1></div></div></div></div>";
        }

        Book[] books = bookService.getSearchBooks(keyword);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<div class=\"white-wrapper special-max-width section-inner\"><div class=\"row nopaddingleftright\"><div class=\"col-md-12\">");
        if (books != null && books.length != 0) {
            for (Book book : books) {
                stringBuilder.append(Tools.generateBook(book, true));
            }
        } else {
            stringBuilder.append("<div class=\"text-center\"><h1>对不起，没有找到您想要的内容。T_T</h1></div>");
        }
        stringBuilder.append("</div></div></div>");

        return stringBuilder.toString();
    }

    @ResponseBody
    @PostMapping("/add2cart")
    public String add2cart(@RequestParam("bid") Integer bid, @RequestParam("qty") Integer qty, HttpServletRequest request) {
        if (bid == null || qty == null || qty < 1 || bookService.selectByPrimaryKey(bid) == null) {
            return "<script>alert('加入购物车失败。');</script>";
        }

        User user = Tools.getUserByRequest(userService, request);
        if (user == null) {
            return "<script>alert('加入购物车失败。');</script>";
        }

        cartItemService.changeCart(user.getUid(), bid, qty, true);

        return "<script>alert('加入购物车成功！');</script>";
    }

    @ResponseBody
    @GetMapping("/comment")
    public String comment(@RequestParam("bid") Integer bid, @RequestParam("page") Integer page) {
        if (bid == null || page == null) {
            return null;
        }

        return Tools.getCommentsHTMLInPageX(bid, page > 0 ? page : 1, userService, commentService);
    }

    @ResponseBody
    @PostMapping("/submit-comment")
    public String submitComment(@RequestParam("bid") Integer bid, @RequestParam("content") String content, HttpServletRequest request) {
        if (bid == null || content == null || content.equals("") || bookService.selectByPrimaryKey(bid) == null) {
            return "<script>alert('评论失败。');</script>";
        }

        User user = Tools.getUserByRequest(userService, request);
        if (user == null) {
            return "<script>alert('评论失败。');</script>";
        }

        commentService.submitComment(user.getUid(), bid, content);

        return "<script>alert('评论成功！'); parent.jumpToPage(1);</script>";
    }

    @ResponseBody
    @PostMapping("/sign-in")
    public String signIn(@RequestParam("uname") String uname, @RequestParam("pwd") String pwd, HttpServletResponse response) {
        if (uname == null || uname.equals("")) {
            return "<script>alert('请输入您的用户名。');</script>";
        }

        if (!userService.isUserNameExist(uname)) {
            return "<script>alert('该用户名不存在，请重新输入。');</script>";
        }

        if (pwd == null || pwd.equals("")) {
            return "<script>alert('请输入您的密码。');</script>";
        }

        User user = userService.selectByUserName(uname);
        if (!user.getPwd().equals(pwd)) {
            return "<script>alert('用户名或密码错误。');</script>";
        }

        Cookie cookie = new Cookie("uid", user.getUid().toString());
        cookie.setMaxAge(60 * 60 * 24 * 7);
        cookie.setPath("/");
        response.addCookie(cookie);

        return "<script>alert('登录成功！'); parent.location.href=\"/\";</script>";
    }

    @ResponseBody
    @GetMapping("/sign-out")
    public String signOut(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("uid")) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }

        return "<script>alert('退出成功！'); location.href=\"/\";</script>";
    }

    @ResponseBody
    @PostMapping("/sign-up")
    public String signUp(@RequestParam("uname") String uname, @RequestParam("pwd") String pwd, @RequestParam("pwdAgain") String pwdAgain, @RequestParam("sex") String sex, @RequestParam("email") String email, @RequestParam("phone") String phone, @RequestParam("address") String address) {
        if (uname == null || uname.equals("")) {
            return "<script>alert('请输入您的用户名。');</script>";
        }

        if (userService.isUserNameExist(uname)) {
            return "<script>alert('该用户名已存在，请重新输入。');</script>";
        }

        if (pwd == null || pwd.equals("") || pwdAgain == null || pwdAgain.equals("") || !pwd.equals(pwdAgain)) {
            return "<script>alert('两次输入密码不一致，请重新输入。');</script>";
        }

        if (sex == null || !sex.equals("男") && !sex.equals("女")) {
            return "<script>alert('请选择您的性别。');</script>";
        }

        if (email == null || email.equals("")) {
            return "<script>alert('请输入您的电子邮箱。');</script>";
        }

        if (phone == null || phone.equals("")) {
            return "<script>alert('请请输入您的电话号码。');</script>";
        }

        if (address == null || address.equals("")) {
            return "<script>alert('请输入您的收货地址。');</script>";
        }

        User user = new User();
        user.setUid(null);
        user.setUname(uname);
        user.setPwd(pwd);
        user.setSex(sex);
        user.setIdentity("用户");
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);

        userService.insertSelective(user);

        return "<script>alert('注册成功！'); parent.location.href=\"/sign-in\";</script>";
    }

    @ResponseBody
    @PostMapping("/profile")
    public String profile(@RequestParam("uname") String uname, @RequestParam("pwd") String pwd, @RequestParam("pwdAgain") String pwdAgain, @RequestParam("sex") String sex, @RequestParam("email") String email, @RequestParam("phone") String phone, @RequestParam("address") String address, @RequestParam("oldPwd") String oldPwd, HttpServletRequest request) {
        User user = Tools.getUserByRequest(userService, request);
        if (user == null) {
            return "<script>alert('修改失败。');</script>";
        }
        String opw = user.getPwd();
        user.setUname(uname);
        user.setPwd(pwd);
        user.setSex(sex);
        user.setEmail(email);
        user.setPhone(phone);
        user.setAddress(address);

        if (uname == null || uname.equals("")) {
            user.setUname(null);
        }

        if (userService.isUserNameExist(uname) && !userService.selectByUserName(uname).getUid().equals(user.getUid())) {
            return "<script>alert('该用户名已存在，请重新输入。');</script>";
        }

        if (pwd == null && pwdAgain == null || pwd != null && pwdAgain != null && pwd.equals("") && pwdAgain.equals("")) {
            user.setPwd(null);
        } else if (pwd == null || !pwd.equals(pwdAgain)) {
            return "<script>alert('两次输入密码不一致，请重新输入。');</script>";
        }

        if (sex == null || !sex.equals("男") && !sex.equals("女")) {
            user.setSex(null);
        }

        if (email == null || email.equals("")) {
            user.setEmail(null);
        }

        if (phone == null || phone.equals("")) {
            user.setPhone(null);
        }

        if (address == null || address.equals("")) {
            user.setAddress(null);
        }

        if (!oldPwd.equals(opw)) {
            return "<script>alert('原密码错误，请重新输入。');</script>";
        }

        userService.updateByPrimaryKeySelective(user);

        return "<script>alert('修改成功！'); parent.location.href=\"/profile\";</script>";
    }

    @PostMapping("/change-cart")
    public void changeCart(@RequestParam("bid") Integer bid, @RequestParam("qty") Integer qty, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (bid == null || qty == null || qty < 1 || bookService.selectByPrimaryKey(bid) == null) {
                response.sendError(400);
                return;
            }

            User user = Tools.getUserByRequest(userService, request);
            if (user == null) {
                response.sendError(400);
                return;
            }
            cartItemService.changeCart(user.getUid(), bid, qty, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ResponseBody
    @GetMapping("/delete-cart-item")
    public String deleteCartItem(@RequestParam("bid") Integer bid, HttpServletRequest request) {
        User user = Tools.getUserByRequest(userService, request);
        if (user == null || bid == null || bookService.selectByPrimaryKey(bid) == null) {
            return "";
        }

        CartItemKey cartItemKey = new CartItemKey();
        cartItemKey.setUid(user.getUid());
        cartItemKey.setBid(bid);
        cartItemService.deleteByPrimaryKey(cartItemKey);

        return Tools.generateCartContentByUserId(user.getUid(), bookService, cartItemService);
    }

    @GetMapping("/submit-order")
    public void submitOrder(HttpServletRequest request, HttpServletResponse response) {
        User user = Tools.getUserByRequest(userService, request);
        try {
            if (user == null) {
                response.sendError(400);
                return;
            }

            if (!orderService.submitOrder(user.getUid())) {
                response.sendError(500);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
