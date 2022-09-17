# OptiGUI

A client side drop-in replacement for OptiFine custom GUIs.

This mod supports most OptiFine GUI resource packs. I didn't do an extensive test with the .properties files, because I couldn't find such a resource pack.
If a resource pack does not work or works differently than using OptiFine, please open an issue.

## How do I use it
1. Download and install [Fabric](https://fabricmc.net/use)
2. Download [Fabric API](https://fabricmc.net/use) and put it in your mods folder
3. Download this mod from [GitHub releases](https://github.com/opekope2/OptiGUI/releases), [Modrinth](https://modrinth.com/mod/optigui/versions), or [CurseForge](https://www.curseforge.com/minecraft/mc-mods/optigui/files)
4. Put this mod in your mods folder

## Supported resource packs

* [Colourful Containers](https://www.planetminecraft.com/texture-pack/colourful-containers-gui/)
* [Colourful Containers Add-On](https://www.planetminecraft.com/texture-pack/updated-colourful-containers-light-mode-gui-optifine-required/)
* [Colourful Containers Dark Mode](https://www.planetminecraft.com/texture-pack/colourful-containers-dark-mode-gui-optifine-required/)
* [Animated RGB GUI](https://www.curseforge.com/minecraft/texture-packs/optifine-animated-rgb-gui) (see FAQ for animation support)
* [NEON20 ANIMATED](https://www.planetminecraft.com/texture-pack/neon20-animated-optifine/)
* [Rybo's Enhanced GUI](https://www.planetminecraft.com/texture-pack/rybo-s-enhanced-gui/)

This list is not exhaustive. If you know a resource pack which works, please open an issue. If you know a resource pack which does not work, but works with OptiFine, please open an issue.

## Partially supported resource packs

* [Advanced GUI](https://www.planetminecraft.com/texture-pack/custom-gui/) ¹

¹ Color shulker boxes, Ender chests and Villager Professions do not work. The resource pack does not include OptiFine-specific assets, and these features are not supported by OptiGUI. (Some mods (Colourful Containers) include assets for these features and work on OptiGUI)

## Incompatible mods

These mods are known to have issues with OptiGUI. Unless noted otherwise, these mods **can** be loaded alongside OptiGUI, but interfere with this mod's functionality.

* [Litematica Printer](https://github.com/aleksilassila/litematica-printer) - print mode resets any open GUI texture. Reopening the GUI fixes the texture. [#15](https://github.com/opekope2/OptiGUI/issues/15)

## Additions to OptiFine

This mod supports some additional GUIs which are not (yet) supported by OptiFine. These start with an underscore (`_`) to minimize the chance of an incompatibility with an upcoming OptiFine update.

### `container` additions:

* `container` accepts the following additional types: `_cartography_table, _grindstone, _loom, _smithing_table, _stonecutter` to replace the GUI of a cartography table, grindstone, loom, smithing table, or stonecutter, respectively.

### `container=chest` additions:

* `_barrel=<true|false>`: replace the GUI of a barrel.

### `container=furnace` additions:

* `variants` accepts the following values: `_furnace, _blast, _blast_furnace, _smoker` to replace the GUI of a furnace, blast furnace (2x), or smoker, respectively.

### `container=villager` additions:

* `professions` additionally accepts the value `_wandering_trader` to replace the GUI of a wandering trader.

## FAQ

### Can I use this mod in a modpack?

Yes, the license allows that.

The mod is included in the [Fabulously Optimized](https://github.com/Fabulously-Optimized/fabulously-optimized) modpack.

### Does this mod support OptiFine GUI animations?

Not by itself, please check out [Animatica](https://github.com/FoundationGames/Animatica). Supported as of OptiGUI v0.2.0.

### Does this mod support OptiFine colors (colors.properties)?

No, see [Colormatic](https://github.com/kvverti/colormatic).

### Does this mod have a configuration GUI/file?

No. Just load a resource pack and it works out-of-the-box.

### Does this mod work with Sodium?

Yes. As of 2022-09-08, Sodium does not interfere with OptiGUI. If Sodium introduces a change which breaks OptiGUI, please open an issue.
