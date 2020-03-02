# Sling – client library for DBMS David

This document describes the design principles behind a client library used to access a new relational database management system called David. The reasons that have led me to develop yet another relational database are beyond the scope of this document but I mention briefly some background for that also in the introduction.

## 1. Introduction

In this paper I will try to define a blueprint for a good database client that works well in different programming environments. The main challenges is to define client interface that can be easily implemented in all different programming environments without the dreaded impedance mismatch that occurs when marshalling data between two different programming environments.

First I want to make a distinction between 2 kinds of data:
* symbolic data
* binary data

These terms are my own because I have not found established terms for the distinction I am trying make.

### 1.1 Symbolic data

This refers to data types like Boolean, Integer and String. Types that a programmer uses in his source code. Assigns them to variables and gives as input to functions. Some call these value types. The representation size of these values is fairly short so that they can be written as literals in source code. Occasionally the string type sometimes breaks this rule and then it is stored into a file and referred to through some object.

The properties of these types are well understood and widely used. They are not ad hoc. They have a standard set of operators defined for working with them like not, and and or for the Boolean type, plus and minus for the Integer type and so on.

These types are indexable and searchable in a database.

This data could also be called metadata because it describes facts about employees, orders and other kind of entities, situations and states of affairs.

Their size is limited - also in the case of string - to some reasonable value and thus returning thousands of them as a single result over the network and storing such a result into memory will not cause problems or performance issues with modern hardware. However I do not want to forbid the possibility of streaming a large result. This might be occasionally needed when transferring large amounts of data between the systems. But this will be a rare occurrence and small results will be the norm.

_Nota bene:_ All data transferred over network is transferred as list of bits and thus one could argue that all results are always streamed over network. Here with streaming I mean specifically that the result is made available in the client before it has been fully transferred.

#### 1.1.1 Primitive and collection values

Symbolic data can be further divided into 2 categories: primitive and collection values.

##### 1.1.1.1 Primitive values

Primitive values are atomic. Atomicity in the context of values means that the value can not be broken into components. However atomicity is not a “natural/eternal” property of some values but it is a matter of definition within some context. For example integer 22 can be broken into 22 components of ones, that compose it by adding them together. Boolean true can be said to be composed of all those sets of 32 consecutive bits where at least one of them is one.

Any system is supported by some subsystem beneath it, supporting the existence of the system above. And what is considered to be atomic in the system, can contain components in the subsystem. To sum up, atomicity is defined for some values in the system. The system in question can not see parts of the atomic values but instead they are indivisible wholes from the perspective of the system.

##### 1.1.1.2 Collection values

There are all kinds of collection types in the computing world like sets, maps, lists, bags, trees, linked lists, graphs, acyclic directed graphs and so on. In the relational model there are only 2 collection types - both of which are based on set - namely tuple and relation.

The idea is that with just these 2 collection types we can represent any data structure. And the language can be kept simple because every new collection requires a lot of new operators and combining data from different collection types becomes quickly very complicated.

However when results are sent to client we can not use relation type because the results need to be sorted and often the client wants also the attributes of a relation to be in certain order. Thus in the client we have to define similar collection types as relation and tuple, but they need to be ordered. These two collection types I will call table and row because they are familiar to users of relational databases.

### 1.2 Binary data

This refers to all data that is stored in a machine readable binary format. These values are typically large (eg. video) but not necessarily (eg. small image). This data is not indexed but metadata about it might be indexed as symbolic data. For example the length of a video, it’s framerate, encoding and colorspace could be stored and indexed as symbolic metadata. Or the speech of an audio track could be recognized into string and indexed as symbolic data. But the binary data itself is just stored as-is into the persistent storage and retrieved from there either partially or completely. Typically it is returned to the client as a stream because of it’s size. And there needs to be a program on the client side that is capable of presenting those values to user eg. video player or word processor.

The big difference is that binary value is not used in a search but some metadata about the binary value is extracted from it and that is stored as symbolic data and used in searches.

