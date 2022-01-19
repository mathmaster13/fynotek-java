# Running Demos

## Running from `.jar` file
Download a jar file from the [releases](https://github.com/mathmaster13/fynotek-java/releases/latest).

Run a demo (do not add the `.java` extension to the demo name):
```
java -cp fynotek-<version>.jar <demo name>
```

## Running from source
Clone the repo:
```
git clone https://github.com/mathmaster13/fynotek-java.git
```
Copy the `com` folder to `fynotek-java/demos`.

Run the rest of the commands here in `fynotek-java/demos`.

Compile the library:
```
javac com/mathmaster13/fynotek/*
```

Compile and run a demo:
```
javac <demo name>.java
java <demo name>
```
