# defusing-bomb

Show how different tech stacks can be tested together with Docker, TestContainer and JUnit5.

## Specs

Bomb start to tick after it's initiated with phone call.

Bomb disarmed when wires green, yellow and blue are cut under two seconds.

## Modules

- Module [rest-api](rest-api/README.md)  provide end points for activating and disarming bomb. 
    - Implemented with C#.
- Module [database](database/README.md)  contains data for module rest-api.
    - Implemented with Microsoft SQL Server.
- Module [test](test/README.md) verify rest-api end points works as expected. 
    - Implemented with Kotlin, JUnit5 and Test Container.