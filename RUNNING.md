# Running Demos
## Running from source
Clone the repo:
```
git clone https://github.com/mathmaster13/FynotekWord.git
```
Copy the `com` folder to `FynotekWord/demos`.

Run the rest of the commands here in `FynotekWord/demos`.

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
Download a jar file.

Run a demo (do not add the `.java` extension to the demo name):
```
java -cp fynotek.jar com.mathmaster13.fynotek.<demo name>
```