# Commons: Hibernate

My collection of reusable Java classes for Hibernate.


## Content

- Abstract decorator for Hibernate (with generic method chaining).


## Download

### Maven artifact
 
If you’re using Maven2, simply add these lines to your _pom.xml_:

    <repositories>
        <repository>
            <id>jirutka.cz</id>
            <name>Jirutka’s repository</name>
            <url>http://repos.jirutka.cz/maven/</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>cz.jirutka.commons</groupId>
            <artifactId>commons-hibernate</artifactId>
            <version>1.0</version>
        </dependency>
    </dependencies>

### Manual download

Otherwise, download jar file from [here](https://github.com/downloads/jirutka/commons-hibernate/commons-hibernate-1.0.jar).

Compile dependencies:

* [hibernate-core](http://www.hibernate.org/downloads.html)
* [slf4j-api](http://www.slf4j.org/download.html)

Test dependencies:

* [junit](https://github.com/KentBeck/junit/downloads)
* [hsqldb](http://sourceforge.net/projects/hsqldb/files/hsqldb/)
* [hibernate-entitymanager](http://www.hibernate.org/downloads.html)


## License

This project is licensed under [LGPL version 3](http://www.gnu.org/licenses/lgpl.txt).