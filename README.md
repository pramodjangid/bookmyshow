# BookMyShow

BookMyShow is a web application that allows users to book tickets for movies. This project is built using Spring Boot and integrates various technologies such as Redis, Kafka, and SFTP server for enhanced functionality.

## Features
  
- **Movie Booking**: Users can browse available movies and book tickets for their preferred seats.

- **Cancellation**: Users can cancel their bookings, which updates the available tickets and reflect on redis database.

- **Integration with Redis**: Redis is used for caching frequently accessed data such as movie information and available tickets to improve performance.

- **Integration with Kafka**: Kafka is used for messaging between different components of the application. Events such as ticket bookings and cancellations are published to Kafka topics for processing.

- **Integration with SFTP Server**: The application supports file uploads and downloads using an SFTP server. This feature can be used for uploading movie trailers, posters, or other media files.

## Technologies Used

- **Spring Boot**: Framework for building Java-based web applications.
  
- **Redis**: In-memory data store used for caching and session management.

- **Kafka**: Distributed streaming platform for building real-time data pipelines and streaming applications.

- **SFTP Server**: Secure File Transfer Protocol server for uploading and downloading files securely.

## Installation

To run the BookMyShow application locally, follow these steps:

1. Clone the repository to your local machine:

   ```bash
   git clone https://github.com/yourusername/bookmyshow.git
