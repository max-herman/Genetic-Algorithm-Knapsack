How to use:

    - Navigate to root directory

    - Execute command 'javac src/*.java' to create .class files

    - Use command 'java src/Population {genome-size} {population-size} {num-generations} {carry-fit} {carry-weak} {num-mutations}' to run Main class

        - ex; 'java src/Population 10 10 100 0.2 0.1 4'
    
    - To try different selection algorithms, change method call on line 255 of Populations.java.