Another big difference is the size needed for these two types of data. If database of symbolic data grows to be multiple terabytes it is already considered huge. Let’s say a tuple consumes in a typical database on average 1 kilobyte. Then a database with size of 1 terabyte would contain 1 billion tuples.

For a video archive 1TB is nothing. Video archives of TV companies start to be already in the magnitude of Petabytes. So symbolic data and binary data need a separate storage solution.

In order to answer how exactly they can be kept apart while still providing easy access for the client to both types of data, we need to talk about software architecture.

### 1.3 Software architecture

Software architecture mainly deals with the question of how a system is split into more or less independent concern areas that together form the complete system.

A single-user application running on a single operating system that is not connected to other processes - whether on the same node or on other nodes – needs to worry only about it’s internal component structure and how it interacts with the services provided by the OS eg. the filesystem.

A multi-user application however is split into two types of software: server and client. A server communicates with multiple clients and a client communicates with one or more servers. A peer-to-peer application is a variation of this where all programs can act both as server and as client.

In the eighties a common architecture was to have desktop applications accessing the same database. But in the nineties - propelled by the browser revolution - there appeared the three-tier architecture where you had one database used by one or more application servers (initially they were just http servers) which were used by browsers by multiple organisations. The main reasons why a separate application server was needed were:

* browser is a thin client and needs help in creating custom user interface,
* a need to provide more functionality than a database could provide on the server side and
* security needed hardening when applications were shared to larger audiences outside of a single organization and thus direct access from clients to database had to be blocked.

The three layers of the three-tier architecture are:

* Presentation tier
* Application tier
* Data tier

_Nota Bene:_ I use tier and layer as synonyms.

Later this has evolved into multitier architecture, as developers started to divide the application layer into multiple layers and shifting more functionality into the middle layer.

The biggest tragedy - when it comes to data modelling and relational databases - happened when domain modelling in the application server became popular. It was clear that the model in the database and in the application server duplicate each other. And the conclusion was that - because you can’t really program in the database but have full expressive freedom in the application tier - the domain model should be implemented in the application layer. The database became just a persistence library and tools like Hibernate were created to map the domain model from the application tier automatically to data tier. The noble idea of having a single unified datamodel for the whole organisation was forgotten and the datamodel was mixed with functionality as is the norm in object-oriented programming.

At the time there were many open source database systems available for free and any developer could easily use a separate database for their application.

The next step in this devolution were Nosql databases. Behind it there was an understandable reason. New companies like Google, Facebook and Amazon had become so big players on the international Internet that their users were counted in hundreds of millions or even billions. You simply can not use a single relational database to satisfy the performance needs of such a large and geographically distributed group of users. So when implementing distributed storage solutions, the developers used lower abstractions to implement them.

SQL and the relational model had already become an old relic in the world of computing trends. Just like generated SQL schemas, NoSQL solutions were marketed to database users as better alternatives to old and clumsy SQL and and the relational model.

Currently a typical organisation is left with multiple separate applications having their own models and own databases as a persistence layer. The term integration hell applies better to organisations unable to integrate the applications than to developers trying to merge multiple branches into trunk.

Next I will outline the architecture that I had in mind when I designed David. Let’s start with a picture.

![Architecture](img/Architecture.png "Architecture")

I have divided the middle tier into two: application and service layer.

* Presentation and application layer __use__ the service layer.
* Application and service layer __use__ the data layer.

The data layer is divided into two types of storage. The symbolic storage is the relational database with ACID properties. The binary storage is basically a key-value map like a filesystem where path is the key and file is the value. (Filesystem also contains keys that do not have a value associated and they are called folders or directories).

The binary storage does not need to implement search capabilities so the simple filesystem index is enough. It needs to be large and be able to read and write fast to/from disks and network. It also does not need complex transaction mechanism. Current journaling filesystems will be enough and preventing writes to same files at the same time is enough.

The processing of binary files is left to service and application layers. There are many different binary types and new ones are constantly created. Formats for video, audio, images, documents and so on.

