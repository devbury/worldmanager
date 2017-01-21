package devbury.worldmanager;

import devbury.keeval.DataSourceProvider;
import devbury.keeval.KeeValRepository;
import devbury.worldmanager.domain.GameMode;
import devbury.worldmanager.domain.ServerDefinition;
import devbury.worldmanager.service.Server;
import devbury.worldmanager.service.ServerManager;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = WorldManagerApplicationTests.class)
@Configuration
@ComponentScan
public class WorldManagerApplicationTests {

    @Autowired
    ServerManager sM;

    @Autowired
    KeeValRepository<ServerDefinition> repository;

    @Bean
    public DataSourceProvider dataSourceProvider() {
        return () -> new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addScript("classpath:/devbury/keeval/schema.sql")
                .build();
    }

    @After
    public void removeAllServers() {
        sM.allServers()
                .stream()
                .map(Server::getName)
                .forEach(sM::removeServer);
    }

    private ServerDefinition serverDef() {
        ServerDefinition sd = new ServerDefinition();
        sd.setPublicPort(10000);
        sd.setName("SimpleServer");
        sd.setEnableCommandBlock(true);
        sd.setGameMode(GameMode.SURVIVAL);
        sd.setForceGameMode(true);
        sd.setSeed("Sample Seed");
        sd.setVersion("1.11.2");
        return sd;
    }

    @Test
    public void tests() throws InterruptedException {
        ServerDefinition sd = serverDef();
        sM.createServer(sd);
        sM.startServer(sd.getName());
        Thread.sleep(10000);
        sM.rebuildServer(sd.getName());
        sM.startServer(sd.getName());
        Thread.sleep(10000);
        sd.setGameMode(GameMode.CREATIVE);
        sd.setForceGameMode(true);
        repository.createOrUpdate(sd.getName(), sd);
        sM.reConfigureServer(sd.getName());
        sM.startServer(sd.getName());
        Thread.sleep(10000);
    }
}
