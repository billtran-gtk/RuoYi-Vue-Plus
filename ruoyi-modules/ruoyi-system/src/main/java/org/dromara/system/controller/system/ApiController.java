package org.dromara.system.controller.system;

import cn.hutool.core.io.FileUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.dromara.common.core.domain.R;
import org.dromara.common.web.core.BaseController;
import org.dromara.system.domain.SysVersion;
import org.dromara.system.domain.vo.SysUserVo;
import org.dromara.system.service.ISysUserService;
import org.dromara.system.service.ISysVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin
@Controller
@RequestMapping("/app")
public class ApiController extends BaseController {
    @Autowired
    private ISysVersionService versionService;
    @Autowired
    private ISysUserService userService;

    @PostMapping("/updateAPK")
    @ResponseBody
    public R<HashMap<String, Object>> updateAPK(@RequestBody Map<String, Object> params) {
        HashMap<String, Object> result = new HashMap<>();
        Map<String, Object> userMap = (Map<String, Object>) params.get("user");
        String username = userMap.get("userName").toString();
        String userId = userMap.get("userId").toString();
        String versionNumber = params.get("versionNumber").toString();
        SysUserVo sysUserVo = userService.login(username, Long.parseLong(userId));
        if (sysUserVo != null) {
            try {
                SysVersion version = versionService.getLatestAPK();
                if (version != null ) {
                    if (version.getVersionNumber().equals(versionNumber)) {
                        return R.ok("当前版本为最新版", result);
                    } else {
                        result.put("file", version.getUrl());
                        return R.ok(result);
                    }
                }else {
                    return R.fail("无版本更新");
                }

            } catch (Exception e) {
                return R.fail("获取失败");
            }

        } else {
            return R.fail("用户名或密码错误");
        }
    }
}
