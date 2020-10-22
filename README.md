# TriangleAPI

## About
This is my attempt to cover the [Triangle Service](https://qa-quiz.natera.com/) with tests. The tests were written with respect to the unit testing values such as independence and atomicity rather than the integration testing approach. Since were there no test scope, a triangle sides range are mostly tested from X,XX to XXX,XX values.

The personal token is hardcode in the project, I can re-designed this to specify it as an argument to the run command if needed.   

The project was not intended to utilize a parallel execution, multiple issues are expected in this case. 


## Issues observed
* AddTriangle_Payload - A payload without any keys is accepted i.e. '{5;6;8}' it's either intentional just not described or it's a bug.
* AddTriangle_Payload - In some cases, we have 'Code 400 - bad Request' e.g. '{"": "3;4;5"}', in other cases 'Code 422 - Unprocessable Entity' e.g. '{"separator": ";" "input": "3;4;5"}' though in all cases the payload about the same way inappropriate. Need criteria to distinguish these cases.
* AddTriangle_Payload - Some custom separator values, e.g. '{"separator": ")", "input": "3)4)5"}' causes the Code 500 instead of Code 422.
* AddTriangle_Payload - The separator key name content seems could be any value, e.g. '{"terminator": ";", "input": "3;4;5"}' will be accepted as a valid payload.
* AddTriangle_Sides - The EP accepts sides with zero value.
* AddTriangle_Sides - The EP accepts sides with negative value.
* AddTriangle_Sides - The EP accepts sides with zero value.
* AddTriangle_Sides - The EP accepts sides where the sum of some two can be < than the third side.
* AddTriangle_Limit - The EP accepts the creation of the 11th triangle when it was stated that only 10 are allowed.
* DeleteTriangle_ID - The EP accepts just any value as the ID and returns the Code 200 instead of 422 
* GetAllTriangle_HTTP_Method - The EP returns the Code 200 if the 'DELETE' HTTP method was used where the Code 405 is expected.
* everything I missed.      


## Tech Stack
* Java 11
* Maven 3.6.2
* Rest Assured 4.3.1
* TestNG 7.1.0
* Allure 2.13


## How to run
Pre-requisites: to run the project you will need Java 11 or higher, Maven installation is not necessary since the project utilized a Maven wrapper. Make sure you have a proper java version in the JAVA_HOME path otherwise Maven most likely fails to run the project. 


To run the project just execute in command-line from the project's root folder:
```
./mvnw clean test
```


## Test results

After tests execution will be complete, you can check the results with the *emailable-report.html* from the /test-output folder or run Allure report dashboard from the same folder by executing the following command:
```
allure serve
```
but this will require [installing Allure](https://docs.qameta.io/allure/#_installing_a_commandline) on your system in the first place. You will also need to add the Allure to your $PATH environment variable to be able to call it from the /test-output folder.
