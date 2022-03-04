package github;

import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.AsyncHttpClient.BoundRequestBuilder;
import com.ning.http.client.AsyncHttpClientConfig;
import com.ning.http.client.AsyncHttpClientConfig.Builder;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import game.collection.IntConcurrentHashMap;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/02/10 10:27
 */
public class GithubRepoRequest  {
    private static final Logger logger = LoggerFactory.getLogger(GithubRepoRequest.class);
    
    public final String url;
    private final int page;
    private final Map<String, String> header;
    private final IntConcurrentHashMap<RepoInfo> responseInfoMap;
    private final HashSet<String> alreadyDownloadSet;
    private boolean success;
    
    public GithubRepoRequest(String lang, int page, Map<String, String> header, IntConcurrentHashMap<RepoInfo> responseInfoMap,
            HashSet<String> alreadyDownloadSet) {
        this.page = page;
        this.header = header;
        this.responseInfoMap = responseInfoMap;
        this.alreadyDownloadSet = alreadyDownloadSet;
        String param = String.format("p=%d&q=language:%s&type=repositories",page,lang);
        url = "https://github.com/search?" + param;
        
    }
    
 
    public void run() {
        logger.debug("请求git仓库信息 url:{}", url);
        Builder builder = new Builder();
        int timeout = 60 * 1000;
        builder.setReadTimeout(timeout);
        builder.setConnectTimeout(timeout);
        builder.setRequestTimeout(timeout);
        AsyncHttpClientConfig config = builder.build();
        AsyncHttpClient asyncHttpsClient = new AsyncHttpClient(config);
        
        BoundRequestBuilder boundRequestBuilder = asyncHttpsClient.prepareGet(url);
   
        for (Entry<String, String> entry : header.entrySet()) {
            boundRequestBuilder.addHeader(entry.getKey(),entry.getValue());
        }
        ListenableFuture<Response> listenableFuture = boundRequestBuilder.execute();
    
        try {
            String responseBody = listenableFuture.get().getResponseBody();
            //                logger.debug("onStartDownload responseBody:{}", responseBody);
            Pattern pattern = Pattern.compile("&quot;https://github.com/([^?]+?)&quot;");
            Matcher matcher = pattern.matcher(responseBody);
            int index = 0;
            int find = 0;
            while (matcher.find())
            {
              
                String repo = matcher.group(1);
                find++;
                if (alreadyDownloadSet.contains(repo)) {
                    logger.debug("onStartDownload downloaded:{}",repo);
                    continue;
                }
                logger.debug("onStartDownload newRepo:{}",repo);
                String[] repoInfo = repo.split("/");
                String userName = repoInfo[0];
                String projectName = repoInfo[1];
                int allRepoIndex = this.page * 10 + index++;
                responseInfoMap.put(allRepoIndex,new RepoInfo(allRepoIndex,userName,projectName));
               
            }
            if (find > 1) {
                success = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }
//        latch.countDown();
    }
    
    public boolean isSuccess() {
        return success;
    }
}
