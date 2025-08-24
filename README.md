# Dynamic TFC Structures

Did you ever dream to fill your **TerraFirmaCraft** world with authentic structures? Dynamic TFC Structures will bring
them to your world!

![](src/main/resources/assets/tfc_structures/textures/logo.png)

Dynamic TFC Structures can activate any world-gen structure and replace their blocks with TerraFirmaCraft analogues like
it was built specially for TFC.\
The mod is also fully configurable, you may find configuration files in **config/tfc\_structures** folder. By default,
only vanilla structures are activated, but you can activate any other modded structure.

## Disclaimer

Be aware that Dynamic TFC Structures is currently in **BETA**, so it **contains bugs** and **there are things to do**.

## Configurations

Dynamic TFC Structures has 3 configuration files:

* **common-config.toml**
  _allowedDimensions_ - list of dimensions where structure block will be replaced with TFC blocks  
  _biomesTagsStructuresToLogs_ - debug-toggle to output biomes, biome-tags and structures to logs  
  _mossyBlocks_/_strippedLogs_/_strippedWood_/_crackedBricks_ - special tags for replacements. All of them will be
  generated to special block-tags.
* **worldgen\_config.json**
  _biomeTags_ - definitions of biome-tags that will be generated to `tfc_structures_datapacks/tfc_structures_main`
  datapack. It also contains structures that will be generated in that biome-tag.  
  _disabledStructures_ - list of structures you don't want to see in your world
  _defaultWorldgenStructures_ - list of structures that exist in game, but was not added to any list above, so they will
  use default generation rules
* **structures\_config.json**
  _structures_ - list of replacement rules for every structure, like replacement preset, lootTablesOverrideMap, etc.
* **replacement\_config.json**
  _directReplacements_ - block-to-block map to direct replacement when structure have spawned  
  _randomReplacements_ - block-to-many-blocks map to random replacement. perBlock=false means whole structure will use
  one block.  
  _tfcWorldReplacements_ - block-to-type map to block replacement with TFC blocks from surroundings(like stone, wood,
  soil, sand or ores)

## TFC Structures Datapacks folder

TFC Structures generates its own data-pack using configuration files, so it has its own directory for datapacks - *
*_tfc\_structures\_datapacks/tfc\_structures\_main_**.\
But you may place YOUR datapacks or KubeJS-like datapack folder(use `tfc_structures_main` datapack as an example)
directly to **_tfc\_structures\_datapacks_** folder, and it will load upon world creation.

## Known issues

* Some structures may be lost(Treasures and Portals)
* Currently, _tfcWorldReplacement_ is not compatible with non-default TFC blocks(
  eg, [ArborFirmaCraft](https://www.curseforge.com/minecraft/mc-mods/arborfirmacraft))