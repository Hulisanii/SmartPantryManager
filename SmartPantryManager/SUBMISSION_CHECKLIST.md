# Assignment Completion & Submission Checklist

- [x] **Gradle Sync & Build**: Project is built in pure Java (JDK 17 / compileSdk 35) with unit test setup (`StrictMatchingTest.java`).
- [x] **Pantry CRUD Operations**: Full Create, Read, Update, and Delete implemented for ingredients with Toast feedback and confirmation dialogs.
- [x] **SQLite Persistence**: Data stored locally in `smart_pantry.db` (`SQLiteOpenHelper`) and persists across app close/reopen.
- [x] **Core Strict Matching Rule**: Tested algorithm; recipes appear ONLY if 100% of required ingredients are present in required quantities.
- [x] **Bonus Stretch Requirement**: Implemented "Almost There" list showing recipes missing exactly 1 ingredient with exact missing quantities.
- [x] **Robust Name & Unit Normalization**: Handled singular/plural variations (`tomatoes` ➔ `tomato`, `eggs` ➔ `egg`) and metric unit conversions (`g` ↔ `kg`, `ml` ↔ `l`).
- [x] **Input Validation**: Form checks for empty fields, non-numeric values, and non-positive quantities with clear user feedback.
- [x] **5 Activity Screens & Navigation**: `MainActivity`, `IngredientFormActivity`, `SuggestedRecipesActivity`, `RecipeDetailActivity`, `SettingsActivity` with smooth activity backstack management.
- [x] **Written Report**: Full Harvard-style report template generated in `written_report.artifact.md` with ER diagram, screen flow, code snippets, and challenges.
- [ ] **GitHub VCS Repository**: Push project to a public GitHub repository with >=10 incremental commits and add URL to report.
- [ ] **Screenshots**: Capture actual screenshots from emulator/device and embed into final PDF/Word report document.
- [ ] **Demonstration Video**: Record a 5–7 minute narrated video following the required demonstration structure.
- [ ] **Final Submission Package**: Compress source code, report PDF, and video into a single ZIP file (< 50 MB) named `Studentnumber_Surname_MobileAppDev700_Assignment.zip`.
