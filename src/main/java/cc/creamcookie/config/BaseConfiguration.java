package cc.creamcookie.config;

import cc.creamcookie.views.BaseInterceptor;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Conditions;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

@Configuration
@RequiredArgsConstructor
public class BaseConfiguration implements WebMvcConfigurer {

    private final Properties properties;
    private final BaseInterceptor baseInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
        registry.addInterceptor(baseInterceptor);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD")
                .allowCredentials(true);
    }


    @Bean
    public ReloadableResourceBundleMessageSource messageSource() {
        String[] baseNames = new String[properties.getLanguages().size()];
        for (int i = 0; i < properties.getLanguages().size(); i++) {
            baseNames[i] = "classpath:/" + properties.getLanguages().get(i);
        }
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setBasenames(baseNames);
        return messageSource;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang");
        return localeChangeInterceptor;
    }

    @Configuration
    @RequiredArgsConstructor
    public static class MessageExtraConfig {

        private final MessageSource messageSource;

        @Bean("messageSourceAccessor")
        public MessageSourceAccessor getMessageSourceAccessor() {
            return new MessageSourceAccessor(messageSource);
        }

        @Bean
        public LocaleResolver localeResolver() {
            CookieLocaleResolver localeResolver = new CookieLocaleResolver();
            localeResolver.setCookieName("i18n");
            localeResolver.setCookieMaxAge(-1);
            localeResolver.setCookiePath("/");
            return localeResolver;
        }

    }


    @Bean
    @ConditionalOnMissingBean(name = "modelMapper")
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setPropertyCondition(Conditions.isNotNull())
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setFieldMatchingEnabled(true);
        return modelMapper;
    }




}
