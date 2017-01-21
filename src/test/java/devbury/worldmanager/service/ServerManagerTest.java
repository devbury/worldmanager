package devbury.worldmanager.service;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import devbury.worldmanager.WorldManagerSettings;
import devbury.worldmanager.domain.GameMode;
import devbury.worldmanager.domain.ServerDefinition;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

public class ServerManagerTest {

    ServerManager sM;

    @Before
    public void before() {
        DockerClient dockerClient = new DefaultDockerClient("unix:///var/run/docker.sock");
        sM = new ServerManager(dockerClient, new WorldManagerSettings());
    }

    @Test
    public void testStuff() {
        System.out.println("active========================================");

        sM.activeServers().forEach(System.out::println);

        System.out.println("inactive======================================");

        sM.inactiveServers().forEach(System.out::println);

    }

    @Test
    public void testRemoval() {
        sM.allServers()
                .stream()
                .map(Server::getContainerId)
                .forEach(sM::removeServer);
    }

    @Test
    public void testCreation() {
        ServerDefinition sD = new ServerDefinition();
        sD.setPublicPort(10000);
        sD.setName("park");
        sD.setEnableCommandBlock(true);
        sD.setGameMode(GameMode.SURVIVAL);
        sD.setForceGameMode(true);
        sD.setOps(Collections.singletonList("Guibourtia"));
        sD.setWhiteList(Collections.singletonList("Guibourtia"));
        //sD.setHardcore(true);
        //sD.setServerDefinitionVersion(3);
        sD.setMaxWorldSize(100);
        sD.setSeed("David Noel");
        //sD.setWorldUrl("http://www.minecraftmaps.com/parkour-maps/parkour-paradise-3/download/mirror-1");
        sD.setVersion("1.11.2");
        sM.startServer(sM.createServer(sD));
        // sM.startServer(sM.reConfigureServer(sM.findContainerIdByName(sD.getName()).get(), sD));
    }

    @Test
    public void testStartAll() {
        sM.startAllServers();
    }

    @Test
    public void testStopAll() {
        sM.stopAllServers();
    }

}