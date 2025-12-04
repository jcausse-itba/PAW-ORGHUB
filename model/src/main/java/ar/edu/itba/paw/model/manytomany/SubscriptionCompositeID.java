package ar.edu.itba.paw.model.manytomany;

import ar.edu.itba.paw.model.Organization;
import ar.edu.itba.paw.model.User;
import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor // Hibernate needs this
@Getter
@Setter
@EqualsAndHashCode
public final class SubscriptionCompositeID implements Serializable {
    private User user;
    private Organization organization;
}
