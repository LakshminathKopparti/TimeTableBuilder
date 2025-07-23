# TimeTableBuilder

**TimeTableBuilder** is a Java-based academic timetable management system developed as part of the Object-Oriented Programming Systems (OOPS) course at BITS Pilani. The application helps automate and streamline the process of creating, editing, and validating academic timetables, with a focus on conflict detection and adherence to BITS-specific scheduling policies.

---

## Technologies Used

- **Frontend:** Java Swing (GUI)
- **Backend:** Java (Core Java, Collections, File I/O)
- **Architecture:** Model-View-Controller (MVC)
- **Persistence:** CSV files and custom file formats

---

## Features

- **Classroom, Course, and Instructor Management**
  - Add, edit, and delete classrooms, courses, and instructors
  - Assign courses to instructors and manage classroom facilities

- **Manual Timetable Creation**
  - Interactive GUI for scheduling lectures and labs
  - Real-time conflict detection for classrooms, instructors, and courses

- **Automatic Timetable Suggestions**
  - Generate multiple valid timetable suggestions based on constraints
  - Navigate between suggestions and save preferred schedules

- **BITS Policy Compliance**
  - Enforces BITS Pilani rules such as day gaps between lectures/labs and time slot preferences
  - Validates timetables for policy adherence

- **Import/Export**
  - Import and export data in CSV format
  - Export timetables to CSV, HTML, or JSON for sharing and reporting

- **User Preferences**
  - Customizable settings for time format, conflict warnings, and policy enforcement

---

## Getting Started

### Prerequisites

- Java JDK 8 or higher
- IntelliJ IDEA (recommended) or any Java IDE

### Setup

1. **Clone the repository:**
git clone https://github.com/yourusername/TimeTableBuilder.git

text
2. **Open the project in IntelliJ IDEA.**
3. **Build the project** (Build → Build Project).
4. **Run the application** by executing the `view.Main` class.

---

## Usage

- **Manage Data:** Use the tabs to add/edit classrooms, courses, and instructors.
- **Create Timetables:** Manually schedule classes or use the auto-suggestion feature.
- **Validate:** Check for conflicts and ensure compliance with BITS policies.
- **Export:** Save or export your timetable in various formats for sharing or printing.

---

## Project Context

This project was developed as part of the OOPS course at BITS Pilani to demonstrate practical application of object-oriented design, MVC architecture, and Java GUI programming in a real-world academic scenario.

---

## License

This project is for educational purposes.

---

*Developed by LakshminathKopparti as part of the OOPS course at BITS Pilani.*
