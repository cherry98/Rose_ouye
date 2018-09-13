package com.orange.oy.network;


/**
 * 所有url
 */
public class Urls {
    public static final String ZHICHI_KEY = "c5c7ef4b9c8a464c823f2b7a8cc36085";  //智齿key

    public static final String checkversionUrl = "http://oy.oyearn.com:8100/android.php?t=android&app=ouyepub_app";
    //    public static final String ip = "https://oy.oyearn.com/";//正式服务器
    //    public static final String ip = "https://oydata.oyearn.cn/";//内测服务器
    public static final String ip = "http://test.oyearn.com:8031/";//测试服务器
    /**
     * OSS专用
     */
//    public static final String EndpointDir = "GZB/androidFiles";//OSS正式服务器文件夹
    public static final String EndpointDir = "GZB/androidTest";
    public static final String Endpoint = "http://oss-cn-hangzhou.aliyuncs.com";//OSS地址-测试
    public static final String Endpoint2 = "http://ouye.oss-cn-hangzhou.aliyuncs.com/" + EndpointDir;//OSS地址 上传服务里的路径修改！！
    public static final String Endpoint3 = "http://ouye.oss-cn-hangzhou.aliyuncs.com/";
    //    public static final String Endpoint2 = "http://ouye-beats.oss-cn-hangzhou.aliyuncs.com/" + EndpointDir;//OSS地址-内测
//    public static final String Identitycard2 = "GZB/andgoidIdentitycard";//正式
    public static final String Identitycard2 = "GZB/androigIdentitycardTest";//身份证测试
    public static final String BucketName = "ouye";//正式
    //     public static final String BucketName = "ouye-beats";//内测
//    public static final String Businesslicense = "GZB/androidBusinesslicense";//营业执照正式
    public static final String Businesslicense = "GZB/androidBusinesslicenseTest";//营业执照测试
    //    public static final String Shakephoto = "GZB/androidShakephoto";//甩图正式
    public static final String Shakephoto = "GZB/androidShakephotoTest";//甩图测试
    /**
     * 接口homeip
     */
    public static final String API = ip + "ouye/mobile/";
    /**
     * 图片拼接
     */
//    public static final String ImgIp = "http://123.57.8.118:8199/";//正式服务器
//    public static final String ImgIp = "http://123.57.8.118:8299/";//内测服务器
    public static final String ImgIp = "http://47.93.120.58:8299/";//测试服务器
    /**
     * 视频拼接
     */
//    public static final String VideoIp = "http://123.57.8.118:8199/";
//    public static final String VideoIp = "http://123.57.8.118:8299/";//内测服务器
    public static final String VideoIp = "http://47.93.120.58:8299/";//测试服务器
    /**
     * OSS上传回调-暗访
     */
    public static final String InsertfileinfoForblack = API + "insertfileinfo3_9";
    /**
     * OSS上传回调
     */
    public static final String Insertfileinfo = API + "insertfileinfo";
    /**
     * 检查文件断点
     */
    public static final String Checkfile = API + "checkfile1_6";
    /**
     * 上传文件
     */
    public static final String Uploadfile = ip + "WebUpload/uploadfile1_6";
    /**
     * 下载文件
     */
    public static final String Downloadtasklist = API + "downloadtasklist1_10";
    /**
     * 暗访出店
     */
    public static final String Blackstartupload = API + "startupload";
    /**
     * 整店上传
     */
    public static final String Startupload = API + "startupload1_12";
    /**
     * 出店问卷
     */
    public static final String Questionnairelist = API + "questionnairelist1_14";
    /**
     * 微信任务上传
     */
    public static final String Newtasklistup = API + "newtasklistup";
    /**
     * 微信页任务接口
     */
    public static final String Newtasklist = API + "newtasklist2_2";
    /**
     * 暗访网点说明
     */
    public static final String Outletdescription = API + "outletdescription";
    /**
     * 定位任务、拍照任务、录音任务资料回收
     */
    public static final String Filecomplete = API + "filecomplete1_8_1";//记得一起改Videocomplete
    /**
     * 视频任务资料回收
     */
    public static final String Videocomplete = API + "filecomplete1_8_1";
    /**
     * 拍照任务置无效资料回收
     */
    public static final String Closetaskcomplete = API + "closetaskcomplete1_8_1";
    /**
     * 任务包置无效资料回收
     */
    public static final String Closepackagecomplete = API + "closepackagecomplete1_10";
    /**
     * 全程录音资料回收
     */
    public static final String Soundup = API + "soundup1_8";
    /**
     * 记录任务说明
     */
    public static final String Recorddesc = API + "recorddesc1_10";
    /**
     * 登录
     */
    public static final String Login = API + "login";//post
    /**
     * 所有城市列表
     */
    public static final String AllCity = API + "city";

    /***
     * 区县信息接口
     */
    public static final String CountyByCity = API + "countyByCity";

