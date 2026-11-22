package com.catts.backend.database.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;

@Entity
@Table(name = "roles_entity")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class RolesEntity implements GrantedAuthority {

    @Id
    private Long id;

    private String name;

    @Override
    public @Nullable String getAuthority() {
        return name;
    }
}