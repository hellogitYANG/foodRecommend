package com.example.foodrecommend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    // 设置时间范围
    static LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
    static LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
    static LocalDateTime now = LocalDateTime.now();
    @Override
    public HashMap<String, Collection<FoodSku>> getYouWantEat(String openId) {
        //口味余弦相似度推荐
        List<FoodSku> kouWeiList = kouWeiFood(openId, userMapper, foodSkuMapper,foodStatsDictionaryMapper);

        //常买三个
        QueryWrapper<Orders> q1 = new QueryWrapper<>();
        q1.eq("user_id",openId);
        q1.ge("create_time", oneWeekAgo).le("create_time", now);
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

        //最近(一天前到现在)浏览三个
        List<FoodSku> liuLan = getLiuLan(openId,userBehaviorMapper, foodSkuMapper);

        //好评次数最多三个
        List<FoodSku> haoPing = getHaoPing(openId, foodCommentsMapper, foodSkuMapper);

        //收藏菜品三个
        List<FoodSku> foodSkus = randomCollectFoodSku(openId, userMapper, foodSkuMapper);

        //基于天气

        //收集起来并返回
        HashSet<FoodSku> allFoodSku = new HashSet<>();
        allFoodSku.addAll(kouWeiList);
        allFoodSku.addAll(foodSkuList);
        allFoodSku.addAll(liuLan);
        allFoodSku.addAll(haoPing);
        allFoodSku.addAll(foodSkus);

        HashMap<String, Collection<FoodSku>> stringListHashMap = new HashMap<>();
        stringListHashMap.put("口味",kouWeiList);
        stringListHashMap.put("常买",foodSkuList);
        stringListHashMap.put("浏览",liuLan);
        stringListHashMap.put("好评",haoPing);
        stringListHashMap.put("收藏",foodSkus);
        stringListHashMap.put("汇总（去重）",allFoodSku);

        return stringListHashMap;
    }
    public static List<FoodSku> kouWeiFood(String openId, BaseMapper<User> userMapper,BaseMapper<FoodSku> foodSkuMapper,BaseMapper<FoodStatsDictionary> foodStatsDictionaryMapper) {
        //用户信息和全部食品和口味字典
        User user = userMapper.selectOne(new QueryWrapper<User>().eq("open_id", openId));
        //全部食品
        List<FoodSku> allFood = foodSkuMapper.selectList(null);
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
            //计算余弦值
            double similarity = CosineSimilarity.calculateCosineSimilarity(userMap,foodMap,dictionaryMap);
            System.out.println(similarity);
            // 假设设定相似度阈值为0.5，可根据实际情况调整
            if (similarity > 0.95) {
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

        // 返回前5个推荐结果
        return recommendedFood.subList(0, Math.min(recommendedFood.size(), 5));
    }


    public static List<FoodSku> getLiuLan(String openId, BaseMapper<UserBehavior> userbehaviorMapper,BaseMapper<FoodSku> foodSkuMapper){
        //最近(一天前到现在)浏览三个
        QueryWrapper<UserBehavior> q2 = new QueryWrapper<>();
        q2.eq("user_id",openId);
        q2.ge("create_time", oneDayAgo).le("create_time", now);
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

    public static List<FoodSku> getHaoPing(String openId, BaseMapper<FoodComments> foodCommentsMapper, BaseMapper<FoodSku> foodSkuMapper){
        //好评次数最多三个
        QueryWrapper<FoodComments> q2 = new QueryWrapper<>();
        q2.eq("user_id",openId);
        q2.ge("comment_star",8);//评分大于或者等于8才算好评
        q2.ge("create_time", oneWeekAgo).le("create_time", now);
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
    public static List<FoodSku> randomCollectFoodSku(String openId,BaseMapper<User> userMapper, BaseMapper<FoodSku> foodSkuMapper){

        User user = userMapper.selectOne(new QueryWrapper<User>().eq("open_id", openId));
        String collectFoodSku = user.getCollectFoodSku();
        if(collectFoodSku==null){
            return new ArrayList<FoodSku>();
        }
        // 使用逗号分割字符串为数组
        String[] foodSkuArray = collectFoodSku.split(",");
        // 将数组转换为 List
        List<String> foodlist = Arrays.asList(foodSkuArray);
        // 打乱列表
        Collections.shuffle(foodlist);
        // 取出前面三个元素
        List<String> randomFoodSkus = foodlist.subList(0, Math.min(foodlist.size(), 3));

        List<FoodSku> foodSkuList = randomFoodSkus.stream().map(foodSkuId -> foodSkuMapper.selectById(foodSkuId)).collect(Collectors.toList());
        return foodSkuList;
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
}




