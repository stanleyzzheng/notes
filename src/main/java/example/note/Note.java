//package example.note;
//
//import org.springframework.data.annotation.Id;
//
//record Note(@Id Long id, String title, String description){
//    public Note(Long id, String title, String description){
//        this.id=id;
//        this.title=title;
//        this.description=description;
//    }
//}

package example.note;

import jakarta.persistence.*;

import java.util.Objects;

@Entity

public class Note {

//    @Id

    private @Id @GeneratedValue Long id;

    private String title;
    private String description;
    private String owner;

    public Note() {
        // Default constructor required by JPA
    }

    public Note(Long id, String title, String description, String owner) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.owner = owner;
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, title, description);
//    }
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        Note note = (Note) o;
//
//        if (!Objects.equals(id, note.id)) return false;
//        if (!Objects.equals(title, note.title)) return false;
//        return Objects.equals(description, note.description);
//    }

// Getters and setters

    // Additional methods if needed

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note = (Note) o;
        return Objects.equals(id, note.id) && Objects.equals(title, note.title) && Objects.equals(description,
                note.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description);
    }
}