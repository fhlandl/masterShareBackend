package toy.masterShareBackend.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@NoArgsConstructor
@Getter
public class Message {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column
    private String title;

    @Column
    private String content;

    @Column(name = "is_opened", nullable = false)
    private Boolean isOpened = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Builder
    public Message(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void setBoard(Board board) {
        if (this.board != null) {
            this.board.getMessages().remove(this);
        }
        this.board = board;
        board.getMessages().add(this);
    }

    public void setAuthor(User author) {
        if (this.author != null) {
            this.author.getMessages().remove(this);
        }
        this.author = author;
        author.getMessages().add(this);
    }

    public void changeLastModifiedAt(LocalDateTime lastModifiedAt) {
        this.lastModifiedAt = lastModifiedAt;
    }
}