    /**
     * 所有省份列表
     */
    public static final String AllProvince = API + "province";
    /**
     * 按照省份查询城市接口
     */
    public static final String GetCityByProvince = API + "cityByProvice";
    /**
     * 注册
     */
    public static final String Register = API + "login/reg2_1";
    /**
     * 发送验证码
     * 注册：ident = 1
     * 找回密码：ident = 0
     * 修改密码：ident = 2
     */
    public static final String RegisterSendSMS = API + "login/sendsms";
    /**
     * 找回密码
     */
    public static final String Findpassword = API + "login/findpwd";
    /**
     * 优惠信息列表
     * 加上title参数就是搜索
     */
    public static final String Youhuilist = API + "youhuilist";
    /**
     * 动态信息列表
     * 加上title参数就是搜索
     */
    public static final String Dynamiclist = API + "dynamiclist";
    /**
     * 优惠信息详情
     */
    public static final String Youhui = API + "youhui";
    /**
     * 动态信息详情
     */
    public static final String Dynamic = API + "dynamic";
    /**
     * 赞和踩
     * type:1(优惠信息),2(动态信息)
     * state:1为赞，2为踩
     */
    public static final String Operation = API + "operation";
    /**
     * 关于我们
     */
    public static final String About = API + "about";
    /**
     * 版本更新
     */
    public static final String Version = API + "version";
    /**
     * 申请代理
     */
    public static final String Applyagent = API + "applyagent";
    /**
     * 问题反馈
     */
    public static final String Addquestion = API + "addquestion";
    /**
     * 邀请好友
     */
    public static final String Invitefriend = API + "invitefriend";
    /**
     * 邀请好友量
     */
    public static final String Statisticsinvite = API + "statisticsinvite";
    /**
     * 启动量
     */
    public static final String Addstatisticsstart = API + "addstatisticsstart";
    /**
     * 修改个人信息
     */
    public static final String UpateUser = API + "upateUser";
    /**
     * 广场开启关闭提交接口
     */
    public static final String PersonalSquare = API + "personalSquare";
    /**
     * 战队队员基本信息页接口
     */
    public static final String TeamMemBerInfo = API + "teamMemberInfo";
    /**
     * 我的团队列表
     */
    public static final String Myteam = API + "myteam";
    /**
     * 昵称账号搜索
     */
    public static final String Search = API + "search";
    /**
     * 添加到团队
     */
    public static final String Addtoteam = API + "addtoteam";
    /**
     * 接受好友请求
     */
    public static final String Accepttoteam = API + "accepttoteam";
    /**
     * 手机号检验接口
     */
    public static final String Finduserphone = API + "finduserphone";
    /**
     * 更换设备发送短信验证码
     */
    public static final String Sendsms = API + "login/sendsms";
    /**
     * 短信验证码验证登录接口
     */
    public static final String Check = API + "login/check";
    /**
     * 任务列表
     * projectname：搜索字段
     */
    public static final String Projectlist = API + "projectlist1_8";
    /**
     * 标准说明
     */
    public static final String Standard = API + "projectstandard";
    /**
     * 更换访员
     */
    public static final String Changeaccessed = API + "changeaccessed3_10";
    /**
     * 网点说明
     */
    public static final String Outletdesc = API + "outletdesc";

    /**
     * 消息-V3.12 列表接口
     */
    public static final String Pushmessage = API + "pushmessage";
    /***
     * 消息评价接口
     */
    public static final String Likemessage = API + "likemessage";

    /**
     * 根据id查询项目信息接口
     */
    public static final String ProjectInfo = API + "getProjectInfo";
    /**
     * 消息-常见问题接口
     */
    public static final String Questionlist = API + "message/questionlist";
    /**
     * 消息-广播列表
     */
    public static final String Announcementlist = API + "message/announcementlist";
    /**
     * 消息-广播-下载信息
     */
    public static final String Announcement = API + "message/announcement";
    /**
     * 任务-完成进度
     */
    public static final String Selectprojectwcjd = API + "selectprojectwcjd";
    /**
     * 任务-暗访-待执行
     */
    public static final String Blackdzxstartlist = API + "start/dzxstartlist";
    /**
     * 任务-完成进度-待执行
     */
    public static final String Dzxstartlist = API + "start/dzxstartlist1_16";
    /**
     * 任务-完成进度-执行完成
     */
    public static final String Zxwcstartlist = API + "start/zxwcstartlist1_12";
    /**
     * 待下载
     */
    public static final String Downloadlist = API + "start/downloadlist";
    /**
     * 任务-完成进度-资料已回收
     */
    public static final String Zlyhsstartlist = API + "start/zlyhsstartlist1_12";
    /**
     * 任务-执行完成列表-任务包内任务列表
     */
    public static final String Tasklistcomplete = API + "tasklistcomplete";
    /**
     * 任务-执行完成列表
     */
    public static final String Taskindexcomplete = API + "taskindexcomplete";
    /**
     * 任务-开始执行-列表
     */
    public static final String Taskindex = API + "taskindex1_10";
    /**
     * 任务包制无效
     */
    public static final String Closepackage = API + "closepackage1_10";
    /**
     * 任务包制无效任务详情查询
     */
    public static final String Closepackagetask = API + "closepackagetask";
    /**
     * 更换店铺接口
     */
    public static final String Changestore = API + "changestore";
    /**
     * 查询任务包属性
     */
    public static final String Packagecategory = API + "packagecategory";
    /**
     * 校验选择的属性
     */
    public static final String Checkcomplete = API + "checkcomplete";
    /**
     * 根据任务包查询子任务
     */
    public static final String Tasklist = API + "tasklist1_8";
    /**
     * 任务包完成接口
     */
    public static final String Packagecomplete = API + "packagecomplete1_8";
    /**
     * 任务-地图定位-提交接口
     */
    public static final String Addlocationtask = API + "addlocationtask1_8_1";
    /**
     * 任务-地图定位&视频任务-获取详情
     */
    public static final String Selectprojectrw = API + "selectprojectrw1_7";
    /**
     * 任务-视频任务说明
     */
    public static final String Selectvideo = API + "selectvideo1_7";
    /**
     * 任务-视频任务-上传
     */
    public static final String Videoup = API + "videoup1_8_1";
    /**
     * 任务-记录任务
     */
    public static final String Record = API + "record2_9";
    /**
     * 任务-记录任务-上传_执行完成
     */
    public static final String Recordup1_3 = API + "pre_recordup1_8";
    /**
     * 任务-记录任务-编辑-上传_执行完成
     */
    public static final String Recordup3_11 = API + "pre_recordup3_11";

