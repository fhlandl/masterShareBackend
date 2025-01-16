package toy.masterShareBackend.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import toy.masterShareBackend.util.IdUtil;

import java.time.LocalDateTime;

@Entity
@Table(name = "message")
@NoArgsConstructor
@Getter
public class Message {

    @Id
    @GeneratedValue
    @Column(name = "message_id")
    private Long id;

    @Column(name = "message_key", nullable = false, unique = true)
    private String messageKey = IdUtil.generateUniqueId();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @Column
    private String sender;

    @Column
    private String title;

    @Column
    private String content;

    @Column(nullable = false)
    private boolean opened = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_modified_at")
    private LocalDateTime lastModifiedAt;

    @Column(nullable = false)
    private boolean deleted = false;

    @Builder
    public Message(String sender, String title, String content) {
        this.sender = sender;
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

    public void open() {
        this.opened = true;
    }

    public void delete() {
        this.deleted = true;
    }
}
