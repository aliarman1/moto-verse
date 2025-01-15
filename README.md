# MotoVerse - Motorcycle Sales Management System

MotoVerse is a Java-based desktop application for managing motorcycle sales, built using Java Swing for the GUI. The system provides different interfaces for administrators, registered users, and guests.

## Features

### Admin Features
- **User Management**
  - View all registered users
  - Delete users
  - Refresh user list

- **Bike Management**
  - Add new bikes with details (brand, model, year, price, condition, stock, description)
  - Update existing bikes
  - Delete bikes
  - View all bikes in inventory
  - Manage stock levels

- **Order Management**
  - View all orders from all users
  - Mark orders as completed
  - Cancel orders
  - Track order status

### User Features
- **Bike Shopping**
  - View available bikes
  - Filter bikes by availability (All/In Stock/Out of Stock)
  - Add bikes to cart
  - Specify purchase quantity

- **Shopping Cart**
  - View items in cart
  - Remove items
  - Clear cart
  - View total price
  - Checkout functionality

- **Order Management**
  - View order history
  - Track order status
  - Cancel pending orders

### Guest Features
- View available bikes
- See bike availability status (Available/Stock Out)
- Browse bike catalog without login

## Technical Details

### File Structure
- `Login.java` - Main entry point and login interface
- `Register.java` - User registration interface
- `AdminFrame.java` - Admin dashboard and management interface
- `UserFrame.java` - User shopping and order management interface
- `GuestFrame.java` - Guest view interface
- `Order.java` - Order data model

### Data Storage
- `registration_data.txt` - Stores user account information
- `bikes_data.txt` - Stores bike inventory data
- `orders_data.txt` - Stores order information

## Setup Instructions

1. Ensure you have Java Development Kit (JDK) installed
2. Clone the repository
3. Compile the Java files:
   ```bash
   javac *.java
   ```
4. Run the application:
   ```bash
   java Login
   ```

## Usage Guide

### Admin Access
- Username: "admin"
- Password: "admin"
- Full access to all management features

### User Access
1. Register a new account
2. Login with registered credentials
3. Browse bikes, manage cart, and place orders

### Guest Access
- Click "Guest" button on login screen
- Browse available bikes without login

## Security Features
- Password validation
- User authentication
- Data validation for all inputs
- Stock level verification
- Order status tracking

## Data Validation
- Email must end with "@gmail.com"
- Phone number must be 11 digits
- Password minimum length: 4 characters
- Price must be greater than 0
- Stock cannot be negative
- Year must be between 1900 and current year + 1

## Error Handling
- File operation error handling
- Input validation
- Stock management
- Order processing validation
- User-friendly error messages

## Contributing
This project is part of [Your Organization/Course]. For major changes, please open an issue first to discuss what you would like to change.