    /**
     * 未通过网点的放弃接口
     */
    public static final String abandonUnpass4 = API + "abandonUnpass";
    /**
     * 任务-记录任务-上传
     */
    public static final String Recordup = API + "recordup1_8_1";
    /**
     * 任务-日历天数
     */
    public static final String Scheduleindex = API + "schedule/index";
    /**
     * 任务-日历详情
     */
    public static final String Scheduledetail = API + "schedule/detail";
    /**
     * 任务-拍照任务
     */
    public static final String Photo = API + "photo1_7";
    /**
     * 任务-拍照任务-有
     */
    public static final String Takephoto = API + "takephoto3_5";
    /**
     * 任务-拍照（有）-提交接口
     */
    public static final String Taskphotoup = API + "taskphotoup1_8_1";
    /**
     * 查看详情
     * state：状态 1为执行完成，2为资料回收
     */
    public static final String Taskdetail = API + "taskdetail1_10";
    /**
     * 任务-返回重做
     */
    public static final String Redo = API + "redo1_12";
    /**
     * 任务-拍照（无）-提交接口
     */
    public static final String Closetask = API + "closetask1_8_1";
    /**
     * 任务-录音详情
     */
    public static final String Soundtask = API + "soundtask1_7";
    /**
     * 任务-录音任务上传
     */
    public static final String Soundtaskup = API + "soundtaskup1_8_1";
    /**
     * 退出统计
     */
    public static final String Addstatistout = API + "addstatistout";
    /**
     * 文章阅读量统计
     */
    public static final String Addstatisticsread = API + "addstatisticsread";
    /**
     * 上传网络设置统计
     */
    public static final String Dataconnection = API + "dataconnection";
    /**
     * 出店问卷调查-记录任务上传
     */
    public static final String OutrvSuey_RecordUp = API + "questionnairelistup";
    /**
     * 出店记录任务、录音任务完成接口
     */
    public static final String OutSurvey_Recordfinish = API + "questionnairecomplete";
    /**
     * 出店录音上传接口
     */
    public static final String OutSurvey_SoundUp = API + "soundupcomplete1_11";
    /**
     * 暗访手机状态
     */
    public static final String Lockscreen = API + "lockscreen";
    /**
     * 拍照、视频任务、记录任务、定位任务、录音任务查看详情接口
     */
    public static final String TaskFinish = API + "showtaskdetail";
    /***
     * 录音任务重做时录音的删除和重做
     */
    public static final String soundupdate = API + "soundupdate";

