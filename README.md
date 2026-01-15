# Backend Core Library

A reusable core library for Spring Boot microservices that provides common functionality including security, exception handling, and database configuration.

## Features

### Security
- **Token-based authentication** using Bearer tokens
- **Service discovery integration** (Eureka) to find authentication services
- **Stateless authentication** suitable for microservices
- Pre-configured `RestTemplate` with load balancing

### Exception Handling
- **Global exception handler** with consistent error responses
- **Custom exception classes**: `ResourceNotFoundException`, `ValidationException`, `ForbiddenException`
- **Error DTOs** for standardized API error responses
- Automatic HTTP status code mapping

### Database
- **JPA configuration** with transaction management
- Auto-configured transaction manager

## Project Structure

```
backend-core/
└── src/main/java/com/backend/core/
    ├── security/           # Authentication and security components
    │   ├── TokenAuthenticationFilter.java
    │   └── SecurityAutoConfiguration.java
    ├── exception/          # Exception handling
    │   ├── GlobalExceptionHandler.java
    │   ├── ResourceNotFoundException.java
    │   ├── ValidationException.java
    │   └── ForbiddenException.java
    ├── database/           # Database configuration
    │   └── JpaConfig.java
    └── dto/                # Data Transfer Objects
        ├── ErrorDto.java
        └── ErrorResponseDto.java
```

## Installation

### Build the Library

```bash
cd backend-core
./gradlew clean build
```

### Add as Dependency

In your service's `build.gradle`:

```gradle
dependencies {
    implementation files('../backend-core/build/libs/backend-core-1.0.0-plain.jar')
}
```

## Usage

### 1. Security Configuration

The library provides `TokenAuthenticationFilter` automatically. Configure Spring Security in your service:

```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### 2. Using Custom Exceptions

Import and use the provided exception classes:

```java
import com.backend.core.exception.ResourceNotFoundException;
import com.backend.core.exception.ValidationException;
import com.backend.core.exception.ForbiddenException;

@Service
public class UserService {
    public User getUser(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(id, "User"));
    }

    public void deleteUser(Long id, Long currentUserId) {
        if (!id.equals(currentUserId)) {
            throw new ForbiddenException("Not authorized to delete this user");
        }
        // ... delete logic
    }
}
```

### 3. Access Authenticated User

In your controllers:

```java
@GetMapping("/me")
public ResponseDto getCurrentUser(Authentication authentication) {
    Long userId = (Long) authentication.getPrincipal();
    return userService.getUser(userId);
}
```

## Auto-Configuration

The library auto-configures the following components:

### Security Components
- `TokenAuthenticationFilter` - JWT authentication filter
- `RestTemplate` - Load-balanced for service discovery
- `ObjectMapper` - JSON processing

### Exception Handling
- `GlobalExceptionHandler` - Handles all exceptions globally and returns standardized error responses

### Database
- `JpaConfig` - Transaction management configuration

## Exception Handling

The `GlobalExceptionHandler` automatically handles:

| Exception | HTTP Status | Description |
|-----------|-------------|-------------|
| `ForbiddenException` | 403 Forbidden | Access denied |
| `ValidationException` | 400 Bad Request | Validation errors |
| `ResourceNotFoundException` | 404 Not Found | Resource not found |
| `AuthenticationException` | 401 Unauthorized | Authentication failed |
| `MethodArgumentNotValidException` | 422 Unprocessable Entity | Request validation failed |
| `Exception` | 500 Internal Server Error | Generic errors |

### Error Response Format

All exceptions return a standardized error response:

```json
{
  "code": 404,
  "error": "Not Found",
  "message": "User resource with id 123 does not exist.",
  "path": "/v1/api/users/123",
  "timestamp": "2025-01-14T10:30:00Z",
  "details": []
}
```

## Requirements

- Java 17+
- Spring Boot 3.4.1+
- Spring Cloud 2024.0.0+
- Eureka server (for service discovery)
- Authentication service registered with Eureka
- Authentication service must have endpoint: `POST /v1/api/auth/validate-token`
  - Accepts: Bearer token in Authorization header
  - Returns: `{"userId": 123}`

## Integration Examples

### Newsfeed Service
```gradle
// build.gradle
dependencies {
    implementation files('../backend-core/build/libs/backend-core-1.0.0-plain.jar')
}
```

```java
// SecurityConfig.java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    // ... configuration
}
```

### Backend Users Management Service
```gradle
// build.gradle
dependencies {
    implementation files('../backend-core/build/libs/backend-core-1.0.0-plain.jar')
}
```

```java
// Using exceptions
import com.backend.core.exception.*;

@Service
public class FriendshipService {
    public void sendFriendRequest(Long fromUserId, Long toUserId) {
        if (fromUserId.equals(toUserId)) {
            throw new ValidationException("Cannot send friend request to yourself");
        }
        // ... logic
    }
}
```

## Development

To modify the library:

1. Make changes in `backend-core` project
2. Rebuild: `./gradlew clean build`
3. Dependent services will pick up changes on next build

## Migration from Separate Projects

If you're migrating from separate implementations:

1. Remove local exception classes
2. Remove local `JpaConfig`
3. Remove local `GlobalExceptionHandler`
4. Update imports to `com.backend.core.*`
5. Add backend-core dependency
6. Build and test

## License

Internal use only
