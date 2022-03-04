package github;

/**
 *
 * 创建人  liangsong
 * 创建时间 2022/02/10 10:36
 */
public class RepoInfo {
    public String getDir() {
        return dir;
    }
    
    private String dir;
    
    public RepoInfo(String s) {
        this.index = 0;
        String[] split = s.split("/");
        this.userName = split[0];
        this.projectName = split[1];
        this.dir = s;
    }
    
    public int getIndex() {
        return index;
    }
    
    public final int index;
    public final String userName;
    public final String projectName;
    
    public RepoInfo(int index,String userName,String projectName) {
        this.index = index;
        this.userName = userName;
        this.projectName = projectName;
    }
}
