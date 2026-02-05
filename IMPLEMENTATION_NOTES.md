# Smart E-Commerce Application

A full-stack e-commerce application with Spring Boot backend and Next.js frontend.

## Features Implemented

### Backend Features

- ✅ **User Management**: Registration, login, profile management with JWT authentication
- ✅ **Product Management**: CRUD operations for products with category support
- ✅ **Category Management**: Organize products by categories
- ✅ **Inventory Management**: Track product stock levels
- ✅ **Order Management**: Create and manage customer orders
- ✅ **Shopping Cart**: Add/remove items, update quantities, checkout
- ✅ **Product Reviews**: Customers can rate and review products (1-5 stars)
- ✅ **Pagination**: All list endpoints support pagination
- ✅ **Error Handling**: Comprehensive error messages from backend to frontend
- ✅ **Performance Monitoring**: Database query metrics and cache statistics
- ✅ **Custom Sorting**: Products and orders support custom sorting algorithms

### Frontend Features

- ✅ **Product Browsing**: View products with category filtering
- ✅ **Product Details**: Detailed product view with reviews
- ✅ **Shopping Cart**: Full cart management with checkout
- ✅ **Reviews**: Add, view product reviews with star ratings
- ✅ **User Authentication**: Login/Register with JWT
- ✅ **Order History**: View past orders
- ✅ **Admin Panel**: Manage products, categories, inventory (for ADMIN users)
- ✅ **Error Handling**: User-friendly error messages

## Technology Stack

### Backend

- **Framework**: Spring Boot 4.0.1
- **Language**: Java 25
- **Database**: PostgreSQL (JDBC)
- **API**: REST + GraphQL
- **Authentication**: JWT Bearer Tokens
- **Documentation**: Swagger/OpenAPI

### Frontend

- **Framework**: Next.js 14 with TypeScript
- **Styling**: Tailwind CSS
- **State Management**: React Context API
- **API Client**: Fetch API with custom wrapper

## Database Schema

### Tables

1. **users** - User accounts with roles (CUSTOMER, ADMIN, SELLER)
2. **categories** - Product categories
3. **products** - Product catalog
4. **inventory** - Stock management
5. **orders** - Customer orders
6. **order_items** - Items in each order
7. **reviews** - Product reviews with ratings
8. **cart** - User shopping carts
9. **cart_items** - Items in shopping carts

## API Endpoints

### REST Endpoints

#### User Endpoints

- `POST /api/users/register` - Register new user
- `POST /api/users/login` - User login
- `GET /api/users/profile` - Get user profile
- `PUT /api/users/updateProfile` - Update profile
- `GET /api/users/all` - Get all users (Admin)

#### Product Endpoints

- `GET /api/products/public/all` - Get all products (with pagination, sorting, filtering)
- `GET /api/products/{id}` - Get product by ID
- `POST /api/products/add` - Add product (Admin)
- `PUT /api/products/update/{id}` - Update product (Admin)
- `DELETE /api/products/{id}` - Delete product (Admin)

#### Category Endpoints

- `GET /api/categories/public/all` - Get all categories
- `GET /api/categories/{id}` - Get category by ID
- `POST /api/categories/add` - Add category (Admin)
- `PUT /api/categories/update/{id}` - Update category (Admin)
- `DELETE /api/categories/{id}` - Delete category (Admin)

#### Cart Endpoints

- `GET /api/cart` - Get user's cart
- `POST /api/cart/add` - Add item to cart
- `PUT /api/cart/item/{itemId}` - Update cart item quantity
- `DELETE /api/cart/item/{itemId}` - Remove item from cart
- `DELETE /api/cart/clear` - Clear cart
- `POST /api/cart/checkout` - Checkout cart (creates order)

#### Review Endpoints

- `GET /api/reviews/all` - Get all reviews
- `GET /api/reviews/product/{productId}` - Get reviews for product
- `POST /api/reviews/add` - Add review
- `PUT /api/reviews/update/{id}` - Update review
- `DELETE /api/reviews/{id}` - Delete review

#### Order Endpoints

- `POST /api/orders/create` - Create order
- `GET /api/orders/all` - Get all orders (Admin)
- `GET /api/orders/user` - Get user's orders
- `GET /api/orders/{id}` - Get order by ID
- `PUT /api/orders/update/{id}` - Update order status (Admin)

### GraphQL Endpoint

- GraphQL endpoint: `http://localhost:8080/graphql`
- GraphiQL UI: `http://localhost:8080/graphiql`

## Setup Instructions

### Backend Setup

1. **Configure Database**:

   ```bash
   # Create PostgreSQL database
   createdb smartshop
   ```

2. **Update application-dev.properties**:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/smartshop
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Run the backend**:
   ```bash
   cd backend/SmartShop
   ./mvnw spring-boot:run
   ```

Backend will start on `http://localhost:8080`

### Frontend Setup

1. **Install dependencies**:

   ```bash
   cd frontend
   npm install
   ```

2. **Configure API URL** (optional):
   Create `.env.local`:

   ```
   NEXT_PUBLIC_API_URL=http://localhost:8080/api
   ```

3. **Run the frontend**:
   ```bash
   npm run dev
   ```

Frontend will start on `http://localhost:3000`

## Key Changes Made

1. **HashMap instead of ConcurrentHashMap**: Updated `CacheManager` and `PerformanceMonitoringAspect` to use `HashMap` with synchronized methods for thread safety.

2. **Product Name Type**: Verified that product name is of type `String` in the entity.

3. **Pagination**: Implemented throughout the backend with `Pageable` support in all list endpoints.

4. **Reviews System**:
   - Database schema with ratings (1-5) and comments
   - Full CRUD operations
   - Display on product detail page with star ratings

5. **Cart System**:
   - Persistent cart tied to user account
   - Add/remove items, update quantities
   - Checkout converts cart to order and clears cart

6. **Error Handling**:
   - Backend returns structured error messages
   - Frontend displays user-friendly error messages
   - Try-catch blocks throughout API calls

## User Roles

- **CUSTOMER**: Can browse products, add reviews, manage cart, create orders
- **SELLER**: Can manage products and inventory
- **ADMIN**: Full access to all features

## Testing

1. Register a new user with CUSTOMER role
2. Browse products and add to cart
3. View and manage cart
4. Checkout cart to create order
5. Add reviews to products
6. Admin users can manage products, categories, and inventory

## API Documentation

Swagger UI available at: `http://localhost:8080/swagger-ui/index.html`

## Performance Monitoring

Admin users can access performance metrics:

- `GET /api/performance/db-metrics` - Database query statistics
- `GET /api/performance/cache-metrics` - Cache hit/miss rates
