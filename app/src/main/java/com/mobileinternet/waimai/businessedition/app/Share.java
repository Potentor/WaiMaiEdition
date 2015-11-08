package com.mobileinternet.waimai.businessedition.app;

/**
 * Created by 海鸥2012 on 2015/7/17.
 * <p/>
 * 存储程序中使用的基本不变的数据，常量
 */
public class Share {

    public static final String share_prefrence_name = "my_config";//配置文件的名字

    public static final String config_has_refund = "has_refund";//名字of退款申请 in config

    public static final String config_has_msg = "has_msg";//name of msg in config

//    public static int  refund_show=0; //是否有退款申请还没看
//
//    public static int msg_show=0;  //是否有系统消息还没看


    public static final String user_name = "username"; //账户名
    public static final String shop_name = "shopname";  //店铺名
    public static final String user_id = "userid";     //用户ID
    public static final String shop_id = "shopid";     //店铺ID
    public static final String token = "token";       //登录token
    public static final String logo = "url_logo";         //用户头像url
    public static final String status = "status";           //餐厅营业状态
    public static final String phone="phone";
    public static final String buse_time="buse_time";         //营业时间




    public static final String isLogOut = "isLogOut";  //是否用户注销了账号


    public static final String refund_notice = "refund_notice";//退款申请界面中提示


    public static final String network_change = "com.me.network.change";//  网络状态变化


    public static final String receivedMsg = "com.me.received.server.msg";//收到了新订单

    public static final String receiveApplayBusinessTime = "com.me.received.apply.business.time";//收到了新订单


    public static final int polling_second = 15;   //轮询间隔的时间（秒）


    //public static final String domain="http://192.168.199.116";
    public static final String domain="http://koufu.weilieshou.com";


    /**
     *网络访问接口
     */

