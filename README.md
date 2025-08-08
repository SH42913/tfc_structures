# TFC Structures

Did you ever dream to fill your **TerraFirmaCraft** world with authentic structures? TFC Structures will bring them to
your world!

TFC Structures can activate any world-gen structure and replace their blocks with TerraFirmaCraft analogues like it was
built specially for TFC. TFC Structures is also fully configurable, you may find configuration files in *
*config/tfc\_structures** folder. By default, only vanilla structures are activated, but you can activate any other
modded structure.

## Disclaimer

Be aware that TFC Structures is currently in **ALPHA**, so it **contains bugs** and there are **a lot of things to do**.

## Configurations

TFC Structures has 3 configuration files:

* **common-config.toml**  
  _allowedDimensions_ \- list of dimensions where structure block will be replaced with TFC blocks  
  _biomesTagsStructuresToLogs_ \- debug-toggle to output biomes, biome-tags and structures to logs  
  _mossyBlocks_/_strippedLogs_/_strippedWood_ \- special tags for replacements, for all of them will be generated
  special block-tags to its own datapack.
* **structure\_config.json**  
  _biomeTags_ \- definitions of biome-tags that will be generated to
  \`tfc\_structures\_datapacks/tfc\_structures\_main\` datapack  
  _activeStructures_ \- definitions of structures you want to see in your world and biomes where they should generate  
  _disabledStructures_ \- list of structures you don't want to see in your world  
  _unregisteredStructures_ \- list of structures that exist in game, but was not added to any list above
* **replacement\_config.json**  
  _directReplacements_ \- block-to-block map to direct replacement when structure have spawned  
  _tfcWorldReplacement_ \- block-to-type map to block replacement with TFC blocks from surroundings(like stone, wood or
  soil)

## TFC Structures Datapacks folder

TFC Structures generates its own data-pack using configuration files, so it has its own directory for datapacks - *
*tfc\_structures\_datapacks/tfc\_structures\_main**. But you may place YOUR datapacks or KubeJS-like datapack folder(use
tfc\_structures\_main datapack as an example) directly to **tfc\_structures\_datapacks** folder, and it will load upon
world creation.