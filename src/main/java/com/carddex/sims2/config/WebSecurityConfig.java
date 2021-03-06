package com.carddex.sims2.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.carddex.sims2.security.JwtAuthenticationEntryPoint;
import com.carddex.sims2.security.JwtAuthorizationTokenFilter;
import com.carddex.sims2.service.JwtUserDetailsService;

@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtAuthenticationEntryPoint unauthorizedHandler;

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	// Custom JWT based security filter
	@Autowired
	JwtAuthorizationTokenFilter authenticationTokenFilter;

	@Value("${jwt.header}")
	private String tokenHeader;

	@Value("${jwt.route.authentication.path}")
	private String authenticationPath;

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoderBean());
	}

	@Bean
	public PasswordEncoder passwordEncoderBean() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				// we don't need CSRF because our token is invulnerable
				.csrf().disable().exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
				// don't create session
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
				.antMatchers("core/api/**").permitAll()
				.antMatchers("core/api/**/**").permitAll()
				// Un-secure H2 Database
				.antMatchers("/h2-console/**/**").permitAll().anyRequest().authenticated();

		// disable page caching
		httpSecurity.headers().frameOptions().sameOrigin() // required to set for H2 else H2 Console will be blank.
				.cacheControl();
	}

	// @Override
	public void configure(WebSecurity web) throws Exception {

		//@formatter:off
		// AuthenticationTokenFilter will ignore the below paths
		web
		.ignoring().antMatchers(HttpMethod.POST, authenticationPath).and()
		.ignoring().antMatchers(HttpMethod.GET, authenticationPath).and()
		.ignoring().antMatchers(HttpMethod.GET, "core/api/modules").and()
		.ignoring().antMatchers(HttpMethod.POST, "core/api/modules").and()
		.ignoring().antMatchers(HttpMethod.GET, "core/api/modules/enabled").and()
		.ignoring().antMatchers(HttpMethod.GET, "core/api/permission/**")

		// allow anonymous resource requests
		.and().ignoring()
		.antMatchers(HttpMethod.GET, "/", "/*.html", "/**/**/**/**","/favicon.ico", "/**/*.html", "/**/*.css", "/**/*.js")
		.antMatchers(HttpMethod.POST, "/", "/*.html", "/**/**/**/**","/favicon.ico", "/**/*.html", "/**/*.css", "/**/*.js")

		// Un-secure H2 Database (for testing purposes, H2 console shouldn't be
		// unprotected in production)
		.and().ignoring().antMatchers("/h2-console/**/**");
		//@formatter:on

	}

}
