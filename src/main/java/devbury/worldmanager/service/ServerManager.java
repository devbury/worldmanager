package devbury.worldmanager.service;

import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;
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

    @Autowired
    public ServerManager(DockerClient dockerClient, WorldManagerSettings settings) {
        this.dockerClient = dockerClient;
        this.settings = settings;
    }

    public List<Server> allServers() {
        return Stream.concat(
                activeServers().stream(),
                inactiveServers().stream())
                .collect(Collectors.toList());
    }

    public List<Server> inactiveServers() {
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

    public List<Server> activeServers() {
        try {
            return dockerClient.listContainers(LABEL)
                    .stream()
                    .map(Mapper::toServer)
                    .collect(Collectors.toList());
        } catch (DockerException | InterruptedException e) {
            throw new DockerRuntimeException(e);
        }
    }

    private List<String> buildEnvironment(ServerDefinition serverDefinition) {
        List<String> env = new LinkedList<>();
        env.add("EULA=TRUE");
        ofNullable(serverDefinition.getVersion()).map(v -> "VERSION=" + v).ifPresent(env::add);
        ofNullable(serverDefinition.getDifficulty()).map(v -> "DIFFICULTY=" + v).ifPresent(env::add);
        ofNullable(serverDefinition.getMaxPlayers()).map(v -> "MAX_PLAYERS=" + v).ifPresent(env::add);
        ofNullable(serverDefinition.getAllowNether()).map(v -> "ALLOW_NETHER=" + v).ifPresent(env::add);
        ofNullable(serverDefinition.getAnnouncePlayerAchievements())
                .map(v -> "ANNOUNCE_PLAYER_ACHIEVEMENTS=" + v).ifPresent(env::add);
        ofNullable(serverDefinition.getEnableCommandBlock()).map(v -> "ENABLE_COMMAND_BLOCK=" + v).ifPresent(env::add);
        ofNullable(serverDefinition.getForceGameMode()).map(v -> "FORCE_GAMEMODE=" + v).ifPresent(env::add);
        ofNullable(serverDefinition.getGenerateStructures()).map(v -> "GENERATE_STRUCTURES=" + v).ifPresent(env::add);
        ofNullable(serverDefinition.getHardcore()).map(v -> "HARDCORE=" + v).ifPresent(env::add);
        ofNullable(serverDefinition.getMaxBuildHeight()).map(v -> "MAX_BUILD_HEIGHT=" + v).ifPresent(env::add);
        ofNullable(serverDefinition.getGameMode()).map(v -> "MODE=" + v).ifPresent(env::add);
        ofNullable(serverDefinition.getMaxWorldSize()).map(v -> "MAX_WORLD_SIZE=" + v).ifPresent(env::add);

        if (serverDefinition.getWorldUrl() != null) {
            env.add("WORLD=" + serverDefinition.getWorldUrl());
        } else {
            env.add("SEED=" + serverDefinition.getSeed());
        }

        if (!serverDefinition.getWhiteList().isEmpty()) {
            env.add("WHITELIST=" + serverDefinition.getWhiteList()
                    .stream()
                    .collect(Collectors.joining(",")));
        }
        if (!serverDefinition.getOps().isEmpty()) {
            env.add("OPS=" + serverDefinition.getOps()
                    .stream()
                    .collect(Collectors.joining(",")));
        }

        return env;
    }

    public String createServer(ServerDefinition serverDefinition) {
        try {
            Map<String, List<PortBinding>> portBindings = new HashMap<>();
            portBindings.put(MINECRAFT_SERVER_PORT,
                    Collections.singletonList(PortBinding.of("0.0.0.0", serverDefinition.getPublicPort())));

            HostConfig hostConfig = HostConfig.builder()
                    .portBindings(portBindings)
                    .restartPolicy(HostConfig.RestartPolicy.always())
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

            ContainerCreation container = dockerClient.createContainer(containerConfig, serverDefinition.getName());
            return container.id();
        } catch (DockerException | InterruptedException e) {
            throw new DockerRuntimeException(e);
        }
    }

    public void startServer(String containerId) {
        try {
            dockerClient.startContainer(containerId);
        } catch (DockerException | InterruptedException e) {
            throw new DockerRuntimeException(e);
        }
    }

    public void startAllServers() {
        inactiveServers().stream().map(Server::getContainerId).forEach(this::startServer);
    }

    public void stopAllServers() {
        activeServers().stream().map(Server::getContainerId).forEach(this::stopServer);
    }

    public void stopServer(String containerId) {
        try {
            dockerClient.stopContainer(containerId, 0);
        } catch (DockerException | InterruptedException e) {
            throw new DockerRuntimeException(e);
        }
    }

    public void removeServer(String containerId) {
        try {
            Volume worldVolume = toWorldVolume(dockerClient.inspectContainer(containerId).name());
            dockerClient.removeContainer(containerId, DockerClient.RemoveContainerParam.forceKill(),
                    DockerClient.RemoveContainerParam.removeVolumes());
            dockerClient.removeVolume(worldVolume);
        } catch (DockerException | InterruptedException e) {
            throw new DockerRuntimeException(e);
        }
    }

    public String rebuildServer(String containerId, ServerDefinition serverDefinition) {
        removeServer(containerId);
        return createServer(serverDefinition);
    }

    public Optional<String> findContainerIdByName(String name) {
        return allServers().stream().filter(s -> s.getName().equals(name)).map(Server::getContainerId).findFirst();
    }

    public String reConfigureServer(String existingContainerId, ServerDefinition serverDefinition) {
        try {
            dockerClient.removeContainer(existingContainerId, DockerClient.RemoveContainerParam.forceKill());
            Thread.sleep(1000L);
            return createServer(serverDefinition);
        } catch (DockerException | InterruptedException e) {
            throw new DockerRuntimeException(e);
        }
    }
}
