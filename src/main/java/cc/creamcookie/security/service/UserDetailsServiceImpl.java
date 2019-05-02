package cc.creamcookie.security.service;

import cc.creamcookie.security.dto.SignDetails;
import cc.creamcookie.security.entity.BaseAccount;
import cc.creamcookie.security.entity.BaseAccountRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

@Transactional(readOnly = true)
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    protected BaseAccountRepository accountRepository;

    @Autowired
    protected MessageSource messageSource;

    @Override
    public UserDetails loadUserByUsername(String arg0) throws UsernameNotFoundException {
        UserDetailsDto dto = this.getAccountWithData(arg0);
        if (dto == null) {
            throw new UsernameNotFoundException(messageSource.getMessage("security.username_not_found", new String[]{arg0}, Locale.getDefault()));
        }

        Collection<SimpleGrantedAuthority> roles = new ArrayList<SimpleGrantedAuthority>();
        roles.add(new SimpleGrantedAuthority("ROLE_USER"));
        if (dto.getEntity().getRoles() != null) {
            for (String role : dto.getEntity().getRoles()) {
                roles.add(new SimpleGrantedAuthority("ROLE_" + role));
            }
        }

        SignDetails user = new SignDetails(dto.getEntity().getSignname(), dto.getEntity().getPassword(), roles);
        user.setObject(dto.getData());
        return user;
    }

    public UserDetailsDto getAccountWithData(String signname) {

        BaseAccount account = accountRepository.findBySignname(signname);

        if (account != null) {

            JSONObject object = new JSONObject();
            object.put("id", account.getId());
            object.put("signname", account.getSignname());

            return new UserDetailsDto(account, object);
        }
        else {
            return null;
        }
    }


    @Getter
    @Setter
    @AllArgsConstructor
    public static class UserDetailsDto {
        private BaseAccount entity;
        private JSONObject data;
    }

}
