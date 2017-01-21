package devbury.worldmanager.service;

public class Server {
    private String name;
    private int publicPort;
    private String containerId;
    private int serverDefinitionVersion;
    private String status;
    private boolean active;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPublicPort() {
        return publicPort;
    }

    public void setPublicPort(int publicPort) {
        this.publicPort = publicPort;
    }

    public String getContainerId() {
        return containerId;
    }

    public void setContainerId(String containerId) {
        this.containerId = containerId;
    }

    public int getServerDefinitionVersion() {
        return serverDefinitionVersion;
    }

    public void setServerDefinitionVersion(int serverDefinitionVersion) {
        this.serverDefinitionVersion = serverDefinitionVersion;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Server{" +
                "name='" + name + '\'' +
                ", publicPort=" + publicPort +
                ", containerId='" + containerId + '\'' +
                ", serverDefinitionVersion=" + serverDefinitionVersion +
                ", status='" + status + '\'' +
                ", active=" + active +
                '}';
    }
}