    /**
     * 单个任务点击重做接口
     */
    public static final String TaskReDo = API + "taskredo";
    /**
     * 用户信息（主要用is_agent）
     */
    public static final String Userinfo = API + "userinfo2_9";
    /**
     * 暗访项目出店问卷定位任务执行完成接口
     */
    public static final String BlackMapFinish = API + "locationup1_13";
    /**
     * 暗访项目出店问卷定位任务资料回收接口
     */
    public static final String BlackMapComplete = API + "locationcomplete1_13";
    /**
     * 暗访项目出店问卷拍照任务执行完成接口
     */
    public static final String BlackTakePhotoFinish = API + "photoup1_13";
    /**
     * 暗访项目出店问卷拍照任务资料回收接口
     */
    public static final String BlackTakePhotoComplete = API + "photocomplete1_13";
    /**
     * 暗访项目出店问卷拍照任务置无效执行完成接口
     */
    public static final String BlackCloseTakephotoNFinish = API + "closephotoup1_13";
    /**
     * 暗访项目出店问卷拍照任务置无效资料回收接口
     */
    public static final String BlackCloseTakephotoComplete = API + "closephotocomplete1_13";
    /**
     * 明访 项目检查项目下的网点是否到可查看时间接口
     */
    public static final String CheckTime = API + "checktime";
    /**
     * 明访 查看人员信息接口
     */
    public static final String AssistantInfo = API + "assistantinfo";
    /**
     * 明访 查看抽签结果接口
     */
    public static final String SelectResult = API + "selectresult";
    /**
     * 明访 查看抽签结果人员详情接口
     */
    public static final String SelectAssistantInfo = API + "selectassistantinfo";
    /**
     * 明访 考试任务（拍摄视频，录音任务，记录任务）执行完成接口
     */
    public static final String AssistantTask = API + "assistanttask";
    /**
     * 明访 考试任务（拍摄视频，录音任务，拍照任务，定位任务）资料回收接口
     */
    public static final String AssistantTaskComplete = API + "assistanttaskcomplete";
    /**
     * 明访 查询网点是否已经抽签完毕接口
     */
    public static final String CheckIsselect = API + "checkisselect";
    /**
     * 明访 考试任务（拍照任务）执行完成接口
     */
    public static final String AssistantTaskphoto = API + "assistanttaskphoto";
    /**
     * 明访 考试任务（定位任务）执行完成接口
     */
    public static final String AssistantTaskLocation = API + "assistanttasklocation";
    /**
     * 明访 考试任务（记录任务）资料回收接口
     */
    public static final String AssistantTaskRecordUp = API + "assistanttaskrecordup";
    /**
     * 扫码任务信息接口
     */
    public static final String ScanTask = API + "scantask3_11";
    /**
     * 扫码任务上传接口
     */
    public static final String ScanTaskup = API + "scantaskup3_11";
    /**
     * 扫码任务资料回收接口
     */
    public static final String ScanTaskComplete = API + "scantaskcomplete";
    /**
     * 支付宝账号绑定接口
     */
    public static final String BindPayAccount = API + "bindpayaccount";
    /**
     * 身份证绑定接口
     */
    public static final String BindIdCard = API + "bindidcard";
    /**
     * VR设备绑定接口
     */
    public static final String BindVR = API + "bindvr";
    /**
     * VR设备状态信息传回接口
     */
    public static final String VRState = API + "vrstate";
    /**
     * 项目列表接口
     */
    public static final String ProjectList2 = API + "projectlist3_19";
    /**
     * 招募令接口
     */
    public static final String Recruitment = API + "recruitment";
    /**
     * 招募问卷接口
     */
    public static final String SelectQuestionnaire = API + "selectquestionnaire";
    /**
     * 招募问卷记录任务和录音任务执行完成接口
     */
    public static final String Recruitmenttask = API + "recruitmenttask";
    /**
     * 招募问卷记录任务资料回收接口
     */
    public static final String RecruitmenRecordComplete = API + "recruitmenrecordcomplete3_6";
    /**
     * 招募问卷录音任务资料回收接口
     */
    public static final String RecruitmentSoundComplete = API + "recruitmentsoundcomplete3_6";
    /**
     * 众包项目检查是否满足招募要求接口
     */
    public static final String CheckApply = API + "checkapply";
    /**
     * 抽取背景图接口
     */
    public static final String RandomPage = API + "randomPage";
    /**
     * 四、	可申请网点列表接口
     */
    public static final String OutletList = API + "start/outletlist3_19";
    /**
     * 抢领接口
     */
    public static final String rob = API + "rob";
    /**
     * 【我的任务】项目列表接口
     */
    public static final String MyTaskProjectList = API + "mytaskprojectlist";
    /**
     * 我的账户-查询提现项目
     */
    public static final String Withdrawals = API + "withdrawals";
    /**
     * 我的账户-账户金额界面接口
     */
    public static final String Myaccount = API + "myaccount3_13";
    /**
     * 我的账户-提现接口
     */
    public static final String Getmoney = API + "getmoney3_9";
    /**
     * 暗访项目出店网点说明接口
     */
    public static final String OutDesc = API + "outdesc";
    /**
     * 【我的奖励】我的奖励接口f
     */
    public static final String MyReward = API + "myReward2_8";
    /**
     * 【我的任务】点执行时校验是否已过期
     */
    public static final String CheckInvalid = API + "checkinvalid3_2";
    /**
     * 体验完成接口
     */
    public static final String Complete = API + "complete";
    /**
     * 电话任务信息接口
     */
    public static final String CallTask = API + "calltask";
    /**
     * 电话任务执行接口
     */
    public static final String CallTaskUp = API + "calltaskup3_11";
    /**
     * 电话任务无法执行
     */
    public static final String UNCallTaskUp = API + "calltaskup";
    /**
     * 资料丢失信息记录接口
     */
    public static final String FileLost = API + "filelost";
    /**
     * 邀请好友页面内容接口
     */
    public static final String InviteFriendInfo = API + "invitefriendinfo";
    /**
     * 暗访微信也输入内容接口
     */
    public static final String InputNote = API + "inputnote";
    /**
     * 地图页面网点信息接口
     */
    public static final String MapOutletList = API + "mapoutletlist3_12";
    /**
     * 用户支付宝绑定接口
     */
    public static final String UserAccountInfo = API + "useraccountinfo";
    /**
     * 广场页搜素的筛选接口
     */
    public static final String SelectOutletList = API + "selectoutletlist";
    /**
     * 【任务】申请的任务--待执行接口、已上传接口（常规项目和暗访项目的网点都有，
     * 暗访暂时不能查看详情）
     */
    public static final String ApplyStartList = API + "start/applystartlist3_11";
    /**
     * 【任务】申请的任务未通过接口和可提现接口
     */
    public static final String MyReward2 = API + "myReward3_11";
    /**
     * 【任务】指派的任务项目列表接口
     */
    public static final String AssignedProjectList = API + "assignedprojectlist";
    /**
     * 招募令新添加的内容
     */
    public static final String RecruitmentInfo = API + "recruitment_info";
    /**
     * 不支持拍照
     */
    public static final String Photolog = API + "photolog";
//    /**
//     * 检测定位城市是否符合网点地址
//     */
//    public static final String CheckPosition = API + "checkposition";
    /**
     * 我的推荐接口
     */
    public static final String MyInvite = API + "myinvite3_13";
    /**
     * 体验项目网点地图接口
     */
    public static final String ExperienceOutletList = API + "experienceoutletlist";
    /**
     * 体验项目网点接口
     */
    public static final String ExperienceOutlet = API + "experienceoutlet";
    /**
     * 体验项目开始体验和我已离店定位准确不做拍照任务的接口
     */
    public static final String ExperienceLocation = API + "experiencelocation";
    /**
     * 开始体验和我已离店拍照任务执行完成接口
     */
    public static final String ExperienceTaskPhotoUp = API + "experiencetaskphotoup";
    /**
     * 开始体验拍照任务置无效执行完成接口
     */
    public static final String CloseExperienceTask1 = API + "closeexperiencetask3_8";
    /**
     * /**
     * 我已离店拍照任务置无效执行完成接口
     */
    public static final String CloseExperienceTask = API + "closeexperiencetask";
    /**
     * 开始体验拍照任务资料回收接口
     */
    public static final String ExperienceFileComplete1 = API + "experiencefilecomplete3_8";
    /**
     * 我已离店拍照任务执行，置无效资料回收接口
     */
    public static final String ExperienceFileComplete = API + "experiencefilecomplete";
    /**
     * 网点体验信息接口
     */
    public static final String ExperienceOutletInfo = API + "experienceoutletinfo";
    /**
     * OSS回调--体验项目照片和全程录音上传回调接口
     */
    public static final String ExperienceInsertFileInfo = API + "insertfileinfo3_9";
    /**
     * 体验评价接口
     */
    public static final String ExperienceComment = API + "experiencecomment";
    /**
     * 体验评价执行完成接口
     */
    public static final String ExperienceCommentUp = API + "experiencecommentup";
    /**
     * 体验评价资料回收接口
     */
    public static final String ExperienceCommentComplete = API + "experiencecommentcomplete";
    /**
     * 已获偶米明细接口
     */
    public static final String OmDetail = API + "omdetail";
    /**
     * 偶米兑换页面接口
     */
    public static final String OmExchangeInfo = API + "omexchangeinfo";
    /**
     * 项目的分享接口
     */
    public static final String ShareProject = API + "shareproject";

