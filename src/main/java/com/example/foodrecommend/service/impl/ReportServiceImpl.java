package com.example.foodrecommend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.Merchant;
import com.example.foodrecommend.beans.Report;
import com.example.foodrecommend.beans.User;
import com.example.foodrecommend.dto.ReportDto;
import com.example.foodrecommend.dto.ReportResponseDto;
import com.example.foodrecommend.mapper.ReportMapper;
import com.example.foodrecommend.service.MerchantService;
import com.example.foodrecommend.service.ReportService;
import com.example.foodrecommend.interceptor.CheckTokenInterceptor;
import com.example.foodrecommend.service.UserService;
import com.example.foodrecommend.utils.GetUserInfoByToken;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 86176
 * @description 针对表【report】的数据库操作Service实现
 * @createDate 2023-11-13 00:14:57
 */
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements ReportService {

    @Resource
    private MerchantService merchantService;
    @Resource
    private ReportMapper reportMapper;

    @Resource
    private UserService userService;

    /**
     * 管理员进行审核，提交要扣除的分数
     *
     * @param map 举报表ID 和 扣除的分数（star）
     * @return 成功/失败
     */
    @Override
    public Boolean handleAuditAndDeductScore(Map map) {
        // 举报表id
        String id = (String) map.get("id");
        // 扣除的分数
        double star = (double) map.get("star");
        // 查询举报表对象
        Report report = this.getById(id);
        // 对商家进行惩罚
        // 1.降分
        Merchant merchant = merchantService.getById(report.getMerchantIdEd());
        // 当前商家分数
        double currentMerchantStar = merchant.getStar();
        // 扣除分数
        if (currentMerchantStar < star) {
            return false;
        }
        merchant.setStar(currentMerchantStar - star);
        // 更新数据库
        boolean update = merchantService.updateById(merchant);

        // TODO 2.罚款 -- 未完成

        return update;
    }

    @Override
    @Transactional
    public int updateByChuli(Report report) {
        //设置已处理
        report.setStatus(1);
        //对应商家进行扣分
        Merchant merchant = merchantService.getById(report.getMerchantIdEd());
        if(merchant.getStar()<report.getDeduction()){
            merchant.setStar(0.0);
        }else {
            merchant.setStar(merchant.getStar()-report.getDeduction());
        }
        merchantService.updateById(merchant);
        return reportMapper.updateById(report);
    }

    @Override
    public IPage<ReportResponseDto> pageByParams(Map<String, Object> params) {
        LambdaQueryWrapper<Report> queryWrapper = new LambdaQueryWrapper<>();

        String token = CheckTokenInterceptor.getToken();
        User user = GetUserInfoByToken.parseToken(token);

        queryWrapper.eq(Report::getUserId , user.getOpenId());

        //分页参数
        long current = 1;
        long size = 10;

        if(params.get("current") != null){
            current = Long.parseLong((String)params.get("current"));
        }
        if(params.get("size") != null){
            size = Long.parseLong((String)params.get("size"));
        }

        if (params.get("type") != null) {
            queryWrapper.eq(Report::getStatus , params.get("type"));
        }

        if (params.get("keyword") != null) {
            queryWrapper.and(w -> {
               w.eq(Report::getId , params.get("keyword")).or().like(Report::getProofText, params.get("keyword"));
            });
        }

        IPage<Report> page = this.page(new Page<Report>(current, size), queryWrapper);
        IPage<ReportResponseDto> iPage = new Page<>();
        iPage.setPages(page.getPages());
        iPage.setTotal(page.getTotal());
        iPage.setSize(page.getSize());
        iPage.setCurrent(page.getCurrent());

        if (page.getTotal() <= 0) {
            return iPage;
        }

        List<Report> records = page.getRecords();
        List<String> merchantIds = new ArrayList<>();
        List<String> userIds = records.stream().map(Report::getUserId).collect(Collectors.toList());

        records.forEach(report -> {
            merchantIds.add(report.getMerchantId());
            if (report.getMerchantIdEd() != null && !merchantIds.contains(report.getMerchantIdEd())) {
                merchantIds.add(report.getMerchantIdEd());
            }
        });

        List<User> users = userService.getUsersByInOpenId(userIds);
        List<Merchant> merchants = merchantService.getMerchantByIds(merchantIds);

        Map<String, User> userMap = users.stream().collect(Collectors.toMap(User::getOpenId, Function.identity(), (k1, k2) -> k2));
        Map<String, Merchant> merchantMap = merchants.stream().collect(Collectors.toMap(Merchant::getId, Function.identity(), (k1, k2) -> k2));

        List<ReportResponseDto> responseDtos = records.stream().map(r -> {
            ReportResponseDto reportResponseDto = new ReportResponseDto();
            BeanUtils.copyProperties(r , reportResponseDto);
            if (userMap.containsKey(r.getUserId())) {
                reportResponseDto.setUser(userMap.get(r.getUserId()));
            }
            if (merchantMap.containsKey(r.getMerchantIdEd())) {
                reportResponseDto.setMerchantEd(merchantMap.get(r.getMerchantIdEd()));
            }
            if (merchantMap.containsKey(r.getMerchantId())) {
                reportResponseDto.setMerchant(merchantMap.get(r.getMerchantId()));
            }
            return reportResponseDto;
        }).collect(Collectors.toList());
        iPage.setRecords(responseDtos);
        return iPage;
    }

    @Override
    public void report(ReportDto reportDto) {
        Report report = new Report();

        StringJoiner sj = new StringJoiner(",");
        String token = CheckTokenInterceptor.getToken();
        User user = GetUserInfoByToken.parseToken(token);

        BeanUtils.copyProperties(reportDto , report);


        reportDto.getImages().forEach(sj::add);

        report.setProofImgUrl(sj.toString());
        report.setUserId(user.getOpenId());

        this.save(report);
    }

}




