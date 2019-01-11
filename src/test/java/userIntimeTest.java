import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.intime.soa.Util.StringUtils;
import com.intime.soa.anygate.junit.GetExcelInfo;
import com.intime.soa.anygate.mapper.GateLogMapper;
import com.intime.soa.anygate.mapper.GateUserIntimeMapper;
import com.intime.soa.anygate.mapper.GateUserMapper;
import com.intime.soa.framework.util.id.IdWorker;
import com.intime.soa.framework.util.validate.CheckUtil;
import com.intime.soa.model.anygate.GateLog;
import com.intime.soa.model.anygate.GateUser;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by qmx on 2017/12/5.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/spring-datasource-dev.xml"})
public class userIntimeTest {


    @Autowired
    GateUserIntimeMapper gateUserIntimeMapper;

    @Autowired
    GateUserMapper gateUserMapper;

    @Autowired
    GateLogMapper gateLogMapper;

    @Test
    public void testAdd() throws Exception {

        List<Map<String,Object>> userList = Lists.newArrayList();

        com.intime.soa.anygate.junit.GetExcelInfo obj = new GetExcelInfo();
        // 此处为我创建Excel路径：E:/zhanhj/studysrc/jxl下
        File file = new File("C:/Users/qmx/Desktop/1.xls");

        try {
            // 创建输入流，读取Excel
            InputStream is = new FileInputStream(file.getAbsolutePath());
            // jxl提供的Workbook类
            Workbook wb = Workbook.getWorkbook(is);
            // Excel的页签数量
            int sheet_size = wb.getNumberOfSheets();

            for (int index = 0; index < sheet_size; index++) {
                // 每个页签创建一个Sheet对象
                Sheet sheet = wb.getSheet(index);
                // sheet.getRows()返回该页的总行数

                for (int i = 1; i < sheet.getRows(); i++) {
                    Map<String,Object> param = Maps.newHashMap();
                    // sheet.getColumns()返回该页的总列数
                    for (int j = 0; j < sheet.getColumns(); j++) {
                        String cellinfo = sheet.getCell(j, i).getContents();
                        System.out.println(cellinfo);
                        if (j==0){
                            param.put("name",cellinfo);
                        }
                        if(j==1){
                            param.put("mobile",cellinfo);
                        }
                    }
                    userList.add(param);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(userList);

//        List<Map<String,Object>> userList = gateUserIntimeMapper.findMobileInsertUser();



        List<GateUser> list = Lists.newArrayList();
        List<GateUser> users = gateUserMapper.query(Maps.newHashMap());

        Set<String> sets = Sets.newHashSet();

        for(GateUser gateUser:users){
            sets.add(gateUser.getMobile());
        }

        for (Map<String,Object> user:userList){
            int before = sets.size();
            sets.add(StringUtils.safeToString(user.get("mobile")));
            int after = sets.size();
            if(after == before+1){
                GateUser gateUser = new GateUser();
                gateUser.setId(IdWorker.getUUID());
                gateUser.setName(StringUtils.safeToString(user.get("name")));
                gateUser.setMobile(StringUtils.safeToString(user.get("mobile")));
                gateUser.setCreateUserName("邱慕夏");
                gateUser.setCreateTime(new Date());
                list.add(gateUser);
            }
        }

        if(!CheckUtil.isEmpty(list)){
            Long result =  gateUserMapper.insertBatchPartly(list);
            System.out.println(result);
        }
    }

    @Test
    public void testInsertUrl(){
        GateLog gateLog = new GateLog();
        gateLog.setUserId("cdfc25385a744f5a8d2cc2c638ebb060");
        gateLog.setUserName("孙东国");
        gateLog.setMobile("13910775806");
        gateLog.setIp("172.168.1.104");
        gateLog.setUrl("http://soaanygateapi/soa-anygate-api/anygate/projects");
        gateLog.setUrlName("获取理财师提成待结算订单列表");
        gateLog.setParam("{\"publishState\":\"0\",\"summary\":\"【今日聚焦】\\n1、“加强版”民企支持新政落地在即 更大力度减税降费政策将出\\n2、李克强：“中国制造”要尽早变为“中国精造”\\n3、A股将迎长期资金 万亿元银行理财入市在望\",\"isPush\":\"1\",\"title\":\"【音频版】财经早知道2018年10月26日|A股将迎长期资金 万亿元银行理财入市在望\",\"publishTime\":\"2018-10-26 07:30:00\",\"productId\":\"\",\"category\":\"4\",\"content\":\"\\n\\n\\n您的浏览器不支持此音频的播放\\n\\n\\n今日聚焦\\n\\n1、&ldquo;加强版&rdquo;民企支持新政落地在即 更大力度减税降费政策将出\\n\\n2、李克强：&ldquo;中国制造&rdquo;要尽早变为&ldquo;中国精造&rdquo;\\n\\n3、A股将迎长期资金 万亿元银行理财入市在望\\n4、证券时报头版评论：为亏损投资者减免股票交易印花税\\n\\n&nbsp;\\n\\n宏观新闻\\n\\n&nbsp;\\n\\n1、&ldquo;加强版&rdquo;民企支持新政落地在即 更大力度减税降费政策将出\\n\\n记者从权威人士处获悉，&ldquo;加强版&rdquo;民企支持新政还在酝酿，有望加快落地，除解决准入准营和融资难题外，还将出台更高层次、更大力度的减税降费政策，着力提高中小企业获得感。\\n\\n&nbsp;\\n\\n2、李克强：&ldquo;中国制造&rdquo;要尽早变为&ldquo;中国精造&rdquo;\\n\\n李克强总理10月24日应邀在中国工会第十七次全国代表大会上作经济形势报告时表示，&ldquo;中国制造&rdquo;要尽早变为&ldquo;中国精造&rdquo;，无论是日常消费品生产，还是高精尖制造，都需要有一大批&ldquo;身怀绝技&rdquo;的大国工匠。只要潜心弘扬精益求精的工匠精神，大胆创新能者多得的激励机制，中国制造就不仅会以性价比风靡全球，更能靠高质量行销世界。\\n\\n&nbsp;\\n\\n3、广州发布20个国企混改项目 涉及总资产约5000亿元\\n\\n近日，广州市国资委发布混合所有制改革20个重点项目，将公开招募战略投资者。20个广州市国资重点混改项目涉及总资产约5000亿元，年营业收入约300亿元，净利润近40亿元。\\n\\n&nbsp;\\n\\n4、港珠澳大桥开通 粤港澳各产业迎崭新机遇\\n\\n专家指出，随着港珠澳大桥的正式开通，粤港澳大湾区在核心发展轴和珠江西岸次轴的融合发展下，辐射范围将大大扩大，影响力深入华南、西南腹地，统筹协作推动空间由多中心转向网络化，辐射东南亚、南亚的经济大格局，提供关键的基建保障，创造难以估量的经济社会价值，令粤港澳大湾区成为足以媲美东京湾区、纽约湾区、旧金山湾区的世界著名湾区，为中国经济发展创建重大战略增长极。\\n\\n&nbsp;\\n\\n金融信息\\n\\n&nbsp;\\n\\n1、A股将迎长期资金 万亿元银行理财入市在望\\n\\n截至目前，包括徽商银行、兴业银行、浦发银行、杭州银行等在内的16家银行拟设立理财子公司。接受记者采访的专家表示，随着银行理财子公司发行的公募产品获准直接投资股市，A股将迎来银行系长期资金，有利于提振股市信心，并带动其他资金进入股市。\\n\\n&nbsp;\\n\\n2、险资将设专项产品参与化解股权质押风险 主投上市公司\\n\\n从某大型保险公司了解到，该公司筹备的参与化解上市公司股权质押风险的专项产品已准备就绪，正在走流程，在履行完相关程序后将很快面世，有望成为首只险资专项产品。另据了解，多数险企目前已经开始着手准备，对相关上市公司进行调研，并根据上市公司业绩情况以及股东财务实力等情况采取相应措施。\\n\\n&nbsp;\\n\\n3、证券时报头版评论：为亏损投资者减免股票交易印花税\\n\\n证券时报头版评论称，为亏损投资者免除或减少股票交易印花税为可行的办法之一。首先，亏损者不承担税负是国际股市的惯例。其次，股票交易印花税在财税总收入中占比较低，但对股市来说仍然存在较明显的抽血效应。第三，与完全取消股票交易印花税相比，减免股票交易印花税能更适用于各种市场环境，可以避免政策频繁调整对市场的冲击。\\n\\n&nbsp;\\n\\n4、券商大集合参照公募基金改造办法有望近期发布\\n\\n针对存量券商大集合产品的整改方案已经清晰。多位知情人士透露，监管机构已经制定了券商大集合产品参照公募基金改造的办法，并有望在近期发布。\\n\\n&nbsp;\\n\\n5、&ldquo;私募管公募&rdquo;模式有望开启\\n\\n日前公布的《商业银行理财子公司管理办法（征求意见稿）》及相关文件中，明确了依法合规、符合条件的私募投资基金管理人可以是银行公募理财产品的投顾和私募理财产品的合作机构。业内人士认为，尽管过去在银行委外资金中也采取过用私募做投顾的方式，但是，征求意见稿此次拟允许私募管理人为公募理财产品做投顾，不仅有望开启&ldquo;私募管公募&rdquo;新模式，为私募带来可观的潜在增量资金，或许还将加速&ldquo;公奔私&rdquo;频率。\\n\\n&nbsp;\\n\\n国际要闻\\n\\n&nbsp;\\n\\n1、美联储加息隐忧再致美股暴跌\\n\\n由于投资者担心美联储加息可能已开始对美国经济造成负面影响，纽约股市三大股指24日在大量抛盘重压下再次出现暴跌。其中，纳斯达克综合指数跌逾4.4%，道琼斯工业平均指数、标准普尔500种股票指数则已抹去今年以来涨幅。全球最大对冲基金桥水基金近日警告，美联储收紧货币政策抑制经济增长泡沫的同时，对金融市场产生压力，美国经济增长可能已经&ldquo;见顶&rdquo;。\\n\\n&nbsp;\\n\\n\\n\",\"author\":\"盈泰财富云\",\"imgUrl\":\"https://upload.simuyun.com/information/zzd_15.jpg\",\"id\":\"1f09e949443042afb7fde9466a8d890b\"}");
        gateLog.setResult(200);
        gateLogMapper.insertPartly(gateLog);
    }
}
