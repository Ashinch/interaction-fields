package com.interactionfields.common.config

import com.fasterxml.jackson.databind.Module
import org.ktorm.database.Database
import org.ktorm.jackson.KtormModule
import org.ktorm.support.mysql.MySqlDialect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import javax.sql.DataSource

/**
 * The configuration of Ktorm.
 *
 * @author Ashinch
 * @date 2021/07/31
 */
@Configuration
class KtormConfiguration(var dataSource: DataSource) {

    /**
     * Register the [Database] instance as a Spring bean.
     */
    @Bean
    fun database() = Database.connectWithSpringSupport(dataSource, dialect = MySqlDialect())

    /**
     * Register Ktorm's Jackson extension to the Spring's container
     * so that we can serialize/deserialize Ktorm entities.
     */
    @Bean
    fun ktormModule(): Module {
        return KtormModule()
    }
}
