# defusing-bomb

Show how different stacks can be used and tested together.

## Specs

Bomb start to tick after it's initiated with phone call.

Bomb disarmed when wires are cut in following order: green, yellow and blue.

Bomb explode
    after two seconds.
    when yellow wire is cut first.
    when blue wire is cut first.
    when green wire is cut first and then blue.


## Modules

Module rest-api provide end points for activating and disarming bomb. Implemented with C#.

Module database contains data for module rest-api. Implemented with Microsoft SQL Server.

Module test verify rest-api end points works as expected. Implemented with Kotlin, JUnit5 and Test Container.

Each module produce Docker image so the whole stack could be tested in any environment with docker engine.