    /**
     * 网点的分享接口
     */
    public static final String ShareOutlet = API + "shareoutlet";
    /**
     * 获取签名信息接口（在分享项目时需要传一个签名值，该值通过调用以下接口获取）
     */
    public static final String Sign = API + "sign";
    /**
     * 推荐体验网点列表接口
     */
    public static final String RecommendOutletList = API + "recommendoutletlist";
    /**
     * 评选信息接口
     */
    public static final String Selection = API + "selection";
    /**
     * 偶米取整兑换接口
     */
    public static final String OmExchange = API + "omexchange";
    /**
     * 评选提交接口
     */
    public static final String SelectionComplete = API + "selectioncomplete";
    /**
     * 体验项目体验问卷记录任务执行完成接口（同常规）
     */
    public static final String ExperiencePreRecordUp = API + "experience_pre_recordup";
    /**
     * 体验项目体验问卷记录任务资料回收接口（同常规）
     */
    public static final String ExperienceRecordUp = API + "experience_recordup";
    /**
     * 放弃任务的退回接口
     */
    public static final String Abandon = API + "abandon";
    /**
     * 加载广告页接口
     */
    public static final String Advertisement = API + "advertisement";
    /**
     * 暗访项目和体验项目文件数上传接口
     */
    public static final String FileNum = API + "filenum";
    /**
     * 提现流程-免税额度详情页面接口
     */
    public static final String GetDutyFreeObtainLog = API + "getDutyFreeObtainLog";
    /**
     * 提现流程-免税额度邀请好友注册详情
     */
    public static final String GetMonthDutyFreeFriends = API + "getMonthDutyFreeFriends";
    /**
     * 提现流程-免税额度邀请好友提交
     */
    public static final String AddDutyFreeFriends = API + "addDutyFreeFriends";
    /**
     * 提现流程-获取免税额度邀请好友信息详情接口==好友明细
     */
    public static final String GetDutyFreeFriends = API + "getDutyFreeFriends";
    /**
     * 身份认证信息接口
     */
    public static final String IdentityInfo = API + "identityInfo";
    /**
     * 历史账单接口
     */
    public static final String BillList = API + "billList3_15";
    /**
     * 新页面提现流程-提现接口
     */
    public static final String NewGetMoney = API + "getmoney3_10";
    /**
     * 提现流程-获取验证已经通过但是还没计算免税额度用过的好友手机号列表
     */
    public static final String GetVerifiedFriends = API + "getVerifiedFriends";
    /**
     * 无店单项目网点申请接口
     */
    public static final String ApplyNoOutletsProject = API + "applyNoOutletsProject3_10";
    /**
     * 提现明细接口
     */
    public static final String GetMoneyOrders = API + "getMoneyOrders";
    /**
     * 拍照任务重做执行完成接口
     */
    public static final String TaskPhotoup_Add = API + "taskphotoup3_11";
    /**
     * 无网点项目任务预览接口（返回结果和 taskindex1_10 接口返回的一样）
     */
    public static final String CheckPreview = API + "checkpreview3_11";
    /**
     * 搜索页面查询用户浏览项目历史记录和热搜接口-搜索页
     */
    public static final String HistoricalAndHotSerach = API + "historicalAndHotSerach";
    /**
     * 搜索接口-搜索页
     */
    public static final String SearchProject = API + "searchProject";
    /**
     * 根据id查询项目信息接口-搜索页
     */
    public static final String GetProjectInfo = API + "getProjectInfo";
    /**
     * 最新提现记录接口
     */
    public static final String WithdrawalInfo = API + "withdrawalInfo";
    /**
     * 【收入明细】界面接口
     */
    public static final String IncomeDetails = API + "incomeDetails";
    /**
     * 隐私协议
     * http://www.oyearn.com/mobile/policy.html
     */
    /**
     * 我的奖励分享链接
     */
    public static final String Bonus = API + "bonus";
    /**
     * 战队成员列表
     */
    public static final String TeamUserList = API + "teamUserList";

    /***
     * 战队信息接口
     */
    public static final String TEAMINFO = API + "teamInfo";

    /***
     * 退出战队接口
     */
    public static final String EXITTEAM = API + "exitTeam";
    /***
     * 审核新成员页面接口
     */
    public static final String APPLYUSERLIST = API + "applyUserList";
    /**
     * 审核拒绝通过接口
     */
    public static final String CHECKAPPLYUSER = API + "checkApplyUser";

    /***
     * 回复接口
     */
    public static final String REPLY = API + "reply";
    /**
     * 战队认证接口
     */
    public static final String TEAMAUTH = API + "teamAuth";

    /**
     * 企业认证接口
     */
    public static final String ENTERPRISEAUTH = API + "enterpriseAuth";

    /**
     * 企业认证回显接口
     */
    public static final String ENTERPRISEAUTHINFO = API + "enterpriseAuthInfo";

    /***
     * 返回保证金金额和账户余额接口
     */
    public static final String PSERSONALAUTH = API + "psersonalAuth";

    /***
     * 保证金缴纳提交接口
     */
    public static final String PSERSONALMoney = API + "psersonalMoney";

    /**
     * 战队成员踢人接口
     */
    public static final String DelUserFromTeam = API + "delUserFromTeam";
    /**
     * 战队特长信息接口
     */
    public static final String TeamSpeciality = API + "teamSpeciality";
    /**
     * 我的战队接口
     */
    public static final String MyTeams = API + "myTeams";
    /**
     * 加入战队页面战队列表以及搜索接口
     */
    public static final String Teamlist = API + "teamlist";
    /**
     * 申请加入战队接口
     */
    public static final String ApplyToTeam = API + "applyToTeam";
    /**
     * 回复接口
     */
    public static final String Reply = API + "reply";
    /**
     * 邀请手机通讯录匹配接口
     */
    public static final String InvitePhonelist = API + "invitePhonelist";
    /**
     * 邀请发送短信模板接口
     */
    public static final String MessageTemplate = API + "messageTemplate";
    /**
     * 加入战队好友邀请接口
     */
    public static final String InviteToTeam = API + "inviteToTeam";

    /**
     * 集图分享页接口
     */
    public static final String ShareActivity = API + "shareActivity";
    /**
     * 保证金说明页面接口
     */
    public static final String margin = API + "margin";

