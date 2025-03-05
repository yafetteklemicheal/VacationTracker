# Vacation Tracker

Vacation Tracker is a mobile application designed to help users manage their vacations and excursions efficiently. It offers a user-friendly interface and a variety of features to keep track of vacation details, generate reports, and receive notifications.

## Features

- **Search Functionality**: A search box on the vacation list page allows users to filter vacations based on input.
- **Database Component**: Utilizes the Room Framework for managing SQLite databases. Users can add, view, edit, and delete vacations and excursions.
- **Report Generation**: Users can generate reports containing vacation and excursion information.
- **Validation Functionality**: Ensures excursion dates fall within vacation dates and validates changes to vacation dates.
- **Industry-Appropriate Security Features**: Room Framework DAOs prevent direct database access, and toast messages inform users of validation errors.
- **User-Friendly GUI**: Intuitive navigation, self-explanatory icons, confirmation messages, and sensible error messages enhance the user experience.
- **Scalable Design Elements**: Implements `ExecutorService` for asynchronous database operations and uses `RecyclerView` with adapters for efficient data management.

## Maintenance User Guide

1. **Prerequisites**:
   - Android Studio installed on your development machine
   - Java Development Kit (JDK) installed
   - Android device or emulator for testing

2. **Setting Up the Project**:
   - Clone the repository or download the project files
   - Open Android Studio and select "File" then select "Open"
   - Navigate to the project directory and open it
   - Allow Android Studio to synchronize and build the project

3. **Running the Application**:
   - Connect your Android device via Wi-Fi or start the Android emulator
   - Click the "Run" button in Android Studio or use the shortcut Shift + F10
   - Select the target device and click "OK"
   - The application will be installed and launched on the selected device

4. **Database Maintenance**:
   - The application uses Room for database management
   - To update the database schema, modify the `Vacation` and `Excursion` entity classes
   - Update the database version in `VacationDatabaseBuilder` and provide migration strategies if necessary

5. **Code Maintenance**:
   - Follow standard Java and Android coding conventions
   - Use version control to manage changes and collaborate with other developers
   - Regularly update dependencies and libraries to their latest versions

## User Guide

1. **Launching the Application**:
   - Locate the Vacation Tracker app icon on your device and tap to open it

2. **Main Screen**:
   - The main screen provides a button to view your vacation list
   - Tap the "Get Started" button to proceed

3. **Vacation List**:
   - Tap the "+" button to add a new vacation
   - The vacation list screen displays all your saved vacations
   - The search box at the top of the page can be used to filter the list of vacations below
   - Each vacation has five buttons:
     - **Edit Icon**: Tap to navigate to the vacation details page where you can edit the vacation details and see the list of associated excursions
     - **Share Icon**: Tap to share the vacation details using the app of your choice
     - **Alert Icon**: Sets notifications for the start and end dates of the vacation
     - **Report Icon**: Generates a report containing all the vacation information along with any excursion information if available
     - **Delete Icon**: Deletes the vacation
   - Tap the "Home Page" button to navigate back to the main screen

4. **Adding/Editing a Vacation**:
   - Fill in the vacation name, hotel, start date, and end date
   - Tap the "Submit Vacation" button to save the vacation
   - To edit an existing vacation, tap the edit icon from the vacation list and update the details
   - To navigate back to the vacation list, tap the "Back" button

5. **Vacation Details**:
   - Tap the "+" button to add a new excursion
   - The vacation details screen shows the vacation information and a list of associated excursions
   - Each excursion in the list has three buttons:
     - **Edit Icon**: Takes you to the excursion details page where you can edit the excursion details
     - **Alert Icon**: Sets notifications for the start date of the excursion
     - **Delete Icon**: Deletes the excursion

6. **Adding/Editing an Excursion**:
   - Fill in the excursion name and date
   - Ensure the excursion date falls within the vacation dates
   - Tap the "Submit Excursion" button to save the excursion
   - To navigate back to the vacation list, tap the "Back" button

7. **Setting Alerts**:
   - You can set alerts for vacations and excursions to receive notifications
   - Tap the alert icon next to the vacation or excursion to set an alert

8. **Sharing Vacation Details**:
   - Tap the share icon next to a vacation to share its details via messaging apps or email