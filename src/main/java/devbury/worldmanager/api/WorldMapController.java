package devbury.worldmanager.api;

import devbury.keeval.KeeValRepository;
import devbury.worldmanager.WorldManagerSettings;
import devbury.worldmanager.domain.WorldMap;
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
@RequestMapping("api/map")
public class WorldMapController {

    private final KeeValRepository<WorldMap> repository;
    private final WorldManagerSettings settings;

    @Autowired
    public WorldMapController(KeeValRepository<WorldMap> repository, WorldManagerSettings settings) {
        this.repository = repository;
        this.settings = settings;
    }

    private File mapFileName(String name) {
        return new File(settings.getMapFolder() + name + ".zip");
    }

    @GetMapping
    public List<WorldMap> findAll() {
        return repository.findAll()
                .stream()
                .sorted(Comparator.comparing(WorldMap::getName))
                .collect(Collectors.toList());
    }

    @DeleteMapping("{name}")
    public void delete(@PathVariable String name) {
        if (mapFileName(name).delete()) {
            repository.delete(name);
        }
    }

    @PostMapping("{name}")
    public void create(@RequestParam("0") MultipartFile file, @PathVariable String name) {
        WorldMap map = new WorldMap();
        map.setName(name);
        repository.create(name, map);
        try {
            FileCopyUtils.copy(file.getInputStream(), new FileOutputStream(mapFileName(name)));
        } catch (IOException e) {
            repository.delete(name);
            throw new RuntimeException("Could not save map file");
        }
    }
}
