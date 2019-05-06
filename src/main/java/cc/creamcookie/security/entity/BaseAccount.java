package cc.creamcookie.security.entity;

import cc.creamcookie.config.hibernate.ListAttributeConverter;
import cc.creamcookie.jpa.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Where;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@SuppressWarnings("serial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity(name = "account")
@Table(name = "account")
@Audited
@AuditOverride(forClass = BaseEntity.class)
public abstract class BaseAccount extends BaseEntity {

    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Include
    protected String signname;

    @Column(nullable = false)
    protected String password;

    @Lob
    @Convert(converter = ListAttributeConverter.class)
    @EqualsAndHashCode.Include
    protected List<String> roles;

    protected boolean active = true;

    protected LocalDateTime lastSignedAt;

}
