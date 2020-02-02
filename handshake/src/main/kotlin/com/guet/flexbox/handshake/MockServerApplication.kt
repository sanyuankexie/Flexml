package com.guet.flexbox.handshake

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter

@Configuration
@SpringBootApplication
open class MockServerApplication {
    private fun buildConfig(): CorsConfiguration {
        val corsConfiguration = CorsConfiguration()
        //  你需要跨域的地址  注意这里的 127.0.0.1 != localhost
        // * 表示对所有的地址都可以访问
        corsConfiguration.addAllowedOrigin("*")
        //  跨域的请求头
        corsConfiguration.addAllowedHeader("*") // 2
        //  跨域的请求方法
        corsConfiguration.addAllowedMethod("*") // 3
        //加上了这一句，大致意思是可以携带 cookie
        //最终的结果是可以 在跨域请求的时候获取同一个 session
        corsConfiguration.allowCredentials = true
        return corsConfiguration
    }

    @Bean
    open fun corsFilter(): CorsFilter {
        val source = UrlBasedCorsConfigurationSource()
        //配置 可以访问的地址
        source.registerCorsConfiguration("/**", buildConfig()) // 4
        return CorsFilter(source)
    }
}

