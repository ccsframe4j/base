package cc.creamcookie.security.entity;

import org.springframework.data.jpa.datatables.repository.DataTablesRepository;

public interface BaseAccountRepository extends DataTablesRepository<BaseAccount, Long> {

    BaseAccount findBySignname(String signname);

}
