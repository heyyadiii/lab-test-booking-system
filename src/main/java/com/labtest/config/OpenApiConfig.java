package com.labtest.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
//import io.swagger.v3.oas.models.servers.Server;
//import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//import java.util.List;

@Configuration
public class OpenApiConfig {
    
  //  @Value("${server.port:8089}")
    //private String serverPort;
    
    @Bean
    public OpenAPI labTestBookingOpenAPI() {
        // Security scheme for JWT
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("JWT token for authentication. Format: Bearer {token}");
        
        // Security requirement
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("bearerAuth");
        
        return new OpenAPI()
                .info(new Info()
                        .title("Lab Test Booking System API")
                        .description("""
                                # Lab Test Booking & Sample Queue Management System
                                
                                A comprehensive REST API for managing laboratory test bookings, sample processing, 
                                and report generation with multi-laboratory support.
                                
                                ## Features
                                - 🔐 JWT-based authentication with role-based access control
                                - 👥 Three user roles: PATIENT, LAB_TECHNICIAN, ADMIN
                                - 🏥 Multi-laboratory support with location-based filtering
                                - 📅 Time slot management with capacity control
                                - 🧪 Sample state machine (BOOKED → COLLECTED → IN_TEST → COMPLETED → REPORTED)
                                - 📄 PDF report generation with professional formatting
                                - 🔄 Optimistic locking for concurrent booking management
                                - 🌐 CORS enabled for frontend integration
                                
                                ## Authentication
                                1. Register or login via `/api/auth/login` to get JWT token
                                2. Click "Authorize" button (🔓) at the top
                                3. Enter: `Bearer {your-token}`
                                4. All authenticated endpoints will now work
                                
                                ## Test Credentials
                                - **Patient**: username=`patient1`, password=`password123`
                                - **Technician**: username=`tech1`, password=`password123`
                                - **Admin**: username=`admin`, password=`password123`
                                
                                ## Workflow
                                1. **Admin** creates laboratories and time slots
                                2. **Patient** searches and books available slots
                                3. **Technician** processes samples through state machine
                                4. **Technician** uploads test reports
                                5. **Patient** views and downloads PDF reports
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("LabFlow Team")
                                .email("support@labflow.com")
                                .url("https://labflow.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
      /*          .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://api.labflow.com")
                                .description("Production Server (Example)")))
       */
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(securityRequirement);
    }
}
