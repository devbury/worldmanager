package devbury.worldmanager.service;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;
import com.spotify.docker.client.messages.Volume;
import devbury.keeval.KeeValRepository;
import devbury.worldmanager.WorldManagerSettings;
import devbury.worldmanager.domain.ServerDefinition;
import devbury.worldmanager.exception.DockerRuntimeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.spotify.docker.client.messages.HostConfig.Bind.from;
import static devbury.worldmanager.service.Mapper.toWorldVolume;
import static java.util.Optional.ofNullable;

@Component
public class ServerManager {
    public static final String VERSION_LABEL_STRING = "devbury.worldmanager.version";
    private static final String MINECRAFT_SERVER_PORT = "25565";
    private static final DockerClient.ListContainersParam LABEL = DockerClient.ListContainersParam.withLabel(
            VERSION_LABEL_STRING);

    private final DockerClient dockerClient;
    private final WorldManagerSettings settings;
    private final KeeValRepository<ServerDefinition> repository;

    @Autowired
    public ServerManager(DockerClient dockerClient, WorldManagerSettings settings,
                         KeeValRepository<ServerDefinition> repository) {
        this.dockerClient = dockerClient;
        this.settings = settings;
        this.repository = repository;
    }

    public List<Server> allServers() {
        return Stream.concat(
                activeServers().stream(),
                inactiveServers().stream())
                .collect(Collectors.toList());
    }

    private List<Server> inactiveServers() {
        try {
            return Stream.concat(
                    dockerClient.listContainers(LABEL, DockerClient.ListContainersParam.withStatusCreated()).stream(),
                    dockerClient.listContainers(LABEL, DockerClient.ListContainersParam.withStatusExited()).stream())
                    .map(Mapper::toServer)
                    .collect(Collectors.toList());
        } catch (DockerException | InterruptedException e) {
            throw new DockerRuntimeException(e);
        }
    }

    private List<Server> activeServers() {
        try {
            return dockerClient.listContainers(LABEL)
                    .stream()
                    .map(Mapper::toServer)
                    .collect(Collectors.toList());
        } catch (DockerException | InterruptedException e) {
            throw new DockerRuntimeException(e);
        }
    }

    private void addToEnv(List<String> env, Object value, String key) {
        ofNullable(value).map(v -> key + "=").ifPresent(env::add);
    }

    private String commaString(List<String> values) {
        return values.stream().collect(Collectors.joining(","));
    }

    private List<String> buildEnvironment(ServerDefinition serverDefinition) {
        List<String> env = new LinkedList<>();
        addToEnv(env, "EULA", "TRUE");
        addToEnv(env, serverDefinition.getVersion(), "VERSION");
        addToEnv(env, serverDefinition.getDifficulty(), "DIFFICULTY");
        addToEnv(env, serverDefinition.getMaxPlayers(), "MAX_PLAYERS");
        addToEnv(env, serverDefinition.getAllowNether(), "ALLOW_NETHER");
        addToEnv(env, serverDefinition.getAnnouncePlayerAchievements(), "ANNOUNCE_PLAYER_ACHIEVEMENTS");
        addToEnv(env, serverDefinition.getEnableCommandBlock(), "ENABLE_COMMAND_BLOCK");
        addToEnv(env, serverDefinition.getForceGameMode(), "FORCE_GAMEMODE");
        addToEnv(env, serverDefinition.getGenerateStructures(), "GENERATE_STRUCTURES");
        addToEnv(env, serverDefinition.getHardcore(), "HARDCORE");
        addToEnv(env, serverDefinition.getMaxBuildHeight(), "MAX_BUILD_HEIGHT");
        addToEnv(env, serverDefinition.getGameMode(), "MODE");
        addToEnv(env, serverDefinition.getMaxWorldSize(), "MAX_WORLD_SIZE");
        addToEnv(env, serverDefinition.getMotd(), "MOTD");
        addToEnv(env, serverDefinition.getPvp(), "PVP");

        if (serverDefinition.getWorld() != null) {
            addToEnv(env, serverDefinition.getWorld(), "WORLD");
        } else {
            addToEnv(env, serverDefinition.getSeed(), "SEED");
        }

        addToEnv(env, settings.getDefaultWhitelist(), "WHITELIST");
        addToEnv(env, commaString(serverDefinition.getWhiteList()), "WHITELIST");
        addToEnv(env, commaString(serverDefinition.getOps()), "OPS");

        return env;
    }

    public void createServer(ServerDefinition serverDefinition) {
        repository.create(serverDefinition.getName(), serverDefinition);
        createContainer(serverDefinition);
    }

    private void createContainer(ServerDefinition serverDefinition) {
        try {
            Map<String, List<PortBinding>> portBindings = new HashMap<>();
            portBindings.put(MINECRAFT_SERVER_PORT,
                    Collections.singletonList(PortBinding.of("0.0.0.0", serverDefinition.getPublicPort())));

            HostConfig hostConfig = HostConfig.builder()
                    .portBindings(portBindings)
                    .restartPolicy(HostConfig.RestartPolicy.unlessStopped())
                    .appendBinds(from(toWorldVolume(serverDefinition)).to("/data/world").build())
                    .build();

            Map<String, String> labels = new HashMap<>();
            labels.put(VERSION_LABEL_STRING, String.valueOf(serverDefinition.getServerDefinitionVersion()));

            ContainerConfig containerConfig = ContainerConfig.builder()
                    .image(serverDefinition.getImage())
                    .labels(labels)
                    .env(buildEnvironment(serverDefinition))
                    .exposedPorts(MINECRAFT_SERVER_PORT)
                    .hostConfig(hostConfig).build();

            dockerClient.createContainer(containerConfig, serverDefinition.getName());
            Thread.sleep(1000);
        } catch (DockerException | InterruptedException e) {
            throw new DockerRuntimeException(e);
        }
    }

    public void startServer(String serverName) {
        try {
            dockerClient.startContainer(serverName);
        } catch (DockerException | InterruptedException e) {
            throw new DockerRuntimeException(e);
        }
    }

    public void stopServer(String serverName) {
        try {
            dockerClient.stopContainer(serverName, 0);
        } catch (DockerException | InterruptedException e) {
            throw new DockerRuntimeException(e);
        }
    }

    public void removeServer(String serverName) {
        removeContainer(serverName);
        repository.delete(serverName);
    }

    private void removeContainer(String serverName) {
        try {
            Volume worldVolume = toWorldVolume(serverName);
            dockerClient.removeContainer(serverName, DockerClient.RemoveContainerParam.forceKill(),
                    DockerClient.RemoveContainerParam.removeVolumes());
            dockerClient.removeVolume(worldVolume);
        } catch (DockerException | InterruptedException e) {
            throw new DockerRuntimeException(e);
        }
    }

    public void rebuildServer(String serverName) {
        removeContainer(serverName);
        repository.findByKey(serverName).ifPresent(this::createContainer);
    }

    public void reConfigureServer(String serverName) {
        try {
            dockerClient.removeContainer(serverName,
                    DockerClient.RemoveContainerParam.forceKill(),
                    DockerClient.RemoveContainerParam.removeVolumes());
        } catch (DockerException | InterruptedException e) {
            throw new DockerRuntimeException(e);
        }
        repository.findByKey(serverName).ifPresent(this::createContainer);

    }
}
