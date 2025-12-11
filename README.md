# 🚖 SmartRideShare

A full-featured backend application for a ride-sharing platform connecting passengers with drivers. Built with **Java Spring Boot**, this project demonstrates scalable RESTful API design, secure payment processing, and real-time data handling.

## 🚀 Key Features

* **User Management:** Secure registration and login for Riders and Drivers using JWT Authentication.
* **Ride Booking System:** Logic to request rides, calculate fares based on distance/time, and match drivers.
* **Payment Integration:** Fully integrated with **Stripe API** for secure and seamless transaction processing.
* **Driver Dashboard:** API endpoints for drivers to accept/decline rides and view earnings.
* **Trip History:** Persisted storage of past rides and transaction details.

## 🛠️ Tech Stack

* **Language:** Java 17+
* **Framework:** Spring Boot 3.x
* **Database:** MySQL / PostgreSQL (Update based on what you used)
* **Security:** Spring Security & JWT
* **Payments:** Stripe API
* **Build Tool:** Maven

## ⚙️ Setup & Installation

1.  **Clone the repository**
    ```bash
    git clone [https://github.com/Manik31Raj/SmartRideShare.git](https://github.com/Manik31Raj/SmartRideShare.git)
    cd SmartRideShare
    ```

2.  **Configure Database**
    * Create a database named `smartrideshare_db`.
    * Update `src/main/resources/application.properties` with your database credentials.

3.  **Configure Environment Variables**
    * Add your Stripe Secret Key in `application.properties` (or set as an environment variable):
        ```properties
        stripe.key=${STRIPE_API_KEY}
        ```

4.  **Run the Application**
    ```bash
    mvn spring-boot:run
    ```





---
*This project is for educational and portfolio purposes.*
