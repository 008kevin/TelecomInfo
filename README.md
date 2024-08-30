[![](https://img.shields.io/github/issues/008kevin/TelecomInfo?style=for-the-badge)](https://github.com/008kevin/TelecomInfo/issues) [![](https://img.shields.io/github/release/008kevin/TelecomInfo?style=for-the-badge)](https://github.com/008kevin/TelecomInfo/releases) [![](https://pluginbadges.glitch.me/api/v1/dl/Downloads-orange.svg?spigot=telecominfo.119255&github=008kevin%2FTelecomInfo&style=for-the-badge)](https://www.spigotmc.org/resources/telecominfo.119255/) [![](https://img.shields.io/github/stars/008kevin/TelecomInfo?style=for-the-badge)]()

[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/B0B712JIZG)


### Get notified about updates here:
[![](https://dcbadge.limes.pink/api/server/https://discord.gg/5hNaqhpczK)](https://discord.gg/5hNaqhpczK)
# About
A simple plugin that is an extension to [dbteku's Telecom plugin](https://www.spigotmc.org/resources/telecom.42914/)
# Requirements
- Minecraft 1.17 and up
- Java 17
- Telecom (beta 0.31 and higher)


- PlaceholderAPI (optional, for placeholder support)
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

# Placeholders
- There are 3 types of placeholders, providing info about different things.
- The placeholderAPI format looks like this: %telecominfo\_[type]\_[carrier]\_[info]%
  - Replace the things in square brackets with the required information, carrier is the name of the carrier you want the info about, and info is the specific information you want to get.
## Types
- carrier
  - carrier - The name of the carrier
  - owner - Owner of the carrier
  - textPrice - Price per text
  - callPrice - Price per call message
  - subscribers - A list of subscribers separated by commas
  - subscriberCount - The number of the current count of subscribers
  - peers - A comma separated list of all peered carriers
- location
  - bestBandTower - The type of tower that is providing the highest type signal
  - bestBandTowerStrength - the strength of the tower that is providing the highest type of signal
  - bestSignalTower - The type of tower providing the strongest signal
  - bestSignalTowerStrength - The signal strength of the tower providing the strongest signal
- area | ***Warning, use not reccomended!*** Can be very resource intensive if frequently queried.
  - averageSignalRadius - The radius that is set in config to be scanned
  - averageSignalStrength - The average strength of signal scanned from the player's location
  - averageCellType - The average type of tower scanned from the player's location
  - coverage - The percentage that signal was found at during the scan

## Using placeholder in the lang
- Currently, PlaceholderAPI is not supported
- These, with a few extras can be used in the lang, but in a different way.
  - To use them, but whatever info you want inside {curly braces}
  - Some commands only support a few types
    - listCommand: carrier
    - infoCommand
      - subscribers: carrier
      - signal: carrier, location
      - general: carrier
      - price: carrier, extra
        - here you can use some extra placeholders:
          - peer - Name of the peered carrier
          - peerOwner - Name of the owner of the peered carrier
          - peerTextPrice - The price of texts at the peered carrier
          - peerCallPrice - The price of call messages at the peered carrier
          - peerSubscribers - The number of subscribers the peered carrier has
