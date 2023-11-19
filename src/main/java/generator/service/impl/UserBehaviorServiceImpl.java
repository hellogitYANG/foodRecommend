package generator.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import generator.beans.UserBehavior;
import generator.service.UserBehaviorService;
import generator.mapper.UserBehaviorMapper;
import org.springframework.stereotype.Service;

/**
* @author 86176
* @description 针对表【user_behavior】的数据库操作Service实现
* @createDate 2023-11-18 00:31:08
*/
@Service
public class UserBehaviorServiceImpl extends ServiceImpl<UserBehaviorMapper, UserBehavior>
    implements UserBehaviorService{

}




