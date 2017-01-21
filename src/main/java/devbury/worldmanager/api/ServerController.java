package devbury.worldmanager.api;

import devbury.keeval.KeeValRepository;
import devbury.worldmanager.domain.ServerDefinition;
import devbury.worldmanager.service.Server;
import devbury.worldmanager.service.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/server")
public class ServerController {

    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);
    private final ServerManager serverManager;
    private final KeeValRepository<ServerDefinition> repository;

    @Autowired
    public ServerController(ServerManager serverManager, KeeValRepository<ServerDefinition> repository) {
        this.serverManager = serverManager;
        this.repository = repository;
    }

    @PostMapping("{serverDefinitionName}")
    public void create(@PathVariable String serverDefinitionName) {
        repository.findByKey(serverDefinitionName).ifPresent(serverManager::createServer);
    }

    @DeleteMapping("{containerId}")
    public void deleteServer(@PathVariable String containerId) {
        serverManager.removeServer(containerId);
    }

    @PutMapping("stop")
    public void stopServer(@RequestBody Server server) {
        serverManager.stopServer(server.getContainerId());
    }

    @PutMapping("start")
    public void startServer(@RequestBody Server server) {
        serverManager.startServer(server.getContainerId());
    }

    @PutMapping("reconfigure")
    public void reconfigureServer(@RequestBody Server server) {
        repository.findByKey(server.getName())
                .ifPresent(sd -> serverManager.reConfigureServer(server.getContainerId(), sd));
    }

    @GetMapping
    public List<Server> allServers() {
        return serverManager.allServers();
    }
}
