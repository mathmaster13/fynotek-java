# Running Demos
## Running from source
Clone the repo:
```
git clone https://github.com/mathmaster13/fynotek.git
```
Copy the `com` folder to `fynotek/demos`.

Run the rest of the commands here in `fynotek/demos`.

Compile the library:
```
javac com/mathmaster13/fynotek/*
```

Compile and run a demo:
```
javac <demo name>.java
java <demo name>
```

## Running from `.jar` file
Download a jar file from the [releases](https://github.com/mathmaster13/fynotek/releases/latest).

Run a demo (do not add the `.java` extension to the demo name):
```
java -cp fynotek-<version>.jar <demo name>
```