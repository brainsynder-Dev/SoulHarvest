# Soul Harvest

A NeoForge mod that aims to help harvest mob drops and experience without any drops or experience being spawned on the ground.
It is still in early development and more features will be added as I get more ideas/recommendations

## History
I originally came up with this mod back in 1.10 (I kept crashing the server I played on due to entity drops :P) even got the majority of the code created back then, but life got the better of me and it was shelved.
Then 1.21 came around and I attempted to reignite the mod, at that point I had the blocks, textures, and models to go along with the code I made before, but again life happened and was shelved again.
Then came `AllTheMods 11` with no MobGrindingUtils or easy way to farm mobs, So I said f*** it and went all out.

## Requirements
- Minecraft `26.1.2`
- NeoForge `26.1.2.124` or newer
- Java `25`

## Development Goals:
- [ ] Add armor set(s)
- [ ] Add tool set(s)
- [ ] In-game wiki (without mod dependencies hopefully) aka guide book
- [ ] Stats collection and gui for the extractor and altar
- [ ] More decoration blocks
- [ ] Figure out good default values for the mob spawn configs 

### End Game Goals:
- [ ] Simulated Mob kills so there is no need for an actual mob farm

## Building
```bash
./gradlew runData build
```

### Development Testing
This command runs the `runData` then `runClient` to ensure the dev client has the most up-to-date data generation
```bash
./gradlew runDataClient
```

## AI Usage Disclosure
AI was used for:
- coming up with rough texture/model renders which the current textures/models are based on
- documentation/description texts

## License
All Rights Reserved
