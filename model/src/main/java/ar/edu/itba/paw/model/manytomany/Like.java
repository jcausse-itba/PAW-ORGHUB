package ar.edu.itba.paw.model.manytomany;

import ar.edu.itba.paw.model.Post;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.schemas.LikeSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = LikeSchema.TABLE_NAME)
@IdClass(LikeCompositeID.class)
public class Like {

    @EqualsAndHashCode.Include
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = LikeSchema.USER_ID, nullable = false)
    private User user;

    @EqualsAndHashCode.Include
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = LikeSchema.POST_ID, nullable = false)
    private Post post;

    public Like(final User user, final Post post) {
        this.user = user;
        this.post = post;
    }
}