    //轮询接口
    public static final String url_polling = domain+"/app.php/Sysmsg/polling";
    //登录接口
    public static final String url_login = domain+"/app.php/User/login";
    //餐厅管理界面fragment
    public static final String url_dining_info = domain+"/app.php/Dining/bascinfo";
    //我的餐厅
    public static final String url_dining_info2 = domain+"/app.php/Dining/shopinfo";
    //修改餐厅地址
    public static final String url_modify_address=domain+"/app.php/Dining/editaddr";
    //修改订餐电话
    public static final String url_modify_phone=domain+"/app.php/Dining/edittel";
    //获取商家公告
    public static final String url_get_shop_notice=domain+"/app.php/Dining/notice";
    //修改商家公告
    public static final String url_modify_shop_notice=domain+"/app.php/Dining/editnotice";
    //获取餐厅营业时间
    public static final String url_buiness_time=domain+"/app.php/Dining/opentime";
    //修改餐厅的营业状态
    public static final String url_modify_shop_status=domain+"/app.php/Dining/editstatus";
    //修改餐厅的预订单支持情况
    public static final String url_modify_support_state=domain+"/app.php/Dining/advorder";
    //获取餐厅指数界面信息
    public static final String url_dining_index=domain+"/app.php/Dining/exp";
    //获取餐厅评价基本信息
    public static final String url_shop_comment=domain+"/app.php/Dining/judges";
    //获取用户全部评论
    public static final String url_comment_all=domain+"/app.php/Dining/comment";
    //获取用户所有未回复的差评
    public static final String url_comment_bad_not_replay=domain+"/app.php/Dining/unreplybad";
    //获取用户所有未回复的评论
    public static final String url_comment_not_replay=domain+"/app.php/Dining/unreply";
    //回复用户
    public static final String url_comment_replay=domain+"/app.php/Dining/reply";
    //获取菜品分类信息
    public static final String url_cat_dish_info=domain+"/app.php/Dishes/cats";
    //修改菜品分类顺序
    public static final String url_cat_modify_cat_order=domain+"/app.php/Dishes/editcatsort";
    //删除菜品分类
    public static final String url_cat_delete=domain+"/app.php/Dishes/delcat";
    //添加菜品分类
    public static final String url_cat_add=domain+"/app.php/Dishes/addcat";
    //修改菜品分类信息
    public static final String url_cat_modify_info=domain+"/app.php/Dishes/editcat";
    //获取单个分类所有菜品列表
    public static final String url_dish_list=domain+"/app.php/Dishes/cais";
    //修改菜品顺序
    public static final String url_dish_modify_order=domain+"/app.php/Dishes/editcaisort";
    //停售/开售菜品
    public static final String url_dish_sail_stop=domain+"/app.php/Dishes/visible";
    //删除菜品
    public static final String url_dish_delete=domain+"/app.php/Dishes/delcai";
    //添加菜品
    public static final String url_dish_add=domain+"/app.php/Dishes/addcai";
    //修改菜品
    public static final String url_dish_modify=domain+"/app.php/Dishes/editcai";
    //获取菜品信息
    public static final String url_dish_info=domain+"/app.php/Dishes/caidetail";
    //获取账户基本信息
    public static final String url_acount_info=domain+"/app.php/User/userinfo";
    //修改密码
    public static final String url_pwd_modify=domain+"/app.php/User/changepwd";
    //修改营业时间
    public static final String url_time_modify=domain+"/app.php/Dining/editweek";
    //获取财务中心信息
    public static final String url_finance_center=domain+"/app.php/Bill/finance";
    //申请提现
    public static final String url_tixian=domain+"/app.php/Bill/applyfor";
    //获取提现记录
    public static final String url_record_tixian=domain+"/app.php/Bill/payhistory";
    //获取营业统计
    public static final String url_statitics=domain+"/app.php/Dining/jilu";
    //获取近七天的每天订单量、每天的销售额
    public static final String url_seven_day_trend=domain+"/app.php/Bill/sale";
    //获取特定一天的营业详情
    public static final String url_even_one_day_business=domain+"/app.php/Dining/operat";
    //获取特定一天的所有订单
    public static final String url_all_order_one_day=domain+"/app.php/Order/oneday";
    //获取单个活动特定天中的所有订单ID与订单总价
    public static final String url_even_order_one_day=domain+"/app.php/Bill/active";
    //获取单个订单的信息
    public static final String url_order_detail=domain+"/app.php/Order/oneorder";
    //获取全部消息
    public static final String url_all_msg=domain+"/app.php/Sysmsg/allmsg";
    //获取各类消息
    public static final String url_every_msg=domain+"/app.php/Sysmsg/msglist";
    //获取商家消息
    public static final String url_shop_msg=domain+"/app.php/Sysmsg/busimsg";
    //标记商家消息已读
    public static final String url_mark_msg=domain+"/app.php/Sysmsg/read";
    //获取已处理的退款订单列表
    public static final String url_handled_refund=domain+"/app.php/Order/dealrefund";
    //获取未处理的退款订单列表
    public static final String url_not_handled_refund=domain+"/app.php/Order/unrefund";
    //同意或拒绝退款
    public static final String url_agree_or_disagree_refund=domain+"/app.php/Order/refund";
    //获取今日订单
    public static final String url_today_order=domain+"/app.php/Order/tdorder";
    //获取昨天订单
    public static final String url_yesterday_order=domain+"/app.php/Order/yestorder";
    //获取前天订单
    public static final String url_brefore_yesterday_order=domain+"/app.php/Order/qtorder";
    //获取待配送订单
    public static final String url_wait_order=domain+"/app.php/Order/unsend";
    //获取新订单
    public static final String url_new_order=domain+"/app.php/Order/neworder";
    //处理订单为无效
    public static final String url_set_invalid_order=domain+"/app.php/Order/cancel";
    //处理订单为待配送状态
    public static final String url_set_wait_order=domain+"/app.php/Order/shipping";
    //处理订单为已送出状态
    public static final String url_set_out_order=domain+"/app.php/Order/sent";
    //获取验证码
    public static final String url_get_code_phone=domain+"/app.php/User/sendcode";
    //绑定手机号码
    public static final String url_bind_phone=domain+"/app.php/User/bindtel";
    //获取消息内容
    public static final String url_msg_content=domain+"/app.php/Sysmsg/content";
    //上传菜品图片
    public static final String url_cai_image=domain+"/app.php/Dishes/uploadFile";
    //上传资质认证图片
    public static final String url_identity=domain+"/app.php/Dining/uploadcard";
    //上传资质认证状态
    public static final String url_identity_status=domain+"/app.php/Dining/identify";
    //一天在线付款订单
    public static final String url_one_day_online=domain+"/app.php/Order/dayonline";
    //一天餐到付款订单
    public static final String url_one_day_offline=domain+"/app.php/Order/dayoffline";
    //获取业务员电话
    public static final String url_custom_phone=domain+"";
    //获取业务经理电话
    public static final String url_manager_phone=domain+"";

















}
