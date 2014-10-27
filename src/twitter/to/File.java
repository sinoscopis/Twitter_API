/*
 * Clase para la gestion de los archivos
 */

package twitter.to;

public class File {
    private String fileName;
    private boolean cache_1;
    private boolean cache_2;
    private boolean cache_3;
    private boolean cache_4;
    private boolean cache_5;
    
    
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String toString() {
    	return " [" + fileName + "," + cache_1 + "," + cache_2 +"," + cache_3 +"," + cache_4 +"," + cache_5 +"]";
    }
}