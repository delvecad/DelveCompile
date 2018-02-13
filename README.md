# DelveCompile
This is a public copy of my in-progress compiler project for CMPT 432.

The language grammar that this compiler accepts can be found at:
http://labouseur.com/courses/compilers/grammar.pdf

PHASES COMPLETE:
-Lexical Analysis

The source code will be written in Java (Java 8). 
I will be working in MacOS 10.12.6, and so setup instructions will be geared toward a Mac environment.

MacOS is able to run Java programs out of the box. Simply follow the steps below to get started.

TO RUN JAVA PROGRAMS FROM TERMINAL:
  1. navigate to the directory where the .java file is located
  2. Compile the code by executing 'javac filename.java'
  3. Run the code by exectuting 'java filename'. Note that the command is 'java' not 'javac', and that file extension should be excluded.
  
If the code compiles and runs correctly, you will see the correct output displayed in the terminal. If any issues arise during either process, errors will indicate what went wrong. It is worth noting that no flags have to be included in the terminal commands to include errors or warnings as they are enabled by default.

TO RUN COMPILER:
  1. navigate to Compiler.java
  2. Compile the code by executing 'javac Compiler.java'
  3. Pipe in a test file to lex by running the command 'java Compiler < TestCases/testfile.txt', replacing testfile.txt with        your test file of choice.
  
The lexer should then display all of the tokens it captured from the text file.
The following phases will be completed roughly every few weeks.

