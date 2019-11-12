# sling (and crossbow)

This is a project initially made for course Ohjelmistotekniikka at the University of Helsinki. The course makes a few demands that affect the structure of this project.
1. The scope of the project needs to contain a graphical user interface and therefore I am putting 2 separate projects into this same repo: sling and crossbow. Once the course is finished I will split them to 2 separate projects.
2. The course requires me to use maven as a build tool. I will convert this project to gradle once the course is over.

Sling is a database client program that implements the required network protocol for communicating with David, a relational DBMS. It plays the same role as JDBC plays when accessing various sql-based DBMS'es. But where JDBC is limited to Java and ODBC is otherwise clunky and limited the idea of Sling is to be small enough so that it will be simple and quick to implement for all languages. The API can be elegant because it does not need to consider the needs of many different products. Suffice to say that one design idea in David was to keep the types to a minimum and simple so that data coming in and out of David could be processed easily withing heterogenous programming environments.

Crossbow is a graphical database client that uses Sling to access David, relational DBMS. It provides user the ability to browse database objects in the database. Manipulate those objects and make queries agains the relvars. But unlike typical database clients it is not limited just for database management. It also tries to develop features found in a spreadsheet program so that a user could use database as a backend for his spreadsheet giving much better structure and machinery for efficient data management. David could be embedded inside the Crossbow and database could be stored inside one file just like in spreadsheet programs like LibreOffice Calc and Microsoft Excel.

## Documentation

[Requirements](https://github.com/rojares/sling/blob/master/dokumentaatio/Requirements.md)
