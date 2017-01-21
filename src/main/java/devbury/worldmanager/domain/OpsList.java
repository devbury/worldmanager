package devbury.worldmanager.domain;

import java.util.Collections;
import java.util.List;

public class OpsList {
    private String listName;
    private List<String> userNames = Collections.emptyList();

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        listName = listName;
    }

    public List<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(List<String> userNames) {
        this.userNames = userNames;
    }
}
