package cc.creamcookie.config.hibernate;

import org.hibernate.envers.DefaultRevisionEntity;
import org.springframework.data.repository.history.support.RevisionEntityInformation;

public class DefaultRevisionEntityInformation implements RevisionEntityInformation {

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.history.support.RevisionEntityInformation#getRevisionNumberType()
     */
    public Class<?> getRevisionNumberType() {
        return Integer.class;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.history.support.RevisionEntityInformation#isDefaultRevisionEntity()
     */
    public boolean isDefaultRevisionEntity() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.data.repository.history.support.RevisionEntityInformation#getRevisionEntityClass()
     */
    public Class<?> getRevisionEntityClass() {
        return DefaultRevisionEntity.class;
    }
}