    /**
     * 战队设置页面接口
     */
    public static final String TeamSetting = API + "teamSetting";
    /**
     * 战队设置信息提交接口
     */
    public static final String TeamSettingUpdate = API + "teamSettingUpdate";
    /**
     * 设置队副提交接口
     */
    public static final String DeputySetting = API + "deputySetting";
    /**
     * 解散战队接口
     */
    public static final String DissolveTeam = API + "dissolveTeam";
    /**
     * 战队公告列表接口
     */
    public static final String NoticeList = API + "noticeList";
    /**
     * 发布公告提交接口
     */
    public static final String PublishNotice = API + "publishNotice";
    /**
     * 公告详情接口
     */
    public static final String NoticeDetail = API + "noticeDetail";
    /**
     * 创建战队提交接口
     */
    public static final String BulidTeam = API + "bulidTeam";
    /**
     * 编辑战队页面接口
     */
    public static final String EditTeamInfo = API + "editTeamInfo";
    /**
     * 编辑战队提交接口
     */
    public static final String UpdateTeam = API + "updateTeam";
    /**
     * 删除公告接口
     */
    public static final String DelNotice = API + "delNotice";
    /**
     * V3.14版本验证码登录
     */
    public static final String IdentifyLogin = API + "login3_14";
    /**
     * 新账号设置密码
     */
    public static final String SetPassword = API + "setPassword";
    /**
     * 战队分配成员列表
     */
    public static final String DistributeTeamUserList = API + "distributeTeamUserList";
    /**
     * 检查是否需要强制踢出
     * data 1需要强制踢出
     * data 0正常踢出，后台直接执行踢人操作
     */
    public static final String CheckDelUserFromTeam = API + "checkDelUserFromTeam";
    /**
     * 队长备注历史
     */
    public static final String TeamMemberRemarkInfo = API + "teamMemberRemarkInfo";
    /**
     * 队长备注上传
     */
    public static final String RemarkTeamMember = API + "remarkTeamMember";
    /**
     * 是否开启广场
     */
    public static final String CheckOpenSquare = API + "checkOpenSquare";
    /**
     * 战队项目任务包信息接口（有网点和无网点的）
     */
    public static final String OutletPackage = API + "outletPackage";
    /**
     * 领取任务包或认证选择战队接口
     */
    public static final String SelectTeam = API + "selectTeam";
    /**
     * 战队项目网点分布明细接口
     */
    public static final String OutletPackageDetail = API + "outletPackageDetail";
    /**
     * 领取确认弹出框信息接口
     */
    public static final String RobConfirm = API + "robConfirm";
    /**
     * 确认领取任务提交接口
     */
    public static final String RobSubmit = API + "robSubmit";
    /**
     * 战队任务等待执行网点信息页接口（有价格和无价格，无分页）
     */
    public static final String WaitExecuteOutlet = API + "waitExecuteOutlet";
    /**
     * 网点单个分配提交接口
     */
    public static final String SingleDistribution = API + "singleDistribution";
    /**
     * 网点批量分配提交接口
     */
    public static final String MultipleDistribution = API + "multipleDistribution";
    /**
     * 奖励金额页面说明
     */
    public static final String RewardInstructions = API + "rewardInstructions?";
    /**
     * 价格调整接口
     */
    public static final String PriceAdjustment = API + "priceAdjustment";
    /**
     * 领取任务协议
     */
    public static final String RobTaskProtocol = API + "robTaskProtocol?";
    /**
     * 战队无网点项目任务包领取详情接口
     */
    public static final String NoOutletPackageDetail = API + "noOutletPackageDetail";
    /**
     * 战队任务等待执行列表
     */
    public static final String WaitExecutePackage = API + "waitExecutePackage";
    /**
     * 战队任务项目状态列表
     */
    public static final String TeamProjectState = API + "teamProjectState";
    /**
     * 队员执行明细页接口
     */
    public static final String TeamMemberExeInfo = API + "teamMemberExeInfo";
    /**
     * 战队任务查看地图
     */
    public static final String OutletPackageMap = API + "outletPackageMap";
    /**
     * 我的偶米页面接口
     */
    public static final String MYOM = API + "myOm";
    /**
     * 战队设置广场开启或关闭接口
     */
    public static final String TeamSquare = API + "teamSquare";

    /***
     * 战队任务项目状态网点信息接口
     */
    public static final String OutLetState = API + "outletState";

    /**
     * 未通过网点放弃接口
     */
    public static final String AbandonUnpassOutlet = API + "abandonUnpassOutlet";

    /**
     * 队员接受任务接口
     */
    public static final String AcceptTeamTask = API + "acceptTeamTask";
    /**
     * 队员放弃任务接口
     */
    public static final String AbandonTeamTask = API + "abandonTeamTask";
    /**
     * 甩图首页
     */
    public static final String ActivityIndex = API + "activityIndex3_18";

    /***
     * 已投放,草稿箱，已结束的任务列表接口
     */
    public static final String ActivityList = API + "activityList3_17";
    /**
     * 我的红包接口
     */
    public static final String MyRedPack = API + "myRedPack";
    /**
     * 随手发任务页面接口
     */
    public static final String ReleaseTask = API + "releaseTask3_20";
    /**
     * 集图活动创建提交接口
     */
    public static final String CreateActivity = API + "createActivity3_18";
    /**
     * 主题分类信息接口
     */
    public static final String ThemeInfo = API + "themeInfo";
    /**
     * 场所类别信息接口
     */
    public static final String PlaceInfo = API + "placeInfo";
    /**
     * 拆红包接口
     */
    public static final String OpenRedPack = API + "openRedPack";
    /**
     * 活动详情页接口
     */
    public static final String ActivityDetail = API + "activityDetail";
    /**
     * 查看主题相册接口
     */
    public static final String ActivityPhotoDetail = API + "activityPhotoDetail";
    /**
     * 本地相册接口
     */
    public static final String ActivityPhotoAlbum = API + "activityPhotoAlbum";
    /**
     * 我参与的活动列表接口
     */
    public static final String ParticipateActivitiy = API + "participateActivitiy";
    /**
     * 相册页面参与的活动——>邀请
     */
    public static final String InviteToActivity = API + "inviteToActivity?";
    /**
     * 本地相册照片删除接口
     */
    public static final String DelPhoto = API + "delPhoto3_19";
    /**
     * 五、	创建位置提交接口
     */
    public static final String CreateAddress = API + "createAddress";
    /**
     * 六、	照片上传回调接口
     */
    public static final String CallbackFileInfo = API + "callbackFileInfo";

