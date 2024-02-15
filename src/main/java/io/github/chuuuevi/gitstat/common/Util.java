package io.github.chuuuevi.gitstat.common;



import io.github.chuuuevi.gitstat.modules.stat.constant.Constants;

import java.util.Date;

/**
 * Created by Jacky on 2015/7/14.
 */
public class Util {

    public static int calcDifDays(Date end, Date start) {
        long gap = end.getTime() - start.getTime();
        return (int)Math.floor(gap / Constants.StaticParam.DAY_MILLIONSECOND);
    }

}
