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
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "max_size")
    private Integer maxSize = 10;

    @OneToMany(mappedBy = "board")
    private List<Message> messages = new ArrayList<>();

    @Builder
    public Board(int maxSize) {
        this.maxSize = maxSize;
    }

    public void setOwner(User owner) {
        if (this.owner != null) {
            this.owner.getBoards().remove(this);
        }
        this.owner = owner;
        owner.getBoards().add(this);
    }

    public void changeMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}
