package cc.creamcookie.config.security;

import cc.creamcookie.security.dto.SignDetails;
import cc.creamcookie.security.entity.BaseAccount;
import cc.creamcookie.security.entity.BaseAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
@RequiredArgsConstructor
public class AuditConfig {

    private final BaseAccountRepository repository;

    @Bean
    public AuditorAware<BaseAccount> createAuditorProvider() {
        System.out.println("createAuditorProvider - BaseAccount");
        return new SecurityAuditor(repository);
    }

    @Bean
    public AuditingEntityListener createAuditingListener() {
        return new AuditingEntityListener();
    }

    public static class SecurityAuditor implements AuditorAware<BaseAccount> {
        private BaseAccountRepository repository;

        public SecurityAuditor(BaseAccountRepository repository) {
            System.out.println("createAuditorProvider - BaseAccount111");
            this.repository = repository;
        }

        @Override
        public Optional<BaseAccount> getCurrentAuditor() {
            System.out.println("createAuditorProvider - BaseAccount2222");
            try {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                SignDetails details = (SignDetails) auth.getPrincipal();
                if (details != null && details.getObject() != null) {
                    return repository.findById(details.getObject().getLong("id"));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return Optional.empty();
        }

    }

}
