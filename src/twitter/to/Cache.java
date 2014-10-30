/*
 * Clase para la gestion de los usuarios
 */

package twitter.to;

public class Cache {
    private int cache_num;
    private String cache_ip;
    private int cache_users;
 
    public int getCacheNum() {
        return cache_num;
    }
    public void setCacheNum(int cache_num) {
        this.cache_num = cache_num;
    }
    public String getCacheIp() {
        return cache_ip;
    }
    public void setCacheIp(String cache_ip) {
        this.cache_ip = cache_ip;
    }
    public int getCacheUsers() {
        return cache_users;
    }
    public void setCacheUsers(int cache_users) {
        this.cache_users = cache_users;
    }
    public String toString() {
    	return " [" + cache_num + "," + cache_ip + ","+ cache_users +"]";
    }
}