package github;

import org.joda.time.IllegalFieldValueException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import file.FileOperator;
import util.StringUtils;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/03/03 20:52
 */
public class LogNewRepoParse {
    private static final Logger logger = LoggerFactory.getLogger(LogNewRepoParse.class);
    public static void main1(String[] args) {
        String path = "D:\\Downloads\\GithubDownload.2022-03-03.log";
        String config = FileOperator.getConfig(path);
        Pattern pattern = Pattern.compile("newRepo:(.+)$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(config);
        HashSet<String> repo = new HashSet<>();
      
        StringBuilder sb = new StringBuilder();
        while (matcher.find())
        {
            String newRepo = matcher.group(1);
            if (repo.contains(newRepo)) {
                continue;
            }
            repo.add(newRepo);
//            sb.append(newRepo).append("\n");
            sb.append("git clone git@github.com:").append(newRepo).append(".git ").append(newRepo).append("\n");
        }
        logger.debug("main repo.size:{}", repo.size());
        logger.debug("main sb:\n{}", sb);
        
    }
    
    public static void main(String[] args) {
        String folder = "E:\\workspace\\github\\AAComplete";
        File file = new File(folder);
        String[] list = file.list();
        Set<String> parsedSet = new HashSet<>();
        for (String s : list) {
            String s1 = s.replaceAll("#", "/").replace(".txt","");
            
            parsedSet.add(s1);
        }
        String[] fileLines = FileOperator.getFileLines("D:\\Downloads\\clone.sh");
        Set<String> removeSet = new HashSet<>();
        StringBuilder sb  = new StringBuilder();
        for (String fileLine : fileLines) {
            if (StringUtils.isEmpty(fileLine)) {
                return;
            }
            String o = fileLine.split(" ")[1];
            if (parsedSet.contains(o)) {
                removeSet.add(o);
                 
                sb.append("rm -rf ").append(o).append("\n");
            }
        }
       logger.debug("main sb.toString:\n{}", sb.toString());
    
    }
}
