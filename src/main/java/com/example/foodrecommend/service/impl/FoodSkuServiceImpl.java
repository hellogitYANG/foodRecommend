package com.example.foodrecommend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.*;
import com.example.foodrecommend.mapper.*;
import com.example.foodrecommend.service.FoodSkuService;
import com.example.foodrecommend.utils.CosineSimilarity;
import org.springframework.beans.factory.annotation.Autowired;
import com.example.foodrecommend.beans.FoodSku;
import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.dto.FoodSkuDto;
import com.example.foodrecommend.mapper.FoodSkuMapper;
import com.example.foodrecommend.mapper.MerchantMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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
    @Resource
    private MerchantMapper merchantMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;


    // 设置时间范围
    static LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
    static LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
    static LocalDateTime now = LocalDateTime.now();
    @Override
    public Page<Map<String, Collection<FoodSku>>> getYouWantEat(Page page,String openId) {
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
                    .collect(Collectors.toList());
                    //去重
                    foodSkuList.forEach(foodSku -> {
                        String id = foodSku.getId();
                        if (id != null) {
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

        //收集起来并返回
        HashSet<FoodSku> allFoodSku = new HashSet<>();
        allFoodSku.addAll(kouWeiList);
        allFoodSku.addAll(foodSkuList);
        allFoodSku.addAll(liuLan);
        allFoodSku.addAll(haoPing);
        allFoodSku.addAll(foodSkus);

        //如果推荐不足15个从口味余弦里面选
        if (allFoodSku.size()<15){
            int size=15-allFoodSku.size();
            List<FoodSku> newkouwei = kouWeiFood(openId, userMapper, foodSkuMapper,foodStatsDictionaryMapper,shownFoodIds,size);
            allFoodSku.addAll(newkouwei);
            //去重
            newkouwei.forEach(foodSku -> {
                String id = foodSku.getId();
                if (id != null) {
                    shownFoodIds.add(id);
                }
            });
        }

        HashMap<String, Collection<FoodSku>> stringListHashMap = new HashMap<>();
        stringListHashMap.put("口味",kouWeiList);
        stringListHashMap.put("常买",foodSkuList);
        stringListHashMap.put("浏览",liuLan);
        stringListHashMap.put("好评",haoPing);
        stringListHashMap.put("收藏",foodSkus);
        stringListHashMap.put("汇总（去重，不足15个追加口味）",allFoodSku);

        addUserViewedFoodSkus(openId,shownFoodIds);

        Page<Map<String, Collection<FoodSku>>> mapPage = new Page<>();
        ArrayList<Map<String, Collection<FoodSku>>> maps = new ArrayList<>();
        maps.add(stringListHashMap);
        mapPage.setRecords(maps);
        return mapPage;
    }
    public static List<FoodSku> kouWeiFood(String openId, BaseMapper<User> userMapper,BaseMapper<FoodSku> foodSkuMapper,BaseMapper<FoodStatsDictionary> foodStatsDictionaryMapper, List<String> shownFoodIds,int num) {
        //用户信息和全部食品和口味字典
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("open_id", openId));
        // 排除已展示的菜品
        QueryWrapper<FoodSku> q = new QueryWrapper<>();
        if (shownFoodIds != null && !shownFoodIds.isEmpty()) {
            q.notIn("id", shownFoodIds);
        }
        List<FoodSku> allFood = foodSkuMapper.selectList(q);
        //口味字典转map key:name  value:Id ,id作为独特整型
        List<FoodStatsDictionary> statsDictionaryList = foodStatsDictionaryMapper.selectList(null);
        Map<String, Integer> dictionaryMap = statsDictionaryList.stream()
                .collect(Collectors.toMap(FoodStatsDictionary::getName, dictionary -> Integer.parseInt(dictionary.getId())));


        //存储口味相似度高的食品
        List<FoodSku> recommendedFood = new ArrayList<>();
        //获取用户口味转为Map
        Map<String,String> userMap = JSONUtil.toBean(user.getFoodStats(), Map.class);
        for (FoodSku food : allFood) {
            //获取菜品口味转为Map
            Map<String,String> foodMap=JSONUtil.toBean(food.getFoodStats(), Map.class);
            //移除掉地区的影响
            if (foodMap.containsKey("地区")){
                foodMap.remove("地区");
            }
            //计算余弦值
            double similarity = CosineSimilarity.calculateCosineSimilarity(userMap,foodMap,dictionaryMap);
            System.out.println(similarity);
            // 假设设定相似度阈值为0.5，可根据实际情况调整
            if (similarity > 0.70) {
                recommendedFood.add(food);
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
        //最近(一天前到现在)浏览三个
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
                .limit(3)
                .map(entry -> foodSkuMapper.selectById(entry.getKey()))
                .collect(Collectors.toList());

        return foodSkuList;
    }

    public static List<FoodSku> getHaoPing(String openId, BaseMapper<FoodComments> foodCommentsMapper, BaseMapper<FoodSku> foodSkuMapper, List<String> shownFoodIds){
        //好评次数最多三个
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
                .limit(3)
                .map(entry -> foodSkuMapper.selectById(entry.getKey()))
                .collect(Collectors.toList());

        return foodSkuList;
    }
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
            // 取出前面三个元素
            List<String> randomFoodSkus = newfoodlist.subList(0, Math.min(newfoodlist.size(), 3));

            List<FoodSku> foodSkuList = randomFoodSkus.stream()
                    .map(foodSkuId -> foodSkuMapper.selectById(foodSkuId))
                    .filter(foodSku -> foodSku != null)
                    .collect(Collectors.toList());
            return foodSkuList;
        }
        return new ArrayList<FoodSku>();

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
        // 查询数据库中的所有菜品
        List<FoodSku> foodSkus = foodSkuMapper.selectList(null);
        List<FoodSkuDto> foodSkuDtos = new ArrayList<>();
        // 遍历每一个菜品并计算它们的推荐值
        foodSkus.forEach(food -> {
            // 创建一个FoodSkuDto对象
            FoodSkuDto foodSkuDto = new FoodSkuDto();
            // 将菜品对象（FoodSku）的属性复制到FoodSkuDto对象中
            BeanUtils.copyProperties(food, foodSkuDto);
            // 获取菜品对应商家的评分
            String merchantId = food.getMerchantId();
            // 查询对应的商家信息
            Merchant merchant = merchantMapper.selectOne(new LambdaQueryWrapper<Merchant>().eq(Merchant::getId, merchantId));
            Integer star = merchant.getStar();
            // 计算推荐值，推荐值是菜品销量和商家评分的乘积
            foodSkuDto.setScoreWeightSales(food.getSalesNum() * star);
            // 将计算结果添加到foodSkuDtos列表中
            foodSkuDtos.add(foodSkuDto);
        });
        // 将foodSkuDtos列表中的元素按照推荐值的大小进行降序排序
        foodSkuDtos.sort(Comparator.comparing(FoodSkuDto::getScoreWeightSales).reversed());
        // 根据传入的数量n，获取推荐值最大的前n个菜品
        List<FoodSku> recommendedFoodSkus = new ArrayList<>();
        // 当菜品总数大于n时，返回前n个菜品；否则，返回所有菜品
        if (foodSkuDtos.size() > n) {
            recommendedFoodSkus.addAll(foodSkuDtos.subList(0, n));  // 如果数量超过n，只返回前n个
        } else {
            recommendedFoodSkus.addAll(foodSkuDtos);  // 如果没有n个菜品，那么全部返回
        }
        // 返回推荐的菜品列表
        return recommendedFoodSkus;
    }

    @Override
    public List<FoodSku> getLocationFood(String openId) {
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("open_id",openId));
        //每家只能推一个
        List<FoodSku> foodSkus = foodSkuMapper.selectList(new QueryWrapper<FoodSku>().orderByDesc("sales_num"));

        ArrayList<FoodSku> locationFood = new ArrayList<>();
        String[] split = user.getPhoneLocation().split(",");

        A:for (int i = 0; i < foodSkus.size(); i++) {
            if (foodSkus.get(i).getFoodStats().contains(split[0]) || foodSkus.get(i).getFoodStats().contains(split[1])){
                //如果已经有此家店铺的食品，直接跳过此食品
                for (FoodSku foodSku : locationFood) {
                    if (foodSku.getMerchantId().equals(foodSkus.get(i).getMerchantId())){
                        continue A;
                    }
                }
                locationFood.add(foodSkus.get(i));
            }
            if (i==4){
                break;
            }
        }
        return locationFood;

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




