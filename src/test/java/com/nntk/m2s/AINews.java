package com.nntk.m2s;

import com.alibaba.dashscope.app.Application;
import com.alibaba.dashscope.app.ApplicationParam;
import com.alibaba.dashscope.app.ApplicationResult;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.google.common.collect.Lists;
import com.nntk.m2s.constant.CommonConst;
import com.nntk.m2s.mp.generate.mapper.TCityMapper;
import com.nntk.m2s.mp.generate.mapper.TCountryMapper;
import com.nntk.m2s.mp.generate.mapper.TNewsMapper;
import com.nntk.m2s.mp.generate.mapper.TProvinceMapper;
import com.nntk.m2s.service.IAiService;
import com.nntk.m2s.service.INewsService;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@Slf4j
class AINews {


    @Autowired
    private TProvinceMapper provinceMapper;

    @Autowired
    private TCountryMapper countryMapper;


    @Autowired
    private TCityMapper cityMapper;


    @Autowired
    private TNewsMapper newsMapper;

    @Autowired
    private IAiService aiService;


    @Autowired
    private INewsService newsService;


    @Test
    void testAiChat() {

        String prompt = """
                %s。
                这段文本中拆分出有哪些是国际新闻，并返回拆分后的片段，以及一个比较清楚的新闻标题。返回json数组格式，每一个元素有两个属性，title代表新闻标题，content代表片段
                """.formatted("风险红色预警持续降雨，造成陇南境内十一条国省干道部分路段出现泥石流堆积塌方等情况。通行受阻，相关部门组织抢险力量搭建应急爆通排今昨天早七点到晚七点，四川成都市中心城区现现降降，最最累累降降水达三三二十三点八毫毫米，部分低洼地出出现积水已全重被困。当地消防部门立即分派抢援人员转移被困群众。当晚八点多，成华区一名孕妇即将临产。接到报警后，救援人员合力将孕妇转送医院台阶，门口也有台阶。然后斜坡坡脚下试探一下，早下试探到了今天受降雨影响，导致积水的五福隧道等多条道路积水已全部排空。成都中心城区交通恢复通畅。中央气象台预计，未来三天，四川盆地、西北地区东部、华北、东北等地仍降雨频繁，国家防灾减灾救灾委员会办公室应急管理部今天会同国家粮食和物资储备局向四川、甘肃、辽宁调拨折叠床、毛毯、家庭应急包等二点五万件。中央救灾物资支持当地妥善做好受灾群众转移安置和救灾救助工作。今年以来，最强高温过程正在影响我国。今天，新疆吐鲁番、陕西关中等地局地气温达四十摄氏度以上，中央气象台今天继续发布高温黄色预警，预计今明两天高温范围将达本轮过程，最大提醒公众做好防暑降温措施。下面来看，一组联播快讯。记者从工业和信息化部了解到，我国将开展号码保护服务业务试点号码保护服务业务是指受快递、外卖、网约车等互联网平台及其他企事业单位委托为平台和委托单位的个人用户分配临时号码代替真实手机号、服务人员和个人用户通过临时号码通信的业务，为了便于用户识别临时号码使用七零零号段由十五位数字组成。国家药监局日前发布新举措，针对医用机器人、高端医学、影像设备、人工智能和新型生物材料、医疗器械等高端医疗器械，从优化特殊审批程序，完善分类和命名原则等十个方面，全力支持高端医疗器械重大创新，促进更多新技术、新材料、新工艺和新方法应用于医疗健康领域。记者从水利部了解到，七月三号小浪底水库出库水流由轻变浊，标志着二零二五年黄河调水调沙正式进入排沙阶段，预计本次排沙将持续到七月九号，排沙量将超过一亿吨，可以为小浪底水利枢纽释放出六千万立方米以上的淤沙库容，确保主汛期有足够的调洪能力。据上海海关消息，今年上半年，上海港进出国际航行船舶达二点三万艘次。同比增长百分之三点二，创历史同期新高，运载进出境集装箱一千六百三十四点六万标箱。同比分别增长百分之七，国际游轮和汽车滚装船增长迅猛。同比分别增长百分之八十点一、百分之十三点五。截至今天，今年以来，中欧班列西安累计开行超三千列，较二零二四年提前四十天突破三千列，同比增幅超百分之二十二。目前，中欧班列西安以常态化开行，十八条国际运输干线，辐射欧洲二十五国亚洲十一国由国家电影局、重庆市政府联合主办的二零二五上合组织国家电影节昨晚在重庆开幕。本届电影节以科技光影上合风采为主题，来自上合组织国家的四十八部优秀影片将集中展映，并将评选出金山茶奖、最佳影片、最佳男女演员等十个奖项期间，还推出跟着电影去旅游、电影、惠民、消费季等系列活动作为配合上合组织峰会召开的重要文化交流活动，上合组织国家电影节是时隔七年后再度在中国举办。今晚，总台央视新闻频道高端访谈栏目将播出对塞奈加尔总理松科的专访松科表示，赞赏中国发展模式高度评价中非命运共同体，期待深化互利合作。本节目还将在央视新闻、央视频、央视网等新媒体平台同步上线。我俄罗斯国防部三号称，俄军已控制哈尔科夫地区的梅洛沃耶村和顿涅茨克地区的拉齐诺镇。俄军当天对乌方军工企业攻击型无人机组装车间以及停放海马斯火箭炮的地点进行打击，并在黑海水域摧毁无人快艇。乌克兰方面今天称，从三号晚上开始，首都基辅以及苏梅、哈尔科夫等多地遭俄罗斯导弹和无人机大规模袭击。当地时间今天上午，基辅才解除空袭，警报，特方称共击落和拦截俄方来袭的二百七十个空中目标。此外，乌方三号称二号丸是用无人机袭击了顿涅茨克地区的俄军弹药库。俄罗斯总统普京与美国总统特朗普三号通电话，俄总统助理乌沙科夫当天表示，普京在通话中说，俄方将继续寻求通过政治谈判解决冲突。但他同时强调，俄罗斯不会放弃消除，导致乌克兰冲突根源的目标。特朗普称，此次通话没有取得任何进展，以军三号继续对巴勒斯坦加沙地带多地发动空袭，加沙地带。卫生部门三号称自当天凌晨起以军袭击，共造成一百零一人死亡，其中包括五十一名正在领取人道主义援助物资的平民。据美国媒体近日报道，美国一家在加沙地带承包安保工作的公司雇员提供视频称，在美国和以色列支持的所谓加沙人道主义基金会物资分发点枪声不断。视频提供者称，他们看到该公司其他雇员经常在分发点现场开枪发射干扰弹和使用胡椒喷雾。据黎巴嫩国家通讯社报道，黎巴嫩奈拜、提、耶省等多地三号晚遭以军空袭。黎巴嫩公共卫生部门称，以色列的袭击，造成一人死亡、三人受伤。以色列国防军当天称轰炸了位于黎巴嫩南部的黎真主党军事基地和武器库等目标。下面来看，一组国际快讯，伊朗外长阿拉格齐三号表示，伊朗将继续遵守不扩散核武器条约及其保障监督协定。他强调，根据伊朗议会近日通过的法案，如果伊朗要与国际原总的机构合作，首先要获得伊朗最高国家安全委员会批准伊朗副外长塔赫特拉万西同一天表示，伊朗没有对美国采取进一步报复行动的计划，但伊朗将继续进行由浓缩活动。美国国会众议院三号以二百一十八票赞成二百一十四票反对的表决结果通过了美国总统特朗普推动的税收与支出法案法案此前已在参院通过，下一步将交由特朗普签署该法案计划在未来十年内减税四万亿美元，并削减至少一点五万亿美元支出民主党指责该法案。劫贫济富将导致近一千二百万人失去医疗保险。据美国国会预算办公室估算，该法案生效后将在未来十年新增约三点三万亿美元财政赤字。美国伊利诺伊州警方三号发布消息称，芝加哥市中心二号晚发生一起大规模枪击事件，造成四人死亡，十四人受伤，多人伤势危重。据美国媒体报道，初步调查显示，当晚附近有一场发布会举行散场时，一辆行驶中的汽车内有枪手朝人群开枪。警方称，枪击嫌疑人目前仍在逃作案动机还在调查中。据日本气象厅消息，日本鹿尔岛县土嘎拉列岛附近海域三号下午发生五点五级地震，今天上午发生四点四级地震，土嘎拉列岛附近海域，近期地震活动频繁，自六月二十一号以来，已观测到该区域震堵一级以上地震超过一千次。据当地政府称，因地震频发，居民无法休息。当地部分民众今天开始向岛外疏散，三号希腊最大岛屿克里特岛的林火仍在持续。受强风影响，大火蔓延至住宅和酒店区域，部分房屋受损。当地媒体报道说，自二号发布撤离令后，当地已有约五千名游客撤离。当地部分地区断电通讯中断，希腊首都雅典东部拉菲那港附近三号也发生林火，部分地区民众被要求撤离。今天的新闻联播播送完了，感谢收看更多新闻资讯，请关注央视新闻客户端，更多移动视频，请下载央视频观众朋友再见再见。");
        String deepSeekResponse = aiService.getDeepSeekResponse(prompt);
        System.out.println(deepSeekResponse);
    }


    @Test
    void testBailian() throws NoApiKeyException, InputRequiredException {

        String prompt = """
                %s。返回这个新闻概要，要求markdown格式"""
                .formatted("印度一女子应聘军警晕倒后在救护车上被轮奸，当地卫生部门甩锅给私人机构", CommonConst.NEWS_NOT_FOUND);

        System.out.println(prompt);

        String bailianResponse = aiService.getBailianResponse(prompt);
        System.out.println(bailianResponse);
    }


}
