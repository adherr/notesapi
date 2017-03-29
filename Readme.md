# Notes API
A coding assessment written in Java using the Jersey framework.

## Running
Assuming you have a functional maven environment, to run the app:

1.  Clone the repo and enter your clone
1.  Prepare your database: `mvn flyway:migrate`
1.  Copy `NotesApi.example.properties` to `NotesApi.properties` and edit it to set the URL of the app
1.  Run the app: `mvn exec:java`
