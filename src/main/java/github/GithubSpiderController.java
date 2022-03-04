package github;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import file.FileOperator;
import game.collection.IntConcurrentHashMap;
import util.ArgParser;
import util.TimeUtils;

import static util.Utils.BR;

;


/**
 *
 * 创建人  liangsong
 * 创建时间 2022/02/09 19:58
 */
public class GithubSpiderController  {
    private static final Logger logger = LoggerFactory.getLogger(GithubSpiderController.class);
 
   
    
    public static void exeRequest(String lang, File configFolder, File downFolder, int startPage, int endPage, int sleep, HashSet<String> alreadyDownloadSet) throws InterruptedException {
        Map<String, String> header = getHeaders(configFolder);
        IntConcurrentHashMap<RepoInfo> responseInfoMap = new IntConcurrentHashMap<>();
        List<String> exceptionUrl = new ArrayList<>();
        int localSleep = 0;
        for (int i = startPage; i < endPage; i++) {
            GithubRepoRequest githubRepoRequest = new GithubRepoRequest(lang, i, header, responseInfoMap,alreadyDownloadSet);
            githubRepoRequest.run();
            if (!githubRepoRequest.isSuccess()) {
               
                if (localSleep <=0) {
                    localSleep = sleep;
                }
                localSleep+= localSleep;
                logger.debug("exeRequest localSleep:{}", localSleep);
                if (localSleep <2* 60*1000) {
                    i--;//小于两分钟，过一会重试
                }
                else if (localSleep > 10*60*1000) {
                    break;
                }
                else 
                {
                    exceptionUrl.add(githubRepoRequest.url);
                    logger.debug("exeRequest 未成功 url:{}", githubRepoRequest.url);
                    if (exceptionUrl.size() > 5) {
                        writeToFile(exceptionUrl);
                    }
                }
            }
            else {
                localSleep = 0;
            }
            
            
            Thread.sleep(sleep + localSleep);
        }
        if (exceptionUrl.size() > 0) {
            writeToFile(exceptionUrl);   
        }
        
        
        logger.debug("onStartDownload responseInfoMap.size:{}", responseInfoMap.size());
        ArrayList<RepoInfo> list = Lists.newArrayList();
        list.addAll(responseInfoMap.values());
        list.sort(Comparator.comparingInt(RepoInfo::getIndex));
        StringBuilder repoInfo = new StringBuilder();
        for (RepoInfo info : list) {
            repoInfo.append(info.userName).append("/").append(info.projectName).append(BR);
        }
        FileOperator.writeFile(downFolder.getAbsolutePath() + "/all_repo_" + TimeUtils.printDate(new Date().getTime()) + ".txt", repoInfo.toString());
        logger.debug("写入所有repo完成!");
    }
    
    private static void writeToFile(List<String> exceptionUrl) {
        try {
            File file = new File("exceptionUrl.log");
            
  
            StringBuilder sb = new StringBuilder();
            for (String s : exceptionUrl) {
                sb.append(s).append("\n");
            }
            Files.write(file.toPath(),sb.toString().getBytes(), StandardOpenOption.CREATE,StandardOpenOption.APPEND);
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            exceptionUrl.clear();
        }
    
    }
    
    private static Map<String, String> getHeaders(File downFolder) {
        String config = FileOperator.getConfig(downFolder.getAbsolutePath() + "/head.txt");
        String s1 = config.replaceAll("\r", "");
        String[] split = s1.split("\n");
        Map<String, String> header = Maps.newHashMap();
        for (String s : split) {
            String[] split1 = s.split(": ");
            header.put(split1[0], split1[1]);
        }
        return header;
    }
    
 
    public static StringBuilder getGitCloneBashContent(File root) {
       
        List<File> repos = Lists.newArrayList();
        FileOperator.traverseFiles(root, (File entry) -> {
            if (entry.getAbsolutePath().equals(root.getAbsolutePath())) {
                return true;
            }
            boolean repo = entry.isFile() && entry.getName().startsWith("all_repo");
            if (repo) {
                repos.add(entry);
            }
            return repo;
        });
        List<RepoInfo> allRepo = new ArrayList<>();
        Set<String> repoSet = Sets.newHashSet();
        for (File repo : repos) {
            String config = FileOperator.getConfig(repo.getAbsolutePath());
            config = config.replace("\r", "");
            String[] split = config.split("\n");
            for (String s : split) {
                if (repoSet.contains(s)) {
                    continue;
                }
                String[] split1 = s.split("/");
                File completeFile = getCompleteFile(root, new File(split1[0]), new File(split1[1]));
                if (completeFile.exists()) {
                    continue;
                }
                repoSet.add(s);
                allRepo.add(new RepoInfo(s));
            }
        }
        
        StringBuilder sb = new StringBuilder();
        for (RepoInfo repoInfo : allRepo) {
            File file = new File(root.getAbsolutePath() + "/" + repoInfo.getDir());
            if (file.exists()) {
                continue;
            }
            sb.append("git clone git@github.com:").append(repoInfo.getDir()).append(".git ").append(repoInfo.getDir()).append("\n");
          
        }
        return sb;
    }
    public static File getCompleteFile(File codeFolder, File userFolder, File repo) {
        return new File(codeFolder.getAbsolutePath() + "/AAComplete/" + userFolder.getName() + "#" + repo.getName() + ".txt");
    }
    private File createShellFile(File root, RepoInfo repoInfo) {
        File shellFile = new File(root.getAbsolutePath() + "/tmpClone.sh");
        StringBuilder sb = new StringBuilder();
        sb.append("git clone git@github.com:").append(repoInfo.getDir()).append(".git ").append(repoInfo.getDir()).append("\n");
        sb.append("echo \"clone complete\"");
        FileOperator.writeFile(shellFile, sb.toString());
        return shellFile;
    }
    
//    public static void main(String[] args) {
//        ArrayList<String> list = Lists.newArrayList("list");
//        writeToFile(list);
//    }
//    
    public static void main(String[] args) throws InterruptedException {
        ArgParser parse = ArgParser.parse(args);
        String src = parse.getKey("src", "下载目录", "github_src");
        String configPath = parse.getKey("config","配置文件夹",src);
        String lang = parse.getKey("lang", "语言", "objective-c");
        int sleep = parse.getIntKey("sleep", "请求间隔秒", 6)*1000;
        int startPage = parse.getIntKey("startPage", "起始页", 1);
        int endPage = parse.getIntKey("endPage", "结束页", 1000);
        File root = new File(src);
        File configFolder = new File(configPath);
        String config = FileOperator.getConfig(configFolder.getAbsolutePath() + "/projects.txt");
        HashSet<String> alreadyDownloadSet = Sets.newHashSet(config.split("\n"));
        logger.debug("main alreadyDownloadSet.size:{}", alreadyDownloadSet.size());
  
        
        exeRequest(lang,configFolder,root,startPage, endPage,sleep,alreadyDownloadSet);
        StringBuilder sb = getGitCloneBashContent(root);
        FileOperator.writeFile(root.getAbsolutePath() + "/clone.sh",sb.toString());
       
    }
}
