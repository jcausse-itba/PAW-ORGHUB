package ar.edu.itba.paw.model;

import ar.edu.itba.paw.model.enums.Role;
import ar.edu.itba.paw.model.schemas.UserRoleSchema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor // Hibernate needs this
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = UserRoleSchema.TABLE_NAME)
@IdClass(UsersRolesCompositeID.class)
public class UsersRoles {

    @EqualsAndHashCode.Include
    @Id
    @ManyToOne(fetch = FetchType.LAZY) // OK
    @JoinColumn(name = UserRoleSchema.USER_ID, nullable = false)
    private User user;

    @EqualsAndHashCode.Include
    @Id
    @ManyToOne(fetch = FetchType.LAZY) //OK
    @JoinColumn(name = UserRoleSchema.ORG_ID, nullable = false)
    private Organization organization;

    @Setter
    @Enumerated(EnumType.ORDINAL)
    @Column(name = UserRoleSchema.ROLE_ID, nullable = false)
    private Role role;

    public UsersRoles(final User user, final Organization organization, final Role role ) {
        this.user = user;
        this.organization = organization;
        this.role = role;
    }
}
