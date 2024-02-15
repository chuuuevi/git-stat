package io.github.chuuuevi.gitstat.modules.stat.service.impl;

/**
 * Created by Jacky on 2015/7/7.
 * <p>
 * Git Stat Service
 */

import com.google.common.collect.Lists;
import io.github.chuuuevi.gitstat.modules.stat.constant.Constants;
import io.github.chuuuevi.gitstat.modules.stat.service.GitStatService;
import io.github.chuuuevi.gitstat.modules.stat.vo.StatData;
import io.github.chuuuevi.gitstat.modules.stat.vo.StatParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class GitStatServiceImpl implements GitStatService {

    private final Pattern pat = Pattern.compile("(\\d+)\\s(\\d+)\\s");

    @Override
    public List<StatData> stat(int method, String startDate,
                               String endDate, String author, String gitRoot) throws Exception {

        Stack<String> result = multiCmdExec(method, startDate, endDate, author, gitRoot);
        StatParam statParam = calcDays(method, startDate, endDate);

        boolean start = true;
        long add = 0, del = 0, num = 0;

        Map<String, StatData> statMap = new HashMap<String, StatData>();
        StatData sd = new StatData();

        while (result != null && !result.empty()) {
            String line = result.pop();
//            System.out.println(line);

            log.debug("$> {}", line);

            Matcher m = pat.matcher(line);
            if (m.find()) {
                MatchResult mr = m.toMatchResult();
                add += Long.parseLong(mr.group(1));
                del += Long.parseLong(mr.group(2));
                num += 1;
            } else {
                String[] infos = line.split(";");
                String name = infos[0];
                String date = infos[1];

                sd.setName(name);
                sd.setAdd(add);
                sd.setDel(del);
                sd.setFile(num);
                sd.setFirst(date);
                sd.setLast(date);
                sd.initDetail((int) statParam.getDays());
                sd.setDetail(statParam.calcDays(date), add, del);

                if (statMap.containsKey(name)) {
                    StatData msd = statMap.get(name);
                    statMap.put(name, msd.addData(sd));
                } else {
                    statMap.put(name, sd);
                }

                add = 0;
                del = 0;
                num = 0;
                sd = new StatData();
            }
        }

        List<StatData> gsd = Lists.newArrayList(statMap.values());
        Collections.sort(gsd);

        return gsd;
    }

    /**
     * calculate the differece between the startDate and endDate, and init the detail array
     *
     * @param method
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException
     */
    private StatParam calcDays(int method, String startDate, String endDate) throws ParseException {
        StatParam sp = new StatParam();
        long days = 0;
        Date startTime = null;
        Date lastTime = null;
        Calendar cur = Calendar.getInstance();
        switch (method) {
            case Constants.GitConstants.STAT_METHOD_CUSTOM -> {
                SimpleDateFormat sdf = new SimpleDateFormat(Constants.StaticParam.SHORT_DATE_FORMAT);
                startTime = sdf.parse(startDate);
                lastTime = sdf.parse(endDate);
                days = (lastTime.getTime() - startTime.getTime()) / Constants.StaticParam.DAY_MILLIONSECOND;
                days += 1;
            }
            case Constants.GitConstants.STAT_METHOD_DAY -> {
                lastTime = cur.getTime();
                cur.add(Calendar.DATE, -1);
                startTime = cur.getTime();
                days = 1;
            }
            case Constants.GitConstants.STAT_METHOD_WEEK -> {
                lastTime = cur.getTime();
                cur.add(Calendar.DATE, -7);
                startTime = cur.getTime();
                days = 7;
            }
            case Constants.GitConstants.STAT_METHOD_MONTH -> {
                lastTime = cur.getTime();
                cur.add(Calendar.MONTH, -1);
                startTime = cur.getTime();
                days = (lastTime.getTime() - startTime.getTime()) / Constants.StaticParam.DAY_MILLIONSECOND;
            }
            case Constants.GitConstants.STAT_METHOD_YEAR -> {
                lastTime = cur.getTime();
                cur.add(Calendar.YEAR, -1);
                startTime = cur.getTime();
                days = (lastTime.getTime() - startTime.getTime()) / Constants.StaticParam.DAY_MILLIONSECOND;
            }
            default -> {
            }
        }
        sp.setStartDate(startTime);
        sp.setEndDate(lastTime);
        sp.setDays(days);
        return sp;
    }

    /**
     * execute git log bash command
     *
     * @param method
     * @param startDate
     * @param endDate
     * @param author
     * @return
     * @throws IOException
     */
    private Stack<String> multiCmdExec(int method,
                                       String startDate,
                                       String endDate,
                                       String author,
                                       String gitRoot) throws IOException {
        Stack<String> s = new Stack<String>();
        String since = null;
        String until = null;
        String committer = null;

        switch (method) {
            case Constants.GitConstants.STAT_METHOD_CUSTOM -> {
                since = startDate;
                until = endDate;
            }
            case Constants.GitConstants.STAT_METHOD_DAY -> since = "1.day.ago";
            case Constants.GitConstants.STAT_METHOD_WEEK -> since = "1.week.ago";
            case Constants.GitConstants.STAT_METHOD_MONTH -> since = "1.month.ago";
            case Constants.GitConstants.STAT_METHOD_YEAR -> since = "1.year.ago";
            default -> {
            }
        }

        String cmd = "git log --pretty=format:\"%cn;%ad;%d\" --numstat --date=iso"
                + " --since=" + since;
        cmd += (until != null && !until.equals("")) ? (" --until=" + until) : "";
//                + " --until=" + until;
//			+ " --committer=" + committer;


        log.info("$> {}", cmd);

        gitRoot = gitRoot.replaceAll("//", File.separator);
        gitRoot = gitRoot.replaceAll("\\\\", "\\" + File.separator);

        String os = System.getProperty("os.name");

        try {
            Process process = null;
            if (os.toLowerCase().startsWith("win")) {
                process = Runtime.getRuntime().exec("cmd", null, new File(gitRoot));
            } else {
                process = Runtime.getRuntime().exec("sh", null, new File(gitRoot));
            }

            SequenceInputStream sis = new SequenceInputStream(
                    process.getInputStream(), process.getErrorStream());
            InputStreamReader isr = new InputStreamReader(sis, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            // next command
            OutputStreamWriter osw = new OutputStreamWriter(process.getOutputStream());
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(cmd);
            bw.newLine();
            bw.flush();
            bw.close();
            osw.close();
            // read
            String line = null;
            String mid = null;
            boolean r = false;
            while (null != (line = br.readLine())) {
                if (!line.startsWith("-")) {
                    Matcher m = pat.matcher(line);
                    if (m.find()) {
                        if (mid != null && !r) {
                            s.add(mid.replace(" +0800", ""));
                        }
                        s.add(line);
                        r = true;
                    } else {
                        if (r) {
                            r = false;
                        }
                        mid = line;
                    }
                }
            }
            process.destroy();
            br.close();
            isr.close();
            return s;
        } catch (IOException e) {
            log.error("$> {}", cmd, e);
        }
        return null;
    }
}
