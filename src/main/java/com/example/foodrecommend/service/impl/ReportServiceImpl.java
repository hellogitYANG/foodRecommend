package com.example.foodrecommend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.foodrecommend.beans.Report;
import com.example.foodrecommend.service.ReportService;
import com.example.foodrecommend.mapper.ReportMapper;
import org.springframework.stereotype.Service;

/**
* @author 86176
* @description 针对表【report】的数据库操作Service实现
* @createDate 2023-11-13 00:14:57
*/
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report>
    implements ReportService{

}




