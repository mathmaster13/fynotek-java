# Running Demos
## Running from source
Clone the repo:
```bash
git clone https://github.com/mathmaster13/fynotek.git
```
Copy the `com` folder to `fynotek/demos`.

Run the rest of the commands here in `fynotek/demos`.

Compile the library:
```bash
javac com/mathmaster13/fynotek/*
```

Compile and run a demo:
```bash
javac <demo name>.java
java <demo name>
```

## Running from `.jar` file
Download a jar file from the [releases](https://github.com/mathmaster13/fynotek/releases/latest).

Run a demo (do not add the `.java` extension to the demo name):
```bash
java -cp fynotek-<version>.jar <demo name>
```