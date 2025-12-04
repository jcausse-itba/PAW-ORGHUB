package ar.edu.itba.paw.model.manytomany;

import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;
import ar.edu.itba.paw.model.schemas.SubscriptionSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = SubscriptionSchema.TABLE_NAME)
@IdClass(SubscriptionCompositeID.class)
public class Subscription {

    @EqualsAndHashCode.Include
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = SubscriptionSchema.USER_ID, nullable = false)
    private User user;

    @EqualsAndHashCode.Include
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = SubscriptionSchema.ORG_ID, nullable = false)
    private Organization organization;

    public Subscription(final User user, final Organization organization) {
        this.user = user;
        this.organization = organization;
    }
}
