# Knowledge Base
## Alexandra Emerson
This program functions as a Knowledge Base which uses the Resolution Refutation Method to track and execute information expressed in Propositional Logic.
This program can operate in two different modes: One mode reads in a filename (if provided), and the other mode is interactive and can only be triggered if no file is provided.
The program will then read the command(s) provided one at a time, and perform the corresponding Knowledge Base function. The list of allowed (and currently supported) commands are given below in 'Allowed Commands'.
* What is not currently working is marked with '(NOT CURRENTLY SUPPORTED)'.
* If a functionality only supports literals, it will be marked with '(ONLY SUPPORTING LITERALS...)'.


### How to run from the Command Prompt (terminal)

1. Download the project folder
2. Open your Command Prompt (terminal)
3. cd to project's src file containing the .java files
4. type (without quotes) 'javac KBDriver.java
5. Next, type (without quotes) 'java Search' (optionally) followed by a filename containing any of the allowed commands listed below.

### Allowed Commands
* HELP : Prints a list of supported commands along with brief descriptions of their behavior.
* DONE, EXIT, QUIT : Terminates the session.
* TELLC <clause> : Adds the given <clause> to the knowledge base.
* PRINT : Prints the clauses currently in the knowledge base
* ASK <query> : Determines if the knowledge base entails <query> using the resolution refutation method. (ONLY SUPPORTING LITERALS; LITERALS MAY INCLUDE PARENTHESES AND/OR NEGATIONS)
* PROOF <query> : Prints a proof of <query> from the knowledge base, obtained via the resolution refutation method. (ONLY SUPPORTING LITERALS; LITERALS MAY INCLUDE PARENTHESES AND/OR NEGATIONS)
* PARSE <sentence> : Prints the parse tree of the given <sentence> in (simplified) propositional logic. (NOT CURRENTLY SUPPORTED)
* CNF <sentence> : Prints the conjunctive normal form representation of the given <sentence> in (simplified) propositional logic. (ONLY SUPPORTING LITERALS; LITERALS MAY INCLUDE PARENTHESES AND/OR NEGATIONS)
* TELL <sentence> : Adds the clauses in the CNF representation of <sentence> to the knowledge base. (NOT CURRENTLY SUPPORTED)

