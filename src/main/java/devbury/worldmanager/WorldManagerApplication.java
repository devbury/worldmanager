package devbury.worldmanager;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import devbury.keeval.KeeValManager;
import devbury.keeval.KeeValRepository;
import devbury.worldmanager.domain.ServerDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableConfigurationProperties(WorldManagerSettings.class)
public class WorldManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorldManagerApplication.class, args);
    }

    @Bean
    public DockerClient dockerClient() {
        return new DefaultDockerClient("unix:///var/run/docker.sock");
    }

    @Bean
    public KeeValRepository<ServerDefinition> serverDefinitionRepository(KeeValManager manager) {
        return manager.repository(ServerDefinition.class);
    }
}
