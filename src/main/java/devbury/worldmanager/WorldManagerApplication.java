package devbury.worldmanager;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import devbury.keeval.KeeValManager;
import devbury.keeval.KeeValRepository;
import devbury.worldmanager.domain.ServerDefinition;
import devbury.worldmanager.domain.WorldMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import java.io.File;

@SpringBootApplication
@EnableConfigurationProperties(WorldManagerSettings.class)
public class WorldManagerApplication {

    private final WorldManagerSettings worldManagerSettings;

    public static void main(String[] args) {
        SpringApplication.run(WorldManagerApplication.class, args);
    }

    @Autowired
    public WorldManagerApplication(WorldManagerSettings worldManagerSettings) {
        this.worldManagerSettings = worldManagerSettings;
    }

    @Bean
    public DockerClient dockerClient() {
        return new DefaultDockerClient("unix:///var/run/docker.sock");
    }

    @Bean
    public KeeValRepository<ServerDefinition> serverDefinitionRepository(KeeValManager manager) {
        return manager.repository(ServerDefinition.class);
    }

    @Bean
    public KeeValRepository<WorldMap> worldMapRepository(KeeValManager manager) {
        return manager.repository(WorldMap.class);
    }

    @PostConstruct
    public void init() {
        // create the maps folder if it does not exist
        new File(worldManagerSettings.getMapFolder()).mkdirs();
    }
}
