# marche-conclu-shop-service

## Swagger/OpenAPI Documentation

### Accessing Swagger UI

When running locally with the `dev-local` profile:
```
http://localhost:5020/swagger-ui.html
```

### API Documentation Endpoints

- **Swagger UI**: `http://localhost:5020/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:5020/api-docs`
- **OpenAPI YAML**: `http://localhost:5020/api-docs.yaml`

### Adding Routes to Swagger

All REST endpoints are **automatically detected** by Swagger. Simply create or update controllers with standard Spring annotations:

```java
@RestController
@RequestMapping("/api/shops")
@Tag(name = "Shop", description = "Shop management APIs")
public class ShopController {

    @Operation(summary = "Get all shops")
    @GetMapping
    public List<Shop> getAllShops() {
        // Your code here
    }

    @Operation(summary = "Create a new shop")
    @PostMapping
    public Shop createShop(@RequestBody Shop shop) {
        // Your code here
    }
}
```

**Key annotations:**
- `@Tag` - Groups endpoints by category
- `@Operation` - Describes what the endpoint does
- `@ApiResponse` - Documents response codes

No additional configuration needed - Swagger automatically discovers all `@RestController` classes!