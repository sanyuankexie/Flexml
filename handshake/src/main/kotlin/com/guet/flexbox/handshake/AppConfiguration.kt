package com.guet.flexbox.handshake

import com.google.gson.Gson
import com.guet.flexbox.handshake.lan.LANAddressProvider
import com.guet.flexbox.handshake.lan.MacOsLANAddressProvider
import com.guet.flexbox.handshake.lan.OtherLANAddressProvider
import com.guet.flexbox.handshake.ui.QrcodeForm
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.HttpMessageConverter
import org.springframework.http.converter.json.GsonHttpMessageConverter
import org.springframework.web.context.ContextLoader
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.awt.GraphicsEnvironment
import java.util.concurrent.ConcurrentHashMap

@Configuration
open class AppConfiguration : WebMvcConfigurer {

    private fun buildConfig(): CorsConfiguration {
        val corsConfiguration = CorsConfiguration()
        //  对所有的地址都可以访问
        corsConfiguration.addAllowedOrigin(CorsConfiguration.ALL)
        //  跨域的请求头
        corsConfiguration.addAllowedHeader(CorsConfiguration.ALL) // 2
        //  跨域的请求方法
        corsConfiguration.addAllowedMethod(CorsConfiguration.ALL) // 3

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

    private fun customGsonHttpMessageConverter(): GsonHttpMessageConverter? {
        val gson = ContextLoader.getCurrentWebApplicationContext()
                ?.getBean("gson") as? Gson ?: this.gosn()
        val gsonMessageConverter = GsonHttpMessageConverter()
        gsonMessageConverter.gson = gson
        return gsonMessageConverter
    }

    override fun configureMessageConverters(
            converters: MutableList<HttpMessageConverter<*>?>
    ) {
        converters.add(customGsonHttpMessageConverter())
    }

    @Bean
    open fun gosn(): Gson {
        return Gson()
    }

    @Bean
    open fun attributes(): ConcurrentHashMap<String, Any> {
        return ConcurrentHashMap()
    }

    @Bean
    open fun qrcodeForm(): QrcodeForm? {
        System.clearProperty("java.awt.headless")
        return if (!GraphicsEnvironment.isHeadless()) {
            QrcodeForm()
        } else {
            null
        }
    }

    @Bean
    open fun addressProvider(): LANAddressProvider {
        return if ((System.getProperty("os.name") ?: "")
                        .contains("mac", ignoreCase = true)) {
            MacOsLANAddressProvider()
        } else {
            OtherLANAddressProvider()
        }
    }
}