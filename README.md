Grabtitude – Aptitude Practice Platform

Grabtitude is a Spring Boot based API for practicing aptitude questions in the form of MCQs.
It supports two roles:

User → can register, solve problems, submit answers, manage their profile.

Admin → can create, update, and manage problems and topics.

The platform also includes authentication with email verification, password reset using OTP, and role-based access control.

🔧 Requirements

To run this project, you need:

Java 21

Maven 3+

MySQL (local or remote)

SendGrid account (for email/OTP)

Required Environment Variables

Set the following environment variables in your system or IDE before running:

DB_USERNAME → your MySQL username (e.g., root)

DB_PASSWORD → your MySQL password

SENDGRID_API_KEY → API key from SendGrid

SENDGRID_SENDER_MAIL → verified sender email address

Database URL is already configured in application.properties as:

jdbc:mysql://localhost:3306/grabtitude

🌐 API Endpoints
🔑 Authentication (/auth)

POST /auth/register-user → Register a new user.

POST /auth/register-admin → Register a new admin.

GET /auth/verify-email?token=... → Verify email using token.

POST /auth/send-otp → Send OTP for password reset.

PATCH /auth/forget-password → Reset forgotten password with OTP.

👑 Admin (/admin) – requires ADMIN role

POST /admin/problem/create → Create a new problem.

PUT /admin/problem/update → Update a problem.

DELETE /admin/problem/delete/{id} → Delete a problem.

POST /admin/topic/create → Create a topic.

GET /admin/topic/get/{id} → Get topic by ID.

GET /admin/topic/get-all?page=&size= → Get all topics (paginated).

PUT /admin/topic/update → Update a topic.

DELETE /admin/topic/delete/{id} → Delete a topic.

👤 User (/user)

DELETE /user/delete → Delete current user account.

PUT /user/update → Update user profile.

POST /user/submit → Submit an answer for a problem.

PATCH /user/reset-password → Reset password (logged-in users).

GET /user/ → User home page.

📚 Problems (/problems)

GET /problems/get/{id} → Get problem by ID.

GET /problems/get-problems?page=&size= → Get problems (paginated).

GET /problems/search?keyword=...&page=&size= → Search problems.

🏠 Root (/)

GET / → Redirects to /home.

GET /home → Home page view.

GET /profile/{id} → Get user profile by ID.

GET /get-user/{email} → Get user by email.

GET /get-user-id/{id} → Get user by ID.

⚠️ Error Handling

Returns 404 for missing resources.

Returns 400 for bad requests or validation errors.
