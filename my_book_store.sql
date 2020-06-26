CREATE DATABASE `mybookstore` DEFAULT CHARACTER SET UTF8MB4;
USE `mybookstore`;

CREATE TABLE `user` (
    `uid` INT PRIMARY KEY AUTO_INCREMENT,
    `pwd` VARCHAR(80) NOT NULL,
    `uname` VARCHAR(20) NOT NULL UNIQUE,
    `sex` CHAR(10) NOT NULL,
    `identity` CHAR(10) NOT NULL,
    `email` VARCHAR(40) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `address` VARCHAR(80) NOT NULL
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `book` (
    `bid` INT PRIMARY KEY AUTO_INCREMENT,
    `bname` VARCHAR(40) NOT NULL,
    `author` VARCHAR(20) NOT NULL,
    `press` VARCHAR(40) NOT NULL,
    `date` DATE NOT NULL,
    `category` CHAR(10) NOT NULL,
    `descn` VARCHAR(200) NOT NULL,
    `price` FLOAT NOT NULL,
    `amount` INT NOT NULL,
    `sales` INT NOT NULL
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `cart_item` (
    `uid` INT NOT NULL REFERENCES `user` (`uid`),
    `bid` INT NOT NULL REFERENCES `book` (`bid`),
    `qty` INT NOT NULL,
    PRIMARY KEY (`uid` , `bid`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `order_info` (
    `oid` INT PRIMARY KEY AUTO_INCREMENT,
    `uid` INT NOT NULL REFERENCES `user` (`uid`),
    `status` CHAR(10) NOT NULL,
    `time` DATETIME NOT NULL
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `order_item` (
    `oid` INT NOT NULL REFERENCES `order_info` (`oid`),
    `bid` INT NOT NULL REFERENCES `book` (`bid`),
    `qty` INT NOT NULL,
    PRIMARY KEY (`oid` , `bid`)
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `bulletin` (
    `bltid` INT PRIMARY KEY AUTO_INCREMENT,
    `content` VARCHAR(200) NOT NULL,
    `time` DATETIME NOT NULL,
    `valid` BOOLEAN NOT NULL
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;

CREATE TABLE `comment` (
    `uid` INT NOT NULL REFERENCES `user` (`uid`),
    `bid` INT NOT NULL REFERENCES `book` (`bid`),
    `content` VARCHAR(200) NOT NULL,
    `time` DATETIME NOT NULL
)  ENGINE=INNODB DEFAULT CHARSET=UTF8MB4;

INSERT INTO `user`(`pwd`, `uname`, `sex`, `identity`, `email`, `phone`, `address`) VALUES('123456', 'Admin', '男', '管理员', '1253649392@qq.com', '136XXXXXXXX', '江苏省南京市浦口区学府路8号');
INSERT INTO `user`(`pwd`, `uname`, `sex`, `identity`, `email`, `phone`, `address`) VALUES('123456', 'Arnold', '男', '用户', '1253649392@qq.com', '136XXXXXXXX', '江苏省南京市浦口区学府路8号');
INSERT INTO `book`(`bname`, `author`, `press`, `date`, `category`, `descn`, `price`, `amount`, `sales`) VALUES('深入理解计算机系统', 'Randal E. Bryant', '机械工业出版社', '2016-7-1', '计算机组成原理', '这本书叫《深入理解计算机系统》。', '139', '100', '50');
INSERT INTO `book`(`bname`, `author`, `press`, `date`, `category`, `descn`, `price`, `amount`, `sales`) VALUES('计算机组成与设计：硬件/软件接口', 'David A. Patterson', '机械工业出版社', '2015-6-1', '计算机组成原理', '这本书叫《计算机组成与设计：硬件/软件接口》。', '99', '100', '40');
INSERT INTO `book`(`bname`, `author`, `press`, `date`, `category`, `descn`, `price`, `amount`, `sales`) VALUES('算法导论', 'Thomas H. Cormen', '机械工业出版社', '2013-1-1', '数据结构与算法', '这本书叫《算法导论》。', '128', '100', '30');
INSERT INTO `book`(`bname`, `author`, `press`, `date`, `category`, `descn`, `price`, `amount`, `sales`) VALUES('操作系统——精髓与设计原理', 'William Stallings', '电子工业出版社', '2017-3-1', '操作系统', '这本书叫《操作系统——精髓与设计原理》。', '79.8', '100', '20');
INSERT INTO `book`(`bname`, `author`, `press`, `date`, `category`, `descn`, `price`, `amount`, `sales`) VALUES('计算机网络：自顶向下方法', 'James F. Kurose', '机械工业出版社', '2018-5-1', '计算机网络', '这本书叫《计算机网络：自顶向下方法》。', '89', '100', '10');
INSERT INTO `book`(`bname`, `author`, `press`, `date`, `category`, `descn`, `price`, `amount`, `sales`) VALUES('编译原理', 'Alfred V.Aho', '机械工业出版社', '2011-1-1', '编译原理', '这本书叫《编译原理》。', '78', '100', '45');
INSERT INTO `bulletin`(`content`, `time`, `valid`) VALUES('新书上架：《计算机网络：自顶向下方法》', NOW(), TRUE);
INSERT INTO `bulletin`(`content`, `time`, `valid`) VALUES('新书上架：《操作系统——精髓与设计原理》', NOW(), TRUE);
INSERT INTO `bulletin`(`content`, `time`, `valid`) VALUES('新书上架：《算法导论》', NOW(), TRUE);
INSERT INTO `comment` VALUES('2', '1', '这本书真不错！', NOW());
INSERT INTO `comment` VALUES('2', '1', '好书！', NOW());
INSERT INTO `comment` VALUES('2', '1', '根本停不下来！', NOW());
INSERT INTO `comment` VALUES('2', '1', '开拓视野！', NOW());
INSERT INTO `comment` VALUES('2', '1', '厉害了老哥！', NOW());
INSERT INTO `comment` VALUES('2', '1', '对我有很大帮助！', NOW());

-- UserMapper----------------------------------------------------------------------
SELECT 
    *
FROM
    `user`
WHERE
    uname = 'Arnold';

SELECT 
    COUNT(`uid`)
FROM
    `User`
WHERE
    `uname` = 'Arnold'
LIMIT 1;

-- BookMapper----------------------------------------------------------------------
SELECT 
    `book`.`bid` `bid`,
    `bname`,
    `author`,
    `press`,
    `date`,
    `category`,
    `descn`,
    `price`,
    `amount`,
    `sales`
FROM
    `book`,
    (SELECT 
        `A`.`bid` `bid`, SUM(`A`.`qty`) `qty`
    FROM
        (SELECT 
        `order_item`.`bid`, `order_item`.`qty`
    FROM
        `order_item`
    WHERE
        `order_item`.`oid` IN (SELECT 
                `order_info`.`oid`
            FROM
                `order_info`
            WHERE
                `order_info`.`status` = '已完成'
                    AND DATE_SUB(NOW(), INTERVAL 7 DAY) <= `order_info`.`time`)) `A`
    GROUP BY `A`.`bid`) `B`
WHERE
    `book`.`bid` = `B`.`bid`
ORDER BY `B`.`qty` DESC
LIMIT 0 , 4;

SELECT 
    *
FROM
    `book`
ORDER BY `sales` DESC
LIMIT 0 , 4;

SELECT DISTINCT
    (`category`)
FROM
    `book`
ORDER BY `category`;

SELECT 
    *
FROM
    `book`
ORDER BY `sales` DESC;

SELECT 
    *
FROM
    `book`
WHERE
    `bname` LIKE CONCAT('%', '%计算机', '%')
ORDER BY `sales` DESC;

SELECT 
    COUNT(*)
FROM
    `book`;

SELECT 
    *
FROM
    `book`
ORDER BY `bname`
LIMIT 0 , 10;

-- CartItemMapper----------------------------------------------------------------------
SELECT 
    *
FROM
    `cart_item`
WHERE
    `uid` = '1'
ORDER BY `bid`;

-- OrderInfoMapper----------------------------------------------------------------------
SELECT 
    *
FROM
    `order_info`
WHERE
    `uid` = '3'
ORDER BY `time` DESC;

SELECT 
    COUNT(*)
FROM
    `order_info`;

SELECT 
    *
FROM
    `order_info`
ORDER BY `time` DESC
LIMIT 0 , 10;

-- OrderItemMapper----------------------------------------------------------------------
SELECT 
    *
FROM
    `order_item`
WHERE
    `oid` = '1'
ORDER BY `bid`;

DELETE FROM `order_item` 
WHERE
    `oid` = '1';

-- BulletinMapper----------------------------------------------------------------------
SELECT 
    *
FROM
    `bulletin`
WHERE
    `valid` = TRUE
ORDER BY `time` DESC
LIMIT 0 , 3;

SELECT 
    *
FROM
    `bulletin`
ORDER BY `time` DESC;

-- CommentMapper----------------------------------------------------------------------
SELECT 
    *
FROM
    `Comment`
WHERE
    `bid` = '1'
ORDER BY `time` DESC
LIMIT 0 , 5;

SELECT 
    COUNT(*)
FROM
    `Comment`
WHERE
    `bid` = '1';
