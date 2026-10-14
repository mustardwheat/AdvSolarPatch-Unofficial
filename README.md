# AdvSolarPatch-Unofficial

A patch and extension mod for Advanced Solar Panels 4.3.0 on Minecraft 1.12.2, forked from [Su5eD/AdvSolarPatch](https://github.com/Su5eD/AdvSolarPatch).

## Fixes

- Crashing with newer IC2 builds (2.8.191+):
  - `java.lang.NoSuchMethodError: ic2.core.block.invslot.InvSlotProcessable.<init>(Lic2/core/block/TileEntityInventory;Ljava/lang/String;ILic2/api/recipe/IMachineRecipeManager;)V`
  - `java.lang.NoSuchMethodError: ic2.core.block.invslot.InvSlotOutput.<init>(Lic2/core/block/TileEntityInventory;Ljava/lang/String;I)V`
  - `java.lang.NoSuchMethodError: ic2.core.block.invslot.InvSlot.<init>(Lic2/core/block/TileEntityInventory;Ljava/lang/String;Lic2/core/block/invslot/InvSlot$Access;ILic2/core/block/invslot/InvSlot$InvSide;)V`
- Removes the `advanced_solar_panels:double_stone_slab` item and its recipe to prevent adjacent block duplication.

## Features

- Configurable max output (EU/t) for the four solar panels in the `solars` category of the Advanced Solar Panels config file:
  - `AdvancedSPMaxOutput` (default `32`)
  - `HybrydSPMaxOutput` (default `128`)
  - `UltimateHSPMaxOutput` (default `512`)
  - `QuantumSPMaxOutput` (default `8192`)
- Hybrid and Ultimate Hybrid Solar Helmets:
  - Implement `IHazmatLike` for full-set radiation protection.
  - Clear negative potion effects, auto-feed canned food, and toggle night vision without applying blindness in bright light.
- Advanced, Hybrid, and Ultimate Hybrid Solar Helmets display day/night EU/t generation in their item tooltips.

## Dependencies

- IndustrialCraft 2 (2.8.191+)
- Advanced Solar Panels (4.3.0)
- MixinBooter (10.2+)

## License

Licensed under [GPL-3.0](LICENSE).
