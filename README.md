Nota Bene: Sekoitan täällä surutta suomea ja englantia mutta sehän sopii hyvin tietojenkäsittelytieteen laitoksen linjaan.

# Sling (and Crossbow)

This is a project initially made for course Ohjelmistotekniikka at the University of Helsinki. The course makes a few demands that affect the structure of this project.
1. The scope of the project needs to contain a graphical user interface and therefore I am putting 2 separate projects into this same repo: sling and crossbow. Once the course is finished I will split them to 2 separate projects.
2. The course requires me to use maven as a build tool. I will convert this project to gradle once the course is over.

Sling is a database client program that implements the required network protocol for communicating with David, a relational DBMS. It plays the same role as JDBC plays when accessing various sql-based DBMS'es. But where JDBC is limited to Java and ODBC is otherwise clunky, the idea of Sling is to be small enough so that it will be simple and quick to implement for all languages. The API can be elegant because it does not need to consider the needs of many different products. Suffice to say that one design idea in David was to keep the types to a minimum and simple so that data coming in and out of David could be processed easily within heterogenous programming environments.

Crossbow is a graphical database client that uses Sling to access David, relational DBMS. It provides user the ability to browse database objects in the database. Manipulate those objects and make queries against the relvars (relvar = relation variable). Within the confines of Ohjelmistotekniikka course Crossbow will be a simple graphical database client. Later I will expand to cover all aspects of database management. But not only that. I will also add features found in a spreadsheet program so that a user could use database as a backend for creating spreadsheets. Spreadsheet is a cross-over between a database and a graphical program to create eg. printable invoices and other kinds of reports. But it is very ad-hoc and lacks structure. My final goal will be to combine a well structures and consistent data model with creating printable reports. And to achieve that the David database needs to run embedded inside Crossbow and store the whole database into a single file so that the user can replace his current spreadsheet program like MS Excel and LibreOffice Calc with Crossbow.

Because in the beginning there is not yet a working version of David database I will need to initially run a stub/placeholder for David inside this project. But before this course is over there will be an initial version of David against which these 2 programs are run against.

## Documentation

[Requirements](https://github.com/rojares/sling/blob/master/dokumentaatio/Requirements.md)
