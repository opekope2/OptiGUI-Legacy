# OptiGUI

A client side drop-in replacement for OptiFine custom GUIs.

This mod supports most OptiFine GUI resource packs. I didn't do an extensive test with the .properties files, because I couldn't find such a resource pack.
If a resource pack does not work or works differently than using OptiFine, please open an issue.

## How do I use it
1. Download ([GitHub releases](https://github.com/opekope2/OptiGUI/releases) / [Modrinth](https://modrinth.com/mod/optigui) / [CurseForge](https://www.curseforge.com/minecraft/mc-mods/optigui))
2. Put it in your mods folder

## OptiFine extensions

This mod supports some additional GUIs which are not (yet) supported by OptiFine. These start with an underscore (`_`) to minimize the chance of an incompatibility with an upcoming OptiFine update.

### `container` extensions:

* `container` accepts the following additional types: `_cartography_table, _grindstone, _loom, _smithing_table, _stonecutter` to replace the GUI of a cartography table, grindstone, loom, smithing table, or stonecutter, respectively.

### `container=chest` extensions:

* `_barrel=<true|false>`: replace the GUI of a barrel.

### `container=furnace` extensions:

* `variants` accepts the following values: `_furnace, _blast, _blast_furnace, _smoker` to replace the GUI of a furnace, blast furnace (2x), or smoker, respectively.

### `container=villager` extensions:

* `professions` additionally accepts the value `_wandering_trader` to replace the GUI of a wandering trader.

## FAQ

### Can I use it in a modpack?

Yes, the license allows that.

The mod is included in the [Fabulously Optimized](https://github.com/Fabulously-Optimized/fabulously-optimized) modpack.

### Does this mod support OptiFine GUI animations?

Not by itself, please check out [Animatica](https://github.com/FoundationGames/Animatica). Supported as of OptiGUI v0.2.0.

### Does this mod support OptiFine colors (colors.properties)?

No, see [Colormatic](https://github.com/kvverti/colormatic).

### Does this mod have a configuration GUI/file?

No. Just load a resource pack and it works out-of-the-box.