    /***
     * 赞助费缴纳接口
     */
    public static final String SponsorshipPay = API + "sponsorshipPay";
    /**
     * 编辑活动信息页接口
     */
    public static final String SelectActivityInfo = API + "selectActivityInfo";
    /**
     * 相册地图查看接口
     */
    public static final String ActivityPhotoMap = API + "activityPhotoMap";
    /**
     * 根据分类选择主题接口
     */
    public static final String ActivityListByTheme = API + "activityListByTheme";
    /**
     * 位置列表接口
     */
    public static final String AddressList = API + "addressList";
    /**
     * 支付页面接口(任务模板)
     */
    public static final String ProjectPayInfo = API + "projectPayInfo3_21";
    /**
     * 获取更多优惠提交接口
     */
    public static final String BigCustomersSubmit = API + "bigCustomersSubmit";
    /**
     * 任务内容接口
     */
    public static final String TemplateTasklist = API + "taskTemplateDetail3_21";
    /**
     * 删除位置接口
     */
    public static final String DelAddress = API + "delAddress";


    /***
     * 创建标签接口
     */
    public static final String CreateLabel = API + "createLabel";
    /**
     * 支付提交接口
     */
    public static final String ProjectPayConfirm = API + "projectPayConfirm3_20";
    /***
     * 已发布的任务详情（待验收、已验收）列表接口
     */
    public static final String OutletStateDetail = API + "outletStateDetail";
    /**
     * 再次投放接口
     */
    public static final String Republish = API + "republish";
    /**
     * 消息-任务详情页接口
     */
    public static final String ShareMessageDetail = API + "shareMessageDetail";

    /**
     * 标签列表接口
     */
    public static final String LabelList = API + "labelList";
    /**
     * 标签中添加或删除手机号接口
     */
    public static final String LabelEdit = API + "labelEdit";

    /**
     * 整体分享接口
     */
    public static final String ShareProjectInfo = API + "shareProjectInfo";

    /**
     * 验收通过或不通过接口
     */
    public static final String CheckOutlet = API + "checkOutlet";
    /**
     * 查看详情页面分享接口
     */
    public static final String ShareTaskDetail = API + "shareTaskDetail";
    /**
     * 关闭项目接口
     */
    public static final String CloseProject = API + "closeProject";
    /**
     * 领奖地址提交接口
     */
    public static final String AcceptPrize = API + "acceptPrize";
    /**
     * 全部活动页面接口(包含我参与的活动)
     */
    public static final String AllActivitiy = API + "allActivitiy3_19";
    /**
     * 排名列表接口
     */
    public static final String RankingInfo = API + "rankingInfo";
    /**
     * 我参与的活动我的照片接口（用户分享照片接口）
     */
    public static final String JoinActivityPhotoAlbum = API + "joinActivityPhotoAlbum";
    /**
     * 拆红包接口 V3.18
     */
    public static final String NewOpenRedPack = API + "openRedPack3_20";
    /**
     * 赞助活动页面接口
     */
    public static final String SponsorActivity = API + "sponsorActivity";
    /**
     * 赞助活动支付页面接口
     */
    public static final String SponsorPayInfo = API + "sponsorPayInfo3_20";
    /**
     * 分享到广场接口
     */

    public static final String ShareToSquare = API + "shareToSquare";
    /**
     * 活动详情接口
     */
    public static final String NewActivityDetail = API + "activityDetail3_18";
    /**
     * 获奖用户信息接口
     */
    public static final String PrizeUserInfo = API + "prizeUserInfo";


    //******************************* V3.18 **************************//


    /**
     * 赞助人详情接口
     */
    public static final String SponsorInfo = API + "sponsorInfo";
    /**
     * 活动奖项展示信息接口
     */
    public static final String PrizeInfo = API + "prizeInfo";

    /*
     *  创建活动填写活动主题页面接口
     *
     */
    public static final String ThemeSytle = API + "themeSytle";
    /**
     * 八、	活动相册接口
     */
    public static final String ThemePhotoAlbum = API + "themePhotoAlbum3_19";
    /**
     * 十三、	图片/活动 分享接口
     */
    public static final String ShareActivityIndex = API + "shareActivityIndex";
    /***
     * 赞助支付接口
     */
    public static final String SponsorPayConfirm = API + "sponsorPayConfirm3_20";

    /**
     * 根据照片选择活动分类信息接口     themeInfoByPhoto
     */
    public static final String ThemeInfoByPhoto = API + "themeInfo";

    /**
     * 根据照片和分类选择活动主题信息接口       activityListByThemeAndPhoto
     */
    public static final String ActivityListByThemeAndPhoto = API + "activityListByTheme";

    /**
     * 自由拍上传图片提交接口
     */
    public static final String ScenePhotoUpload = API + "scenePhotoUpload";

    /**
     * 广告查看接口
     */
    public static final String ShowAdvertisement = API + "showAdvertisement";

    /**
     * 图片点赞或取消赞接口
     */
    public static final String PraisePhoto = API + "praisePhoto";
    /***
     * 图片评论信息页接口
     */
    public static final String CommentList = API + "commentList";

    /**
     * 图片评论提交接口
     */
    public static final String CommentPhoto = API + "commentPhoto";
    /**
     * 图片评论点赞接口
     */
    public static final String PraiseComment = API + "praiseComment";
    /**
     * 分享成功接口
     */
    public static final String ShareSuccess = API + "shareSuccess";
    /**
     * 广告评论信息页接口
     */
    public static final String AdCommentList = API + "adCommentList";
    /**
     * 广告点赞或取消赞接口写
     */
    public static final String PraiseAd = API + "praiseAd";

    /**
     * 广告评论提交接口                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             息页c接口
     */
    public static final String CommentAd = API + "commentAd";

    /***
     * 草稿箱活动删除接口（新增）
     */
    public static final String DelActivityInfo = API + "delActivityInfo";

    /**
     * 排名说明页面接口
     */
    public static final String RankingDescription = API + "rankingDescription";
    /**
     * 领取现金或额外红包接口
     */
    public static final String GetRedPack = API + "getRedPack3_20";

    /**
     * 新评论页面接口
     */
    public static final String NewComments = API + "newComments";

    /**
     * 评论发送接口
     */
    public static final String SendComment = API + "sendComment";

