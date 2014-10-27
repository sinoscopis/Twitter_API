/*
 * Clase para la gestion de los amigos
 */

package twitter.to;

public class Follower {
	
	private int userId;
	private int cache;
    private int followers;
 
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public int getCacheNum() {
        return cache;
    }
    public void setCacheNum(int cache) {
        this.cache = cache;
    }
    public int getFollowersNum() {
        return followers;
    }
    public void setFollowersNum(int followers) {
        this.followers = followers;
    }
    public String toString() {
    	return "[" + userId + "," + cache + "," + followers + "]";
    }
}

