# Smart Pantry Manager — Java Android Application

A native Java Android application designed to eliminate food waste by tracking ingredients available in a home pantry and suggesting recipes strictly based on what is currently in stock.

---

## 📌 Project Overview & Value Proposition
Food waste often occurs because households forget what ingredients they have or buy duplicates. The **Smart Pantry Manager** solves this problem by enforcing a **Strict-Matching Rule**:
> A recipe is suggested **ONLY** if 100% of its required ingredients are present in the user's pantry in at least the required quantities. No shopping trip required!

---

## 🛠️ Architecture & Technology Stack
- **Programming Language**: 100% Java (JDK 17 / Android SDK 35)
- **IDE**: Android Studio
- **Database / Persistence**: Local SQLite via `SQLiteOpenHelper` (`smart_pantry.db`)
- **Navigation**: Explicit `Intent` parameters with custom navigation bar
- **UI Components**: Programmatic and declarative layout structure (`LinearLayout`, `ListView`, `ScrollView`, `EditText`, `AlertDialog`, `DatePickerDialog`, `Switch`)
- **Unit Testing**: JUnit 4 for algorithm validation (`StrictMatchingTest.java`)

---

## 💡 Database Justification (Section 3.2)
SQLite was chosen for the Smart Pantry Manager because:
1. **Full Offline Functionality**: Pantry management is a daily home activity that must work reliably without an internet connection or server dependencies.
2. **Zero Overhead**: `SQLiteOpenHelper` provides zero-configuration local persistence embedded directly within the Android OS.
3. **Data Integrity & Speed**: Fast local CRUD transactions guarantee instant UI updates and seamless data persistence across app restarts.

---

## 📱 Application Screens & Navigation Structure (5 Screens)
1. **Pantry List (`MainActivity.java`)**:
   - Displays current pantry inventory using a custom `ArrayAdapter`.
   - Displays real-time expiry alerts for ingredients expiring within 3 days or already expired.
   - Tap any item for an action menu to **Edit** or **Delete** with confirmation dialog.
   - Shows empty-state guidance when pantry is empty.

2. **Add / Edit Ingredient (`IngredientFormActivity.java`)**:
   - Form with input validation for ingredient name, quantity, unit, and optional expiry date.
   - Includes quick-unit selection chips (`piece`, `g`, `kg`, `ml`, `l`, `clove`, `slice`, `tbsp`, `pinch`, `can`).
   - Interactive `DatePickerDialog` for selecting expiry dates.

3. **Suggested Recipes (`SuggestedRecipesActivity.java`)**:
   - **Suggested Recipes (Strict Matches)**: Displays only recipes where every single required ingredient is present in required quantities.
   - **Almost There (Missing 1 Ingredient)**: Stretch goal feature displaying recipes missing only 1 ingredient with exact missing quantities (e.g. "Missing: 30 g cheese").
   - Displays empty-state message if zero recipes qualify.
   - Automatically refreshes lists upon returning (`onResume()`).

4. **Recipe Detail (`RecipeDetailActivity.java`)**:
   - Displays required ingredients with formatted quantities, step-by-step preparation steps, and strict match verification badge.

5. **Settings & Preferences (`SettingsActivity.java`)**:
   - Preference toggle to enable/disable expiry alerts on the pantry list.
   - Displays technical details and justifications for database storage and unit conversion rules.

---

## 🧪 Core Algorithm & Strict-Matching Rules (Section 2.3)
The strict-matching algorithm in `DatabaseHelper.java`:
1. **Ingredient Normalization**: Handles singular and plural variations (e.g., `"tomatoes"` ➔ `"tomato"`, `"eggs"` ➔ `"egg"`, `"potatoes"` ➔ `"potato"`).
2. **Unit Normalization & Compatibility**: Converts between metric mass (`g` ↔ `kg`) and volume (`ml` ↔ `l`), and standardizes count units (`pieces`, `cloves`, `slices`, `tbsp`, `tsp`, `pinches`, `cans`).
3. **Quantity Verification**: Sums all matching pantry entries and verifies `availableQuantity >= requiredQuantity`.

---

## 🚀 How to Run the App in Android Studio
1. Launch **Android Studio**.
2. Select **Open** and choose the `SmartPantryManager` directory.
3. Allow Gradle to sync dependencies.
4. Launch an Android Emulator (API 23 or higher) or connect a physical device with USB debugging enabled.
5. Click **Run 'app'** (or press Shift+F10).

---

## 🧪 Running Automated Unit Tests
To execute the strict-matching algorithm unit tests:
```bash
./gradlew test
```
Or right-click `StrictMatchingTest.java` in Android Studio and select **Run 'StrictMatchingTest'**.

---

## 📜 Submission Package Requirements
The final submission ZIP (`Studentnumber_Surname_MobileAppDev700_Assignment.zip`) contains:
- Complete Android Studio source code
- Written Report (`Studentnumber_Surname_MobileAppDev700_Assignment.pdf`) including Harvard references, screen flow diagrams, ER diagram, screenshots, key code snippets, challenges & solutions.
- Narrated 5–7 Minute Video Demonstration (`.mp4`)