Let’s consider handling multimedia. You would need to ingest a variety of different files. Part of this process you would check that the video has enough of frames per second, enough resolution etc. You would maybe convert it to certain storage format decided by the organisation. You might recognize the speech from the audio track into text and index it. You might extract some images from the video and so on. All of this would be implemented as service that we could name eg. Ingest server. It would update the metadata, related to the binary file, into the symbolic database and commit it if everything was stored successfully in the binary storage.

This means that the symbolic database needs to implement a 2-phase commit. This also means that guarding the consistency of the binary storage is left to the service layer. The most secure solution would be to let the transaction manager of the symbolic database to manage all data. But I think it has performance characteristics that are too far from optimal and therefore I have decided to keep them separate.

Binary storage is big and simple (Goliath) whereas symbolic storage is small and smart (David).

Another service you would have to implement for multimedia files is a streaming server that could give access to videos in different formats and bandwidths. Again the functionality is somewhat organisation dependent and somewhat generic. It could be bought from an external company and configured to use the organisation's datamodel. It could start it’s life as part of an application but when multiple applications start using it, it could be separated as a service.

Because applications and services do not have a state, other than those variables needed for the runtime operation, they can be multiplied and the load can be distributed to multiple nodes that can be distributed geographically. Because the database uses MVCC then read-only versions of the database can be duplicated easily and thus the reading of the database can be distributed.

However all write operations have to be directed globally to one central database and this will be the main limiting factor of this architecture.

The reason why I don’t even want to consider distributing the write transactions is because I want to support full set of database constraints and enforce serialized isolation.

## 2 Typology

Value is always of some type. Sometimes it is only partially defined but we need to know something about the type of the value before we know how to understand the value. One can put a lot of logic and meaning into type. For example set of those strings that match the syntax of an email address or even those strings that represent such email addresses for which a server is ready to deliver mail to. Implementing such a type requires a lot of code because every time a new value is created, the system needs to make sure that it can send email to that address. Otherwise the value would not belong to that type.

Therefore in practice the types are kept very simple because making checks for the values all the time would be too expensive. There are three types that have stood the test of time. They are boolean, integer and string. They can be found in virtually every programming environment. Their exact definitions do vary but in most parts they are the same.

In some systems the boolean type has 2 values and in some 3. In David the boolean type has a third value null.

The upper and lower bounds of the integer type vary. Also what happens when the value overflows is often left to the implementation, undocumented and subject to change. However this issue is not strictly related to integer type but to operators, operating on it. In David the integer is defined to have the same boundaries as Java Long.

String can have different character sets, collations, length constraints etc. Luckily the world is slowly standardising on Unicode and utf-8 encoding, although encoding is not really part of the type definition. In David I have decided to limit the string type to Unicode BMP (Basic Multilingual Plane) because there is currently no complete support in different programming environments for characters outside of BMP.

If we look at types in JSON we see that they have identified those same 3 types to be universal.

As mentioned earlier, for the collection types that we use are relation and tuple on the server-side and table and row on the client-side. We don’t need nested object structures or lists like JSON does. Well, actually our table and row both can be seen as a list.

We need nested tables as much as a user interface needs nested tables. Preferably not.

Creating new types like ranges and volumes by combining tuple with primitive types is still an open research issue and thus will be scoped out of the first version of Sling.

In addition to these 3 types there are other types that might be universal enough to be considered primitive types of David but their universality is not as good as with the first 3 types.

Here is the proposed type hierarchy for David. The language defined for David is called D*.

![Typology](img/Typology.png "Typology")

Floating point is the most widely supported type of them on all platforms. However it is rarely used. It is said that it is used in scientific and engineering applications. I only know that it is used in audio related applications. It works well with multiplication but addition is quite useless when adding together large and small numbers (the small numbers just disappear due to rounding). It is even more rare in database context.

