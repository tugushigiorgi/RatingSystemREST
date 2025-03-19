# Game Marketplace

## Project Overview
This project is a **Game Marketplace** platform that connects game sellers with buyers. The platform has two distinct user views: **Admin View** and **Sellers View**. Sellers can list, update, and manage their games, while admins have control over user registrations, review approvals, and overall platform moderation.

## Features
### Admin View
- **Seller Registrations Management**: Admins can view and approve or deny seller registration requests after email verification.
- **Review Moderation**: Admins can see submitted comments (reviews) for sellers from anonymous users and decide whether to approve or reject them.
- **Seller and Review Management**: Admins can see specific registered users (approved sellers) and view the reviews that have already been approved for those sellers.

### Sellers View
- **Game Management**:
  - Add new games to the platform.
  - Update game details (title, description, price, photo etc.).
  - Remove games from their listing.

### General Features
- **Main Page (Index)**:
  - Displays the **Top 5 Rated Sellers**, determined by the highest-rated (Average) approved reviews.
  - Enables users to **search for game objects** based on title and also filter by Seller Rating.
  - Allows users to view all games listed by a specific seller.
- **Authentication & Authorization**:
  - Users must **register and confirm their email** via a verification link sent to their inbox.
  - Admins must **approve or deny** new seller registration requests.
  - Approved sellers can sign in and manage their profiles.
  - Users can **reset their passwords** via an email reset link if needed.
- **Profile Page**:
  - Sellers can **view reviews** written about them.
  - Sellers can **change their password** securely.
  - Sellers can **list and manage all their uploaded games**.

## Setup Instructions

### 1. Configure Email Sending
To enable email functionality for account verification and password recovery, set up email credentials in `application.properties`:
```properties
spring.mail.username=your-email@example.com
spring.mail.password=your-email-password
```

### 2. Set Up File Upload Directory
Create a folder on your system where uploaded game objects will be stored.(folder is generated for each seller, which includes 2 folder - Images for game obejct and Profile - for profile photo) Then, define this directory in `application.properties`:
```properties
file.upload-dir=/path/to/upload-directory
```

### 3. Set Up MySQL Database with Docker
The project uses **MySQL** as the database, running inside a **Docker container**. To set it up, ensure **Docker** is installed and running,got to root directory of this project  then execute:
```sh
docker-compose up --build -d
```
This will automatically pull and start a MySQL container with the required configuration.

## Technologies Used
- **Spring Boot** - Backend framework for building the application.
- **Spring Security** - Handles authentication and authorization with JWT.
- **MySQL** - Relational database for storing game data, users, and reviews.
- **Docker** - Used to containerize the MySQL database for easy setup and deployment.
- **Spring Mail** - Handles email verification and password reset emails.
- **Spring Data JPA** - Simplifies database interactions with repositories.
- **React** - Frontend github link - https://github.com/tugushigiorgi/RatingAppFrontend.

## How to Run
1. **Clone the repository**:
   ```sh
   git clone https://github.com/your-username/game-marketplace.git
   cd game-marketplace
   ```
2. **Configure `application.properties`** with your email settings, file storage path, and database connection details.
3. **Start the MySQL database container**:
   ```sh
   docker-compose up --build -d
   ```
4. **Access the application** in your browser at:
   ```
   http://localhost:8080
   ```

## Database Scheme 
![Entities drawio](https://github.com/user-attachments/assets/ba2ed1a0-cd4e-4e65-84e7-8b2ccc9cf96b)

##Here is Screenshots

<img width="1279" alt="WriteUSerReviewAdminApproval" src="https://github.com/user-attachments/assets/1f779c41-2b86-46b5-836f-aa8220e6c286" />
<img width="1280" alt="WriteUSerReview" src="https://github.com/user-attachments/assets/860b4625-afa7-4cbc-89fd-eea8411535c3" />
<img width="985" alt="RESETPASWORDEMAIL" src="https://github.com/user-attachments/assets/d62afd90-487f-430d-8933-c63bcfe818c4" />
<img width="1280" alt="ResetPasswordModal" src="https://github.com/user-attachments/assets/5d20f3de-4daa-42bc-8f21-c2d04dbfd11d" />
<img width="1280" alt="ResetPasswordF" src="https://github.com/user-attachments/assets/eeeb877e-6cc5-44e2-85eb-d99f9eff89f9" />
<img width="1280" alt="registerPage" src="https://github.com/user-attachments/assets/ce540058-f182-472a-a978-e992b136a90a" />
<img width="1280" alt="RegisterConfirmationCodeModal" src="https://github.com/user-attachments/assets/d4c6cb92-96d8-493d-8861-9cae53686417" />
<img width="1279" alt="PROFILE_UPDATEPAGE" src="https://github.com/user-attachments/assets/04052d92-9049-4963-ae48-dc930f3027f6" />
<img width="1280" alt="PROFILE_MYREVIEWS" src="https://github.com/user-attachments/assets/316f9b5b-06f0-4b9e-94f1-54fc23d1195b" />
<img width="1280" alt="PROFILE_DELETEGAME" src="https://github.com/user-attachments/assets/2abfdd05-2057-4c93-be83-8a85bad47367" />
<img width="1280" alt="Profile_addnewGame" src="https://github.com/user-attachments/assets/7b065b24-6b52-436f-aba5-b5c9609179ca" />
<img width="1280" alt="PROFIEL_UPDATEPAGE" src="https://github.com/user-attachments/assets/13450043-34f0-483f-a237-45566294c6f4" />
<img width="1280" alt="PROFIEL_ADDEDGAMES" src="https://github.com/user-attachments/assets/addab478-0025-4468-a278-f4e1b3b6b72b" />
<img width="1280" alt="PASSWORDRESETPAGE" src="https://github.com/user-attachments/assets/56ac8cc5-b17c-4dfb-a39d-29b8a2d3e70b" />
<img width="1280" alt="MainPage2" src="https://github.com/user-attachments/assets/ac9f5e1f-9ee0-47af-ba16-9e3a15e4807e" />
<img width="1280" alt="MainPage" src="https://github.com/user-attachments/assets/bbd45f7f-0522-4b18-b9f9-5bd040b2f17d" />
<img width="1280" alt="loginPage" src="https://github.com/user-attachments/assets/d91c7250-51bc-4cc2-8fdd-a3ca4af6e4aa" />
<img width="1278" alt="Login_notYetapprovedByAdmin" src="https://github.com/user-attachments/assets/46d05db8-a45a-4b19-bcd5-403c9c415df0" />
<img width="1280" alt="EmailConfirmedModal" src="https://github.com/user-attachments/assets/7aaa9692-0da9-4c2a-9eea-3dcbf0abdabb" />
<img width="1280" alt="ADMIN_USERAPPROVERREVIEWS" src="https://github.com/user-attachments/assets/476a2c1a-5ca0-4f6a-9774-25c146efb8c8" />
<img width="1280" alt="admin_SELLERS" src="https://github.com/user-attachments/assets/dded6024-4fe4-4d1b-87ba-bd32c93a772a" />
<img width="1280" alt="Admin_RegistrationRequestsList" src="https://github.com/user-attachments/assets/b9ed8aef-2806-4d35-aea8-b2729db3099d" />
<img width="1280" alt="ADMIN_COMMENTREQUESTS" src="https://github.com/user-attachments/assets/4863e065-2dae-465f-b0d0-58528402dc50" />






