package devbury.worldmanager.api;

import devbury.keeval.KeeValRepository;
import devbury.worldmanager.domain.ServerDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/definition")
public class ServerDefinitionController {
    private static final Logger logger = LoggerFactory.getLogger(ServerController.class);
    private final KeeValRepository<ServerDefinition> repository;

    @Autowired
    public ServerDefinitionController(KeeValRepository<ServerDefinition> repository) {
        this.repository = repository;
    }


    @GetMapping("{key}")
    public Optional<ServerDefinition> findById(@PathVariable String key) {
        return repository.findByKey(key);
    }

    @GetMapping
    public List<ServerDefinition> serverDefinitions() {
        return repository.findAll()
                .stream()
                .sorted(Comparator.comparing(ServerDefinition::getName))
                .collect(Collectors.toList());
    }

    @PostMapping
    public void create(@RequestBody ServerDefinition serverDefinition) {
        // validate
        repository.create(serverDefinition.getName(), serverDefinition);
    }

    @DeleteMapping("{name}")
    public void delete(@PathVariable String name) {
        repository.delete(name);
    }
}
