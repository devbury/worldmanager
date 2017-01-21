package devbury.worldmanager;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class WorldManagerSettings {

    private String rootdir = System.getProperty("user.dir");

    public String getRootdir() {
        return rootdir;
    }

    public String getWorldsDir(){
        return getRootdir() + "/worlds";
    }

    public void setRootdir(String rootdir) {
        this.rootdir = rootdir;
    }
}
