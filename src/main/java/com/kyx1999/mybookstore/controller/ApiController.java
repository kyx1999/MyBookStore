package com.kyx1999.mybookstore.controller;

import com.kyx1999.mybookstore.model.*;
import com.kyx1999.mybookstore.service.*;
import com.kyx1999.mybookstore.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;

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
    private BulletinService bulletinService;

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
    @GetMapping("/comment-count")
    public String commentCount(@RequestParam("bid") Integer bid) {
        if (bid == null) {
            return null;
        }

        return commentService.getCommentCountByBid(bid).toString();
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
    @PostMapping("/delete-cart-item")
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

    @PostMapping("/submit-order")
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

    @ResponseBody
    @GetMapping("/books-count")
    public String booksCount(HttpServletRequest request) {
        User user = Tools.getUserByRequest(userService, request);
        if (user == null || !user.getIdentity().equals("管理员")) {
            return "";
        }

        return bookService.getBooksCount().toString();
    }

    @ResponseBody
    @GetMapping("/books")
    public String books(@RequestParam("page") Integer page, HttpServletRequest request) {
        User user = Tools.getUserByRequest(userService, request);
        if (user == null || !user.getIdentity().equals("管理员") || page == null) {
            return "";
        }

        Book[] books = bookService.getBooksByPage(page > 0 ? page : 1);
        StringBuilder booksTables = new StringBuilder();
        if (books != null || books.length != 0) {
            booksTables.append(Tools.generateBooksTables(books));
        }
        booksTables.append("<table>" +
                "<tbody>" +
                "<tr class=\"odd-row\">" +
                "<th class=\"first\"><input class=\"form-control\" name=\"bnameNew\" placeholder=\"书名*\" type=\"text\"/></th>" +
                "<th><input class=\"form-control\" name=\"authorNew\" placeholder=\"作者*\" type=\"text\"/></th>" +
                "<th><input class=\"form-control\" name=\"pressNew\" placeholder=\"出版社*\" type=\"text\"/></th>" +
                "<th class=\"last\">出版日期：<input class=\"form-control\" name=\"dateNew\" type=\"date\"/></th>" +
                "</tr>" +
                "<tr>" +
                "<th class=\"first\"><input class=\"form-control\" name=\"categoryNew\" placeholder=\"分类*\" type=\"text\"/></th>" +
                "<th><input class=\"form-control\" min=\"0\" name=\"priceNew\" placeholder=\"价格*\" type=\"number\"/></th>" +
                "<th><input class=\"form-control\" min=\"0\" name=\"amountNew\" placeholder=\"库存*\" step=\"1\" type=\"number\"/></th>" +
                "<th id=\"sNew\" class=\"last\">销量：0</th>" +
                "</tr>" +
                "<tr class=\"odd-row\">" +
                "<th class=\"first\" colspan=\"2\">" +
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
                "<td class=\"last\" colspan=\"2\"><textarea class=\"form-control\" name=\"descnNew\" placeholder=\"简介*\"></textarea></td>" +
                "</tr>" +
                "<tr>" +
                "<th class=\"first\" colspan=\"3\"></th>" +
                "<th class=\"last\"><button class=\"btn btn-primary\" onclick=\"addBook()\">添加商品</button></th>" +
                "</tr>" +
                "</tbody>" +
                "</table>");

        return booksTables.toString();
    }

    @ResponseBody
    @PostMapping("/upload")
    public String upload(MultipartFile file, HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = Tools.getUserByRequest(userService, request);
            if (user == null || !user.getIdentity().equals("管理员") || file == null || file.isEmpty()) {
                response.sendError(400);
                return "";
            }

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(file.getBytes());
            String filename = Base64.getEncoder().encodeToString(messageDigest.digest());
            File newFile = new File(new ClassPathResource("").getFile().getAbsolutePath() + "\\static\\images\\temp\\" + filename);
            if (!newFile.getParentFile().exists() && !newFile.getParentFile().mkdirs()) {
                return "<script>alert('上传失败。');</script>";
            }
            file.transferTo(newFile);

            return "<script>alert('上传成功。'); parent.refreshUpload(window.name, \"" + filename + "\");</script>";

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return "<script>alert('上传失败。');</script>";
    }

    @PostMapping("/add-book")
    public void addBook(@RequestParam("bname") String bname, @RequestParam("author") String author, @RequestParam("press") String press, @RequestParam("date") String dateString, @RequestParam("category") String category, @RequestParam("descn") String descn, @RequestParam("price") Float price, @RequestParam("amount") Integer amount, @RequestParam("picture") String picture, HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = Tools.getUserByRequest(userService, request);
            if (user == null || !user.getIdentity().equals("管理员")) {
                response.sendError(400);
                return;
            }

            if (bname == null || bname.equals("") || author == null || author.equals("") || press == null || press.equals("") || dateString == null || dateString.equals("") || category == null || category.equals("") || descn == null || descn.equals("") || price == null || price < 0 || amount == null || amount < 0 || picture == null || picture.equals("")) {
                response.sendError(400);
                return;
            }

            File oldFile = new File(new ClassPathResource("").getFile().getAbsolutePath() + "\\static\\images\\temp\\" + picture);
            if (oldFile.exists() && oldFile.isFile()) {
                Book book = new Book();
                book.setBname(bname);
                book.setAuthor(author);
                book.setPress(press);
                book.setDate(new Date(new SimpleDateFormat("yyyy-MM-dd").parse(dateString).getTime()));
                book.setCategory(category);
                book.setDescn(descn);
                book.setPrice(price);
                book.setAmount(amount);
                book.setSales(0);
                bookService.insertSelective(book);

                File newFile = new File(new ClassPathResource("").getFile().getAbsolutePath() + "\\static\\images\\books\\" + book.getBid() + ".jpg");
                if (!oldFile.renameTo(newFile)) {
                    bookService.deleteByPrimaryKey(book.getBid());
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/change-book")
    public void changeBook(@RequestParam("bid") Integer bid, @RequestParam("bname") String bname, @RequestParam("author") String author, @RequestParam("press") String press, @RequestParam("date") String dateString, @RequestParam("category") String category, @RequestParam("descn") String descn, @RequestParam("price") Float price, @RequestParam("amount") Integer amount, @RequestParam("picture") String picture, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = Tools.getUserByRequest(userService, request);
            if (user == null || !user.getIdentity().equals("管理员")) {
                response.sendError(400);
                return;
            }

            if (bid == null || bname == null || bname.equals("") || author == null || author.equals("") || press == null || press.equals("") || dateString == null || dateString.equals("") || category == null || category.equals("") || descn == null || descn.equals("") || price == null || price < 0 || amount == null || amount < 0) {
                response.sendError(400);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        try {
            Book book = bookService.selectByPrimaryKey(bid);
            book.setBname(bname);
            book.setAuthor(author);
            book.setPress(press);
            book.setDate(new Date(new SimpleDateFormat("yyyy-MM-dd").parse(dateString).getTime()));
            book.setCategory(category);
            book.setDescn(descn);
            book.setPrice(price);
            book.setAmount(amount);
            bookService.updateByPrimaryKeySelective(book);

            if (picture != null && !picture.equals("")) {
                File newFile = new File(new ClassPathResource("").getFile().getAbsolutePath() + "\\static\\images\\temp\\" + picture);
                if (newFile.exists() && newFile.isFile()) {
                    File oldFile = new File(new ClassPathResource("").getFile().getAbsolutePath() + "\\static\\images\\books\\" + book.getBid() + ".jpg");
                    if (oldFile.delete()) {
                        if (!newFile.renameTo(oldFile)) {
                            throw new IOException("修改图片出错。");
                        }
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/delete-book")
    public void deleteBook(@RequestParam("bid") Integer bid, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            User user = Tools.getUserByRequest(userService, request);
            if (user == null || !user.getIdentity().equals("管理员") || bid == null) {
                response.sendError(400);
                return;
            }

            bookService.deleteByPrimaryKey(bid);

        } catch (IOException e) {
            e.printStackTrace();
        }

        File oldFile = new File(new ClassPathResource("").getFile().getAbsolutePath() + "\\static\\images\\books\\" + bid + ".jpg");
        if (oldFile.exists() && oldFile.isFile()) {
            if (!oldFile.delete()) {
                throw new IOException("删除图片出错。");
            }
        }
    }

    @GetMapping("/download")
    public void download(HttpServletRequest request, HttpServletResponse response) {
        User user = Tools.getUserByRequest(userService, request);
        if (user == null || !user.getIdentity().equals("管理员")) {
            return;
        }

        response.setHeader("Content-Type", "application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=Sales.csv");

        StringBuilder sbd = bookService.getSales();

        byte[] buffer = new byte[1024];
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new ByteArrayInputStream(sbd.toString().getBytes("GBK")));
            OutputStream os = response.getOutputStream();
            int i = bis.read(buffer);
            while (i != -1) {
                os.write(buffer, 0, i);
                i = bis.read(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @PostMapping("/change-bulletin")
    public void changeBulletin(@RequestParam("bltid") Integer bltid, @RequestParam("content") String content, @RequestParam("valid") Boolean valid, HttpServletRequest request, HttpServletResponse response) {
        try {
            if (bltid == null || content == null || valid == null || bulletinService.selectByPrimaryKey(bltid) == null) {
                response.sendError(400);
                return;
            }

            User user = Tools.getUserByRequest(userService, request);
            if (user == null || !user.getIdentity().equals("管理员")) {
                response.sendError(400);
                return;
            }
            bulletinService.changeBulletin(bltid, content, valid);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ResponseBody
    @PostMapping("/add-bulletin")
    public String addBulletin(@RequestParam("content") String content, @RequestParam("valid") Boolean valid, HttpServletRequest request) {
        User user = Tools.getUserByRequest(userService, request);
        if (user == null || !user.getIdentity().equals("管理员") || content == null || valid == null) {
            return "";
        }

        Bulletin bulletin = new Bulletin();
        bulletin.setBltid(null);
        bulletin.setContent(content);
        bulletin.setTime(new Date());
        bulletin.setValid(valid);
        bulletinService.insertSelective(bulletin);

        return Tools.generateBulletinContent(bulletinService);
    }

    @ResponseBody
    @PostMapping("/delete-bulletin")
    public String deleteBulletin(@RequestParam("bltid") Integer bltid, HttpServletRequest request) {
        User user = Tools.getUserByRequest(userService, request);
        if (user == null || !user.getIdentity().equals("管理员") || bltid == null || bulletinService.selectByPrimaryKey(bltid) == null) {
            return "";
        }

        bulletinService.deleteByPrimaryKey(bltid);

        return Tools.generateBulletinContent(bulletinService);
    }

    @ResponseBody
    @GetMapping("/orders-count")
    public String ordersCount(HttpServletRequest request) {
        User user = Tools.getUserByRequest(userService, request);
        if (user == null || !user.getIdentity().equals("管理员")) {
            return "";
        }

        return orderService.getOrdersCount().toString();
    }

    @ResponseBody
    @GetMapping("/orders")
    public String orders(@RequestParam("page") Integer page, HttpServletRequest request) {
        User user = Tools.getUserByRequest(userService, request);
        if (user == null || !user.getIdentity().equals("管理员") || page == null) {
            return "";
        }

        OrderInfo[] orderInfos = orderService.getOrderInfosByPage(page > 0 ? page : 1);
        if (orderInfos == null || orderInfos.length == 0) {
            return "<table><tbody><tr class=\"odd-row\"><th class=\"first last\">暂无订单</th></tr></tbody></table>";
        }

        return Tools.generateOrdersTables(orderInfos, bookService, orderService);
    }

    @PostMapping("/finish-order")
    public void finishOrder(@RequestParam("oid") Integer oid, HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = Tools.getUserByRequest(userService, request);
            if (user == null || !user.getIdentity().equals("管理员") || oid == null) {
                response.sendError(400);
                return;
            }

            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setOid(oid);
            orderInfo.setStatus("已完成");
            orderService.updateByPrimaryKeySelective(orderInfo);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/delete-order")
    public void deleteOrder(@RequestParam("oid") Integer oid, HttpServletRequest request, HttpServletResponse response) {
        try {
            User user = Tools.getUserByRequest(userService, request);
            if (user == null || !user.getIdentity().equals("管理员") || oid == null) {
                response.sendError(400);
                return;
            }

            orderService.deleteOrder(oid);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
