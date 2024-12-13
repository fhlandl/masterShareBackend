package toy.masterShareBackend.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "board")
@NoArgsConstructor
@Getter
public class Board {

    @Id
    @GeneratedValue
    private Long id;

    @OneToOne(mappedBy = "board")
    private User owner;

    @Column(name = "max_size")
    private Integer maxSize;

    @OneToMany(mappedBy = "board")
    private List<Message> messages = new ArrayList<>();

    @Builder
    public Board(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public void changeMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}
