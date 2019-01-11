package com.intime.soa.util;

/**
 * Created by qmx on 2017/12/4.
 */
public interface Constants {
    // 空字符串
    public static final String EMPTY = "";

    // 主键
    public static final String ID = "id";
    // 主键集合
    public static final String IDS = "ids";
    // 角色
    public static final String NAME = "name";
    // 用户ID
    public static final String USERID = "userId";
    // 结果合计
    public static final String SUM = "sum";
    // 结果列表
    public static final String ROWS = "rows";
    // 结果记录数
    public static final String TOTAL = "total";
    // 查询之后的结果数
    public static final String COUNT = "count";
    // 逻辑标志
    public static final String TRUE_FLAG = "0";


    // 分页SQL文
    public static final String PAGENO = "pageno";
    public static final String PAGESIZE = "pagesize";
    public static final String TIMESTAMP = "timestamp";
    // SQL关键字
    public static final String LIMIT = "limit";
    public static final String OFFSET = "offset";

    // SQLKEY
    public static final String BY = "by";
    public static final String ORDER = "order";
    public static final String AND = "&";
    // 点
    public static final String POINT = ".";
    // 逗号
    public static final String COMMA = ",";
    // 百分号
    public static final String PERCENT = "%";
    // 空格字符串
    public static final String BLANKSPACE = " ";
    // 单引号
    public static final String SINGLEQUOTATION = "'";
    //AnyGate 给项目负责人定制的角色Code
    public static final String AnyGate_Admin="anygate-manager";
    //AnyGate 项目code
    public static final String AnyGate = "anygate";

    public static final String AnyGate_SuperAdmin="anygate-admin";
    //角色中配置各个项目的项目管理员的角色Code
    public static final String ADMIN = "-admin";

//    public static List<String> ENVS = Arrays.asList("d6","d7","d8","d9","d10","d11","t1","t2","t3","t4","t5","t6","t7","sim","pro");

    public static final String ANYGATE_CATALOG ="api";

    public static final String SLANT = "/";

    public static final String REL = "rel";

    public static final String PARAM_REL="?rel=";
}
