package cc.creamcookie.jpa;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.DateType;

public class CCSMetadataBuilderContributor implements MetadataBuilderContributor {

    @Override
    public void contribute(MetadataBuilder metadataBuilder) {
        metadataBuilder.applySqlFunction("distance", new SQLFunctionTemplate(DateType.INSTANCE, "ACOS(SIN(?1) * SIN(?3) + COS(?1) * COS(?3) * COS(?4 - ?2))"));
    }

}