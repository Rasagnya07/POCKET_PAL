# PocketPal – Personal Finance Manager (Java Swing)

PocketPal Swing is a desktop-based personal finance application built entirely in **Java (Swing)**.  
It includes **Login/Sign-up**, **Expense Tracking**, **Needs vs Wants Classification**,  
**Budget Monitoring**, **Badge Progression**, **Weekly/Monthly Reports**,  
and a clean **EMI Calculator** — all with a pastel teal UI theme.

This project uses **Object-Oriented Programming**, **Encapsulation**, **File Handling**,  
**CSV storage**, and **Modular Java classes**, making it ideal for academic submission and real use.

---

## Features

### Login / Signup System
- Each user gets a **separate CSV file** storing:
  - Profile details  
  - All expenses  
  - Budget limits  
  - Badge progress  

### Expense Management
- Add, view, and classify expenses (Needs/Wants)
- Auto-detection of overspending
- Overspending shows:
  - Pop-up warning  
  - Dashboard alert  
  - Optional badge penalty  

### Weekly & Monthly Reports
- Auto-generated insights:
  - Total spending  
  - Needs vs Wants breakdown  
  - Category-wise analysis  
- Exportable CSV Summary (1 click)

### EMI Calculator
- Built-in calculator for:
  - Principal  
  - Interest Rate  
  - Duration (months)  
- Outputs monthly EMI using standard formula

### Budget & Limits
- Set category-wise budgets  
- Visual feedback on progress  
- Alerts when overspending happens  

### Badge System
Badges for:
- Saving consistently  
- Managing budgets  
- Reducing wants  
- Maintaining streaks  

Each badge shows:
- Progress bar  
- Unlock status  
- Auto-suggestions to improve

### Modern Pastel Teal UI
- Clean, minimal layout  
- Consistent typography  
- Soft teal color palette  
- Responsive Swing dialogs  

### File Persistence (CSV)
All data is stored in a **single CSV per user**, including:
- expenses  
- budgets  
- badges  
- profile  

This ensures:
- Easy debugging  
- Transparent data  
- Professor-friendly structure  

---

## Technology Stack

| Component | Technology |
|----------|------------|
| Interface | Java Swing |
| Language | Java 21 |
| Storage | CSV File System |
| Paradigm | Object-Oriented Programming |
| IDE | VS Code (recommended) |
| Design | Encapsulation, Modular Classes |

---

## Project Structure:

- PocketPalSwing.java          
- Expense.java                 
- NeedsWantsClassifier.java    
- Budget.java                  
- BudgetManager.java           
- Badge.java                   
- BadgeManager.java            
- BadgeThresholds.java         
- UserStorage.java             
- EMICalculator.java           
- Theme.java                   
- UtilsFileHelpers.java        
- ConsoleColors.java           
- Persistable.java
- ReflectionDemo.java

## How to Run

-Open VS Code or Command Prompt
-Go to your project directory:
cd C:\Users\App\pocketpalSwing
-Compile all files
javac *.java
-Run the GUI
java PocketPalSwing
-The application window will open instantly.

## Learning Outcomes
-Understanding of real-world finance management problem
-Designing a modular and scalable solution
-Applying OOP principles (Encapsulation, Constructors, Methods)
-Implementing file handling using CSV
-Building Java Swing GUI with events, dialogs, and navigation
-Testing, debugging, and validating user inputs

## Creators
Rasagnya M, Parinita T, Hasini K
Engineering Students – Academic Project






