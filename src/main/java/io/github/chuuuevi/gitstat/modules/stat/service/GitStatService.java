package io.github.chuuuevi.gitstat.modules.stat.service;

import io.github.chuuuevi.gitstat.modules.stat.vo.StatData;

import java.util.List;

/**
 * Created by Jacky on 2015/7/14.
 */
public interface GitStatService {
    List<StatData> stat(int method, String startDate,
                        String endDate, String author, String gitRoot) throws Exception;
}
