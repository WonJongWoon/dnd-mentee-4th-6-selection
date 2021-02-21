package com.selection.domain.user;

import com.selection.domain.BaseEntity;
import com.selection.security.oauth.AuthProvider;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "email")
})
@Getter
public class User extends BaseEntity {

    @Email
    @Column(nullable = false)
    private String email;

    @NotNull
    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public User(@Email String email,
        @NotNull AuthProvider provider, Role role) {
        this.email = email;
        this.provider = provider;
        this.role = role;
    }

    public String getRoleAuthority() {
        return this.role.getAuthority();
    }
}
