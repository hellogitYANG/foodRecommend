package com.example.foodrecommend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.*;
import com.example.foodrecommend.dto.FoodSkuDto;
import com.example.foodrecommend.dto.FoodSkuRecommend;
import com.example.foodrecommend.mapper.*;
import com.example.foodrecommend.service.FoodSkuService;
import com.example.foodrecommend.utils.CosineSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 86176
 * @description 针对表【food_sku】的数据库操作Service实现
 * @createDate 2023-11-13 00:14:57
 */
@Service
public class FoodSkuServiceImpl extends ServiceImpl<FoodSkuMapper, FoodSku>
    implements FoodSkuService{
    @Autowired
    OrdersMapper ordersMapper;
    @Autowired
    UserBehaviorMapper userBehaviorMapper;
    @Autowired
    FoodSkuMapper foodSkuMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    FoodCommentsMapper foodCommentsMapper;
    @Autowired
    FoodStatsDictionaryMapper foodStatsDictionaryMapper;
    @Autowired
    private MerchantRechargeMapper merchantRechargeMapper;
    @Resource
    private MerchantMapper merchantMapper;

    @Resource
    private DgtxPlacesMapper dgtxPlacesMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;


    // 设置时间范围
    static LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
    static LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
    static LocalDateTime now = LocalDateTime.now();
    @Override
    public Page<FoodSkuRecommend> getYouWantEat(Page page,String openId) {
        //第一页清空已显示的数据
        if(page.getCurrent()==1){
            clearUserViewedFoodSkus(openId);
        }
        //获取该用户已显示的数据
        Set<String> viewedFoodSkuIds = getUserViewedFoodSkus(openId);
        List<String> shownFoodIds=new ArrayList<>();
        for (String viewedFoodSkuId : viewedFoodSkuIds) {
            shownFoodIds.add(viewedFoodSkuId);
        }
        //口味余弦相似度推荐num个
        int num=3;
        List<FoodSku> kouWeiList = kouWeiFood(openId, userMapper, foodSkuMapper,foodStatsDictionaryMapper,shownFoodIds,num);
        //去重
        kouWeiList.forEach(foodSku -> {
            String id = foodSku.getId();
            if (id != null) {
                shownFoodIds.add(id);
            }
        });

        //用户协同三个
        int num2=3;
        List<FoodSku> xietongList = getXietong(openId, userMapper, foodSkuMapper,foodStatsDictionaryMapper,ordersMapper,shownFoodIds,num2);
        //去重
        xietongList.forEach(foodSku -> {
            String id = foodSku.getId();
            if (id != null) {
                shownFoodIds.add(id);
            }
        });

        //常买三个
        QueryWrapper<Orders> q1 = new QueryWrapper<>();
        q1.eq("user_id",openId);
        q1.ge("create_time", oneWeekAgo).le("create_time", now);
        // 排除已展示的菜品
        if (shownFoodIds != null && !shownFoodIds.isEmpty()) {
            q1.notIn("food_sku_id", shownFoodIds);
        }
        List<Orders> orders = ordersMapper.selectList(q1);
            // 统计每个商品出现的次数
            Map<String, Long> itemCountMap = orders.stream()
                    .collect(Collectors.groupingBy(Orders::getFoodSkuId, Collectors.counting()));

            // 按照商品出现次数降序排序
            List<FoodSku> foodSkuList = itemCountMap.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(3)
                    .map(entry -> foodSkuMapper.selectById(entry.getKey()))
                    .filter(Objects::nonNull) // 过滤掉空值，避免一些菜已经呗删除
                    .collect(Collectors.toList());
                    //去重
                    foodSkuList.forEach(foodSku -> {
                        if (foodSku!=null && foodSku.getId() != null) {
                            String id = foodSku.getId();
                            shownFoodIds.add(id);
                        }
                    });

        //最近(一天前到现在)浏览三个
        List<FoodSku> liuLan = getLiuLan(openId,userBehaviorMapper, foodSkuMapper,shownFoodIds);
            //去重
            liuLan.forEach(foodSku -> {
                String id = foodSku.getId();
                if (id != null) {
                    shownFoodIds.add(id);
                }
            });

        //好评次数最多三个
        List<FoodSku> haoPing = getHaoPing(openId, foodCommentsMapper, foodSkuMapper,shownFoodIds);
            //去重
            haoPing.forEach(foodSku -> {
                String id = foodSku.getId();
                if (id != null) {
                    shownFoodIds.add(id);
                }
            });


        //收藏菜品三个
        List<FoodSku> foodSkus = randomCollectFoodSku(openId, userMapper, foodSkuMapper,shownFoodIds);
        System.out.println(foodSkus);
            //去重
            foodSkus.forEach(foodSku -> {
                String id = foodSku.getId();
                if (id != null) {
                    shownFoodIds.add(id);
                }
            });
        //广告三个
        List<FoodSku> guanggao = getGuanggao(openId, merchantRechargeMapper, foodSkuMapper, shownFoodIds);
            //去重
            guanggao.forEach(foodSku -> {
                String id = foodSku.getId();
                if (id != null) {
                    shownFoodIds.add(id);
                }
            });

        //收集起来并返回
        List<FoodSkuRecommend> allFoodSku = new ArrayList<>();
        //口味3
        for (FoodSku foodSku : kouWeiList) {
            FoodSkuRecommend foodSkuRecommend = new FoodSkuRecommend();
            BeanUtil.copyProperties(foodSku,foodSkuRecommend);
            foodSkuRecommend.setStatus("口味");
            allFoodSku.add(foodSkuRecommend);
        }
        //常买3
        for (FoodSku foodSku : foodSkuList) {
            FoodSkuRecommend foodSkuRecommend = new FoodSkuRecommend();
            BeanUtil.copyProperties(foodSku,foodSkuRecommend);
            foodSkuRecommend.setStatus("常买");
            allFoodSku.add(foodSkuRecommend);
        }
        //广告3
        for (FoodSku foodSku : guanggao) {
            FoodSkuRecommend foodSkuRecommend = new FoodSkuRecommend();
            BeanUtil.copyProperties(foodSku,foodSkuRecommend);
            foodSkuRecommend.setStatus("广告");
            allFoodSku.add(foodSkuRecommend);
        }
        //协同3
        for (FoodSku foodSku : xietongList) {
            FoodSkuRecommend foodSkuRecommend = new FoodSkuRecommend();
            BeanUtil.copyProperties(foodSku,foodSkuRecommend);
            foodSkuRecommend.setStatus("协同");
            allFoodSku.add(foodSkuRecommend);
        }
        //浏览2
        for (FoodSku foodSku : liuLan) {
            FoodSkuRecommend foodSkuRecommend = new FoodSkuRecommend();
            BeanUtil.copyProperties(foodSku,foodSkuRecommend);
            foodSkuRecommend.setStatus("浏览");
            allFoodSku.add(foodSkuRecommend);
        }
        //好评2
        for (FoodSku foodSku : haoPing) {
            FoodSkuRecommend foodSkuRecommend = new FoodSkuRecommend();
            BeanUtil.copyProperties(foodSku,foodSkuRecommend);
            foodSkuRecommend.setStatus("好评");
            allFoodSku.add(foodSkuRecommend);
        }
        //收藏2
        for (FoodSku foodSku : foodSkus) {
            FoodSkuRecommend foodSkuRecommend = new FoodSkuRecommend();
            BeanUtil.copyProperties(foodSku,foodSkuRecommend);
            foodSkuRecommend.setStatus("收藏");
            allFoodSku.add(foodSkuRecommend);
        }


        //如果推荐不足n(15个)个从口味余弦里面选
        if (allFoodSku.size()<18){
            int size=18-allFoodSku.size();
            List<FoodSku> newkouwei = kouWeiFood(openId, userMapper, foodSkuMapper,foodStatsDictionaryMapper,shownFoodIds,size);
            //补充size
            for (FoodSku foodSku : newkouwei) {
                FoodSkuRecommend foodSkuRecommend = new FoodSkuRecommend();
                BeanUtil.copyProperties(foodSku,foodSkuRecommend);
                foodSkuRecommend.setStatus("口味");
                allFoodSku.add(foodSkuRecommend);
            }
            //去重
            newkouwei.forEach(foodSku -> {
                String id = foodSku.getId();
                if (id != null) {
                    shownFoodIds.add(id);
                }
            });
        }

        addUserViewedFoodSkus(openId,shownFoodIds);

        Page<FoodSkuRecommend> mapPage = new Page<>();
        mapPage.setRecords(allFoodSku);
        mapPage.setTotal(allFoodSku.size());
        return mapPage;
    }
    public static List<FoodSku> kouWeiFood(String openId, BaseMapper<User> userMapper,BaseMapper<FoodSku> foodSkuMapper,BaseMapper<FoodStatsDictionary> foodStatsDictionaryMapper, List<String> shownFoodIds,int num) {
        //用户信息和全部食品和口味字典
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("open_id", openId));
        //如果口味为空设置单个口味上去，也影响不到余弦计算
        if(user.getFoodStats().isEmpty()){
            user.setFoodStats("{\"口味\":\"\"}");
        }
        // 排除已展示的菜品
        QueryWrapper<FoodSku> q = new QueryWrapper<>();
        if (shownFoodIds != null && !shownFoodIds.isEmpty()) {
            q.notIn("id", shownFoodIds);
        }
        List<FoodSku> allFood = foodSkuMapper.selectList(q);
        //口味字典转map key:name  value:Id ,id作为独特整型
        List<FoodStatsDictionary> statsDictionaryList = foodStatsDictionaryMapper.selectList(null);
        Map<String, Integer> dictionaryMap = statsDictionaryList.stream()
                .collect(Collectors.toMap(FoodStatsDictionary::getName, dictionary -> Integer.parseInt(String.valueOf(dictionary.getId()))));


        //存储口味相似度高的食品
        List<FoodSku> recommendedFood = new ArrayList<>();
        //获取用户口味转为Map
        Map<String,String> userMap = JSONUtil.toBean(user.getFoodStats(), Map.class);
        for (FoodSku food : allFood) {
            try {
                // 获取菜品口味转为Map，处理可能的转换异常
                Map<String,String> foodMap = JSONUtil.toBean(food.getFoodStats(), Map.class);

                // 移除掉地区的影响
                foodMap.remove("地区");

                // 计算余弦值
                double similarity = CosineSimilarity.calculateCosineSimilarity(userMap, foodMap, dictionaryMap);

                // 根据相似度阈值进行判断
//                if (similarity > 0.70) {
                    recommendedFood.add(food);
//                }
            } catch (Exception e) {
                // 记录日志信息
                System.err.println("Error processing food item: " + food.getName() + " - Exception: " + e.getMessage());
                // 跳过当前引发异常的数据条目
                continue;
            }
        }

        // 对推荐结果进行排序
        Collections.sort(recommendedFood, Comparator.comparingDouble(food -> {
            // 获取每个食品的相似度
            Map<String, String> foodMap = JSONUtil.toBean(food.getFoodStats(), Map.class);
            double similarity = CosineSimilarity.calculateCosineSimilarity(userMap, foodMap, dictionaryMap);
            return -similarity; // 从高到低排序
        }));

        // 返回前num个推荐结果
        return recommendedFood.subList(0, Math.min(recommendedFood.size(), num));
    }


    public static List<FoodSku> getLiuLan(String openId, BaseMapper<UserBehavior> userbehaviorMapper,BaseMapper<FoodSku> foodSkuMapper, List<String> shownFoodIds){
        //最近(一天前到现在)浏览两个
        QueryWrapper<UserBehavior> q2 = new QueryWrapper<>();
        q2.eq("user_id",openId);
        q2.ge("create_time", oneDayAgo).le("create_time", now);
        // 排除已展示的菜品
        if (shownFoodIds != null && !shownFoodIds.isEmpty()) {
            q2.notIn("food_sku_id", shownFoodIds);
        }
        List<UserBehavior> userBehaviors = userbehaviorMapper.selectList(q2);
        // 统计每个商品出现的次数
        Map<String, Long> itemCountMap = userBehaviors.stream()
                .collect(Collectors.groupingBy(UserBehavior::getFoodSkuId, Collectors.counting()));

        // 按照商品出现次数降序排序
        List<FoodSku> foodSkuList = itemCountMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(2)
                .map(entry -> foodSkuMapper.selectById(entry.getKey()))
                .filter(Objects::nonNull) // 过滤掉空值
                .collect(Collectors.toList());

        return foodSkuList;
    }

    public static List<FoodSku> getHaoPing(String openId, BaseMapper<FoodComments> foodCommentsMapper, BaseMapper<FoodSku> foodSkuMapper, List<String> shownFoodIds){
        //好评次数最多两个
        QueryWrapper<FoodComments> q2 = new QueryWrapper<>();
        q2.eq("user_id",openId);
        q2.ge("comment_star",8);//评分大于或者等于8才算好评
        q2.ge("create_time", oneWeekAgo).le("create_time", now);
        if (shownFoodIds != null && !shownFoodIds.isEmpty()) {
            q2.notIn("food_sku_id", shownFoodIds);
        }
        List<FoodComments> foodCommentsList = foodCommentsMapper.selectList(q2);
        // 统计每个商品出现的次数
        Map<String, Long> itemCountMap = foodCommentsList.stream()
                .collect(Collectors.groupingBy(FoodComments::getFoodSkuId, Collectors.counting()));

        // 按照商品出现次数降序排序
        List<FoodSku> foodSkuList = itemCountMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(2)
                .map(entry -> foodSkuMapper.selectById(entry.getKey()))
                .filter(Objects::nonNull) // 过滤掉空值
                .collect(Collectors.toList());

        return foodSkuList;
    }
    //收藏
    public static List<FoodSku> randomCollectFoodSku(String openId,BaseMapper<User> userMapper, BaseMapper<FoodSku> foodSkuMapper, List<String> shownFoodIds){

        User user = userMapper.selectOne(new QueryWrapper<User>().eq("open_id", openId));
        String collectFoodSku = user.getCollectFoodSku();
        if(collectFoodSku==null){
            return new ArrayList<FoodSku>();
        }
        // 使用逗号分割字符串为数组
        String[] foodSkuArray = collectFoodSku.split(",");
        // 将数组转换为 List
        List<String> foodlist = Arrays.asList(foodSkuArray);
        List<String> newfoodlist = new ArrayList<>();

        for (String FoodId : foodlist) {
            if (shownFoodIds.contains(FoodId)){
                continue;
            }
            newfoodlist.add(FoodId);
        }
        if (newfoodlist.size()>0){
            // 打乱列表
            Collections.shuffle(newfoodlist);
            // 取出前面两个元素
            List<String> randomFoodSkus = newfoodlist.subList(0, Math.min(newfoodlist.size(), 2));

            List<FoodSku> foodSkuList = randomFoodSkus.stream()
                    .map(foodSkuId -> foodSkuMapper.selectById(foodSkuId))
                    .filter(foodSku -> foodSku != null)
                    .collect(Collectors.toList());
            return foodSkuList;
        }
        return new ArrayList<FoodSku>();

    }
    public static List<FoodSku> getGuanggao(String openId, BaseMapper<MerchantRecharge> merchantRechargeMapper, BaseMapper<FoodSku> foodSkuMapper, List<String> shownFoodIds){
        // 使用 QueryWrapper 构造查询条件
        QueryWrapper<MerchantRecharge> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("recharge_type", 1)
                .le("start_time", LocalDateTime.now())
                .ge("end_time", LocalDateTime.now());

        List<MerchantRecharge> validRecharges = merchantRechargeMapper.selectList(queryWrapper);

        // 计算所有有效充值记录的总金额
        BigDecimal totalAmount = validRecharges.stream()
                .map(MerchantRecharge::getAmount) // 提取每条记录的充值金额
                .reduce(BigDecimal.ZERO, BigDecimal::add); // 将所有金额累加起来

        // 创建一个用于存储每条记录及其对应权重的映射表
        Map<MerchantRecharge, Double> weightMap = new HashMap<>();
        for (MerchantRecharge recharge : validRecharges) {
            // 计算每条记录的权重。权重是该记录金额占总金额的比例。
            // 使用 BigDecimal 来处理金额的计算，以保持精度。
            double weight = recharge.getAmount().divide(totalAmount, 2, BigDecimal.ROUND_HALF_UP).doubleValue();

            // 将记录及其计算出的权重存储在映射表中
            weightMap.put(recharge, weight);
        }

        // 根据权重随机选择三条记录
        List<MerchantRecharge> merchantRecharges = selectRandomByWeight(validRecharges, weightMap, 3);
        System.out.println(merchantRecharges);
        List<String> skuIds = merchantRecharges.stream().map(MerchantRecharge::getFoodSkuId).collect(Collectors.toList());
        return foodSkuMapper.selectList(new QueryWrapper<FoodSku>().in("id",skuIds));
    }

    //协同过滤
    public static List<FoodSku> getXietong(String openId, BaseMapper<User> userMapper, BaseMapper<FoodSku> foodSkuMapper,BaseMapper<FoodStatsDictionary> foodStatsDictionaryMapper,BaseMapper<Orders> ordersMapper, List<String> shownFoodIds,int num){
        //存储相似用户
        ArrayList<User> usersList = new ArrayList<>();
        //获取当前用户口味
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("open_id", openId));
        //如果口味为空设置单个口味上去，也影响不到余弦计算
        if(user.getFoodStats().isEmpty()){
            user.setFoodStats("{\"口味\":\"\"}");
        }
        Map<String,String> userMap = JSONUtil.toBean(user.getFoodStats(), Map.class);
        //获取口味字典
        List<FoodStatsDictionary> statsDictionaryList = foodStatsDictionaryMapper.selectList(null);
        Map<String, Integer> dictionaryMap = statsDictionaryList.stream()
                .collect(Collectors.toMap(FoodStatsDictionary::getName, dictionary -> Integer.parseInt(String.valueOf(dictionary.getId()))));

        //查询全部用户，找出最相似口味的num个用户
        List<User> users = userMapper.selectList(null);
        for (User u : users) {
            if (!u.getOpenId().equals(openId)) { // 排除当前用户
                usersList.add(u);
            }
        }
        Collections.sort(usersList, Comparator.comparingDouble(u -> {
            // 获取每个食品的相似度
            Map<String, String> uMap = JSONUtil.toBean(u.getFoodStats(), Map.class);
            double similarity = CosineSimilarity.calculateCosineSimilarity(userMap, uMap, dictionaryMap);
            return -similarity; // 从高到低排序
        }));
        List<User> newUserList = usersList.subList(0, Math.min(usersList.size(), num));

        // 存储推荐的食品
        List<FoodSku> recommendedFood = new ArrayList<>();
        // 查询每个相似用户最常购买的食品
        for (User u : newUserList) {
            // 假设 Order 有一个 food_sku_id 字段和一个 open_id 字段
            // 查询该用户购买次数最多的食品
            List<Map<String, Object>> mostPurchased = ordersMapper.selectMaps(new QueryWrapper<Orders>()
                    .select("food_sku_id, COUNT(*) as purchase_count")
                    .eq("user_id", u.getOpenId())
                    .groupBy("food_sku_id")
                    .orderByDesc("purchase_count")
                    .notIn("food_sku_id", shownFoodIds)
                    .last("LIMIT 1"));

            if (!mostPurchased.isEmpty()) {
                // 获取购买次数最多的食品ID
                String foodSkuId = (String) mostPurchased.get(0).get("food_sku_id");
                // 根据食品ID获取食品详情
                FoodSku foodSku = foodSkuMapper.selectById(foodSkuId);
                if(foodSku!=null){
                    recommendedFood.add(foodSku);
                    // 将该食品ID添加到已展示列表中，避免重复推荐
                    shownFoodIds.add(foodSkuId);
                }
            }
        }

        return recommendedFood;

    }


    //广告部分计算权重随机返回指定条数充值记录
    private static List<MerchantRecharge> selectRandomByWeight(List<MerchantRecharge> items, Map<MerchantRecharge, Double> weightMap, int count) {
        List<MerchantRecharge> selected = new ArrayList<>();
        for (int i = 0; i < count && !items.isEmpty(); i++) {
            // 计算所有项目的总权重
            double totalWeight = items.stream().mapToDouble(weightMap::get).sum();

            // 生成一个随机数，用于确定选中哪个项目,Math.random() 生成一个大于等于 0 且小于 1 的随机浮点数。这意味着 Math.random() 的结果永远不会达到 1。
            double randomValue = Math.random() * totalWeight;

            // 累计权重，用于确定随机数落在哪个区间
            //遍历记录集合，累加每个项目的权重。一旦累计权重达到或超过随机生成的值，就选择当前遍历到的项目。
            // 这确保了权重越高的项目被选中的概率越大。
            //权重越大的记录很容易一下子就满足了
            double cumulativeWeight = 0.0;
            for (MerchantRecharge item : new ArrayList<>(items)) {
                cumulativeWeight += weightMap.get(item);
                if (cumulativeWeight >= randomValue) {
                    // 当累计权重超过或等于随机数时，选择当前项目
                    selected.add(item);

                    // 从可选集合中移除已选项目，避免重复选择
                    items.remove(item);
                    break;
                }
            }
        }
        return selected;
    }

    /**
     * 通过销量和评分推荐菜品
     *
     * @param n 推荐菜品的数量
     * @return 推荐的菜品列表
     * 功能描述：
     * 通过销量和评分来推荐菜品。推荐值的计算公式为：
     * 推荐值 = 菜品销量 * 商家的评分
     */
    @Override
    public List<FoodSku> recommendBySalesAndScore(Integer n) {
        // 从数据库获取菜品及其对应商家的评分信息，并计算出推荐值后根据推荐值降序排序
        List<FoodSkuDto> foodSkuDtos = foodSkuMapper.selectFoodAndMerchant();

        // 检查n是否为有效的正数
        if (n == null || n <= 0) {
            return new ArrayList<>(); // 如果n无效，返回空列表
        }

        // 使用流API处理列表并返回结果
        return foodSkuDtos.stream()
                .limit(n)
                .collect(Collectors.toList());
    }

    @Override
    public Page<FoodSku> getLocationFood(Page<FoodSku> page,String openId) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("open_id",openId));
        if(user.getPhoneLocation().isEmpty()){
            return new Page<>();
        }
        String[] split = user.getPhoneLocation().split(",");
        //查出这个地区
        DgtxPlaces cname = dgtxPlacesMapper.selectOne(new QueryWrapper<DgtxPlaces>().eq("cname", split[1]));

        Page<FoodSku> foodSkuPage = foodSkuMapper.selectPage(page, new QueryWrapper<FoodSku>().eq("place_id", cname.getId()).orderByDesc("sales_num"));

        return foodSkuPage;

    }

    @Override
    public Map<String, Object> getSkuInfo(User user, Serializable id) {
        //查出用户收藏
        User openUser = userMapper.selectOne(new QueryWrapper<User>().eq("open_id", user.getOpenId()));
        //查出菜品信息
        FoodSku foodSku = foodSkuMapper.selectById(id);
        if(openUser.getCollectFoodSku()==null || openUser.getCollectFoodSku().isEmpty()){
            Map<String, Object> map = BeanUtil.beanToMap(foodSku);
            map.put("currentUserIsCollect",false);
            return map;
        }else {
            String[] split = openUser.getCollectFoodSku().split(",");
            ArrayList<String> collectList = new ArrayList<>(Arrays.asList(split));
            Map<String, Object> map = BeanUtil.beanToMap(foodSku);
            if (collectList.contains(id)){
                map.put("currentUserIsCollect",true);
            }else {
                map.put("currentUserIsCollect",false);
            }
            return map;
        }

    }

    @Override
    public int saveFoodSku(FoodSku foodSku) {
        String foodStats = foodSku.getFoodStats();
        String[] split = foodStats.split(",");
        HashMap<String, String> mapStats = new HashMap<>();
        for (String s : split) {
            FoodStatsDictionary dictionary = foodStatsDictionaryMapper.selectList(new QueryWrapper<FoodStatsDictionary>().eq("name", s)).get(0);
            mapStats.put(dictionary.getStatsLevel(),dictionary.getName());
        }
        //获取所有类型,如果不存在此类型就设置为此类型的其他
        List<Map<String, Object>> results = foodStatsDictionaryMapper.selectMaps(new QueryWrapper<FoodStatsDictionary>().select("stats_level").groupBy("stats_level"));
        for (Map<String, Object> result : results) {
            String statsLevel = (String)result.get("stats_level");
            //如果不包含此类型，设置为此类型的其他
            if(!mapStats.containsKey(statsLevel)){
                mapStats.put(statsLevel,"其他"+"("+statsLevel+")");
            }
        }
        String parseStats = JSONUtil.parse(mapStats).toString();
        foodSku.setFoodStats(parseStats);
        return foodSkuMapper.insert(foodSku);
    }

    public void addUserViewedFoodSkus(String userId, List<String> foodSkuIds) {
        String key = "user:viewed:" + userId;
        for (String foodSkuId : foodSkuIds) {
            redisTemplate.opsForSet().add(key, foodSkuId);
        }
    }
    public Set<String> getUserViewedFoodSkus(String userId) {
        String key = "user:viewed:" + userId;
        return redisTemplate.opsForSet().members(key);
    }
    public void clearUserViewedFoodSkus(String userId) {
        String key = "user:viewed:" + userId;
        redisTemplate.delete(key);
    }


}