    /**
     * 举报投诉照片提交接口
     */
    public static final String InformPhoto = API + "informPhoto";
    /**
     * 礼品奖励列表接口
     */
    public static final String GiftList = API + "giftList";
    /**
     * 收货地址列表接口
     */
    public static final String ConsigneeAddressList = API + "consigneeAddressList";
    /**
     * 收货地址删除接口
     */
    public static final String DelConsigneeAddress = API + "delConsigneeAddress";
    /**
     * 领取礼品提交接口
     */
    public static final String GetGift = API + "getGift";
    /**
     * 店铺地址删除接口
     */
    public static final String DelMerchantOutlet = API + "delMerchantOutlet";
    /***
     * 添加分店提交接口
     */
    public static final String AddMerchantOutlet = API + "addMerchantOutlet";
    /***
     * 网店列表接口
     */
    public static final String OnlineStoreList = API + "onlineStoreList";
    /**
     * 十一、	更多模板接口
     */
    public static final String TemplateList = API + "templateList";
    /**
     * 赞助活动模板选择
     */
    public static final String SponsorshipTemplateList = API + "sponsorshipTemplateList";
    /**
     * 十五、	我投放的赞助-（投放中、已结束）接口
     */
    public static final String SponsorshipActivityList = API + "sponsorshipActivityList";
    /**
     * 四十六、	集图活动模板信息查询接口
     */
    public static final String ActivityTemplateDetail = API + "activityTemplateDetail";
    /**
     * 五十一、	微信登录校验接口
     */
    public static final String WechatLogin = API + "wechatLogin";
    /**
     * 礼品库列表接口
     */
    public static final String GiftLibrary = API + "giftLibrary";
    /***
     * 礼品删除接口
     */
    public static final String DelGift = API + "delGift";
    /**
     * 礼品添加提交接口
     */
    public static final String AddGift = API + "addGift";
    /**
     * 店铺地址列表接口
     */
    public static final String MerchantOutletList = API + "merchantOutletList";
    /**
     * 收货地址添加或编辑提交接口
     */
    public static final String UpdateConsigneeAddress = API + "updateConsigneeAddress";
    /**
     * 收货地址编辑页面接口
     */
    public static final String EditConsigneeAddress = API + "editConsigneeAddress";
    /**
     * 邀请发任务接口
     */
    public static final String InviteReleaseTask = API + "inviteReleaseTask?";
    /**
     * 校验是否进行商户认证和是否有礼品库接口
     */
    public static final String CheckMerchantCA = API + "checkMerchantCA";
    /**
     * 礼品卡券页面接口
     */
    public static final String MyGift = API + "myGift";
    /**
     * 我的礼品列表接口
     */
    public static final String MyGiftDetail = API + "myGiftDetail";
    /**
     * 消息列表页接口
     */
    public static final String MessageList = API + "messageList";
    /**
     * 网店删除接口
     */
    public static final String DelOnlineStore = API + "delOnlineStore";
    /**
     * 网店添加提交接口
     */
    public static final String AddOnlineStore = API + "addOnlineStore";

    /***
     * 项目奖励接口
     */
    public static final String ProjectReward = API + "projectReward";
    /**
     * 认证商户提交接口
     */
    public static final String MerchantCA = API + "merchantCA";

    /***
     * 我的商户页面接口
     */
    public static final String MyMerchant = API + "myMerchant";
    /**
     * 商户logo上传回调接口
     */
    public static final String MerchantLogoUpload = API + "merchantLogoUpload";
    /**
     * 商户信息查询接口
     */
    public static final String MerchantInfo = API + "merchantInfo";
    /***
     * 商户信息提交接口
     */
    public static final String MerchantInfoSubmit = API + "merchantInfoSubmit";
    /***
     * 商户账号页面接口
     */
    public static final String MerchantAccount = API + "merchantAccount";
    /**
     * 商户充值接口
     */
    public static final String MerchantRecharge = API + "merchantRecharge";

    /***
     * 二十五、	商户账户明细接口
     */
    public static final String MerchantAccountDetail = API + "merchantAccountDetail";

    /**
     * 商户认证信息查询接口
     */
    public static final String MerchantCAInfo = API + "merchantCAInfo";
    /**
     * 消息详情接口
     */
    public static final String MessageDetail = API + "messageDetail";

    /***
     * 赞助活动任务模板查询接口
     */
    public static final String SponsorTemplateDetail = API + "sponsorTemplateDetail";
    /**
     * 添加微信绑定接口
     */
    public static final String BindWeChat = API + "bindWeChat";
    /**
     * 草稿箱信息回显接口
     */
    public static final String ProjectPayInfoShow = API + "projectPayInfoShow";
    /**
     * 集图活动再次投放信息获取
     */
    public static final String Republish2 = API + "republish2";

    /***
     * 甩投任务追加费用页面接口
     */
    public static final String Selectprice = API + "selectprice";

    /*****
     * 甩投任务追加费用提交接口
     */

    public static final String AddpricePayInfo = API + "addpricePayInfo";

    /***
     * 甩投任务追加费用确认支付接口
     */
    public static final String AddpricePayConfirm = API + "addpricePayConfirm";

    /***
     * 邮件导入是否成功校验接口
     */
    public static final String CheckImportMobile = API + "checkImportMobile";

    /***
     * 邮件附件导入手机号提交接口
     */
    public static final String ImportMobileSubmit = API + "importMobileSubmit";
    /**
     * 网店体验任务说明页面接口
     */
    public static final String ExperienceTaskInfo = API + "experienceTaskInfo";
    /**
     * 体验任务查看详情接口
     */
    public static final String ExperienceTaskDetail = API + "experienceTaskDetail";
    /**
     * 全部截图页面接口
     */
    public static final String PrintscreenList = API + "printscreenList";
    /**
     * 体验任务截图回调接口
     */
    public static final String CallbackExperienceFileInfo = API + "callbackExperienceFileInfo";
    /**
     * 体验任务执行完成接口
     */
    public static final String ExperienceTaskComplete = API + "experienceTaskComplete";
    /**
     * 四、	逛店截图评论页面接口
     */
    public static final String PageCommentList = API + "pageCommentList";
    /**
     * 五、	逛店截图评论点赞/取消赞接口
     */
    public static final String PraisePageComment = API + "praisePageComment";
}

