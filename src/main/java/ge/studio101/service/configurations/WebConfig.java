package ge.studio101.service.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<IpAddressFilter> ipAddressFilter() {
        FilterRegistrationBean<IpAddressFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new IpAddressFilter());
        registrationBean.addUrlPatterns("/swagger-ui/index.html"); // или любой другой URL вашей Swagger страницы
        return registrationBean;
    }
}
