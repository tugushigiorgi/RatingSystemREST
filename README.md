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
   git clone https://github.com/tugushigiorgi/RatingSystemREST.git
   cd RatingSystemRest
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

# Here is Screenshots

## Admin Pages 

<img width="1280" alt="Admin_RegistrationRequestsList" src="https://github.com/user-attachments/assets/917f4b9c-54f1-415f-a37e-3269c7516ee7" />
<img width="1280" alt="ADMIN_COMMENTREQUESTS" src="https://github.com/user-attachments/assets/816e9e04-d2d6-4ca5-ada1-cdcb07731e58" />
<img width="1280" alt="ADMIN_USERAPPROVERREVIEWS" src="https://github.com/user-attachments/assets/bd548b33-06e1-42a1-beed-67127f71bdf4" />
<img width="1280" alt="admin_SELLERS" src="https://github.com/user-attachments/assets/9e610ac0-140b-4402-a56c-1895a4a38716" />
<img width="1280" alt="ADMIN_USERAPPROVERREVIEWS" src="https://github.com/user-attachments/assets/b9cb8ff2-6393-451c-af3a-cba3abec0751" />

## Seller Profile Pages
<img width="1280" alt="PROFIEL_ADDEDGAMES" src="https://github.com/user-attachments/assets/7d2b8b0d-078c-4ec1-99ba-71003c31c495" />
<img width="1280" alt="PROFIEL_UPDATEPAGE" src="https://github.com/user-attachments/assets/b7ab03d5-846c-4c69-a7f3-4830a268eec0" />
<img width="1280" alt="Profile_addnewGame" src="https://github.com/user-attachments/assets/23e84ffe-086c-4b09-a432-cbdae9234884" />
<img width="1280" alt="PROFILE_DELETEGAME" src="https://github.com/user-attachments/assets/be26fbd9-f850-4f8e-b021-9cacff7a7b9a" />
<img width="1280" alt="PROFILE_MYREVIEWS" src="https://github.com/user-attachments/assets/eb69f4ef-eae9-4415-9dca-c1a5f7615386" />
<img width="1279" alt="PROFILE_UPDATEPAGE" src="https://github.com/user-attachments/assets/14ce5a5b-b0ce-4018-af42-350f0cba8a2a" />

## List specific seller and write review page
<img width="1280" alt="WriteUSerReview" src="https://github.com/user-attachments/assets/6ffc815d-6679-4e74-84e0-ff1db8197a34" />
<img width="1279" alt="WriteUSerReviewAdminApproval" src="https://github.com/user-attachments/assets/b602f1ec-8899-4a1d-9456-22f9747753cd" />

## Main page

<img width="1280" alt="MainPage" src="https://github.com/user-attachments/assets/a75ed4c7-c19d-4eb1-9793-1ae795bf968b" />
<img width="1280" alt="MainPage2" src="https://github.com/user-attachments/assets/1c503ebd-6f19-4287-87a8-87a2e488385d" />


## Login & Registration Pages with modals
<img width="1278" alt="Login_notYetapprovedByAdmin" src="https://github.com/user-attachments/assets/2a676fd2-d2bb-4092-bee0-a36425006d24" />
<img width="1280" alt="loginPage" src="https://github.com/user-attachments/assets/1a179772-d0b4-40db-b92f-b54483a32027" />
<img width="1280" alt="registerPage" src="https://github.com/user-attachments/assets/226eaaa0-8a76-4517-bb36-075a2c6375c0" />
<img width="1280" alt="ResetPasswordF" src="https://github.com/user-attachments/assets/fd195dcb-2700-4f94-9c10-df4379ba43c8" />
<img width="1280" alt="ResetPasswordModal" src="https://github.com/user-attachments/assets/987ea462-a093-4a99-94e3-fcf18f211f34" />
<img width="1280" alt="RegisterConfirmationCodeModal" src="https://github.com/user-attachments/assets/c2560360-a868-45f1-9d09-1f418cbf653c" />
<img width="1280" alt="PASSWORDRESETPAGE" src="https://github.com/user-attachments/assets/1e4f7898-629e-416f-b845-7e8807c8030e" />
<img width="1280" alt="EmailConfirmedModal" src="https://github.com/user-attachments/assets/d6393b23-25b4-4cbc-b276-e6fb73b613b2" />









