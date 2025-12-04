package ar.edu.itba.paw.model;

import lombok.*;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor // Hibernate needs this
@Getter
@Setter
@EqualsAndHashCode
public final class UsersRolesCompositeID implements Serializable {
    private User user;
    private Organization organization;
}
