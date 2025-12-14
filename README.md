# Battleship War Z

A Naval Battle game built with JavaFX, demonstrating advanced Object-Oriented Programming concepts, Design Patterns, and Threading.

## Authors
- **David - Brandon**

## Features
- **MVC Architecture**: Clear separation of concerns.
- **Object-Oriented Design**:
    - **Polymorphism**: Abstract classes and interfaces (`IShip`).
    - **Encapsulation**: Private fields and getter/setter access.
    - **Inheritance**: Custom exceptions and component extensions.
- **Design Patterns**:
    - **Singleton**: `GameStatistics` ensures a single source of truth for game data.
    - **Adapter**: `StatisticsDisplayAdapter` converts data for different views.
    - **Observer**: `GameEventObserver` handles game events decoupled from logic.
- **Multithreading**:
    - `GameTimerThread`: Real-time game timer updates.
    - `AIShipPlacementThread`: Concurrent AI board setup.
- **Persistence**:
    - **Binary Serialization**: Save and load game states (`GameSaver`).
    - **File I/O**: Player profile management (`ProfileManager`).

## Project Structure
- `com.example.myfirstnavalbattle`
    - `controller`: Logic handling (Stage controllers).
    - `model`: Business logic and data structures.
        - `dto`: Data Transfer Objects for serialization.
    - `view`: UI management (`SceneManager`, `AnimationsManager`).
    - `persistence`: Data saving/loading logic.
    - `exception`: Custom error handling (`NavalBattleException`).

## Running the Project
1. **Prerequisites**: Java 17+, Maven.
2. **Build**: `mvn clean install`
3. **Run**: `mvn javafx:run`
4. **Tests**: `mvn test`

## How to Play
1. Enter your name or select a profile.
2. Choose your captain.
3. Place your ships on the board (Drag & Drop or Random).
4. Battle against the AI!
    - **Hit**: Red marker.
    - **Miss**: Water splash.
    - **Sunk**: Ship destroyed.

Enjoy the game!
 

