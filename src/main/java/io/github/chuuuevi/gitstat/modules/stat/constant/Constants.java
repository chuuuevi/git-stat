package io.github.chuuuevi.gitstat.modules.stat.constant;

/**
 * Created by Jacky on 2015/7/13.
 */
public interface Constants {

     interface StaticParam {
        String SHORT_DATE_FORMAT = "yyyy-MM-dd";
        long DAY_MILLIONSECOND = 24 * 60 * 60 * 1000;
    }

    interface GitConstants {
        int STAT_METHOD_DAY = 1;
        int STAT_METHOD_WEEK = 2;
        int STAT_METHOD_MONTH = 3;
        int STAT_METHOD_YEAR = 4;
        int STAT_METHOD_CUSTOM = 0;
    }
}
