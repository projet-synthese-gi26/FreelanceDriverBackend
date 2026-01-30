package com.yowyob.template.infrastructure.config;

import com.yowyob.template.domain.model.ProductStatus;
import com.yowyob.template.domain.model.TripType;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.postgresql.PostgresqlConnectionFactoryProvider;
import io.r2dbc.postgresql.codec.EnumCodec;
import io.r2dbc.postgresql.extension.CodecRegistrar;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class R2dbcConfig {

    @Value("${spring.r2dbc.url}")
    private String r2dbcUrl;

    @Value("${spring.r2dbc.username}")
    private String r2dbcUsername;

    @Value("${spring.r2dbc.password}")
    private String r2dbcPassword;

    @Bean
    public ConnectionFactory connectionFactory() {
        CodecRegistrar enumCodecs = EnumCodec.builder()
                .withEnum("product_status", ProductStatus.class)
                .withEnum("trip_type", TripType.class)
                .build();

        ConnectionFactoryOptions options = ConnectionFactoryOptions.parse(r2dbcUrl)
                .mutate()
                .option(ConnectionFactoryOptions.USER, r2dbcUsername)
                .option(ConnectionFactoryOptions.PASSWORD, r2dbcPassword)
                .build();

        PostgresqlConnectionConfiguration.Builder builder = PostgresqlConnectionFactoryProvider.builder(options);
        builder.extendWith(enumCodecs);

        return new PostgresqlConnectionFactory(builder.build());
    }
}
