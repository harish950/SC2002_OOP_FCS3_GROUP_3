# 🏗️ BTO Management System

A comprehensive command-line application for managing Build-To-Order (BTO) housing projects.

## 👥 Development Team  
**NTU SC2002 OOP FCS3 GROUP 3**  
- Jovan Ho Wei Quan  
- Ethan Chuah Jhein Kiat  
- Htet Moe Han  
- Gay Jun Kai, Brendan  
- Harish Raaj  

## 📋 Project Overview

The **BTO Management System** is designed to manage the entire lifecycle of 🏢 HDB (Housing & Development Board) BTO projects, from creation and listing to application processing and flat booking. It provides specialized interfaces for different user roles: Applicants, HDB Officers, HDB Managers, and Administrators.

This system was developed as part of the *SC2002 Object-Oriented Design & Programming* course at Nanyang Technological University.

## ✨ Key Features

### 🧍 For Applicants
- View available BTO projects filtered by eligibility criteria  
- Apply for BTO projects based on age and marital status requirements  
- Track application status (Pending/Successful/Unsuccessful/Booked)  
- Request application withdrawal  
- Submit, view, edit, and delete enquiries about projects  

### 👷 For HDB Officers
- Register to handle specific BTO projects  
- View project details and application statistics  
- Process flat bookings for successful applicants  
- Generate booking receipts  
- View and respond to project-specific enquiries  

### 🧑‍💼 For HDB Managers
- Create, edit, and delete BTO project listings  
- Toggle project visibility to applicants  
- Approve/reject HDB Officer registrations  
- Approve/reject BTO applications  
- Manage withdrawal requests  
- Generate various reports and statistics  
- View and respond to all enquiries  

### 🛡️ For Administrators
- View and filter all users by role  
- Create new users of any role  
- View system-wide statistics  
- Reset user passwords  

## 🏛️ System Architecture

The system follows an object-oriented design with a **3-layer architecture**:

1. `Entity Layer`: Core domain objects  
2. `Controller Layer`: Business logic  
3. `Boundary Layer`: User interfaces  
4. `Data Layer`: Data access objects  

## 🚀 How to Run

1. Ensure you have **Java JDK 8 or higher** installed  
2. Compile the project:  