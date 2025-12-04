package ar.edu.itba.paw.webapp.config;

import ar.edu.itba.paw.webapp.auth.AuthUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@ComponentScan({"ar.edu.itba.paw.webapp.auth"})
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Configuration
public class WebAuthConfig extends WebSecurityConfigurerAdapter {
    @Autowired private AuthUserDetailsService authUserDetailsService;

    @Value("classpath:rememberMe.key")
    private Resource rememberMeKey;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        super.configure(auth);
        auth.userDetailsService(authUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.userDetailsService(authUserDetailsService);

        http.sessionManagement()
                .invalidSessionUrl("/landing")

            /*--- Authorizations ---*/
            /* All authorization is now handled by @PreAuthorize annotations on controller methods */
            .and().authorizeRequests().anyRequest().permitAll()

            /*--- LogIn ---*/
            .and().formLogin()
                .defaultSuccessUrl("/", false)
                .usernameParameter("email")
                .passwordParameter("password")
                .loginPage("/login")
            .and().rememberMe()
                .rememberMeParameter("rememberMe")
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))
                .key(readRememberMeKey())
                .userDetailsService(authUserDetailsService)

            /*--- LogOut ---*/
            .and().logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/home")

            /*--- Exceptions ---*/
            .and().exceptionHandling().accessDeniedPage("/403")

            /*--- Miscellaneous ---*/
            .and().csrf().disable();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    private String readRememberMeKey() {
        try {
            return new String (rememberMeKey.getInputStream().readAllBytes());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}
