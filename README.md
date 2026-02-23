# One Minute Math Challenge

A simple command-line Kotlin application that generates timed arithmetic problems and evaluates user performance.

## Overview

One Minute Math Challenge is an interactive math quiz game.
The user selects:

* Minimum and maximum operand values
* Number of problems
* Which operations to include (addition, subtraction, multiplication, division)

The game runs under a fixed time limit and reports results at the end.

## Features

* Configurable operand range
* Configurable number of questions
* Selectable operations:

    * `a` – Addition
    * `s` – Subtraction
    * `m` – Multiplication
    * `d` – Division
* Global time limit for the entire session
* Immediate termination by typing `q`
* Summary report:

    * Total correct
    * Incorrect answers with corrections
    * Unanswered questions

## Requirements

* Kotlin 2.3
* Java 21+

The project hasn't been tested with other versions of Kotlin or Java

## How to Run

### Using IntelliJ IDEA

1. Open the project.
2. Ensure Kotlin is configured.
3. Run the `main()` function.

### Using Command Line

Make sure you are in the root directory.

#### Method 1
Compile:

```bash
kotlinc src/*.kt -include-runtime -d OMMG.jar
```
This compiles both Kotlin all in the `src` folder.

Run:

```bash
java -jar OMMG.jar
```

#### Method 2
Compile:
```bash
kotlinc src/*.kt
```
This compiles all Kotlin files in the `src` folder.

Run:

```bash
kotlin MainKt
```


Adjust file names as needed depending on your project structure.

### Example

```bash
(KotlinOneMinuteMath) % kotlinc src/*.kt -include-runtime -d OMMG.jar
(KotlinOneMinuteMath) % java -jar OMMG.jar

Welcome to the One Minute Math Challenge!

You will have 60 seconds to solve as many problems as possible.
To quit at any time, press 'q'.
To answer a question, enter your answer and then press 'Enter' or 'Return'.
NOTE: A blank or empty answer will count as a unanswered question.

Enter the smallest number that can be used in each problem (Doesn't apply to divisors): 
Given number must be between -100 and 100 (Exclusive):
1
Enter the largest number that can be used in each problem (Doesn't apply to divisors): 
Given number must be between 1 and 100 (Exclusive):
10
Enter the number of problems you want to try and answer
Given number must be less than 250:
3 
What operations do you want to be included?
    Enter any combination of
    - 'a' for addition
    - 's' for subtraction
    - 'm' for multiplication
    - 'd' for division
    (Ex. 'as' for addition and subtraction, or 'asmd' for all)
    Any duplicates will be ignored and input must be any combination of the above 4 letters.
Operation(s):
asmd
Type 'start' when you're ready to start
start
60 seconds remaining
9+7=
16
52 seconds remaining
6+9=
15
51 seconds remaining
8-10=
-2
You finished with 50 seconds to spare!

====================

You got every problem right!
```

## Game Flow

1. User enters configuration parameters.
2. Program generates problems.
3. User types `start` to begin.
4. Timer begins.
5. User answers as many questions as possible before time expires.
6. Results are displayed.

## Notes

* Entering a blank answer counts as unanswered.
* Non-numeric answers are marked incorrect.
* Typing `q` exits the program immediately.
