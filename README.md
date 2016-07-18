# tool-db
这是个数据库小工具，可以用来接连数据库进行增删改查操作

## 功能
- 可以将查询结果封装在Map中
```java
DBTool dbTool = DBTool.getInstant();
List<Map<String, Object>> list = dbTool.list("select * from tLog limit 10");
```
- 可以将查询结果映射成对象
```java
DBTool dbTool = DBTool.getInstant();
List<Log> logs = dbTool.list("select * from tLog limit 1000", Log.class);
```

## 配置
- 配置很简单只需要在项目的classPath路径下建立一个dbTool.properties配置文件即可
```properties
url=jdbc:mysql://127.0.0.1:3306/?characterEncoding=utf8&useSSL=false
username=root
password=root
dbname=test 
```
- log4j日志配置文件采用默认即可，如有需要也可以自定义
