package cc.creamcookie.jpa;

import org.hibernate.boot.MetadataBuilder;
import org.hibernate.boot.spi.MetadataBuilderContributor;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.type.DateType;

public class CCSMetadataBuilderContributor implements MetadataBuilderContributor {

    @Override
    public void contribute(MetadataBuilder metadataBuilder) {

        String distanceQuery = "111.111" +
                " * DEGREES(ACOS(LEAST(COS(RADIANS(?1)) " +
                " * COS(RADIANS(?3))\n" +
                " * COS(RADIANS(?2 - ?4))\n" +
                " + SIN(RADIANS(?1))\n" +
                " * SIN(RADIANS(?3)), 1.0)))";


        metadataBuilder.applySqlFunction("distance",
                new SQLFunctionTemplate(DateType.INSTANCE, distanceQuery));

    }

}