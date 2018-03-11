package spring.boot.grunt.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.resource.CssLinkResourceTransformer;
import org.springframework.web.servlet.resource.ResourceResolver;
import org.springframework.web.servlet.resource.ResourceUrlProvider;
import org.springframework.web.servlet.resource.VersionResourceResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import spring.boot.grunt.helper.ResourceHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
//TODO:WebMvcConfigurerAdapter deprecated 확인.

    @Autowired
    private ResourceHelper resourceHelper;


    /*
    @Override public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/static/");
    }
    */

    @Override public void addResourceHandlers(ResourceHandlerRegistry registry) {
        ResourceResolver resolver = new VersionResourceResolver()
                .addContentVersionStrategy("/**");

        registry.addResourceHandler("/resources/**")
                .addResourceLocations("classpath:/static/")
                .resourceChain(false)
                .addResolver(resolver);
    }

    @Bean
    public FreeMarkerViewResolver freeMarkerViewResolver(ResourceUrlProvider urlProvider) {

        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();

        Map<String, Object> map = new HashMap<>();
        map.put("helper", resourceHelper);

        resolver.setAttributesMap(map);
        resolver.setContentType("text/html;charset=utf-8");
        resolver.setSuffix(".ftl");
        resolver.setExposeSpringMacroHelpers(true);

        return resolver;
    }
}