Decimal is used heavily in databases because of money. So it must be supported. But again a decimal type is not very common in programming environments. For example in java you have only java.lang.BigDecimal for that. In javascript you have just one number type for integer, decimal and float. Internally it is stored as floating point and thus can represent 53 bits worth of integers (the rest of the 64 bits are used for sign and exponent). So that one type will support all 3 numeric types in javascript environment. If the database returns a very large integer it will not fit inside javascript number. But this can happen in other environments too and then it is up to the user to turn the canonical string representation to something usable in the environment.

Temporal types – Timepoint, Date and Time – are often used and quite established types in the world of data management. However their definition has not settled as much as the definition of integer. In many languages they are implemented only partially or incorrectly. Java got the temporal types correct and complete only recently in version 8 when the java.time package was added.

These types can be represented by an integer. It is just that the integer is interpreted in a very specific way. The different meaning assigned to these integers creates the new temporal type. The database system needs to implement operators for temporal types so that searching them becomes easy.

This has led me to the following conclusion. All types have a canonical string representation and they are sent over network in that format. Then the client library makes the decision what kind of getters and setters are provided in that environment for a value of certain database type. Also the user can always construct any objects he wants using the canonical string representation.

For example the client in java environment could offer methods like getPrimitiveInt, getInteger, getPrimitiveLong, getLong, getBigInteger, getString, getPrimitiveBoolean, getBoolean, getDouble, getCalendar etc.

The getString method would work always and return the canonical string representation. But getBoolean would throw an exception if the value is not of type boolean. Also getPrimitiveInt would throw an exception if the value is null or if it does not fit inside the boundaries of int.

At the client side it is very important that the user can get the values in those types that he can actually use in his environment. Otherwise he will have to convert the value to some other type and this will have to be repeated both when reading and writing. And this is essentially what is called the impedance mismatch. This I want to avoid as much as possible.

### 2.1 Null value

Tony Hoare, the inventor of null reference, said in Qcon conference in London 2009:

_“I call it my billion-dollar mistake. It was the invention of the null reference in 1965. At that time, I was designing the first comprehensive type system for references in an object oriented language [ALGOL W]. My goal was to ensure that all use of references should be absolutely safe, with checking performed automatically by the compiler. But I couldn't resist the temptation to put in a null reference, simply because it was so easy to implement. This has led to innumerable errors, vulnerabilities, and system crashes, which have probably caused a billion dollars of pain and damage in the last forty years.”_

I have a completely opposite view. I think that the null value is maybe the most useful value of all. In SQL it is not a value but some kind of marker. And the third truth-value in SQL is called unknown. My idea is that there is just one null value and this value, just like all other values, is always typed. So integer:null and boolean:null are in fact the same value but they are just mentioned to be part of 2 different sets. So the null value is included into all primitive types but of course when defining an attribute for a relation, the domain can further constrain the domain to exclude the null value.

I will not give here the reasons why null value is indispensable in database context but I only want to say that when a value is returned to the client it can always be null and the null value is always wrapped in some type. This will need to be remembered when reading and writing values from/to the client API.

## 3. Connection, Session and Interaction

As transport protocol, TCP is the only choice because the client needs to read/write all data uncorrupted in sequential order. First a connection is opened between client and server using TCP’s three-way handshake. Then server waits for the client to authenticate. If this does not happen within a configured time the server closes connection. Server can also close the connection after certain amount of failed login attempts.

After a successful login server creates a session for the client that includes the client’s identity and other possible session parameters that affect interactions happening within the session. The client can also configure the session after it has been successfully created.

Within the lifetime of a session a client can run zero to n interactions. Interaction is one request/response cycle and it also corresponds with one transaction. If the client wants to execute multiple statements in one transaction he will include them inside one interaction. If he wants to run them in separate transactions he will have to execute them in separate interactions.

Interaction is a list of D* statements that is sent as text using the session. The response is formed using instructions given by the user in the statements so the user will know exactly what kind of response to expect. Also he can receive multiple results inside a single response, so the system is very flexible.

As a minimum, response will always include information, if the interaction was successful or not. And if it was not, it will also include the error code and localizable message.

### 3.1 Protocol interaction diagram

TBD.

### 4. Canonical string representations for all the types

TBD.
