package io.github.chuuuevi.gitstat.modules.action;

import io.github.chuuuevi.gitstat.common.Code;
import io.github.chuuuevi.gitstat.common.Result;
import io.github.chuuuevi.gitstat.modules.stat.service.GitStatService;
import io.github.chuuuevi.gitstat.modules.stat.vo.StatData;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by Jacky on 2015/7/7.
 */
@Controller
public class GitController {

   @Resource
    private GitStatService gitStatService;

    @RequestMapping("/")
    public String index() {
        return "redirect:stat/gitstat.html";
    }

    @RequestMapping("/git/stat")
    @ResponseBody
    public Result<List<StatData>> statGit(@Valid @RequestParam(value = "method", required = true) @Min(0) @Max(3) Integer method,
                                          @RequestParam(value = "startDate", required = false) String startDate,
                                          @RequestParam(value = "endDate", required = false) String endDate,
                                          @RequestParam(value = "name", required = false) String name,
                                          @RequestParam(value = "gitRoot", required = true) String gitRoot) {

        try {
            return Result.success(gitStatService.stat(method, startDate, endDate, name, gitRoot));
        } catch (Exception e) {
            return Result.failure(Code.ERROR, e.getMessage());
        }
    }
}
