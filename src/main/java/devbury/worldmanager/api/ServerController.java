package devbury.worldmanager.api;

import devbury.keeval.KeeValRepository;
import devbury.worldmanager.domain.ServerDefinition;
import devbury.worldmanager.service.Server;
import devbury.worldmanager.service.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @PostMapping
    public void create(@RequestBody ServerDefinition serverDefinition) {
        serverManager.createServer(serverDefinition);
    }

    @DeleteMapping("{serverName}")
    public void deleteServer(@PathVariable String serverName) {
        serverManager.removeServer(serverName);
    }

    @PutMapping("stop")
    public void stopServer(@RequestBody Server server) {
        serverManager.stopServer(server.getName());
    }

    @PutMapping("start")
    public void startServer(@RequestBody Server server) {
        serverManager.startServer(server.getName());
    }

    @PutMapping("reconfigure")
    public void reconfigureServer(@RequestBody Server server) {
        serverManager.reConfigureServer(server.getName());
    }

    @PutMapping("rebuild")
    public void rebuildServer(@RequestBody Server server) {
        serverManager.rebuildServer(server.getName());
    }

    @GetMapping
    public List<Server> allServers() {
        return serverManager.allServers()
                .stream()
                .sorted(Comparator.comparing(Server::getName))
                .collect(Collectors.toList());
    }
}
