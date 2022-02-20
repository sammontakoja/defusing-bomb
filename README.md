# defusing-bomb

Bomb start to tick after it's initiated with phone call.

Bomb disarmed when wires are cut in following order: green, yellow and blue.

Bomb explode
    after two seconds.
    when yellow wire is cut first.
    when blue wire is cut first.
    when green wire is cut first and then blue.

After bomb is disarmed
    it wont explode after two seconds
    it won't hurt if wires are cut again.


## Modules

Module rest-api provide end points for activating and disarming bomb.

Module database contains data for module rest-api.

Module test verify rest-api end points works as expected.
