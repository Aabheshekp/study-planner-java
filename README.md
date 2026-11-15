# Study Planner (Java)

A Java Swing desktop app for tracking study tasks by subject and due date. Mark tasks complete, add and remove items, and persist the list locally. Data is stored under the current user's home directory in `.study-planner/tasks.tsv`; each row is Base64 encoded to preserve tabs and punctuation.

## Requirements

JDK 17 or newer. No external libraries are needed.

## Run

```sh
javac -d out src/main/java/com/example/studyplanner/StudyPlanner.java
java -cp out com.example.studyplanner.StudyPlanner
```

The application stores data on the same computer and does not sync it. Back up the local file when needed.

### Category

App development | Java 17+, Swing
