# About
A simple plugin that is an extension to [dbteku's Telecom plugin](https://www.spigotmc.org/resources/telecom.42914/)
# Requirements
- Minecraft 1.17 and up
- Java 17
- Telecom (beta 0.31 and higher)
# Commands
- `/carrierinfo [subcommand] [carrier name]`
  - general 
    - Gives you information about a given carrier
  - price 
    - Has information about the prices of the carrier, including the carriers peers
  - signal
    - Info about nearby signal of the carrier
  - subscribers
    - prints out a list of people subscribed to the carrier
- `/listcarriers`
  - Displays a table of all carriers, their owners, and their pricing
# Config
[Default config](https://github.com/008kevin/TelecomInfo/blob/main/src/main/resources/config.yml)
- debug - prints some messages to the console to help debug issues
- listCommand
  - itemsPerPage - defines how many rows should be on a ingle page of the list command
- infoCommand
  - signal
    - scanRadius - how many blocks away should the scan go around the player (higher values will result in worse accuracy unless scans per radius is increased). The scan is done in a square
    - scansPerRadius - how many scans should be performed in the given radius. (higher values will cause more scans requiring more cpu power)
- lang
  - Everything under here is related to what messages the plugin displays
  - There are some placeholders that can be used if applicable, examples can be found in the default config
  - Possible placeholders are:
    - carrier - carrier name
    - owner - carrier owner
    - textPrice - price per text of carrier
    - callPrice - price per call message of carrier
    - peers - a list of peers
    - subscriberCount - number of subscribers
    - subscribers - list of subscribers
    - peer - name of the peer carrier
    - peerOwner - owner of the peer carrier
    - peerTextPrice - price per text of the peer carrier
    - peerCallPrice - price per call message of the peer carrier
    - peerSubscribers - number of subscribers of the peer carrier
    - bestBandTower - type of best tower searched by type
    - bestBandTowerStrength - strength of best tower searched by type
    - bestSignalTower - type of best tower searched by signal strength
    - bestSignalTowerStrength - strength of best tower searched by signal strength
    - averageSignalArea - the size of the area of the signal scan
    - averageSignalStrength - the average strength from the scan
    - averageCellType - the most found tower type by the scan
    - coverage - percentage value of the coverage
