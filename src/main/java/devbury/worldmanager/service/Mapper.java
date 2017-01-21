package devbury.worldmanager.service;

import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.Volume;
import devbury.worldmanager.domain.ServerDefinition;

class Mapper {

    private Mapper() {
    }

    static Server toServer(Container container) {
        Server s = new Server();
        s.setContainerId(container.id());
        s.setPublicPort(container.ports()
                .stream()
                .map(Container.PortMapping::publicPort)
                .filter((i) -> i != 0)
                .findFirst()
                .orElse(0));
        s.setStatus(container.status());
        s.setServerDefinitionVersion(Integer.valueOf(container.labels().get(ServerManager.VERSION_LABEL_STRING)));
        s.setName(container.names().stream().map(n -> n.substring(1)).findFirst().orElse(""));
        s.setActive("running".equals(container.state()));
        return s;
    }

    static Volume toWorldVolume(ServerDefinition serverDefinition) {
        return toWorldVolume(serverDefinition.getName());
    }

    static Volume toWorldVolume(String serverName) {
        return Volume.builder().name(serverName + "-world").build();
    }
}
