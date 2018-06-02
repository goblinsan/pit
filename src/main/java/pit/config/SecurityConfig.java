package pit.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private RESTAuthenticationEntryPoint authenticationEntryPoint;
    @Autowired
    private RESTAuthenticationSuccessHandler authenticationSuccessHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().antMatchers("/", "/css/**").permitAll();
        http.authorizeRequests().antMatchers("/player/**", "/admin/**").authenticated();
        http.cors();
        http.csrf().disable();
        http.exceptionHandling().authenticationEntryPoint(authenticationEntryPoint);
        http.formLogin().successHandler(authenticationSuccessHandler);
        http.formLogin().failureHandler(new SimpleUrlAuthenticationFailureHandler());
        http.httpBasic();
    }

    @Bean
    @Override
    public UserDetailsService userDetailsService() {
        UserDetails james = User.withDefaultPasswordEncoder().username("JAMES").password("password").roles("USER", "ADMIN").build();
        UserDetails luke = User.withDefaultPasswordEncoder().username("LUKE").password("password").roles("USER").build();
        UserDetails mason = User.withDefaultPasswordEncoder().username("MASON").password("password").roles("USER").build();
        UserDetails dani = User.withDefaultPasswordEncoder().username("DANI").password("password").roles("USER").build();
        UserDetails will = User.withDefaultPasswordEncoder().username("WILL").password("password").roles("USER").build();
        UserDetails kimi = User.withDefaultPasswordEncoder().username("KIMI").password("password").roles("USER").build();
        UserDetails chico = User.withDefaultPasswordEncoder().username("CHICO").password("password").roles("USER").build();
        UserDetails debbie = User.withDefaultPasswordEncoder().username("DEBBIE").password("password").roles("USER").build();
        UserDetails owen = User.withDefaultPasswordEncoder().username("OWEN").password("password").roles("USER").build();

        UserDetails admin = User.withDefaultPasswordEncoder().username("admin").password("chase123").roles("ADMIN").build();

        return new InMemoryUserDetailsManager(james, luke, mason, dani, will, kimi, chico, debbie, owen, admin);
    }

}
