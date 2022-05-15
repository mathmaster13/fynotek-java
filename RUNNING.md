# Running Demos

## Running from `.jar` file
Download a jar file from the [releases](https://github.com/mathmaster13/fynotek-java/releases/latest).

If you are using version 2.0 or greater, the file name should contain `with-demos`.

Run a demo (do not add the `.java` or `.kt` extension to the demo name):
```
java -cp file-name.jar <demo name>
```

## Running from source
Clone the repo:
```
git clone https://github.com/mathmaster13/fynotek-java.git
```
Copy all files in the `src/demos` folder to the root of the `src` folder.

Compile the library:
```
javac io/github/mathmaster13/aspenlangs/fynotek/*.java
```

Compile and run a demo:
```
javac <demo name>.java
java <demo name>
```
