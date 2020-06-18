package com.kyx1999.mybookstore.controller;

import com.kyx1999.mybookstore.model.Book;
import com.kyx1999.mybookstore.model.OrderInfo;
import com.kyx1999.mybookstore.model.OrderItem;
import com.kyx1999.mybookstore.model.User;
import com.kyx1999.mybookstore.service.*;
import com.kyx1999.mybookstore.util.Tools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Map;

@Controller
public class HomeController {

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

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        Tools.setLayout(userService, bulletinService, model, request, true);

        Book[] books = bookService.getTop4SalesBooksThisWeek();
        if (books != null) {
            for (int i = 0; i < books.length; i++) {
                model.addAttribute("top" + (i + 1), books[i]);
            }
        }

        return "/index";
    }

    @GetMapping("/category")
    public String category(@RequestParam("selected") String selected, Model model, HttpServletRequest request) {
        Tools.setLayout(userService, bulletinService, model, request, true);

        String[] categories = bookService.getCategories();
        if (selected == null || selected.equals("") || categories == null || Arrays.binarySearch(categories, selected) < 0) {
            selected = "全部";
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (selected.equals("全部")) {
            stringBuilder.append(Tools.generateCategory(true, "全部"));
        } else {
            stringBuilder.append(Tools.generateCategory(false, "全部"));
        }
        if (categories != null) {
            for (String category : categories) {
                if (selected.equals(category)) {
                    stringBuilder.append(Tools.generateCategory(true, category));
                } else {
                    stringBuilder.append(Tools.generateCategory(false, category));
                }
            }
            model.addAttribute("categories", stringBuilder.toString());
        }

        stringBuilder = new StringBuilder();
        Book[] books = bookService.getAllBooks();
        if (books != null) {
            for (Book book : books) {
                stringBuilder.append(Tools.generateBook(book, false));
            }
            model.addAttribute("books", stringBuilder.toString());
        }

        return "/category";
    }

    @GetMapping("/search")
    public String search(Model model, HttpServletRequest request) {
        Tools.setLayout(userService, bulletinService, model, request, true);

        return "/search";
    }

    @GetMapping("/book")
    public String book(@RequestParam("bid") Integer bid, Model model, HttpServletRequest request) {
        Tools.setLayout(userService, bulletinService, model, request, true);

        Book book = bookService.selectByPrimaryKey(bid);
        if (book != null) {
            model.addAttribute("book", book);
            DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String bookDate = simpleDateFormat.format(book.getDate());
            model.addAttribute("bookDate", bookDate);
            DecimalFormat decimalFormat = new DecimalFormat(".00");
            String bookPrice = decimalFormat.format(book.getPrice());
            model.addAttribute("bookPrice", bookPrice);
        }

        model.addAttribute("commentCount", commentService.getCommentCountByBid(bid));
        model.addAttribute("comments", Tools.getCommentsHTMLInPageX(bid, 1, userService, commentService));

        Book[] books = bookService.getTopXSalesBooks(6);
        if (books != null) {
            for (int i = 0; i < books.length; i++) {
                model.addAttribute("top" + (i + 1), books[i]);
            }
        }

        String[] categories = bookService.getCategories();
        if (categories != null) {
            if (categories.length > 1) {
                String[] categories2 = new String[categories.length / 2];
                String[] categories1 = new String[categories.length - categories2.length];
                System.arraycopy(categories, 0, categories1, 0, categories1.length);
                System.arraycopy(categories, categories1.length, categories2, 0, categories2.length);
                model.addAttribute("categories1", categories1);
                model.addAttribute("categories2", categories2);
            } else if (categories.length == 1) {
                model.addAttribute("categories1", categories);
            }
        }

        return "/book";
    }

    @GetMapping("/sign-in")
    public String signIn(Model model, HttpServletRequest request) {
        Tools.setLayout(userService, bulletinService, model, request, false);

        return "/sign-in";
    }

    @GetMapping("/sign-up")
    public String signUp(Model model, HttpServletRequest request) {
        Tools.setLayout(userService, bulletinService, model, request, false);

        return "/sign-up";
    }

    @GetMapping("/profile")
    public String profile(Model model, HttpServletRequest request) {
        Tools.setLayout(userService, bulletinService, model, request, false);

        User user = Tools.getUserByRequest(userService, request);
        if (user != null) {
            model.addAttribute("user", user);
        }

        return "/profile";
    }

    @GetMapping("/cart")
    public String cart(Model model, HttpServletRequest request) {
        Tools.setLayout(userService, bulletinService, model, request, false);

        User user = Tools.getUserByRequest(userService, request);
        if (user != null) {
            model.addAttribute("cartContent", Tools.generateCartContentByUserId(user.getUid(), bookService, cartItemService));
        }

        return "/cart";
    }

    @GetMapping("/order")
    public String order(Model model, HttpServletRequest request) {
        Tools.setLayout(userService, bulletinService, model, request, false);

        User user = Tools.getUserByRequest(userService, request);
        if (user != null) {
            OrderInfo[] orderInfos = orderService.getOrderInfosByUserId(user.getUid());
            if (orderInfos != null && orderInfos.length != 0) {
                Map<OrderInfo, OrderItem[]> orders = orderService.getOrdersByOrderInfos(orderInfos);
                Float total = 0F;
                StringBuilder orderTables = new StringBuilder();
                DateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                DecimalFormat decimalFormat = new DecimalFormat(".00");
                for (OrderInfo orderInfo : orderInfos) {
                    orderTables.append("<table><tbody><tr><th colspan=\"2\">订单编号：").append(orderInfo.getOid()).append("</th><th colspan=\"2\">下单时间：").append(simpleDateFormat.format(orderInfo.getTime())).append("</th><th colspan=\"1\">订单状态：").append(orderInfo.getStatus()).append("</th></tr><tr><th>序号</th><th>书名</th><th>单价</th><th>数量</th><th>小计</th></tr>");
                    OrderItem[] orderItems = orders.get(orderInfo);
                    Book[] books = bookService.getBooksByOrderItems(orderItems);
                    for (int i = 0; i < books.length; i++) {
                        Float subtotal = books[i].getPrice() * orderItems[i].getQty();
                        orderTables.append("<tr>").append("<td>").append(i + 1).append("</td><td>").append(books[i].getBname()).append("</td><td>").append(books[i].getPrice()).append("</td><td>").append(orderItems[i].getQty()).append("</td><td>").append(subtotal).append("</td></tr>");
                        total += subtotal;
                    }
                    orderTables.append("</tbody><tfoot><tr><th colspan=\"3\"></th><th>合计：</th><th>").append(decimalFormat.format(total)).append("</th></tr></tfoot></table>");
                }
                model.addAttribute("orderTables", orderTables.toString());
            } else {
                model.addAttribute("orderTables", "<table><tbody><tr class=\"odd-row\"><th class=\"first last\">暂无订单</th></tr></tbody></table>");
            }
        }

        return "/order";
    }

    @GetMapping("/about")
    public String about(Model model, HttpServletRequest request) {
        Tools.setLayout(userService, bulletinService, model, request, false);

        return "/about";
    }

    @GetMapping("/coming-soon")
    public String comingSoon(Model model, HttpServletRequest request) {
        Tools.setLayout(userService, bulletinService, model, request, true);

        return "/coming-soon";
    }
}
