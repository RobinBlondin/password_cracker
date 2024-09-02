# Password Cracker

Password Cracker is a Spring Boot web application with a Thymeleaf front-end that allows users to create SHA-256 hashed password lists from large files containing words or passwords. The application efficiently processes unlimited-sized input files, hashes the contents, sorts the resulting hashes, and provides a user-friendly interface to search for specific hashes to retrieve their respective original passwords.

## Getting Started

### Prerequisites

Before you start, ensure you have the following installed:

- [Java 17+](https://www.oracle.com/java/technologies/javase-jdk17-downloads.html)
  
  Make sure to update build.gradle.kts with the version you run
  ```kotlin
  java {
	  toolchain {
		  languageVersion = JavaLanguageVersion.of(<your-java-version>)
	  }
  }
  ```
  
- [MySQL](https://dev.mysql.com/downloads/mysql/)
- A GitHub account for OAuth integration

### Database setup

1. **Create a MySQL Database**:

   You need to create a local MySQL database for the application to use. You can do this by running the following commands in your MySQL client:

   ```sql
   CREATE DATABASE password_cracker;
   CREATE USER 'root'@'localhost' IDENTIFIED BY 'your_password';
   GRANT ALL PRIVILEGES ON password_cracker.* TO 'root'@'localhost';
   FLUSH PRIVILEGES;
   ```
<br>

### Setup

1. **Clone the Repository**

   ```bash
   git clone https://github.com/yourusername/password_cracker.git
   cd password_cracker```

   

2. **Prepare the Passwords file**
   
   Create a file named `passwords.txt` inside the `/files` directory. This file should contain one password per line.
   ```bash
   /files
       passwords.txt
   ```

   Example `passwords.txt`:
   ```
   password1
   password2
   password3
   password4
   ``` 

   <br>

3. **Generate hashes**

   To generate a file `hashes.txt` containing each password and its corresponding hash, run the `HashPasswords` command-line runner withe the argument `hashPasswords`:
   
   **Unix:**
   ```bash
   ./gradlew bootRun --args='hashPasswords'
   ```
   **Windows:**
   ```bash
   gradlew.bat bootRun --args='hashPasswords'
   ``` 
   This will create a file named `hashes.txt` inside the `/files` directory. This may take a while depending on the size of the file.  

   <br>

4. **Sort the hashes**

   To sort the `hashes.txt` file by hash value, run the `SortHashes` command-line runner with the argument `sortHashes`:
   
   Unix:
    ```bash
    ./gradlew bootRun --args='sortHashes'
    ```
    Windows:
   ```bash
   gradlew.bat bootRun --args='sortHashes'
   ``` 
   

    This will create a sorted version of `hashes.txt` in the `/files` directory. This may take a while depending on the size of the original file. 

   <br>

5. **Configure the `.env` File**

   Create a `.env` file in the root directory of the project with the following structure:
    ```
    # Database
    SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/password_cracker
    SPRING_DATASOURCE_USERNAME=<your-database-username>
    SPRING_DATASOURCE_PASSWORD=<your-database-password>
    SPRING_JPA_HIBERNATE_DDL_AUTO=update
    SPRING_JPA_SHOW_SQL=true
    SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.MySQL8Dialect

    # GitHub OAuth
    SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_ID=<your-github-client-id>
    SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_CLIENT_SECRET=<your-github-client-secret>
    SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GITHUB_REDIRECT_URI=<your-github-redirect-uri>

    # Email Configuration
    SPRING_MAIL_HOST=smtp.example.com
    SPRING_MAIL_PORT=587
    SPRING_MAIL_USERNAME=fake_user
    SPRING_MAIL_PASSWORD=fake_password
    SPRING_MAIL_PROPERTIES_MAIL_SMTP_AUTH=true
    SPRING_MAIL_PROPERTIES_MAIL_SMTP_STARTTLS_ENABLE=true
    ```
    Replace the placeholder values with your actual database credentials and Github OAuth client details. If you just want to try the content of the application, leave the email values as is.(NOTE: Account registration will not work if you do).
    Otherwise replace them with your own mail values.

   <br>

7.  **Run the application**

   Finally, start the application.
   
   **Unix:**
   ```bash
   ./gradlew bootRun
   ```
   **Windows:**
   ```bash
   gradlew.bat bootRun
   ``` 
    
   Open your web browswer and navigate to `http://localhost:8080`. You can create an account or login using your GitHub account to access the password cracker functionality.

  

  


     
