package co.edu.udea.compumovil.gr10_20172.sugiereme;

import java.util.List;

/**
 * Created by Davquiroga on 8/11/2017.
 */

public class Category {
    List<Element> elements;
    String title;
    String description;
    String image;

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
