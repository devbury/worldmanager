package devbury.worldmanager;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;

@ConfigurationProperties(prefix = "worldmanager")
public class WorldManagerSettings {

    private String defaultWhitelist;
    private String mapFolder;

    @PostConstruct
    public void init() {
        if (StringUtils.isEmpty(mapFolder)) {
            setMapFolder("maps");
        }
        new File(getMapFolder()).mkdirs();
    }

    public String getMapFolder() {
        return mapFolder;
    }

    public void setMapFolder(String mapFolder) {
        if (new File(mapFolder).isAbsolute()) {
            this.mapFolder = mapFolder;
        } else {
            this.mapFolder = System.getProperty("user.dir") + "/" + mapFolder;
        }
    }

    public String getDefaultWhitelist() {
        return defaultWhitelist;
    }

    public void setDefaultWhitelist(String defaultWhitelist) {
        this.defaultWhitelist = defaultWhitelist;
    }
}
