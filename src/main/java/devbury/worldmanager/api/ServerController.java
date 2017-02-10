package devbury.worldmanager.api;

import devbury.keeval.KeeValRepository;
import devbury.worldmanager.WorldManagerSettings;
import devbury.worldmanager.domain.ServerDefinition;
import devbury.worldmanager.service.Server;
import devbury.worldmanager.service.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/server")
public class ServerController {

    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);
    private final ServerManager serverManager;
    private final WorldManagerSettings settings;
    private final KeeValRepository<ServerDefinition> repository;

    @Autowired
    public ServerController(ServerManager serverManager, KeeValRepository<ServerDefinition> repository,
                            WorldManagerSettings settings) {
        this.serverManager = serverManager;
        this.repository = repository;
        this.settings = settings;
    }

    private File mapFileName(String name) {
        return new File(settings.getMapFolder() + name + ".zip");
    }

    @PostMapping
    public void create(@RequestParam("0") MultipartFile file, @RequestBody ServerDefinition serverDefinition) {
        try {
            FileCopyUtils.copy(
                    file.getInputStream(),
                    new FileOutputStream(mapFileName(serverDefinition.getName())));
        } catch (IOException e) {
            mapFileName(serverDefinition.getName()).delete();
            throw new RuntimeException("Could not save map file");
        }
        serverManager.createServer(serverDefinition);
    }

    @DeleteMapping("{serverName}")
    public void deleteServer(@PathVariable String serverName) {
        serverManager.removeServer(serverName);
        mapFileName(serverName).delete();
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
