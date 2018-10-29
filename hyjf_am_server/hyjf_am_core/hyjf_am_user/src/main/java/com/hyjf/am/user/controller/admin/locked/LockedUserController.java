/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.user.controller.admin.locked;

import com.hyjf.am.user.service.front.user.LockedUserService;
import com.hyjf.am.vo.admin.locked.LockedUserInfoVO;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author cui
 * @version LockedUserController, v0.1 2018/9/20 17:39
 */

@Api(value = "登录失败锁定用户管理")
@RestController
@RequestMapping("/am-user/lockeduser")
public class LockedUserController {
    @Autowired
    private LockedUserService lockedUserService;

    /**
     * 输错密码次数超限的用户信息
     *
     * @param lockedUserInfoVO
     * @return
     */
    @RequestMapping(value = "/insertLockedUser", method = RequestMethod.POST)
    @ResponseBody
    public int insertLockedUser(@RequestBody LockedUserInfoVO lockedUserInfoVO) {
        return lockedUserService.inserLockedUser(lockedUserInfoVO);
    }


}
