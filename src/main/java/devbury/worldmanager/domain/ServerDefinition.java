package devbury.worldmanager.domain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Collections;
import java.util.List;

public class ServerDefinition {

    public static final String DEFAULT_SERVER_IMAGE = "itzg/minecraft-server";
    public static final int DEFAULT_MINECRAFT_SERVER_PORT = 25565;

    private String image = DEFAULT_SERVER_IMAGE;
    private int publicPort = DEFAULT_MINECRAFT_SERVER_PORT;

    @NotNull
    @Pattern(regexp = "^/?[a-zA-Z0-9_-]+$")
    private String name;

    private String version = null;
    private Difficulty difficulty = null;
    private List<String> whiteList = Collections.emptyList();
    private List<String> ops = Collections.emptyList();
    private Integer maxPlayers = null;
    private Integer maxWorldSize = null;
    private Boolean allowNether = null;
    private Boolean announcePlayerAchievements = null;
    private Boolean enableCommandBlock = null;
    private Boolean forceGameMode = null;
    private Boolean generateStructures = null;
    private Boolean hardcore = null;
    private Boolean pvp = null;
    private Integer maxBuildHeight = null;
    private GameMode gameMode = null;
    private String seed = String.valueOf(System.currentTimeMillis());
    private String world = null;
    private String motd = null;
    private int serverDefinitionVersion = 0;

    public Boolean getPvp() {
        return pvp;
    }

    public void setPvp(Boolean pvp) {
        this.pvp = pvp;
    }

    public String getMotd() {
        return motd;
    }

    public void setMotd(String motd) {
        this.motd = motd;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPublicPort() {
        return publicPort;
    }

    public void setPublicPort(int publicPort) {
        this.publicPort = publicPort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public List<String> getOps() {
        return ops;
    }

    public void setOps(List<String> ops) {
        this.ops = ops;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(Integer maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public Integer getMaxWorldSize() {
        return maxWorldSize;
    }

    public void setMaxWorldSize(Integer maxWorldSize) {
        this.maxWorldSize = maxWorldSize;
    }

    public Boolean getAllowNether() {
        return allowNether;
    }

    public void setAllowNether(Boolean allowNether) {
        this.allowNether = allowNether;
    }

    public Boolean getAnnouncePlayerAchievements() {
        return announcePlayerAchievements;
    }

    public void setAnnouncePlayerAchievements(Boolean announcePlayerAchievements) {
        this.announcePlayerAchievements = announcePlayerAchievements;
    }

    public Boolean getEnableCommandBlock() {
        return enableCommandBlock;
    }

    public void setEnableCommandBlock(Boolean enableCommandBlock) {
        this.enableCommandBlock = enableCommandBlock;
    }

    public Boolean getForceGameMode() {
        return forceGameMode;
    }

    public void setForceGameMode(Boolean forceGameMode) {
        this.forceGameMode = forceGameMode;
    }

    public Boolean getGenerateStructures() {
        return generateStructures;
    }

    public void setGenerateStructures(Boolean generateStructures) {
        this.generateStructures = generateStructures;
    }

    public Boolean getHardcore() {
        return hardcore;
    }

    public void setHardcore(Boolean hardcore) {
        this.hardcore = hardcore;
    }

    public Integer getMaxBuildHeight() {
        return maxBuildHeight;
    }

    public void setMaxBuildHeight(Integer maxBuildHeight) {
        this.maxBuildHeight = maxBuildHeight;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public String getSeed() {
        return seed;
    }

    public void setSeed(String seed) {
        this.seed = seed;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public int getServerDefinitionVersion() {
        return serverDefinitionVersion;
    }

    public void setServerDefinitionVersion(int serverDefinitionVersion) {
        this.serverDefinitionVersion = serverDefinitionVersion;
    }
}
