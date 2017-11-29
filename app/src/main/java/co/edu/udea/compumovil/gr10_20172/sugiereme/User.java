package co.edu.udea.compumovil.gr10_20172.sugiereme;

/**
 * Created by Davquiroga on 28/11/2017.
 */

public class User {
    String id;
    Element elementVote;
    Category categoryVote;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Element getElementVote() {
        return elementVote;
    }

    public void setElementVote(Element elementVote) {
        this.elementVote = elementVote;
    }

    public Category getCategoryVote() {
        return categoryVote;
    }

    public void setCategoryVote(Category categoryVote) {
        this.categoryVote = categoryVote;
    }
}
