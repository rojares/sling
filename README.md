# Sling

This is a project initially made for course TIETOKANTASOVELLUS at the University of Helsinki.

Sling is a database client program that implements the required network protocol for communicating with David, a relational DBMS. It plays the same role as JDBC plays when accessing various sql-based DBMS'es. But where JDBC is limited to Java and ODBC is otherwise clunky, the idea of Sling is to be small enough so that it will be simple and quick to implement for all languages. The API can be elegant because it does not need to consider the needs of many different products. Suffice to say that one design idea in David was to keep the types to a minimum and simple so that data coming in and out of David could be processed easily within heterogenous programming environments.
