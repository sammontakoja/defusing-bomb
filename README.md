# defusing-bomb

Show how different stacks can be used and tested together to defuse bomb.

## Specs

Bomb start to tick after it's initiated with phone call.

Bomb disarmed when wires are cut in following order: green, yellow and blue.

Bomb explode
    after two seconds.
    when yellow wire is cut first.
    when blue wire is cut first.
    when green wire is cut first and then blue.

## Modules

- Module [rest-api](rest-api/README.md)  provide end points for activating and disarming bomb. 
    - Implemented with C#.
- Module [database](database/README.md)  contains data for module rest-api.
    - Implemented with Microsoft SQL Server.
- Module [test](test/README.md) verify rest-api end points works as expected. 
    - Implemented with Kotlin, JUnit5 and Test Container.

Each module produce Docker image so the whole stack could be tested in any environment with docker engine.

## How to run and test all the modules

TODO